package com.cts.AuditSeverity.controller;


import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.cts.AuditSeverity.fiegnclient.*;
import com.cts.AuditSeverity.fiegnclient.AuditBenchmarkFeignclient;
import com.cts.AuditSeverity.fiegnclient.AuditCheckListProxy;
import com.cts.AuditSeverity.fiegnclient.AuthClient;
import com.cts.AuditSeverity.pojo.AuditBenchmark;
import com.cts.AuditSeverity.pojo.AuditDetails;
import com.cts.AuditSeverity.pojo.AuditRequest;
import com.cts.AuditSeverity.pojo.AuditResponse;
import com.cts.AuditSeverity.pojo.AuditType;
import com.cts.AuditSeverity.pojo.QuestionsEntity;
import com.cts.AuditSeverity.service.AuditRequestResponse;
import com.cts.AuditSeverity.service.TokenService;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * This class is handling all the end points for Audit Checklist microservice. 
 * This controller has mappings as-
 * postmapping-auditSeverity()
 *
 */


@RestController
@Slf4j
public class SeverityController {
	
	@Autowired
	private AuditRequestResponse service;
	@Autowired
	AuthClient authClient;
	@Autowired
	AuditCheckListProxy checklistProxy;
	
	@Autowired
	TokenService tokenService;
	
	
	@Autowired
	AuditBenchmarkFeignclient auditBenchmarkFeignclient;
	/**
	 * 
	 * @param token
	 * @param auditRequest
	 * @return ResponseEntity<Response>
	 */
	@PostMapping("/ProjectExecutionStatus")
	public AuditResponse auditSeverity(@RequestHeader(name = "Authorization",required = true)String token,@RequestBody AuditRequest auditRequest) {
		try {
		int accNoAnswers = 0;
		int actualNoAnswers = 0;
		ResponseEntity<?> responseEntity = null;
		List<QuestionsEntity> questionsList = null;
		AuditResponse response = null;
		AuditDetails aDetails=auditRequest.getAuditDetails();
		List<QuestionsEntity> aRes = aDetails.getAuditQuestions();
		if (tokenService.checkTokenValidity(token)) {
			List<AuditBenchmark> benchmarkList = auditBenchmarkFeignclient.getBenchmarkMap(token).getBody();
			
			for (AuditBenchmark benchmark : benchmarkList) {
				//auditRequest.getAuditDetails().getAuditType())
				if (benchmark.getAuditType().equalsIgnoreCase(aDetails.getAuditType())) {
					accNoAnswers = benchmark.getAccNoAnswers();
				}
			}
			AuditType auditType = new AuditType(auditRequest.getAuditDetails().getAuditType());
			questionsList = checklistProxy.getChecklist(token, auditType).getBody();
			
			for(QuestionsEntity q : aRes) {
				log.info("response: {}", q.getResponse());
				if(q.getResponse().equalsIgnoreCase("NO")) ++actualNoAnswers;
			}
			
            log.info("no answers {}",actualNoAnswers);
            
            
			if (actualNoAnswers <= accNoAnswers) {
				responseEntity = new ResponseEntity<AuditResponse>(new AuditResponse(
						"green", "no action needed"), HttpStatus.OK);
				response = (AuditResponse) responseEntity.getBody();
				service.saveResponse(response);
			} else if (auditRequest.getAuditDetails().getAuditType().equalsIgnoreCase("Internal")) {
				responseEntity = new ResponseEntity<AuditResponse>(
						new AuditResponse("red",
								"Action to be taken in one week"),
						HttpStatus.OK);
				response = (AuditResponse) responseEntity.getBody();
				service.saveResponse(response);
			} else if (auditRequest.getAuditDetails().getAuditType().equalsIgnoreCase("SOX")) {
				responseEntity = new ResponseEntity<AuditResponse>(
						new AuditResponse("red",
								"Action to be taken in two weeks"),
						HttpStatus.OK);
				response = (AuditResponse) responseEntity.getBody();
				service.saveResponse(response);
			}
			service.saveRequest(auditRequest);
			return response;
		} else {
			if(log.isInfoEnabled() && log.isErrorEnabled())
			{
				log.error("Token Expired!");
				log.info("String end");				
			}

			responseEntity = new ResponseEntity<String>("Token Expired!", HttpStatus.FORBIDDEN);
			return response;
		}
}finally {}
		
		}

}

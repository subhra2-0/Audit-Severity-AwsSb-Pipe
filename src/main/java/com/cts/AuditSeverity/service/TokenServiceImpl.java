package com.cts.AuditSeverity.service;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import com.cts.AuditSeverity.exception.FeignProxyException;
import com.cts.AuditSeverity.fiegnclient.AuthClient;
import com.cts.AuditSeverity.pojo.AuthResponse;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
/**
 * 

 * This class is implementing {@link TokenService}.
 * The method of this class is used in other classes to validate token received.
 * 
 * @see AuthClient
 *
 */
@Service
@Slf4j
public class TokenServiceImpl implements TokenService {

	/**
	 * Interface interacting with Auth microservice
	 */
	@Autowired
	private AuthClient authClient;

	/**
	 * @param token
	 * @return boolean this method will check the token validity by communicating
	 *         with auth client.
	 */
	public Boolean checkTokenValidity(String token)  {
		log.info("Audit severity token validation starts");
    	log.debug("token", token);

		try {
			log.debug("validation sucess:");
			AuthResponse authResponse = authClient.getValidity(token).getBody();
			if(authResponse==null) throw new FeignProxyException();

			log.info("Audit severity token validation ends");
			
			return authResponse.isValid();
		} catch (FeignProxyException fe) {
			
			log.debug("validation fails");
			log.error("feign exception",fe);
			
			
			return false;
		}catch(FeignException e) {
			log.debug("validation fails");
			log.error("feign exception",e);
						return false;
		}
		
	}

}

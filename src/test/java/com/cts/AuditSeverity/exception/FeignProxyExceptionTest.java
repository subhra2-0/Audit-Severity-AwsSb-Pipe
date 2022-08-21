package com.cts.AuditSeverity.exception;


import static org.junit.jupiter.api.Assertions.assertNotNull;

//import org.junit.Test;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 *
 */

@ContextConfiguration @Slf4j
@SpringBootTest
public class FeignProxyExceptionTest {

	/**
	 * to test the FeignProxyException
	 * 
	 */
	
	@Test
	public void testInvalidAuthorizationException() {
		log.info("start");
		FeignProxyException feignProxyException = new FeignProxyException();
		assertNotNull(feignProxyException);
		log.info("end");

	}
}

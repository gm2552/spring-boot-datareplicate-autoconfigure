package org.springframework.boot.autoconfigure.r2dbc;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class SpringBootDatareplicateAutoconfigureApplicationTests {

	@Autowired
	private ApplicationContext ctx;
	
	@Test
	void contextLoads() {
		
		assertTrue(ctx.containsBean("connectionFactory"));
		assertTrue(ctx.containsBean("replicatedConnectionFactory"));
		assertNotNull(ctx.getBean(ReplicatedTransactionContext.class));
	}

}

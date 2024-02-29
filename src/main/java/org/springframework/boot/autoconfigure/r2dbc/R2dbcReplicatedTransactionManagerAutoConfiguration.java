package org.springframework.boot.autoconfigure.r2dbc;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;
import org.springframework.transaction.ReactiveTransactionManager;

@AutoConfiguration(before = R2dbcTransactionManagerAutoConfiguration.class)
@ConditionalOnClass({ R2dbcTransactionManager.class, ReactiveTransactionManager.class })
@ConditionalOnSingleCandidate(ReplicatedConnectionFactory.class)
@AutoConfigureOrder(Ordered.LOWEST_PRECEDENCE)
public class R2dbcReplicatedTransactionManagerAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean(ReactiveTransactionManager.class)
	public R2dbcTransactionManager connectionFactoryTransactionManager(ReplicatedConnectionFactory connectionFactory) {
		return new R2dbcTransactionManager(connectionFactory);
	}	
	
}

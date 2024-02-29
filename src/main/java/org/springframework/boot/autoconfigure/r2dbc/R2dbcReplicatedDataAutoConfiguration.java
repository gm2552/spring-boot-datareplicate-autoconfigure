package org.springframework.boot.autoconfigure.r2dbc;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.context.annotation.Bean;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.core.DatabaseClient;

@AutoConfiguration(after = R2dbcAutoConfiguration.class)
@ConditionalOnClass({ DatabaseClient.class, R2dbcEntityTemplate.class })
@ConditionalOnSingleCandidate(ReplicatedConnectionFactory.class)
public class R2dbcReplicatedDataAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public R2dbcEntityTemplate r2dbcEntityTemplate(ReplicatedConnectionFactory connectionFactory) {
		return new R2dbcEntityTemplate(connectionFactory);
	}
}

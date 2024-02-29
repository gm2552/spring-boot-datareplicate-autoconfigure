package org.springframework.boot.autoconfigure.r2dbc;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnResource;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration.PropertiesR2dbcConnectionDetails;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import io.r2dbc.spi.ConnectionFactory;

@AutoConfiguration(before = {R2dbcAutoConfiguration.class})
@ConditionalOnClass(ConnectionFactory.class)
@ConditionalOnResource(resources = "classpath:META-INF/services/io.r2dbc.spi.ConnectionFactoryProvider")
@EnableConfigurationProperties(ReplicatedDataSource.class)
@Import({ ReplicatedConnectionFactoryConfigurations.PoolConfiguration.class,
	ReplicatedConnectionFactoryConfigurations.GenericConfiguration.class, ReplicatedConnectionFactoryConfigurations.ReplicatedConfiguration.class })
public class R2dbcReplicatedAutoConfiguration
{	
	@Bean
	@ConditionalOnProperty(prefix = "spring.r2dbc.replicated", name={"rw.url", "ro.url"})
	public ReplicatedPropertiesR2dbcConnectionDetails replicatedPropertiesR2dbcConnectionDetails(ReplicatedDataSource source ) {
	
		return new ReplicatedPropertiesR2dbcConnectionDetails(source.getReplicated());
		
	}	
	
	static class ReplicatedPropertiesR2dbcConnectionDetails
	{		
		private final ReplicatedDataSource.Replicated dataSource;
		
		private final PropertiesR2dbcConnectionDetails rwDetails;
		
		private final PropertiesR2dbcConnectionDetails roDetails;
		
		ReplicatedPropertiesR2dbcConnectionDetails(ReplicatedDataSource.Replicated replicateDataSource) {
			
			this.dataSource = replicateDataSource;
			this.rwDetails = new PropertiesR2dbcConnectionDetails(replicateDataSource.getRw());
			this.roDetails = new PropertiesR2dbcConnectionDetails(replicateDataSource.getRo());
		}
		
		public String getName() {
			return dataSource.getName();
		}
		
		public R2dbcProperties getReadWriteR2dbcProperties() {
			return dataSource.getRw();
		}
		
		public R2dbcProperties getReadOnlyR2dbcProperties() {
			return dataSource.getRo();
		}
		
		public PropertiesR2dbcConnectionDetails getReadWriteConnectionDetails() {
			return rwDetails;
		}
		
		public PropertiesR2dbcConnectionDetails getReadOnlyConnectionDetails() {
			return roDetails;
		}
	}
}

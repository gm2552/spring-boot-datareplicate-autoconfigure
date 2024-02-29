package org.springframework.boot.autoconfigure.r2dbc;


import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration.PropertiesR2dbcConnectionDetails;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcProperties.Pool;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcReplicatedAutoConfiguration.ReplicatedPropertiesR2dbcConnectionDetails;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.r2dbc.ConnectionFactoryDecorator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.data.util.Pair;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.spi.ConnectionFactory;

public abstract class ReplicatedConnectionFactoryConfigurations
{
	public static final String DEFAULT_TX_ROUTING_KEY = "REPLICATED_TX";
	
	public static final String DEFAULT_RW_KEY = "RW";
	
	public static final String DEFAULT_RO_KEY = "RO";
	
	@Configuration(proxyBeanMethods = false)
	@ConditionalOnMissingBean(ReplicatedConnectionFactory.class)
	static class PoolConfiguration {

		@Configuration(proxyBeanMethods = false)
		@ConditionalOnClass(ConnectionPool.class)
		static class PooledConnectionFactoryConfiguration {
			
			@ConditionalOnProperty(prefix = "spring.r2dbc.replicated", name={"rw.url", "ro.url"})
			@Conditional(RWPooledConnectionFactoryCondition.class)
			@Bean(destroyMethod = "dispose")
			ConnectionPool rwReplicatedConnectionFactory(ObjectProvider<ReplicatedPropertiesR2dbcConnectionDetails> connectionDetails, ResourceLoader resourceLoader,
					ObjectProvider<ConnectionFactoryOptionsBuilderCustomizer> customizers,
					ObjectProvider<ConnectionFactoryDecorator> decorators)
			{
				
				var replicateDetails = connectionDetails.getIfAvailable();
			
				return createPooledConnectionFactor(replicateDetails.getReadWriteR2dbcProperties(), replicateDetails.getReadWriteConnectionDetails(),
						resourceLoader, customizers, decorators);
			}
			
			@ConditionalOnProperty(prefix = "spring.r2dbc.replicated", name={"rw.url", "ro.url"})
			@Conditional(ROPooledConnectionFactoryCondition.class)
			@Bean(destroyMethod = "dispose")
			ConnectionPool roReplicatedConnectionFactory(ObjectProvider<ReplicatedPropertiesR2dbcConnectionDetails> connectionDetails, ResourceLoader resourceLoader,
					ObjectProvider<ConnectionFactoryOptionsBuilderCustomizer> customizers,
					ObjectProvider<ConnectionFactoryDecorator> decorators)
			{
				
				var replicateDetails = connectionDetails.getIfAvailable();
			
				return createPooledConnectionFactor(replicateDetails.getReadOnlyR2dbcProperties(), replicateDetails.getReadOnlyConnectionDetails(),
						resourceLoader, customizers, decorators);
			}
			
			private ConnectionPool createPooledConnectionFactor(R2dbcProperties properties, PropertiesR2dbcConnectionDetails connectionDetails, 
					ResourceLoader resourceLoader, ObjectProvider<ConnectionFactoryOptionsBuilderCustomizer> customizers,
					ObjectProvider<ConnectionFactoryDecorator> decorators)
			{

				
				var connectionFactory = ConnectionFactoryConfigurations.createConnectionFactory(properties, 
						connectionDetails, resourceLoader.getClassLoader(), 
						customizers.orderedStream().toList(), decorators.orderedStream().toList());
				
				R2dbcProperties.Pool pool =  properties.getPool();
				PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
				ConnectionPoolConfiguration.Builder builder = ConnectionPoolConfiguration.builder(connectionFactory);
				map.from(pool.getMaxIdleTime()).to(builder::maxIdleTime);
				map.from(pool.getMaxLifeTime()).to(builder::maxLifeTime);
				map.from(pool.getMaxAcquireTime()).to(builder::maxAcquireTime);
				map.from(pool.getMaxCreateConnectionTime()).to(builder::maxCreateConnectionTime);
				map.from(pool.getInitialSize()).to(builder::initialSize);
				map.from(pool.getMaxSize()).to(builder::maxSize);
				map.from(pool.getValidationQuery()).whenHasText().to(builder::validationQuery);
				map.from(pool.getValidationDepth()).to(builder::validationDepth);
				map.from(pool.getMinIdle()).to(builder::minIdle);
				map.from(pool.getMaxValidationTime()).to(builder::maxValidationTime);
				return new ConnectionPool(builder.build());
			}
		}
	}
	
	@Configuration(proxyBeanMethods = false)
	@ConditionalOnMissingBean(ReplicatedConnectionFactory.class)
	static class GenericConfiguration {

		@Bean
		@ConditionalOnProperty(prefix = "spring.r2dbc.replicated.rw.pool", value = "enabled", havingValue = "false",
		matchIfMissing = false)		
		ConnectionFactory rwReplicatedConnectionFactory(ObjectProvider<ReplicatedPropertiesR2dbcConnectionDetails> connectionDetails, ResourceLoader resourceLoader,
				ObjectProvider<ConnectionFactoryOptionsBuilderCustomizer> customizers,
				ObjectProvider<ConnectionFactoryDecorator> decorators) {
			
			
			var replicateDetails = connectionDetails.getIfAvailable();
			
			return createGenericConnectionFactor(replicateDetails.getReadWriteR2dbcProperties(), replicateDetails.getReadWriteConnectionDetails(),
					resourceLoader, customizers, decorators);
		}

		@Bean
		@Primary
		@ConditionalOnProperty(prefix = "spring.r2dbc.replicated.ro.pool", value = "enabled", havingValue = "false",
		matchIfMissing = false)		
		ConnectionFactory roReplicatedConnectionFactory(ObjectProvider<ReplicatedPropertiesR2dbcConnectionDetails> connectionDetails, ResourceLoader resourceLoader,
				ObjectProvider<ConnectionFactoryOptionsBuilderCustomizer> customizers,
				ObjectProvider<ConnectionFactoryDecorator> decorators) {
			
			
			var replicateDetails = connectionDetails.getIfAvailable();
			
			return createGenericConnectionFactor(replicateDetails.getReadOnlyR2dbcProperties(), replicateDetails.getReadOnlyConnectionDetails(),
					resourceLoader, customizers, decorators);
		}
		
		private ConnectionFactory createGenericConnectionFactor(R2dbcProperties properties, PropertiesR2dbcConnectionDetails connectionDetails, 
				ResourceLoader resourceLoader, ObjectProvider<ConnectionFactoryOptionsBuilderCustomizer> customizers,
				ObjectProvider<ConnectionFactoryDecorator> decorators)
		{

			
			return ConnectionFactoryConfigurations.createConnectionFactory(properties, 
					connectionDetails, resourceLoader.getClassLoader(), 
					customizers.orderedStream().toList(), decorators.orderedStream().toList());
		}
		
	}	
	
	@ConditionalOnProperty(prefix = "spring.r2dbc.replicated", name={"rw.url", "ro.url"})
	@Configuration(proxyBeanMethods = false)
	static class ReplicatedConfiguration {
		
		@Bean
		@ConditionalOnMissingBean(ReplicatedTransactionContext.class)
		ReplicatedTransactionContext replicatedTransactionContext()
		{
			return new ReplicatedTransactionContext(DEFAULT_TX_ROUTING_KEY, DEFAULT_RW_KEY, DEFAULT_RO_KEY);
		}
		
		@Bean
		@ConditionalOnMissingBean(ReplicatedConnectionFactory.class)
		ReplicatedConnectionFactory replicatedConnectionFactory(@Qualifier("rwReplicatedConnectionFactory") ConnectionFactory rwFactory,
				@Qualifier("roReplicatedConnectionFactory") ConnectionFactory roFactory, ReplicatedTransactionContext context)
		{
			return new ReplicatedRoutingConnectionFactory(Pair.of(context.getRwKey(), rwFactory),
					Pair.of(context.getRoKey(), roFactory), context.getRoutingKey());
		}
	}

	static abstract class AbstractPooledConnectionFactoryCondition extends SpringBootCondition {

		@Override
		public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
			BindResult<Pool> pool = Binder.get(context.getEnvironment())
				.bind(getPropertyPrefix() + "pool", Bindable.of(Pool.class));
			if (hasPoolUrl(context.getEnvironment())) {
				if (pool.isBound()) {
					throw new MultipleConnectionPoolConfigurationsException();
				}
				return ConditionOutcome.noMatch("URL-based pooling has been configured");
			}
			if (pool.isBound() && !ClassUtils.isPresent("io.r2dbc.pool.ConnectionPool", context.getClassLoader())) {
				throw new MissingR2dbcPoolDependencyException();
			}
			if (pool.orElseGet(Pool::new).isEnabled()) {
				return ConditionOutcome.match("Property-based pooling is enabled");
			}
			return ConditionOutcome.noMatch("Property-based pooling is disabled");
		}

		private boolean hasPoolUrl(Environment environment) {
			String url = environment.getProperty(getPropertyPrefix() + "url");
			return StringUtils.hasText(url) && url.contains(":pool:");
		}

		protected abstract String getPropertyPrefix();
	}
	
	static class RWPooledConnectionFactoryCondition extends AbstractPooledConnectionFactoryCondition
	{
		protected String getPropertyPrefix()
		{
			return "spring.r2dbc.replicated.rw";
		}
	}
	
	static class ROPooledConnectionFactoryCondition extends AbstractPooledConnectionFactoryCondition
	{
		protected String getPropertyPrefix()
		{
			return "spring.r2dbc.replicated.ro";
		}
	}	
}

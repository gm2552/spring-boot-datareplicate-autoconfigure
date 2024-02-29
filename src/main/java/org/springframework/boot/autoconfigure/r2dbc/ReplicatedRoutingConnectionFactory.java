package org.springframework.boot.autoconfigure.r2dbc;

import java.util.Map;

import org.springframework.data.util.Pair;
import org.springframework.r2dbc.connection.lookup.AbstractRoutingConnectionFactory;

import io.r2dbc.spi.ConnectionFactory;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

public class ReplicatedRoutingConnectionFactory extends AbstractRoutingConnectionFactory implements ReplicatedConnectionFactory
{	
	private final Object routingKey;
	
	private final Pair<Object, ConnectionFactory> rwConnectionFactory;
	
	private final Pair<Object, ConnectionFactory> roConnectionFactory;
	
	public ReplicatedRoutingConnectionFactory(Pair<Object, ConnectionFactory> rwConnectionFactory, Pair<Object, ConnectionFactory> roConnectionFactory,
			Object routingKey) {
		
		this.rwConnectionFactory = rwConnectionFactory;
		this.roConnectionFactory = roConnectionFactory;
		
		this.routingKey = routingKey;
		
		this.setTargetConnectionFactories(Map.of(rwConnectionFactory.getFirst(), rwConnectionFactory.getSecond(), 
				roConnectionFactory.getFirst(), roConnectionFactory.getSecond()));
		this.setDefaultTargetConnectionFactory(rwConnectionFactory.getSecond());
	}
	
	
	@Override
	protected Mono<Object> determineCurrentLookupKey() {
		
		return Mono.deferContextual(contextView -> {

			if (contextView.hasKey(routingKey))
				return Mono.just(contextView.get(routingKey));

			return Mono.empty();

		});
	}

	@Override
	public Context getRWRoutingContext() {
		
		return Context.of(routingKey, rwConnectionFactory.getFirst());
	}
	
	@Override
	public Context getRORoutingContext() {
		
		return Context.of(routingKey, roConnectionFactory.getFirst());
	}
}

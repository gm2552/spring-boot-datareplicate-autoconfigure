package org.springframework.boot.autoconfigure.r2dbc;

public class ReplicatedTransactionContext {
	
	private final Object routingKey;
	
	private final Object rwKey;
	
	private final Object roKey;
	
	public ReplicatedTransactionContext(Object routingKey, Object rwKey, Object roKey) {
		
		this.routingKey = routingKey;
		
		this.rwKey = rwKey;
		
		this.roKey = roKey;
	}

	public Object getRoutingKey() {
		return routingKey;
	}

	public Object getRwKey() {
		return rwKey;
	}

	public Object getRoKey() {
		return roKey;
	}

	
}

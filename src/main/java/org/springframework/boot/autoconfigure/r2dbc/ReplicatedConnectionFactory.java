package org.springframework.boot.autoconfigure.r2dbc;

import io.r2dbc.spi.ConnectionFactory;
import reactor.util.context.Context;

public interface ReplicatedConnectionFactory extends ConnectionFactory {

	public Context getRWRoutingContext();
	
	public Context getRORoutingContext();
}

# Replicate data source autoconfiguration

This library implements a POC of auto configuring a replicated R2DBC ConnectionFactory using
the `spring.r2dbc.replicated` properties.  

## Configuration

Auto configuration is triggered based on the following properties being present:

```
spring.r2dbc.replicated.rw.url
spring.r2dbc.replicated.ro.url
```

If both of these properties are present, a ConnectionFactory is create for each URL and then fed into an
`AbstractRoutingConnectionFactory` implementation the encapsulates the previous two connection factory to implement
a "smart" router.  The router is configured with default `ReplicatedTransactionContext` if one does not already exist 
and sets the routing keys within the `AbstractRoutingConnectionFactory`.  A developer can override the routing keys
by creating their own `ReplicatedTransactionContext` context bean.


## Testing

This library includes a trivial test that load spring boot application and assert the existence of the
`replicatedConnectionFactory` bean.
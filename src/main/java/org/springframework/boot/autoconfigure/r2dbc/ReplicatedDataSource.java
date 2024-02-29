package org.springframework.boot.autoconfigure.r2dbc;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.r2dbc")
public class ReplicatedDataSource 
{
	private Replicated replicated;
	
	public void setReplicated(Replicated replicated) {
		this.replicated = replicated;
	}
	
	public Replicated getReplicated() {
		return this.replicated;
	}
	
	public static class Replicated
	{
		private String name;
		
		private R2dbcProperties rw;
		
		private R2dbcProperties ro;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public R2dbcProperties getRw() {
			return rw;
		}

		public void setRw(R2dbcProperties rw) {
			this.rw = rw;
		}

		public R2dbcProperties getRo() {
			return ro;
		}

		public void setRo(R2dbcProperties ro) {
			this.ro = ro;
		}
	}
}

package config;

public class GmailAnalyzeConfig {
	
	private GmailAnalyzeConfig() {}
	
	public static class GmailConfig {
		
		private String user = "me";
		
		public String getUser() {
			return user;
		}
		
		public void setUser(String user) {
			this.user = user;
		}
		
	}
	
	public static class ElasticConfig {
		
		private String host = "localhost";
		private int port = 9300;
		private String mapping = "mapping.json";
		
		public String getMapping() {
			return mapping;
		}
		public void setMapping(String mapping) {
			this.mapping = mapping;
		}
		public String getHost() {
			return host;
		}
		public void setHost(String host) {
			this.host = host;
		}
		public int getPort() {
			return port;
		}
		public void setPort(int port) {
			this.port = port;
		}		
		
	}
	
	private ElasticConfig elastic;
	private GmailConfig gmail;
	
	public ElasticConfig getElastic() {
		return elastic;
	}
	public void setElastic(ElasticConfig elastic) {
		this.elastic = elastic;
	}
	public GmailConfig getGmail() {
		return gmail;
	}
	public void setGmail(GmailConfig gmail) {
		this.gmail = gmail;
	}

}

package config;

import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class GmailAnalyzeConfigFactory {

	private static Logger log = LoggerFactory.getLogger(GmailAnalyzeConfigFactory.class);

	private static GmailAnalyzeConfig config;

	public static GmailAnalyzeConfig getConfig() throws Exception {

		if(config != null) {
			return config;
		}

		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());			
		InputStream src = GmailAnalyzeConfigFactory.class.getResourceAsStream("/config.yaml");
		
		return config = mapper.readValue(src, GmailAnalyzeConfig.class);
		
	}

}

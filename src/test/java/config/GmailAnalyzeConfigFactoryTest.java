package config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class GmailAnalyzeConfigFactoryTest {

	@Test
	public void testGetConfig() throws Exception {
		GmailAnalyzeConfig config = GmailAnalyzeConfigFactory.getConfig();		
		assertNotNull(config);
	}
	
	@Test
	public void testGetConfigTwiceShouldBeSame() throws Exception {
		GmailAnalyzeConfig config1 = GmailAnalyzeConfigFactory.getConfig();
		GmailAnalyzeConfig config2 = GmailAnalyzeConfigFactory.getConfig();
		assertEquals(config1, config2);
	}

}

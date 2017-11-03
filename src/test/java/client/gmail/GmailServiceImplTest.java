package client.gmail;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;

@RunWith(JUnit4.class)
public class GmailServiceImplTest {
	
	private static Gmail gmail;
	private static GmailServiceImpl gmailService;
	
	@BeforeClass
	public static void setUp() throws IOException {
		gmail = GmailSetup.getGmailService();
		gmailService = new GmailServiceImpl("me", gmail);
	}
	
	@Test
	public void testGetFirstPage() throws Exception {
		
		GmailPage firstPage = gmailService.getFirstPage(12);
		
		assertNotNull(firstPage);
		assertNotNull(firstPage.getMessages());
		assertNotNull(firstPage.getNextPageToken());		
		assertEquals(12, firstPage.getMessages().size());
		
	}
	
	@Test
	public void testGetNextPage() throws Exception {
		
		GmailPage firstPage = gmailService.getFirstPage(12);
		String pageToken = firstPage.getNextPageToken();
		
		GmailPage nextPage = gmailService.getNextPage(12, pageToken);
		assertNotNull(nextPage);
		assertNotNull(nextPage.getMessages());
		assertEquals(12, nextPage.getMessages().size());
		
	}

	@Test
	public void testGetBatch() throws IOException {
		// some hard-coded message ids from my account
		List<String> testIds = Arrays.asList(
								"15f6ecbed1029b2e",
								"15f6ec6cf5e4c9d8",
								"15f6e7c4cd1f0352",
								"15f6e45a9ad5e58e",
								"15f6e352f7170688",
								"15f6e2008be1b5b9",
								"15f6e0da0418de9b",
								"15f6e0568729fb3d",
								"15f6db08021cefc5",
								"15f6da63cabb1179"
								);
		
		List<Message> batch = gmailService.getBatch(testIds);
		
		assertEquals(10, batch.size());
		
		batch.forEach(m -> {
			assertNotNull(m);
		});

	}

}

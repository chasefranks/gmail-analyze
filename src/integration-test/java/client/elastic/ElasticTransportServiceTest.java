package client.elastic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.text.CharacterPredicates;
import org.apache.commons.text.RandomStringGenerator;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import com.google.api.services.gmail.model.MessagePartHeader;

@RunWith(JUnit4.class)
public class ElasticTransportServiceTest {
	
	private ElasticTransportService elasticService;
	private TransportClient client;
	private RandomStringGenerator generator;
	
	// the name of the temporary index that we will work under for test purposes
	private String testIndex = "gmail-analyze-it";
	private String elasticHost = "localhost";
	
	@Before
	public void setUp() throws UnknownHostException, FileNotFoundException {
		
		generator = new RandomStringGenerator
							.Builder()
								.withinRange('0', 'z')
								.filteredBy(CharacterPredicates.LETTERS, CharacterPredicates.DIGITS)
								.build();
		
		client = new PreBuiltTransportClient(Settings.EMPTY)
			    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(elasticHost), 9300));
		
		// create test fixture
		elasticService = new ElasticTransportService(testIndex, client);	
		
		// delete index if it exists
		elasticService.deleteGmailIndex();
		
		// create index and mapping
		elasticService.createMapping();
		
	}

	/**
	 * Indexes a single message.
	 */
	@Test
	public void testIndexMessage() {		
		Message testMessage = generateRandomTestMessage("abc123");		
		elasticService.indexMessage(testMessage);
		
		GetResponse response = client.prepareGet(testIndex, "message", "abc123")
								.get();
		
		// assert message was indexed and can be retrieved
		assertNotNull(response);
	}

	@Test
	public void testIndexMessages() {
		
		List<Message> randomMessages = Arrays.asList(
			generateRandomTestMessage(),
			generateRandomTestMessage(),
			generateRandomTestMessage(),
			generateRandomTestMessage(),
			generateRandomTestMessage()
		);
		
		BulkResponse response = elasticService.indexMessages(randomMessages);
		response.forEach(itemResponse -> {
			itemResponse.getFailure()
		});	
		
	}
	
	private Message generateRandomTestMessage() {
		
		Message testMessage = new Message();
		testMessage.setId(generator.generate(10));
		
		Date now = new Date();
		
		testMessage.setInternalDate(now.getTime());
		
		MessagePartHeader from = new MessagePartHeader();
		from.setName("From");
		from.setValue("Gmail Analyzer Testing <nowhere@example.com>");
		
		MessagePartHeader subject = new MessagePartHeader();
		subject.setName("Subject");
		subject.setValue("Gmail Analyzer Testing is Happening");
		
		MessagePart messagePart = new MessagePart();
		messagePart.setHeaders(Arrays.asList(from, subject));
		
		testMessage.setPayload(messagePart);
		
		return testMessage;
		
	}
	
	private Message generateRandomTestMessage(String id) {		
		Message testMessage = generateRandomTestMessage();
		testMessage.setId(id);
		
		return testMessage;		
	}
	
	@After
	public void tearDown() {
		elasticService.deleteGmailIndex();
		client.close();
	}

}

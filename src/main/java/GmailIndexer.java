import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.cluster.health.ClusterHealthStatus;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.Profile;

import client.elastic.ElasticTransportService;
import client.gmail.GmailPage;
import client.gmail.GmailService;
import client.gmail.GmailServiceImpl;
import client.gmail.GmailSetup;
import config.GmailAnalyzeConfig;
import config.GmailAnalyzeConfigFactory;

public class GmailIndexer {
	
	private static Logger log = LoggerFactory.getLogger(GmailIndexer.class);
	
	private static long PAGE_SIZE = 100L;
	
	private static Gmail gmail;
	private static TransportClient client;
	
	public static void main(String[] args) throws Exception {
		
		GmailAnalyzeConfig config = GmailAnalyzeConfigFactory.getConfig();
		
		InetAddress inetAddress = InetAddress.getByName(config.getElastic().getHost());
		
		TransportAddress transportAddress = new InetSocketTransportAddress(inetAddress, config.getElastic().getPort());
		
		// set up Elasticsearch and test connection
		client = new PreBuiltTransportClient(Settings.EMPTY)
			    			.addTransportAddress(transportAddress);
		
		// test Elasticsearch cluster
		testElastic(client);
		
		String userId = config.getGmail().getUser();		
		ElasticTransportService elasticService = new ElasticTransportService(userId, client);
		
		// set up Gmail service
		gmail = GmailSetup.getGmailService();
		testGmail(gmail);		
		
		GmailService gmailService = new GmailServiceImpl(userId, gmail);	
		
		// create index and mapping
		elasticService.createMapping();		
		
		log.info("populating index, be patient this can take a while...");
		/*
		 * main loop - retrieves each page of Gmail message of requested page size
		 * message will at first have only id and threadId populated, so we
		 * then iterate over each of these to retrieve the actual message meta data and populate
		 * a List with our data to index
		 */
		GmailPage page = gmailService.getFirstPage(PAGE_SIZE);
		
		while (page.getMessages() != null) {
			elasticService.indexMessages(page.getMessages());
			
			if (page.getNextPageToken() != null) {
				page = gmailService.getNextPage(PAGE_SIZE, page.getNextPageToken());
			} else {
				break;
			}
			
		}		
		
		log.info("indexing complete, exiting...");
		log.info("try this command to see if your index has been populated:");
		log.info("curl http://{}:{}/{}/_search?pretty", config.getElastic().getHost(), config.getElastic().getPort(), userId);
		client.close();
			
	}

	private static void testElastic(TransportClient client) {
		
		log.info("Setting up/Testing Elastic Transport client");
		ClusterHealthResponse health = client.admin().cluster().prepareHealth().get();
		
		log.info("Cluster Health Status: " + health.getStatus());
		
		if (health.getStatus() == ClusterHealthStatus.YELLOW)
			log.info(" (YELLOW is normal before indices have been created)");		
		
		log.info("Cluster Nodes: " + health.getNumberOfNodes());		
		log.info("Elasticsearch looks good!...");
		
	}
	
	private static void testGmail(Gmail gmail) throws IOException {
		log.info("Testing connection to Gmail API");
		
		Profile profile = gmail.users().getProfile("me").execute();
		log.info("User Profile: " + profile.toPrettyString());
		
		log.info("connection to Gmail looks good");
	}

}

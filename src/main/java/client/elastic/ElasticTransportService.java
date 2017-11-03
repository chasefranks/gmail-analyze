package client.elastic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.services.gmail.model.Message;

public class ElasticTransportService extends ElasticService {
	
	private static Logger log = LoggerFactory.getLogger(ElasticTransportService.class);
	
	private TransportClient client;

	public ElasticTransportService(String index, TransportClient client) {
		super(index);
		this.client = client;
	}

	@Override
	public IndexResponse indexMessage(Message m) {		
		return client.prepareIndex(gmailIndex, messageType, m.getId())
			.setSource(mapMessage(m))
			.get();
	}

	@Override
	public BulkResponse indexMessages(List<Message> messages) {
		
		log.debug("indexing " + messages.size() + " messages");
		
		// use bulk API
		BulkRequestBuilder bulkRequest = client.prepareBulk();
		
		// stream over the messages, map to a Map<String,String>, and request to index to the bulk API request
		messages.stream()
			.map(this::mapMessage)
			.map(source -> client.prepareIndex(gmailIndex, messageType, source.get("id")).setSource(source))
			.forEach(indexRequestBuilder -> {
				bulkRequest.add(indexRequestBuilder);
			});
		
		// execute
		return bulkRequest.get();
		
	}

	public void deleteGmailIndex() {
		IndicesAdminClient adminClient = this.client.admin().indices();		
		adminClient.delete(new DeleteIndexRequest(gmailIndex));		
	}
	
	public void createMapping() throws FileNotFoundException {
		
		IndicesAdminClient adminClient = this.client.admin().indices();
		
		File mappingFile = new File("elastic/mapping.json");
		
		BufferedReader reader = new BufferedReader(new FileReader(mappingFile));
		
		StringBuilder source = new StringBuilder();
		
		reader.lines()
			.forEach(line -> source.append(line));
		
		// create index
		adminClient.prepareCreate(gmailIndex)
			.get();
		
		log.info("creating index " + gmailIndex + " with mapping:");
		log.info(source.toString());
		
		// PUT mapping for message type
		adminClient.preparePutMapping(gmailIndex)
			.setType("message")
			.setSource(source.toString(), XContentType.JSON)
			.get();
		
	}

}

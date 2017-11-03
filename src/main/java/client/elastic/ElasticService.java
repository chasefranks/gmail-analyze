package client.elastic;

import java.util.HashMap;
import java.util.List;

import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexResponse;

import com.google.api.services.gmail.model.Message;

/**
 * The idea here for this abstract class is to keep our API separate from the actual Elasticsearch client implementation,
 * which is in flux at this time. For example, the transport client is hinted to be deprecated in favor of a high-level REST
 * client (see this <a href="https://www.elastic.co/guide/en/elasticsearch/client/java-api/current/java-api.html">warning</a>).
 * 
 * @author chase
 */
public abstract class ElasticService {
	
	String gmailIndex = "gmail_messages";	
	String messageType = "message";
	
	public ElasticService(String index) {
		this.gmailIndex = index;
	}
	
	public abstract IndexResponse indexMessage(Message message);
	
	public abstract BulkResponse indexMessages(List<Message> messages);
	
	public HashMap<String, String> mapMessage(Message message) {
		
		HashMap<String, String> map = new HashMap<>();
		
		map.put("gmail_id", message.getId());
		map.put("received", Long.toString(message.getInternalDate()));
		
		message.getPayload().getHeaders()
			.stream() // need to null check here (see bugfix https://trello.com/c/cf9sWkWL)
			.forEach(header -> {
				switch (header.getName()) {
				case "From":
					map.put("from", header.getValue());
					break;					
				case "Subject":
					map.put("subject", header.getValue());
					break;
				default:
					break;
				}
			});
		
		return map;
		
	}

	public String getGmailIndex() {
		return gmailIndex;
	}

	public void setGmailIndex(String gmailIndex) {
		this.gmailIndex = gmailIndex;
	}

	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

}

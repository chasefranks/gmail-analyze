package client.gmail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.batch.json.JsonBatchCallback;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.http.HttpHeaders;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;

public class GmailServiceImpl implements GmailService {
	
	private static final Logger log = LoggerFactory.getLogger(GmailServiceImpl.class);

	private String userId;
	private Gmail gmail;	

	public GmailServiceImpl(String userId, Gmail gmail) {
		this.userId = userId;
		this.gmail = gmail;
	}

	@Override
	public GmailPage getFirstPage(long size) throws IOException {
		
		log.debug("getting first page of Gmail messages: size=" + size);

		// get list of messages, these will only have message,thread id populated
		ListMessagesResponse listResponse = gmail
				.users()
				.messages()
				.list(userId)
				.setMaxResults(size)
				.execute();

		// map message to message id and perform batch request
		List<String> messageIds = listResponse.getMessages()
				.stream()
				.map(m -> m.getId())
				.collect(Collectors.toList());

		List<Message> fullMessages = getBatch(messageIds);

		GmailPage firstPage = new GmailPage(fullMessages, listResponse.getNextPageToken());

		return firstPage;

	}

	@Override
	public GmailPage getNextPage(long size, String pageToken) throws IOException {
		
		log.debug("getting page of size {} using next page token {}", size, pageToken);
		
		ListMessagesResponse listResponse = gmail
											.users()
											.messages()
											.list(userId)
											.setMaxResults(size)
											.setPageToken(pageToken)
											.execute();
		
		// map message to message id and perform batch request
		List<String> messageIds = listResponse.getMessages()
												.stream()
												.map(m -> m.getId())
												.collect(Collectors.toList());
		
		
		List<Message> fullMessages = getBatch(messageIds);

		GmailPage nextPage = new GmailPage(fullMessages, listResponse.getNextPageToken());

		return nextPage;
		
	}

	List<Message> getBatch(List<String> messageIds) throws IOException {
		
		log.debug("performing batch GET request with " + messageIds.size() + " message ids");
		
		final List<Message> messages = new ArrayList<>();
		
		// start batch request container
		BatchRequest batch = gmail.batch();
		
		/*
		 * for each message id, build GET request and queue it in batch request
		 * along with callback
		 */
		for (String id: messageIds) {			
			gmail
				.users()
				.messages()
					.get(userId, id)
					.setFormat("metadata")
					.setMetadataHeaders(Arrays.asList("From", "Subject"))
					.queue(batch, new JsonBatchCallback<Message>() {				
						@Override
						public void onSuccess(Message t, HttpHeaders responseHeaders) throws IOException {
							messages.add(t);
						}
						
						@Override
						public void onFailure(GoogleJsonError e, HttpHeaders responseHeaders) throws IOException {
							log.error("error getting message with id " + id);				
						}
					});			
		}		
		
		batch.execute();
		return messages;
		
	}

}

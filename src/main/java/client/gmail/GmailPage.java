package client.gmail;

import java.util.List;

import com.google.api.services.gmail.model.Message;

/**
 * Holds a single page of Gmail messages as well as the next page token.
 * @author chase
 */
public class GmailPage {

	private List<Message> messages;
	private String nextPageToken;
	
	public GmailPage() {}

	public GmailPage(List<Message> messages, String nextPageToken) {
		this.messages = messages;
		this.nextPageToken = nextPageToken;
	}
	public List<Message> getMessages() {
		return messages;
	}
	public void setMessages(List<Message> messages) {
		this.messages = messages;
	}
	public String getNextPageToken() {
		return nextPageToken;
	}
	public void setNextPageToken(String nextPageToken) {
		this.nextPageToken = nextPageToken;
	}

}

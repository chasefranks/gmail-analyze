package client.gmail;

import java.io.IOException;

public interface GmailService {	
	
	/**
	 * @param size the max number of messages to return
	 * @return the first page of results
	 * @throws IOException 
	 */
	GmailPage getFirstPage(long size) throws IOException;	
	
	/**
	 * @param size the max number of messages to return
	 * @param pageToken the token from the previous page of results
	 * @return the next page of results
	 * @throws IOException 
	 */
	GmailPage getNextPage(long size, String pageToken) throws IOException;	

}

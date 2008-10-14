package org.intalio.tempo.uiframework;

import junit.framework.TestCase;

import org.intalio.tempo.security.ws.TokenClient;

public class ConfigurationTest extends TestCase {
	public void  testAll()throws Exception{
		
		Configuration conf = Configuration.getInstance();
		String feedUrl = "http://www.intalio.org/feed";
		String baseUrl = "http://www.intalio.org/baseUrl";
		String serviceEndpoint = "http://www.intalio.org/";
		TokenClient tc = new TokenClient(serviceEndpoint);
		conf.setFeedUrl(feedUrl);
		conf.setFeedItemBaseUrl(baseUrl);
		conf.setPagingLength(1);
		conf.setRefreshTime(2);
		conf.setTokenClient(tc);
		conf.setServiceEndpoint(serviceEndpoint);
		
		assertTrue(conf.getFeedItemBaseUrl().equals(baseUrl));
		assertTrue(conf.getFeedUrl().equals(feedUrl));
		assertTrue(conf.getPagingLength() == 1);
		assertTrue(conf.getRefreshTime() == 2);
		assertTrue(conf.getServiceEndpoint().equals(serviceEndpoint));
		assertTrue(conf.getTokenClient() == tc);
		
	}
}

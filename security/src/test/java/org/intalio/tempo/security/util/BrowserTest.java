package org.intalio.tempo.security.util;

import junit.framework.TestCase;

public class BrowserTest extends TestCase{
	public static void main(String[] args) {
		junit.textui.TestRunner.run(BrowserTest.class);
	}
	
	public void testMain()throws Exception{
		String[] args = new String[]{"-config", "src/test/resources/SimpleSpringTest.xml", "-roles", "-users"};
		
		Browser browser = new Browser();
		browser.main(args);
	}

	public void testMain2()throws Exception{
		String[] args = new String[]{"-config", "src/test/resources/SimpleSpringTest.xml", "-user",  "intalio:eng1", "-password", "eng1"};
		
		Browser browser = new Browser();
		browser.main(args);
	}
	
	public void testMain3()throws Exception{
		String[] args = new String[]{"-config", "src/test/resources/SimpleSpringTest.xml", "-user",  "intalio:eng1", "-password", "incorrectpassword"};
		
		Browser browser = new Browser();
		browser.main(args);
	}
	
	public void testMain4()throws Exception{
		String[] args = new String[]{"-config", "src/test/resources/SimpleSpringTest.xml", "-user",  "intalio:eng1", "-role", "incorrectrole"};
		
		Browser browser = new Browser();
		browser.main(args);
	}
	
	public void testMain5()throws Exception{
		String[] args = new String[]{"-config", "src/test/resources/SimpleSpringTest.xml", "-user",  "intalio:eng1", "-role", "intalio\\eng"};
		
		Browser browser = new Browser();
		browser.main(args);
	}
	
	public void testMain6()throws Exception{
		String[] args = new String[]{"-config", "src/test/resources/SimpleSpringTest.xml", "-user",  "intalio:eng1"};
		
		Browser browser = new Browser();
		browser.main(args);
	}
	
	public void testMain7()throws Exception{
		String[] args = new String[]{"-config", "src/test/resources/SimpleSpringTest.xml", "-role", "intalio\\eng"};
		
		Browser browser = new Browser();
		browser.main(args);
	}

}

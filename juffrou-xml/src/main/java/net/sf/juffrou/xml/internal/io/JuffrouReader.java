package net.sf.juffrou.xml.internal.io;


public interface JuffrouReader {

	String next();

	String enterNode();
	
	void exitNode();
	
	String getNodeName();
	
	String getText();
}

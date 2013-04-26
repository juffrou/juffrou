package org.juffrou.xml.internal.io;

public interface JuffrouReader {

	String next();

	String enterNode();
	
	void exitNode();
	
	String getValue();
}

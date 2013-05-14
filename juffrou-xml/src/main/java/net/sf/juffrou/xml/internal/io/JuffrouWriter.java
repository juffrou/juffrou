package net.sf.juffrou.xml.internal.io;

public interface JuffrouWriter {

	void startNode(String nodeName);
	void endNode();
	void setAttribute(String attributeName, String value);
	void write(String value);
}

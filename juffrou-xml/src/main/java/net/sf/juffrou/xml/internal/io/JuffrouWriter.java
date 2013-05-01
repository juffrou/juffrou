package net.sf.juffrou.xml.internal.io;

public interface JuffrouWriter {

	void startNode(String nodeName);
	void endNode();
	void write(String value);
}

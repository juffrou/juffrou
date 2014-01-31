package net.sf.juffrou.xml.internal.io;

import net.sf.juffrou.xml.internal.NodeType;

public interface JuffrouWriter {

	void startNode(String nodeName, NodeType nodeType);
	void endNode();
	void setAttribute(String attributeName, String value);
	void write(String value);
}

package net.sf.juffrou.xml.internal.io;


public interface JuffrouReader {

	/**
	 * Enter into the contents of an element. If the element has attributes position in the first attribute. If not, position in the first child element.
	 * @return the name of the first attribute or first child element.
	 */
	String enterNode();

	/**
	 * Position in the next attribute or next sibling element
	 * @return the name of the attribute or element
	 */
	String next();

	void exitNode();
	
	String getNodeName();
	
	String getText();
}

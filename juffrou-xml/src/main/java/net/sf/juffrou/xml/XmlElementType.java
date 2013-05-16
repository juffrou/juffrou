package net.sf.juffrou.xml;


public enum XmlElementType {

	BEAN("bean"),
	BOOLEAN("boolean"),
	BYTE("byte"),
	CHARACTER("character"),
	DOUBLE("double"),
	FLOAT("float"),
	INTEGER("integer"),
	LONG("long"),
	SHORT("short"),
	STRING("string"),
	BIGINTEGER("biginteger"),
	BIGDECIMAL("bigdecimal"),
	DATE("date"),
	ENUM("enum"),
	LIST("list"),
	SET("set"),
	MAP("map");
	
	private String tag;
	private XmlElementType(String tag) {
		this.tag = tag;
	}
	
	public String xml() {
		return tag;
	}
}

What's new in Juffrou 2.0.10

juffrou-reflect Module:

 Nested BeanWrappers now have reference to their parent and nestedBW.getBean() or setBean(...) return or set the value of the parent wrapper property.
 
juffrou-xml-spring Module:

 JuffrouXmlSpring extends JuffrouXml and can now be parameterized as a bean.
 JuffrouMarshaller may be configured with a juffrouXmlSpring property or mapping location properties.
What's new in Juffrou 2.1.3

Improvements to Juffrou-reflect - added support for virtual properties, i.e. no actual property, but getter and setter methods present.

Advancements towards compatibility between JuffrouBeanWrapper and SpringFramework's BeanWrapperImpl

New in Juffrou 2.1.2

juffrou-reflect Module:

 JuffrouBeanWrapper.getNestedBeanWrapper can now receive a nested path as parameter.
 
juffrou-xml-spring Module:

 new JuffrouSpringBeanWrapper is the Spring framework's BeanWrapper implementation using JuffrouBeanWrapper.
 
New in Juffrou 2.0.10

juffrou-reflect Module:

 Nested BeanWrappers now have reference to their parent and nestedBW.getBean() or setBean(...) return or set the value of the parent wrapper property.
 
juffrou-xml-spring Module:

 JuffrouXmlSpring extends JuffrouXml and can now be parameterized as a bean.
 JuffrouMarshaller may be configured with a juffrouXmlSpring property or mapping location properties.
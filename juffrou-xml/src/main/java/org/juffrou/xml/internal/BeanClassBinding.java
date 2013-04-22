package org.juffrou.xml.internal;

import java.util.Map;

import org.juffrou.util.reflect.BeanWrapperContext;

public class BeanClassBinding {

	private BeanWrapperContext beanWrapperContext;
	private String xmlElementName;
	private Map<String, BeanPropertyBinding> beanPropertiesToMarshall;
}

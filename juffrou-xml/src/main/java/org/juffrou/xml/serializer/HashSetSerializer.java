package org.juffrou.xml.serializer;

import java.util.Collection;
import java.util.HashSet;

import org.juffrou.xml.internal.JuffrouBeanMetadata;

public class HashSetSerializer extends AbstractCollectionSerializer {

	public HashSetSerializer(JuffrouBeanMetadata xmlBeanMetadata) {
		super(xmlBeanMetadata);
	}

	@Override
	protected Collection<?> instantiateCollection() {
		return new HashSet();
	}

}

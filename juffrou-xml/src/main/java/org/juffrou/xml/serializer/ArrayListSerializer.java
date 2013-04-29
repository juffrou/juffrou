package org.juffrou.xml.serializer;

import java.util.ArrayList;
import java.util.Collection;

import org.juffrou.xml.internal.JuffrouBeanMetadata;

public class ArrayListSerializer extends AbstractCollectionSerializer {

	public ArrayListSerializer(JuffrouBeanMetadata xmlBeanMetadata) {
		super(xmlBeanMetadata);
	}

	@Override
	protected Collection<?> instantiateCollection() {
		return new ArrayList();
	}

}

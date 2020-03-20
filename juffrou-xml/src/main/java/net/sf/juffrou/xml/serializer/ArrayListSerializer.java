package net.sf.juffrou.xml.serializer;

import net.sf.juffrou.xml.internal.JuffrouBeanMetadata;

import java.util.ArrayList;
import java.util.Collection;

public class ArrayListSerializer extends AbstractCollectionSerializer {

	public ArrayListSerializer(JuffrouBeanMetadata xmlBeanMetadata) {
		super(xmlBeanMetadata);
	}

	@Override
	protected Collection<?> instantiateCollection() {
		return new ArrayList();
	}

}

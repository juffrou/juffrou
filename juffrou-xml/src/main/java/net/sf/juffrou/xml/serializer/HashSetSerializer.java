package net.sf.juffrou.xml.serializer;

import net.sf.juffrou.xml.internal.JuffrouBeanMetadata;

import java.util.Collection;
import java.util.HashSet;

public class HashSetSerializer extends AbstractCollectionSerializer {

	public HashSetSerializer(JuffrouBeanMetadata xmlBeanMetadata) {
		super(xmlBeanMetadata);
	}

	@Override
	protected Collection<?> instantiateCollection() {
		return new HashSet();
	}

}

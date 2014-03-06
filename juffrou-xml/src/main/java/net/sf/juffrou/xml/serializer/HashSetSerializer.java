package net.sf.juffrou.xml.serializer;

import java.util.Collection;
import java.util.HashSet;

import net.sf.juffrou.xml.internal.JuffrouBeanMetadata;

public class HashSetSerializer extends AbstractCollectionSerializer {

	public HashSetSerializer(JuffrouBeanMetadata xmlBeanMetadata) {
		super(xmlBeanMetadata);
	}

	@Override
	protected Collection<?> instantiateCollection() {
		return new HashSet();
	}

}

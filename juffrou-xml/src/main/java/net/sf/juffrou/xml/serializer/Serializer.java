package net.sf.juffrou.xml.serializer;

import net.sf.juffrou.util.reflect.BeanWrapper;
import net.sf.juffrou.xml.internal.io.JuffrouReader;
import net.sf.juffrou.xml.internal.io.JuffrouWriter;

/**
 * Serializes and deserializes objects.<p>
 * The target of serialization is a bean property and is presented through a bean wrapper and a property name.
 * @author cemartins
 */
public interface Serializer {

	/**
	 * Serializes a bean property and writes it.
	 * @param writer the writer to write the object to
	 * @param valueOwner bean wrapper that holds the object in one of its properties
	 * @param valuePropertyName property in the bean wrapper to access the object
	 */
	void serialize(JuffrouWriter writer, BeanWrapper valueOwner, String valuePropertyName);
	
	/**
	 * Reads the serialized form of a bean property and sets its value to the bean.
	 * @param reader the reader from which to read
	 * @param valueOwner bean wrapper that holds the object in one of its properties 
	 * @param valuePropertyName property in the bean wrapper to access the object
	 */
	void deserialize(JuffrouReader reader, BeanWrapper valueOwner, String valuePropertyName);
}

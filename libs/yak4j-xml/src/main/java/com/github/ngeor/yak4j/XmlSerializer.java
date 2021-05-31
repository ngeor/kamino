package com.github.ngeor.yak4j;

import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/**
 * (De)serializes an object into XML.
 */
@SuppressWarnings("WeakerAccess")
public class XmlSerializer {
    /**
     * Serializes an object into XML.
     *
     * @param whatever The object to serialize.
     * @param clazz    The type of the object.
     * @param <T>      The type of the object.
     * @return The XML representation.
     */
    public <T> String serialize(T whatever, Class<T> clazz) {
        StringWriter writer = new StringWriter();
        try {
            JAXBContext context = JAXBContext.newInstance(clazz);
            Marshaller marshaller = context.createMarshaller();
            marshaller.marshal(whatever, writer);
            return writer.toString();
        } catch (JAXBException ex) {
            throw new XmlRuntimeException(ex);
        }
    }

    /**
     * De-serializes an object from XML.
     * @param xml The XML representation.
     * @param clazz The class to de-serialize into.
     * @param <T> The class to de-serialize into.
     * @return The de-serialized object.
     */
    @SuppressWarnings("unchecked")
    public <T> T deserialize(String xml, Class<T> clazz) {
        try {
            JAXBContext context = JAXBContext.newInstance(clazz);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            return (T) unmarshaller.unmarshal(new StringReader(xml));
        } catch (JAXBException ex) {
            throw new XmlRuntimeException(ex);
        }
    }

    /**
     * De-serializes from XML.
     * @param xml The XML representation.
     * @param clazz One or more classes that might be able to deserialize into.
     * @return The de-serialized object.
     */
    public Object deserializeAny(String xml, Class<?>... clazz) {
        try {
            JAXBContext context = JAXBContext.newInstance(clazz);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            return unmarshaller.unmarshal(new StringReader(xml));
        } catch (JAXBException ex) {
            throw new XmlRuntimeException(ex);
        }
    }
}

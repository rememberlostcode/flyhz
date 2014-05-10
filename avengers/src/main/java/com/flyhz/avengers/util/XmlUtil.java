
package com.flyhz.avengers.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.charset.Charset;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Schema;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

public class XmlUtil {

	private static final Logger	log	= LoggerFactory.getLogger(XmlUtil.class);

	@SuppressWarnings("unchecked")
	public static <T> T convertXmlToObject(Class<T> clazz, URL xmlUrl, Schema schema) {
		JAXBContext jc;
		try {
			jc = JAXBContext.newInstance(clazz);
			Unmarshaller um = jc.createUnmarshaller();
			if (schema != null) {
				um.setSchema(schema);
			}
			return (T) um.unmarshal(xmlUrl);
		} catch (JAXBException e) {
			log.error("", e);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T convertStringToObject(Class<T> clazz, String strXml, Schema schema) {
		JAXBContext jc;
		InputStream is = null;
		try {
			jc = JAXBContext.newInstance(clazz);
			Unmarshaller um = jc.createUnmarshaller();
			if (schema != null) {
				um.setSchema(schema);
			}
			is = new ByteArrayInputStream(strXml.getBytes(Charset.forName("UTF-8")));
			return (T) um.unmarshal(is);
		} catch (JAXBException e) {
			log.error("", e);
			return null;
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}

		}
	}

	public static <T> String convertObjectToString(T jaxbObject, Schema schema) {
		JAXBContext jc;
		ByteArrayOutputStream os = null;
		try {
			jc = JAXBContext.newInstance(jaxbObject.getClass());
			Marshaller marshaller = jc.createMarshaller();
			if (schema != null) {
				marshaller.setSchema(schema);
			}
			os = new ByteArrayOutputStream();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
			marshaller.marshal(jaxbObject, os);
			return os.toString("UTF-8");
		} catch (JAXBException e) {
			log.error("", e);
			return null;
		} catch (UnsupportedEncodingException e) {
			log.error("", e);
			return null;
		} finally {
			if (os != null)
				try {
					os.close();
				} catch (IOException e) {
				}
		}
	}

	public static String convertXmlToString(URL xmlUrl) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			factory.setNamespaceAware(false);
			builder = factory.newDocumentBuilder();
			Document doc = builder.parse(xmlUrl.getFile());
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			OutputStream os = new ByteArrayOutputStream();
			Source source = new DOMSource(doc.getLastChild());
			transformer.transform(source, new StreamResult(os));
			return os.toString();
		} catch (Exception e) {
			log.error("", e);
		}
		return null;
	}

	public static Document convertStringToXml(String str) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			factory.setNamespaceAware(false);

			builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new ByteArrayInputStream(str.getBytes()));
			return doc;
		} catch (Exception e) {
			log.error("", e);
		}
		return null;
	}

}

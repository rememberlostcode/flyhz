
package com.flyhz.avengers.util;

import java.net.URL;

import javax.xml.XMLConstants;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.commons.lang.StringUtils;
import org.xml.sax.SAXException;

import com.flyhz.avengers.Avengers;

/**
 * 类说明:分类工具类
 * 
 * @author robin
 * @version 创建时间：2013-5-28 下午6:16:54
 */
public class AvengersDataUtil {

	/**
	 * 把Avengers对象转化为Xml字符串
	 * 
	 * @param Avengers
	 * @return
	 */
	public static String convertAvengersToXmlStr(Avengers Avengers) {
		if (Avengers == null)
			return null;

		String data = null;
		try {
			ClassLoader classLoader = AvengersDataUtil.class.getClassLoader();
			URL schemaUrl = classLoader.getResource("avengers.xsd");
			Schema schema = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
											.newSchema(schemaUrl);
			data = XmlUtil.convertObjectToString(Avengers, schema);
		} catch (SAXException e) {
			e.printStackTrace();
		}
		return data;
	}

	/**
	 * 获取分类数据
	 * 
	 * @return
	 */
	public static Avengers getDataByXmlStr(String strXml) {
		if (StringUtils.isBlank(strXml))
			return null;

		Avengers data = null;
		try {
			ClassLoader classLoader = AvengersDataUtil.class.getClassLoader();
			URL schemaUrl = classLoader.getResource("avengers.xsd");
			Schema schema = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
											.newSchema(schemaUrl);
			data = XmlUtil.convertStringToObject(Avengers.class, strXml, schema);
		} catch (SAXException e) {
			e.printStackTrace();
		}
		return data;
	}

	/**
	 * 把xml文件转化为xml字符串
	 * 
	 * @param fileName
	 * @return
	 */
	public static String getDataByXmlFileName(String fileName) {
		if (StringUtils.isBlank(fileName))
			return null;

		String data = null;
		try {
			ClassLoader classLoader = AvengersDataUtil.class.getClassLoader();
			URL schemaUrl = classLoader.getResource("avengers.xsd");
			Schema schema = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
											.newSchema(schemaUrl);
			URL xmlUrl = classLoader.getResource(fileName);
			Avengers Avengers = XmlUtil.convertXmlToObject(Avengers.class, xmlUrl, schema);
			data = XmlUtil.convertObjectToString(Avengers, schema);
		} catch (SAXException e) {
			e.printStackTrace();
		}
		return data;
	}

	/**
	 * 把Avengers对象转化为Xml字符串
	 * 
	 * @param Avengers
	 * @return
	 */
	public static String objectToXmlStr(Avengers avengers) {
		if (avengers == null)
			return null;

		String data = null;
		try {
			ClassLoader classLoader = AvengersDataUtil.class.getClassLoader();
			URL schemaUrl = classLoader.getResource("avengers.xsd");
			Schema schema = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
											.newSchema(schemaUrl);
			data = XmlUtil.convertObjectToString(avengers, schema);
		} catch (SAXException e) {
			e.printStackTrace();
		}
		return data;
	}
}

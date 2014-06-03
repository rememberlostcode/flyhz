//
//package com.flyhz.avengers.framework.util;
//
//import java.net.URL;
//import java.util.List;
//import java.util.regex.Pattern;
//
//import javax.xml.XMLConstants;
//import javax.xml.validation.Schema;
//import javax.xml.validation.SchemaFactory;
//
//import org.apache.commons.lang.StringUtils;
//import org.xml.sax.SAXException;
//
///**
// * 类说明:分类工具类
// * 
// * @author robin
// * @version 创建时间：2013-5-28 下午6:16:54
// */
//@Deprecated
//public class AvengersDataUtil {
//
//	/**
//	 * 获取分类数据
//	 * 
//	 * @return
//	 */
//	public static Domains getDataByXmlStr(String strXml) {
//		if (StringUtils.isBlank(strXml))
//			return null;
//
//		Domains data = null;
//		try {
//			ClassLoader classLoader = AvengersDataUtil.class.getClassLoader();
//			URL schemaUrl = classLoader.getResource("avengers.xsd");
//			Schema schema = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
//											.newSchema(schemaUrl);
//			data = XmlUtil.convertStringToObject(Domains.class, strXml, schema);
//		} catch (SAXException e) {
//			e.printStackTrace();
//		}
//		return data;
//	}
//
//	/**
//	 * 把xml文件转化为xml字符串
//	 * 
//	 * @param fileName
//	 * @return
//	 */
//	public static Domains getDataByXmlFileName(String fileName) {
//		if (StringUtils.isBlank(fileName))
//			return null;
//
//		Domains avengers = null;
//		try {
//			ClassLoader classLoader = AvengersDataUtil.class.getClassLoader();
//			URL schemaUrl = classLoader.getResource("avengers.xsd");
//			Schema schema = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
//											.newSchema(schemaUrl);
//			URL xmlUrl = classLoader.getResource(fileName);
//			avengers = XmlUtil.convertXmlToObject(Domains.class, xmlUrl, schema);
//		} catch (SAXException e) {
//			e.printStackTrace();
//		}
//		return avengers;
//	}
//
//	/**
//	 * 把Avengers对象转化为Xml字符串
//	 * 
//	 * @param Avengers
//	 * @return
//	 */
//	public static String objectToXmlStr(Domains avengers) {
//		if (avengers == null)
//			return null;
//
//		String data = null;
//		try {
//			ClassLoader classLoader = AvengersDataUtil.class.getClassLoader();
//			URL schemaUrl = classLoader.getResource("avengers.xsd");
//			Schema schema = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
//											.newSchema(schemaUrl);
//			data = XmlUtil.convertObjectToString(avengers, schema);
//		} catch (SAXException e) {
//			e.printStackTrace();
//		}
//		return data;
//	}
//
//	/**
//	 * 查询url对应的解析器类名
//	 * 
//	 * @param url
//	 * @param templates
//	 * @return String
//	 */
//	public static String getUrlParserName(String url, Domain domain) {
//		if (StringUtils.isNotBlank(url)) {
//			// 循环匹配节点
//			if (domain != null && domain.getTemplates() != null) {
//				List<Template> templates = domain.getTemplates().getTemplate();
//				if (templates != null && !templates.isEmpty()) {
//					for (Template template : templates) {
//						String whiteRegex = template.getUrl();
//						if (StringUtils.isNotBlank(whiteRegex)) {
//							boolean result = Pattern.compile(whiteRegex).matcher(url).find();
//							if (result) {
//								return template.getParser();
//							}
//						}
//					}
//				}
//			}
//		}
//		return null;
//	}
// }

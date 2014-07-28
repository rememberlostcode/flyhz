//
//package com.flyhz.avengers.domains.abercrombie;
//
//import java.util.Map;
//import java.util.Set;
//
//import com.flyhz.avengers.framework.Analyze;
//import com.flyhz.avengers.framework.Fetch;
//import com.flyhz.avengers.framework.config.XConfiguration;
//
///**
// * 获取AbercrombieEncode工具类
// * 
// * @author fuwb 20140619
// */
//public class AbercrombieEncodeUtil {
//	private static final String	ENCODE	= "UTF-8";
//
//	/**
//	 * 获取fetchUrl的编码方式
//	 * 
//	 * @param context
//	 * @return String
//	 */
//	@SuppressWarnings("unchecked")
//	public static String getFetchUrlEncode(Map<String, Object> context) {
//		if (context != null && !context.isEmpty()) {
//			String fetchUrl = (String) context.get(Fetch.FETCH_URL);
//			// 判断参数URL属于哪个domain
//			if (context != null && !context.isEmpty()) {
//				Set<String> domainRoots = context.keySet();
//				for (String domainRoot : domainRoots) {
//					if (fetchUrl.indexOf(domainRoot) > -1) {
//						// 获取匹配domain的fetchEvents
//						Map<String, Object> domain = (Map<String, Object>) context.get(domainRoot);
//						if (domain != null && domain.get(XConfiguration.ENCODING) != null) {
//							return (String) domain.get(XConfiguration.ENCODING);
//						}
//						break;
//					}
//				}
//			}
//		}
//		return ENCODE;
//	}
//
//	/**
//	 * 获取analyzeUrl的编码方式
//	 * 
//	 * @param context
//	 * @return String
//	 */
//	@SuppressWarnings("unchecked")
//	public static String getAnalyzeUrlEncode(Map<String, Object> context) {
//		if (context != null && !context.isEmpty()) {
//			String analyzeUrl = (String) context.get(Analyze.ANALYZE_URL);
//			// 判断参数URL属于哪个domain
//			if (context != null && !context.isEmpty()) {
//				Set<String> domainRoots = context.keySet();
//				for (String domainRoot : domainRoots) {
//					if (analyzeUrl.indexOf(domainRoot) > -1) {
//						// 获取匹配domain的fetchEvents
//						Map<String, Object> domain = (Map<String, Object>) context.get(domainRoot);
//						if (domain != null && domain.get(XConfiguration.ENCODING) != null) {
//							return (String) domain.get(XConfiguration.ENCODING);
//						}
//						break;
//					}
//				}
//			}
//		}
//		return ENCODE;
//	}
// }

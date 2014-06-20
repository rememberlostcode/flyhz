
package com.flyhz.avengers.framework.config;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flyhz.avengers.framework.Event;
import com.flyhz.avengers.framework.config.xml.XConstructor;
import com.flyhz.avengers.framework.config.xml.XDomain;
import com.flyhz.avengers.framework.config.xml.XDomains;
import com.flyhz.avengers.framework.config.xml.XEvent;
import com.flyhz.avengers.framework.config.xml.XEvents;
import com.flyhz.avengers.framework.config.xml.XFilter;
import com.flyhz.avengers.framework.config.xml.XTemplate;
import com.flyhz.avengers.framework.config.xml.XTemplates;
import com.flyhz.avengers.framework.lang.AvengersConfigurationException;
import com.flyhz.avengers.framework.util.StringUtil;
import com.flyhz.avengers.framework.util.XmlUtil;

public class XConfiguration {

	private static final Logger			LOG						= LoggerFactory.getLogger(XConfiguration.class);

	private final Map<String, Object>	avengersConfiguration	= new HashMap<String, Object>();

	/**
	 * Map<String,Map<String,Object>>,key值是avengers.xml中domain.root,value是key为
	 * CRAWL_EVENTS
	 * ,FETCH_EVENTS,TEMPLATE_EVENTS,ANALYZE_EVENTS,URLFILTER_BEFORE_CRAWL
	 * ,URLFILTER_AFTER_CRAWL
	 */
	public static final String			AVENGERS_DOMAINS		= "avengers.domains";

	/**
	 * List<Event>
	 */
	public static final String			CRAWL_EVENTS			= "crawl.events";

	/**
	 * String
	 */
	public static final String			ENCODING				= "encoding";

	/**
	 * Long
	 */
	public static final String			CRAWL_PERIOD			= "crawl.period";

	/**
	 * Long
	 */
	public static final String			CRAWL_DEPTH				= "crawl.depth";

	/**
	 * List<Event>
	 */
	public static final String			FETCH_EVENTS			= "fetch.events";

	/**
	 * List<Event>
	 */
	public static final String			TEMPLATE_EVENTS			= "template.events";

	/**
	 * List<Event>
	 */
	public static final String			ANALYZE_EVENTS			= "analyze.events";

	/**
	 * List<String>
	 */
	public static final String			URLFILTER_BEFORE_CRAWL	= "crawl.before.filter";

	/**
	 * List<String>
	 */
	public static final String			URLFILTER_AFTER_CRAWL	= "crawl.after.filter";

	private XConfiguration() {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		URL avengersXml = classLoader.getResource("avengers.xml");
		URL avengersSchema = classLoader.getResource("avengers.xsd");
		XDomains domains = XmlUtil.convertXmlToObject(XDomains.class, avengersXml, avengersSchema);
		LOG.info("domains is {}", domains);
		if (domains != null) {
			List<XDomain> listDomain = domains.getDomain();
			if (listDomain != null && !listDomain.isEmpty()) {
				Map<String, Map<String, Object>> domainsMap = new HashMap<String, Map<String, Object>>();
				avengersConfiguration.put(AVENGERS_DOMAINS, domainsMap);
				for (XDomain domain : listDomain) {
					String root = domain.getRoot();
					String encoding = domain.getEncoding();
					Long depth = domain.getDepth();
					Long period = domain.getPeriod();

					if (StringUtil.isNotBlank(root)) {
						if (domainsMap.keySet().contains(root)) {
							throw new AvengersConfigurationException("domain root" + root
									+ " duplicated");
						}

					}

					Map<String, Object> domainMap = new HashMap<String, Object>();
					domainsMap.put(root, domainMap);
					domainMap.put(ENCODING, encoding);
					domainMap.put(CRAWL_DEPTH, depth);
					domainMap.put(CRAWL_PERIOD, period);

					XFilter xBeforeCrawlFilters = domain.getUrlFilterBeforeCrawl();
					if (xBeforeCrawlFilters != null) {
						List<String> beforeCrawlFilterList = xBeforeCrawlFilters.getValue();
						if (beforeCrawlFilterList != null && !beforeCrawlFilterList.isEmpty()) {
							domainMap.put(URLFILTER_BEFORE_CRAWL, beforeCrawlFilterList);
						}
					}

					XFilter xAfterCrawlFilters = domain.getUrlFilterAfterCrawl();
					if (xAfterCrawlFilters != null) {
						List<String> afterCrawlFilterList = xAfterCrawlFilters.getValue();
						if (afterCrawlFilterList != null && !afterCrawlFilterList.isEmpty()) {
							domainMap.put(URLFILTER_AFTER_CRAWL, afterCrawlFilterList);
						}
					}

					XEvents crawlEvents = domain.getCrawlEvents();

					if (crawlEvents != null) {
						List<XEvent> listEvent = crawlEvents.getEvent();
						if (listEvent != null && !listEvent.isEmpty()) {

							List<Event> crawlEventList = new ArrayList<Event>();
							try {
								for (XEvent event : listEvent) {
									Class<?> clazz = Class.forName(event.getClazz());
									Event e = null;
									XConstructor constructor = event.getConstructor();
									if (constructor != null) {
										List<String> args = constructor.getArg();
										@SuppressWarnings("unchecked")
										Class<String>[] paramTypes = new Class[args.size()];
										for (int i = 0; i < args.size(); i++) {
											paramTypes[i] = String.class;
										}

										e = (Event) clazz.getConstructor(paramTypes).newInstance(
												args.toArray());
									} else {
										e = (Event) clazz.newInstance();
									}
									crawlEventList.add(e);
								}
							} catch (Throwable e) {
								LOG.error("root:" + root + "crawl event配置出错", e);
								throw new AvengersConfigurationException("", e);
							}
							domainMap.put(CRAWL_EVENTS, crawlEventList);

						}
					}
					XEvents fetchEvents = domain.getFetchEvents();
					if (fetchEvents != null) {
						List<XEvent> listEvent = fetchEvents.getEvent();
						if (listEvent != null && !listEvent.isEmpty()) {
							List<Event> fetchEventList = new ArrayList<Event>();
							try {
								for (XEvent event : listEvent) {
									Class<?> clazz = Class.forName(event.getClazz());
									Event e = (Event) clazz.newInstance();
									fetchEventList.add(e);
								}
							} catch (Throwable e) {
								LOG.error("root:" + root + " fetch event配置出错", e);
								throw new AvengersConfigurationException("", e);
							}
							domainMap.put(FETCH_EVENTS, fetchEventList);
						}
					}
					XTemplates templates = domain.getTemplates();
					if (templates != null) {
						List<XTemplate> templateList = templates.getTemplate();
						for (XTemplate template : templateList) {
							XEvents templateApplyEvents = template.getTemplateApplyEvents();
							if (templateApplyEvents != null) {
								List<XEvent> listEvent = templateApplyEvents.getEvent();
								if (listEvent != null && !listEvent.isEmpty()) {
									List<Event> templateEventList = new ArrayList<Event>();
									try {
										for (XEvent event : listEvent) {
											Class<?> clazz = Class.forName(event.getClazz());
											Event e = (Event) clazz.newInstance();
											templateEventList.add(e);
										}
									} catch (Throwable e) {
										LOG.error("root:" + root + "templateApply event配置出错", e);
										throw new AvengersConfigurationException("", e);
									}
									domainMap.put(TEMPLATE_EVENTS + "." + template.getUrl(),
											templateEventList);
								}
							}
						}
					}
				}
			}
		}
		if (LOG.isDebugEnabled()) {
			LOG.debug(domains.toString());
		}
	}

	private static class Singleton {
		private static final XConfiguration	singleton	= new XConfiguration();
	}

	public static Map<String, Object> getAvengersContext() {
		return Singleton.singleton.avengersConfiguration;
	}
}

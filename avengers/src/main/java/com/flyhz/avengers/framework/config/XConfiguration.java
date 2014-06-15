
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
import com.flyhz.avengers.framework.config.xml.XTemplate;
import com.flyhz.avengers.framework.config.xml.XTemplates;
import com.flyhz.avengers.framework.lang.AvengersConfigurationException;
import com.flyhz.avengers.framework.util.StringUtil;
import com.flyhz.avengers.framework.util.XmlUtil;

public class XConfiguration {

	private static final Logger			LOG						= LoggerFactory.getLogger(XConfiguration.class);

	private final Map<String, Object>	avengersConfiguration	= new HashMap<String, Object>();

	public static final String			AVENGERS_DOMAINS		= "avengers.domains";
	public static final String			CRAWL_EVENTS			= "crawl.events";
	public static final String			FETCH_EVENTS			= "fetch.events";
	public static final String			TEMPLATE_EVENTS			= "template.events";
	public static final String			ANALYZE_EVENTS			= "analyze.events";
	public static final String			URLFILTER_BEFORE_CRAWL	= "crawl.before.filter";
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
					if (StringUtil.isNotBlank(root)) {
						if (domainsMap.keySet().contains(root)) {
							throw new AvengersConfigurationException("domain root" + root
									+ " duplicated");
						}
					}
					XEvents crawlEvents = domain.getCrawlEvents();
					Map<String, Object> domainMap = new HashMap<String, Object>();
					domainsMap.put(root, domainMap);
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

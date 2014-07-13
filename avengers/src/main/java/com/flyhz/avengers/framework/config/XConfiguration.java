
package com.flyhz.avengers.framework.config;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flyhz.avengers.framework.config.xml.XConstructor;
import com.flyhz.avengers.framework.config.xml.XDomain;
import com.flyhz.avengers.framework.config.xml.XDomains;
import com.flyhz.avengers.framework.config.xml.XEvent;
import com.flyhz.avengers.framework.config.xml.XEvents;
import com.flyhz.avengers.framework.config.xml.XFilter;
import com.flyhz.avengers.framework.config.xml.XGlobalVariable;
import com.flyhz.avengers.framework.config.xml.XProxy;
import com.flyhz.avengers.framework.config.xml.XTemplate;
import com.flyhz.avengers.framework.config.xml.XTemplates;
import com.flyhz.avengers.framework.lang.AvengersConfigurationException;
import com.flyhz.avengers.framework.lang.Event;
import com.flyhz.avengers.framework.util.StringUtil;
import com.flyhz.avengers.framework.util.XmlUtil;

public class XConfiguration {

	private static final Logger			LOG						= LoggerFactory.getLogger(XConfiguration.class);

	private final Map<String, Object>	avengersConfiguration	= new HashMap<String, Object>();

	/**
	 * {@link Integer}
	 */
	public static final String			NUM_FETCH_CONTAINERS	= "number.fetch.containers";

	/**
	 * {@link Integer}
	 */
	public static final String			NUM_FETCH_THREADS		= "number.fetch.threads";

	/**
	 * Map<String,String>
	 */
	public static final String			PROXY					= "proxy";
	/**
	 * {@link String}
	 */
	public static final String			PROXY_HOST				= "proxy.host";
	/**
	 * {@link Integer}
	 */
	public static final String			PROXY_PORT				= "proxy.port";
	/**
	 * {@link String}
	 */
	public static final String			PROXY_USERNAME			= "proxy.username";
	/**
	 * {@link String}
	 */
	public static final String			PROXY_PASSWORD			= "proxy.password";

	/**
	 * {@link Set}
	 */
	public static final String			ROOTS					= "roots";

	/**
	 * List<Event>
	 */
	public static final String			CRAWL_EVENTS			= "crawl.events";

	/**
	 * String
	 */
	public static final String			ENCODING				= "encoding";

	/**
	 * Integer
	 */
	public static final String			CRAWL_PERIOD			= "crawl.period";

	/**
	 * Integer
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

	/**
	 * List<XTemplate>
	 */
	public static final String			DOMAIN_TEMPLATES		= "domain.templates";

	private XConfiguration() {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		URL avengersXml = classLoader.getResource("avengers.xml");
		URL avengersSchema = classLoader.getResource("avengers.xsd");
		XDomains domains = XmlUtil.convertXmlToObject(XDomains.class, avengersXml, avengersSchema);
		LOG.info("domains is {}", domains);
		if (domains != null) {
			XGlobalVariable globalVariable = domains.getGlobalVariable();
			if (globalVariable != null) {
				avengersConfiguration.put(NUM_FETCH_CONTAINERS,
						globalVariable.getNumFetchContainers());
				avengersConfiguration.put(NUM_FETCH_THREADS, globalVariable.getNumFetchThreads());
			}
			XProxy proxy = domains.getProxy();
			if (proxy != null) {
				Map<String, Object> proxyMap = new HashMap<String, Object>();
				proxyMap.put(PROXY_HOST, proxy.getProxyHost());
				proxyMap.put(PROXY_PORT, proxy.getProxyPort());
				proxyMap.put(PROXY_USERNAME, proxy.getUsername());
				proxyMap.put(PROXY_PASSWORD, proxy.getPassword());
				avengersConfiguration.put(PROXY, proxyMap);
			}
			List<XDomain> listDomain = domains.getDomain();
			if (listDomain != null && !listDomain.isEmpty()) {
				Set<String> roots = new HashSet<String>();
				this.avengersConfiguration.put(ROOTS, roots);
				for (XDomain domain : listDomain) {
					String root = domain.getRoot();
					String encoding = domain.getEncoding();
					Integer depth = Integer.valueOf(domain.getDepth());
					Integer period = domain.getPeriod();

					if (StringUtil.isBlank(root)) {
						continue;
					}
					if (avengersConfiguration.keySet().contains(root)) {
						throw new AvengersConfigurationException("domain root" + root
								+ " duplicated");
					}
					roots.add(root);
					Map<String, Object> domainMap = new HashMap<String, Object>();
					avengersConfiguration.put(root, domainMap);
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
						domainMap.put(DOMAIN_TEMPLATES, templateList);
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


package com.flyhz.avengers.framework;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flyhz.avengers.common.event.TemplateApplyEvent;
import com.flyhz.avengers.framework.config.XConfiguration;
import com.flyhz.avengers.framework.config.xml.XTemplate;
import com.flyhz.avengers.framework.lang.AvengersConfigurationException;
import com.flyhz.avengers.framework.util.StringUtil;

public class Analyze extends AvengersExecutor {
	private static final Logger	LOG					= LoggerFactory.getLogger(Analyze.class);
	public static final String	ANALYZE_URL			= "analyze.url";
	public static final String	ANALYZE_TEMPLATE	= "analyze.template";

	public static void main(String[] args) {
		try {
			LOG.info("analyze begin..............");
			AvengersExecutor analyze = new Analyze();
			analyze.execute(args);
			LOG.info("analyze end..............");
		} catch (Throwable th) {
			LOG.error("", th);
			System.exit(0);
		}
	}

	@Override
	Logger getLog() {
		return LOG;
	}

	@Override
	Map<String, Object> initArgs(String[] args) {
		opts.addOption("url", true, "analyze the url");
		CommandLine cliParser;
		try {
			cliParser = new GnuParser().parse(opts, args);
		} catch (ParseException e) {
			StringBuffer sb = new StringBuffer();
			for (String arg : args) {
				sb.append(arg).append(" ");
			}
			throw new RuntimeException(sb.toString(), e);
		}
		if (args.length == 0) {
			printUsage(opts);
			throw new IllegalArgumentException("No args specified for Fetch to initialize");
		}

		if (cliParser.hasOption("help")) {
			printUsage(opts);
			System.exit(0);
		}

		if (cliParser.hasOption("debug")) {
			dumpOutDebugInfo();
			System.exit(0);
		}

		String url = cliParser.getOptionValue("url");
		if (StringUtil.isBlank(url)) {
			System.exit(0);
		}

		Map<String, Object> context = new HashMap<String, Object>();
		context.put(ANALYZE_URL, url);
		return context;
	}

	@SuppressWarnings("unchecked")
	@Override
	List<Event> initAvengersEvents(Map<String, Object> context) {
		// 默认调用TemplateApplyEvent
		List<Event> events = new ArrayList<Event>();
		events.add(new TemplateApplyEvent());
		// 查询是否有自定义TemplateEvent
		String url = (String) context.get(ANALYZE_URL);
		if (StringUtils.isNotBlank(url)) {
			Map<String, Object> domains = (Map<String, Object>) context.get(XConfiguration.AVENGERS_DOMAINS);
			// 判断参数URL属于哪个domain
			if (domains != null && !domains.isEmpty()) {
				Set<String> domainRoots = domains.keySet();
				for (String domainRoot : domainRoots) {
					if (url.indexOf(domainRoot) > -1) {
						// 获取匹配domain的templateEvents
						Map<String, Object> domain = (Map<String, Object>) domains.get(domainRoot);
						// 获取匹配domain的templateList
						if (domain != null && domain.get(XConfiguration.DOMAIN_TEMPLATES) != null) {
							List<XTemplate> templates = (List<XTemplate>) domain.get(XConfiguration.DOMAIN_TEMPLATES);
							for (XTemplate xtemplate : templates) {
								String templateUrl = xtemplate.getUrl();
								String pattern = xtemplate.getPattern();
								// 模板URL不为空;模板URL匹配待分析URL
								System.out.println(Pattern.compile(templateUrl).matcher(url).find());
								if (StringUtils.isNotBlank(templateUrl)
										&& Pattern.compile(templateUrl).matcher(url).find()) {
									// context增加URL分析模板
									if (StringUtils.isNotBlank(pattern)) {
										try {
											Class<?> clazz = Class.forName(pattern);
											Template template = (Template) clazz.newInstance();
											context.put(ANALYZE_TEMPLATE, template);
										} catch (Throwable e) {
											LOG.error("clazz:" + pattern + "initAvengersEvents出错",
													e);
											throw new AvengersConfigurationException("", e);
										}
									}
									// 模板events不为空
									if (domain.get(XConfiguration.TEMPLATE_EVENTS) != null) {
										List<Event> customEvents = (List<Event>) domain.get(XConfiguration.TEMPLATE_EVENTS);
										events.addAll(customEvents);
									}
									break;
								}
							}
						}
						break;
					}
				}
			}
		}
		return events;
	}
}

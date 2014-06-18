
package com.flyhz.avengers.framework;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flyhz.avengers.common.event.TemplateApplyEvent;
import com.flyhz.avengers.framework.config.XConfiguration;

public class Analyze extends AvengersExecutor {
	private static final Logger	LOG			= LoggerFactory.getLogger(Analyze.class);
	public static final String	ANALYZE_URL	= "analyze.url";

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
		return null;
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
						// 获取匹配domain的fetchEvents
						Map<String, Object> domain = (Map<String, Object>) domains.get(domainRoot);

						domain.get(XConfiguration.TEMPLATE_EVENTS);

						if (domain != null && domain.get(XConfiguration.FETCH_EVENTS) != null) {
							List<Event> customEvents = (List<Event>) domain.get(XConfiguration.FETCH_EVENTS);
							events.addAll(customEvents);
						}
						break;
					}
				}
			}
		}
		return events;
	}
}

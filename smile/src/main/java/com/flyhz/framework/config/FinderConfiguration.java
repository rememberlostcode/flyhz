
package com.flyhz.framework.config;

import javax.annotation.Resource;

import org.springframework.beans.factory.InitializingBean;

import com.flyhz.framework.view.velocity.LostVelocityEngine;

public class FinderConfiguration implements InitializingBean {

	@Resource
	private WebConfigurer	webConfigurer;

	@Override
	public void afterPropertiesSet() throws Exception {
		this.webConfigurer.setProperties(WebConfigurer.LOST_VELOCITY, new LostVelocityEngine(
				webConfigurer));
		// this.properties.put(SPRING_RESOURCE_LOADER, new
		// DefaultResourceLoader());
	}
}

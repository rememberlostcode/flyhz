
package com.flyhz.avengers.domains.coach.template;

/**
 * 类说明：Coach表列页模板类
 * 
 * @author robin 2014-5-8上午10:15:04
 * 
 */
public class CoachUrlTemplate {
	private String	mainEls			= "div#productGrid";						// 找出整体详情父节点
	private String	linksElements	= "div.oneByOne";							// 找出产品url
	private String	imgUrlFilter	= "http://s7d2.scene7.com/is/image/Coach";	// 图片过滤

	public String getMainEls() {
		return mainEls;
	}

	public void setMainEls(String mainEls) {
		this.mainEls = mainEls;
	}

	public String getLinksElements() {
		return linksElements;
	}

	public void setLinksElements(String linksElements) {
		this.linksElements = linksElements;
	}

	public String getImgUrlFilter() {
		return imgUrlFilter;
	}

	public void setImgUrlFilter(String imgUrlFilter) {
		this.imgUrlFilter = imgUrlFilter;
	}

}

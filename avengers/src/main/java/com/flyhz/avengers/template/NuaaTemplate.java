
package com.flyhz.avengers.template;

/**
 * 
 * 类说明：nuaa解析模板类
 * 
 * @author robin 2014-5-8下午3:11:14
 * 
 */
public class NuaaTemplate {
	private String	mainEls		= "div#list-middle";			// 找出整体详情父节点
	private String	trEls		= "table > tbody > tr";		// 找出表体节点
	private String	tdEls		= "table > tbody > tr > td";	// 找出表体节点
	private String	linkEls		= "a";							// 找出所有连接节点
	private String	linkAttr	= "href";						// 找出连接节点属性值
	private Integer	trNum		= 1;							// 找出第几行里的url

	public String getMainEls() {
		return mainEls;
	}

	public void setMainEls(String mainEls) {
		this.mainEls = mainEls;
	}

	public String getTrEls() {
		return trEls;
	}

	public void setTrEls(String trEls) {
		this.trEls = trEls;
	}

	public String getTdEls() {
		return tdEls;
	}

	public void setTdEls(String tdEls) {
		this.tdEls = tdEls;
	}

	public String getLinkEls() {
		return linkEls;
	}

	public void setLinkEls(String linkEls) {
		this.linkEls = linkEls;
	}

	public String getLinkAttr() {
		return linkAttr;
	}

	public void setLinkAttr(String linkAttr) {
		this.linkAttr = linkAttr;
	}

	public Integer getTrNum() {
		return trNum;
	}

	public void setTrNum(Integer trNum) {
		this.trNum = trNum;
	}

}

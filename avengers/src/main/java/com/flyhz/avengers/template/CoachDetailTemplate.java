
package com.flyhz.avengers.template;

/**
 * 类说明：详情页模板类
 * 
 * @author robin 2014-5-8上午10:15:04
 * 
 */
public class CoachDetailTemplate {
	private String	mainEls			= "div#prod_container";					// 找出整体详情父节点
	private String	prodElements	= "div.prod_desc_container";				// 找出属性详情
	private String	name			= "h1";
	private String	style			= "div.pdTabProductStyle";
	private String	styleSeparate	= "Style No.";
	private String	price			= "span#pdTabProductPriceSpan";
	private String	description		= "div#pdpDescription";
	private String	color			= "span#selectedColorText";
	private String	imgEls			= "table[id=pdp-left]";
	private String	selectImg		= "img";
	private String	imgAttr			= "data-original";
	private String	imgUrlFilter	= "http://s7d2.scene7.com/is/image/Coach";	// 图片过滤

	public String getMainEls() {
		return mainEls;
	}

	public void setMainEls(String mainEls) {
		this.mainEls = mainEls;
	}

	public String getProdElements() {
		return prodElements;
	}

	public void setProdElements(String prodElements) {
		this.prodElements = prodElements;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public String getStyleSeparate() {
		return styleSeparate;
	}

	public void setStyleSeparate(String styleSeparate) {
		this.styleSeparate = styleSeparate;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getImgEls() {
		return imgEls;
	}

	public void setImgEls(String imgEls) {
		this.imgEls = imgEls;
	}

	public String getSelectImg() {
		return selectImg;
	}

	public void setSelectImg(String selectImg) {
		this.selectImg = selectImg;
	}

	public String getImgAttr() {
		return imgAttr;
	}

	public void setImgAttr(String imgAttr) {
		this.imgAttr = imgAttr;
	}

	public String getImgUrlFilter() {
		return imgUrlFilter;
	}

	public void setImgUrlFilter(String imgUrlFilter) {
		this.imgUrlFilter = imgUrlFilter;
	}

}

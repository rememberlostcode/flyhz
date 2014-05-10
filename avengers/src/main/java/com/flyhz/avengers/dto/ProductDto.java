
package com.flyhz.avengers.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import org.apache.commons.lang3.StringUtils;

/**
 * The persistent class for the product database table.
 * 
 */

public class ProductDto implements Serializable {
	private static final long	serialVersionUID	= 1L;
	private Integer				id;
	private String				name;
	private String				brandName;
	private String				brandstyle;
	private String				description;
	private String				imgs;
	private BigDecimal			localprice;
	private BigDecimal			purchasingprice;
	private String				style;
	private String				color;

	public ProductDto() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public String getBrandstyle() {
		return brandstyle;
	}

	public void setBrandstyle(String brandstyle) {
		this.brandstyle = brandstyle;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getImgs() {
		return imgs;
	}

	public void setImgs(String imgs) {
		this.imgs = imgs;
	}

	public BigDecimal getLocalprice() {
		return localprice;
	}

	public void setLocalprice(BigDecimal localprice) {
		this.localprice = localprice;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getPurchasingprice() {
		return purchasingprice;
	}

	public void setPurchasingprice(BigDecimal purchasingprice) {
		this.purchasingprice = purchasingprice;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("{");
		if (StringUtils.isNotBlank(this.name)) {
			sb.append("\"name\":\"");
			sb.append(this.name);
			sb.append("\"");
		}
		if (StringUtils.isNotBlank(this.brandName)) {
			if (sb.length() > 1)
				sb.append(",");
			sb.append("\"brandName\":\"");
			sb.append(this.brandName);
			sb.append("\"");
		}
		if (StringUtils.isNotBlank(this.brandstyle)) {
			if (sb.length() > 1)
				sb.append(",");
			sb.append("\"brandstyle\":\"");
			sb.append(this.brandstyle);
			sb.append("\"");
		}
		if (StringUtils.isNotBlank(this.description)) {
			if (sb.length() > 1)
				sb.append(",");
			sb.append("\"description\":\"");
			sb.append(this.description);
			sb.append("\"");
		}
		if (StringUtils.isNotBlank(this.imgs)) {
			if (sb.length() > 1)
				sb.append(",");
			sb.append("\"imgs\":\"");
			sb.append(this.imgs);
			sb.append("\"");
		}
		if (this.localprice != null) {
			if (sb.length() > 1)
				sb.append(",");
			sb.append("\"localprice\":\"");
			sb.append(this.localprice);
			sb.append("\"");
		}
		if (this.purchasingprice != null) {
			if (sb.length() > 1)
				sb.append(",");
			sb.append("\"purchasingprice\":\"");
			sb.append(this.purchasingprice);
			sb.append("\"");
		}
		if (StringUtils.isNotBlank(this.style)) {
			if (sb.length() > 1)
				sb.append(",");
			sb.append("\"style\":\"");
			sb.append(this.style);
			sb.append("\"");
		}

		if (StringUtils.isNotBlank(this.color)) {
			if (sb.length() > 1)
				sb.append(",");
			sb.append("\"color\":\"");
			sb.append(this.color);
			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

}
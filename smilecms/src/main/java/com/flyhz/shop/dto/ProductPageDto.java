
package com.flyhz.shop.dto;

/**
 * 产品分页DTO
 * 
 * @author fuwb 20140624
 */
public class ProductPageDto {
	private Integer	brandId;	// 品牌ID
	private String	brandstyle; // 产品款号
	private Integer	categoryId; // 分类ID
	private String	name;		// 产品名称
	private Integer	start;		// 分页开始位置
	private Integer	end;		// 分页结束位置
	private Integer	pagesize;	// 每页产品数量

	public Integer getBrandId() {
		return brandId;
	}

	public void setBrandId(Integer brandId) {
		this.brandId = brandId;
	}

	public String getBrandstyle() {
		return brandstyle;
	}

	public void setBrandstyle(String brandstyle) {
		this.brandstyle = brandstyle;
	}

	public Integer getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getStart() {
		return start;
	}

	public void setStart(Integer start) {
		this.start = start;
	}

	public Integer getEnd() {
		return end;
	}

	public void setEnd(Integer end) {
		this.end = end;
	}

	public Integer getPagesize() {
		return pagesize;
	}

	public void setPagesize(Integer pagesize) {
		this.pagesize = pagesize;
	}
}

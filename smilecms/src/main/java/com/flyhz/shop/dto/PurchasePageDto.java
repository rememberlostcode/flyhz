
package com.flyhz.shop.dto;

/**
 * 查询代购进图分页参数DTO
 * 
 * @author Administrator
 */
public class PurchasePageDto {
	private Integer	start;			// 分页开始位置
	private Integer	end;			// 分页结束位置
	private Integer	pagesize;		// 每页产品数量
	private Integer	brandId;		// 品牌ID
	private String	brandstyle;	// 产品款号
	private String	name;			// 产品名称
	private String	status;		// 代购状态
	private String	orderNumber;	// 海狗订单号

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}
}

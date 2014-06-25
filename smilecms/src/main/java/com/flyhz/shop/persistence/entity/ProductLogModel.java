
package com.flyhz.shop.persistence.entity;

import java.util.Date;

/**
 * 产品日志Model
 * 
 * @author fuwb 20140625
 */
public class ProductLogModel {
	private Integer	id;		// 主键ID
	private Integer	productId;	// 产品ID
	private Integer	userId;	// 操作用户ID
	private Date	gmtModify;	// 编辑时间
	private String	operate;	// 操作类型: add--增加 edit--编辑 delete--删除
	private String	beforeInfo; // 操作前产品数据
	private String	afterInfo;	// 操作后产品数据

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Date getGmtModify() {
		return gmtModify;
	}

	public void setGmtModify(Date gmtModify) {
		this.gmtModify = gmtModify;
	}

	public String getOperate() {
		return operate;
	}

	public void setOperate(String operate) {
		this.operate = operate;
	}

	public String getBeforeInfo() {
		return beforeInfo;
	}

	public void setBeforeInfo(String beforeInfo) {
		this.beforeInfo = beforeInfo;
	}

	public String getAfterInfo() {
		return afterInfo;
	}

	public void setAfterInfo(String afterInfo) {
		this.afterInfo = afterInfo;
	}
}

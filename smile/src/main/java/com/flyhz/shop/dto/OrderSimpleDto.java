
package com.flyhz.shop.dto;

import java.util.Date;

public class OrderSimpleDto {
	private Integer	id;

	private String	status;
	
	private Integer userId;
	
	private Date gmtModify;
	
	private LogisticsDto logisticsDto;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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

	public LogisticsDto getLogisticsDto() {
		return logisticsDto;
	}

	public void setLogisticsDto(LogisticsDto logisticsDto) {
		this.logisticsDto = logisticsDto;
	}

}


package com.holding.smile.dto;

import java.util.List;

/**
 * 返回值对象
 * 
 * @author robin 2013-12-28 上午11:26:19
 * 
 */
public class RtnGoodsDto {

	private Integer				code;		// 系统代码
	private List<BrandJGoods>	data;		// 返回数据
	private ValidateDto			validate;	// 校验结果

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public List<BrandJGoods> getData() {
		return data;
	}

	public void setData(List<BrandJGoods> data) {
		this.data = data;
	}

	public ValidateDto getValidate() {
		return validate;
	}

	public void setValidate(ValidateDto validate) {
		this.validate = validate;
	}

}

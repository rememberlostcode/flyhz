
package com.holding.smile.dto;

import java.util.List;

import com.holding.smile.entity.Category;
import com.holding.smile.entity.JGoods;

/**
 * 返回值对象
 * 
 * @author robin 2013-12-28 上午11:26:19
 * 
 */
public class RtnValueDto {
	private Integer				code;		// 系统代码
	private List<JGoods>		data;		// 返回数据
	private List<BrandJGoods>	brandData;	// 返回按品牌分类的数据
	private List<Category>		cateData;	// 返回分类数据
	private ValidateDto			validate;	// 校验结果
	private String				atData;

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public List<JGoods> getData() {
		return data;
	}

	public void setData(List<JGoods> data) {
		this.data = data;
	}

	public List<BrandJGoods> getBrandData() {
		return brandData;
	}

	public void setBrandData(List<BrandJGoods> brandData) {
		this.brandData = brandData;
	}

	public List<Category> getCateData() {
		return cateData;
	}

	public void setCateData(List<Category> cateData) {
		this.cateData = cateData;
	}

	public ValidateDto getValidate() {
		return validate;
	}

	public void setValidate(ValidateDto validate) {
		this.validate = validate;
	}

	public String getAtData() {
		return atData;
	}

	public void setAtData(String atData) {
		this.atData = atData;
	}
}

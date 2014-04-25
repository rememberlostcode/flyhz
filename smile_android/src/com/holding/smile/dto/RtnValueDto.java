
package com.holding.smile.dto;

import java.util.List;

import com.holding.smile.entity.CartItem;
import com.holding.smile.entity.Category;
import com.holding.smile.entity.Consignee;
import com.holding.smile.entity.Idcard;
import com.holding.smile.entity.JGoods;
import com.holding.smile.entity.JIndexJGoods;
import com.holding.smile.entity.JSort;
import com.holding.smile.entity.SUser;

/**
 * 返回值对象
 * 
 * @author robin 2013-12-28 上午11:26:19
 * 
 */
public class RtnValueDto {
	private Integer				code;			// 系统代码
	private JGoods				goodDetail;	// 返回商品详情数据
	private List<JGoods>		data;			// 返回数据
	private List<BrandJGoods>	brandData;		// 返回按品牌分类的数据
	private List<Category>		cateData;		// 返回分类数据
	private JIndexJGoods		indexData;		// 返回首页数据
	private List<JSort>			sortData;		// 返回排行数据
	private ValidateDto			validate;		// 校验结果
	private String				atData;
	private List<Consignee>		consigneeData;	// 返回收货人地址集合
	private Consignee			consignee;		// 返回收货人地址
	private SUser				userData;		// 返回用户数据
	private List<Idcard>		idcardsData;	// 身份证集合
	private OrderDto			orderData;		// 订单数据
	private ProductDto			productData;	// 更改商品时用
	private List<OrderDto>		orderListData;	// 订单集合
	private List<CartItem>		cartData;		// 购物车数据

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public JGoods getGoodDetail() {
		return goodDetail;
	}

	public void setGoodDetail(JGoods goodDetail) {
		this.goodDetail = goodDetail;
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

	public JIndexJGoods getIndexData() {
		return indexData;
	}

	public void setIndexData(JIndexJGoods indexData) {
		this.indexData = indexData;
	}

	public List<JSort> getSortData() {
		return sortData;
	}

	public void setSortData(List<JSort> sortData) {
		this.sortData = sortData;
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

	public List<Consignee> getConsigneeData() {
		return consigneeData;
	}

	public void setConsigneeData(List<Consignee> consigneeData) {
		this.consigneeData = consigneeData;
	}

	public SUser getUserData() {
		return userData;
	}

	public void setUserData(SUser userData) {
		this.userData = userData;
	}

	public Consignee getConsignee() {
		return consignee;
	}

	public void setConsignee(Consignee consignee) {
		this.consignee = consignee;
	}

	public List<Idcard> getIdcardsData() {
		return idcardsData;
	}

	public void setIdcardsData(List<Idcard> idcardsData) {
		this.idcardsData = idcardsData;
	}

	public OrderDto getOrderData() {
		return orderData;
	}

	public void setOrderData(OrderDto orderData) {
		this.orderData = orderData;
	}

	public ProductDto getProductData() {
		return productData;
	}

	public void setProductData(ProductDto productData) {
		this.productData = productData;
	}

	public List<OrderDto> getOrderListData() {
		return orderListData;
	}

	public void setOrderListData(List<OrderDto> orderListData) {
		this.orderListData = orderListData;
	}

	public List<CartItem> getCartData() {
		return cartData;
	}

	public void setCartData(List<CartItem> cartData) {
		this.cartData = cartData;
	}

}

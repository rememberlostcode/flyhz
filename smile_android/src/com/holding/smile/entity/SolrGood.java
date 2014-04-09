
package com.holding.smile.entity;

public class SolrGood {
	/**
	 * 省钱排序号
	 */
	private Integer	seqorder;
	/**
	 * 时间排序号
	 */
	private Integer	seqorder_time;
	/**
	 * 物品ID
	 */
	private Integer	id;
	/**
	 * 名称
	 */
	private String	name;
	/**
	 * 物品类型ID
	 */
	private Integer	type;
	/**
	 * 物品类型英文名
	 */
	private String	type_en;
	/**
	 * 物品类型中文名
	 */
	private String	type_cn;
	/**
	 * 购进价格
	 */
	private Double	purchasing_price;
	/**
	 * 当地价格
	 */
	private Double	local_price;
	/**
	 * 品牌ID
	 */
	private Integer	brand;
	/**
	 * 品牌英文名
	 */
	private String	brand_en;
	/**
	 * 品牌中文名
	 */
	private String	brand_cn;
	/**
	 * 描述
	 */
	private String	description;
	/**
	 * 发布时间
	 */
	private String	post_time;
	/**
	 * 图片URL
	 */
	private String	img_urls;
	/**
	 * 图片URL
	 */
	private String	img_urls_s;
	/**
	 * 图片URL
	 */
	private String	img_urls_m;
	/**
	 * 序列号/型号
	 */
	private String	sn;
	/**
	 * 省了多少钱
	 */
	private Double	money;

	private Float	_version_;

	public Integer getSeqorder() {
		return seqorder;
	}

	public void setSeqorder(Integer seqorder) {
		this.seqorder = seqorder;
	}

	public Integer getSeqorder_time() {
		return seqorder_time;
	}

	public void setSeqorder_time(Integer seqorder_time) {
		this.seqorder_time = seqorder_time;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getType_en() {
		return type_en;
	}

	public void setType_en(String type_en) {
		this.type_en = type_en;
	}

	public String getType_cn() {
		return type_cn;
	}

	public void setType_cn(String type_cn) {
		this.type_cn = type_cn;
	}

	public Double getPurchasing_price() {
		return purchasing_price;
	}

	public void setPurchasing_price(Double purchasing_price) {
		this.purchasing_price = purchasing_price;
	}

	public Double getLocal_price() {
		return local_price;
	}

	public void setLocal_price(Double local_price) {
		this.local_price = local_price;
	}

	public Integer getBrand() {
		return brand;
	}

	public void setBrand(Integer brand) {
		this.brand = brand;
	}

	public String getBrand_en() {
		return brand_en;
	}

	public void setBrand_en(String brand_en) {
		this.brand_en = brand_en;
	}

	public String getBrand_cn() {
		return brand_cn;
	}

	public void setBrand_cn(String brand_cn) {
		this.brand_cn = brand_cn;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPost_time() {
		return post_time;
	}

	public void setPost_time(String post_time) {
		this.post_time = post_time;
	}

	public String getImg_urls() {
		return img_urls;
	}

	public void setImg_urls(String img_urls) {
		this.img_urls = img_urls;
	}

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	public Float get_version_() {
		return _version_;
	}

	public void set_version_(Float _version_) {
		this._version_ = _version_;
	}

	public Double getMoney() {
		return money;
	}

	public void setMoney(Double money) {
		this.money = money;
	}

	public String getImg_urls_s() {
		return img_urls_s;
	}

	public void setImg_urls_s(String img_urls_s) {
		this.img_urls_s = img_urls_s;
	}

	public String getImg_urls_m() {
		return img_urls_m;
	}

	public void setImg_urls_m(String img_urls_m) {
		this.img_urls_m = img_urls_m;
	}

}

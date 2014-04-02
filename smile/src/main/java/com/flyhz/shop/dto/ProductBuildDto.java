
package com.flyhz.shop.dto;

import java.math.BigDecimal;
import java.util.Date;

public class ProductBuildDto {

	private Integer		id;
	/**
	 * 商品名称
	 */
	private String		n;
	/**
	 * 商品描述
	 */
	private String		d;
	/**
	 * 商品封面
	 */
	private String		p;
	/**
	 * 商品最后更新时间
	 */
	private Date		t;
	/**
	 * 本地价格
	 */
	private BigDecimal	lp;
	/**
	 * 代购价格
	 */
	private BigDecimal	pp;
	/**
	 * 差价
	 */
	private BigDecimal	sp;
	/**
	 * 分数排序
	 */
	private Integer		sf;
	/**
	 * 时间排序
	 */
	private Integer		st;

	private Integer		bid;
	private String		be;
	private String		bc;

	private Integer		cid;
	private String		ce;
	private String		cc;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getN() {
		return n;
	}

	public void setN(String n) {
		this.n = n;
	}

	public String getP() {
		return p;
	}

	public void setP(String p) {
		this.p = p;
	}

	public Date getT() {
		return t;
	}

	public void setT(Date t) {
		this.t = t;
	}

	public BigDecimal getLp() {
		return lp;
	}

	public void setLp(BigDecimal lp) {
		this.lp = lp;
	}

	public BigDecimal getPp() {
		return pp;
	}

	public void setPp(BigDecimal pp) {
		this.pp = pp;
	}

	public BigDecimal getSp() {
		return sp;
	}

	public void setSp(BigDecimal sp) {
		this.sp = sp;
	}

	public Integer getSf() {
		return sf;
	}

	public void setSf(Integer sf) {
		this.sf = sf;
	}

	public Integer getSt() {
		return st;
	}

	public void setSt(Integer st) {
		this.st = st;
	}

	public String getD() {
		return d;
	}

	public void setD(String d) {
		this.d = d;
	}

	public Integer getBid() {
		return bid;
	}

	public void setBid(Integer bid) {
		this.bid = bid;
	}

	public String getBe() {
		return be;
	}

	public void setBe(String be) {
		this.be = be;
	}

	public String getBc() {
		return bc;
	}

	public void setBc(String bc) {
		this.bc = bc;
	}

	public Integer getCid() {
		return cid;
	}

	public void setCid(Integer cid) {
		this.cid = cid;
	}

	public String getCe() {
		return ce;
	}

	public void setCe(String ce) {
		this.ce = ce;
	}

	public String getCc() {
		return cc;
	}

	public void setCc(String cc) {
		this.cc = cc;
	}

}
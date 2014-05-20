
package com.holding.smile.entity;

import java.io.Serializable;
import java.math.BigDecimal;

public class JGoods implements Serializable {
	private static final long	serialVersionUID	= -1956085742090097519L;

	private Integer				id;
	/**
	 * 商品名称
	 */
	private String				n;
	/**
	 * 商品描述
	 */
	private String				d;
	/**
	 * 商品款号
	 */
	private String				bs;
	/**
	 * 商品封面
	 */
	private String[]			p;
	/**
	 * 商品最后更新时间
	 */
	private String				t;
	/**
	 * 本地价格
	 */
	private BigDecimal			lp;
	/**
	 * 代购价格
	 */
	private BigDecimal			pp;
	/**
	 * 差价
	 */
	private BigDecimal			sp;
	/**
	 * 分数，用于默认排序
	 */
	private Integer				sf;
	/**
	 * 时间排序值
	 */
	private Integer				st;
	/**
	 * 折扣排序值
	 */
	private Integer				sd;
	/**
	 * 销售量排序值
	 */
	private Integer				ss;
	/**
	 * 品牌ID
	 */
	private Integer				bid;
	/**
	 * 品牌名称
	 */
	private String				be;

	/**
	 * 分类ID
	 */
	private Integer				cid;
	/**
	 * 分类名称
	 */
	private String				ce;
	/**
	 * 颜色
	 */
	private String				c;
	/**
	 * 颜色图片
	 */
	private String				ci;
	/**
	 * 已销售数量
	 */
	private Integer				sn;

	/**
	 * 周销售数量
	 */
	private Integer				zsn;

	private Integer				seq;

	private String				_version_;

	/**
	 * 原图数组
	 */
	private String[]			bp;

	public String[] getBp() {
		return bp;
	}

	public void setBp(String[] bp) {
		this.bp = bp;
	}

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

	public String getBs() {
		return bs;
	}

	public void setBs(String bs) {
		this.bs = bs;
	}

	public String[] getP() {
		return p;
	}

	public void setP(String[] p) {
		this.p = p;
	}

	public String getT() {
		return t;
	}

	public void setT(String t) {
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

	public String getC() {
		return c;
	}

	public void setC(String c) {
		this.c = c;
	}

	public String getCi() {
		return ci;
	}

	public void setCi(String ci) {
		this.ci = ci;
	}

	public Integer getSd() {
		return sd;
	}

	public void setSd(Integer sd) {
		this.sd = sd;
	}

	public Integer getSs() {
		return ss;
	}

	public void setSs(Integer ss) {
		this.ss = ss;
	}

	public Integer getSn() {
		return sn;
	}

	public void setSn(Integer sn) {
		this.sn = sn;
	}

	public Integer getZsn() {
		return zsn;
	}

	public void setZsn(Integer zsn) {
		this.zsn = zsn;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public String get_version_() {
		return _version_;
	}

	public void set_version_(String _version_) {
		this._version_ = _version_;
	}

}

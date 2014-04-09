
package com.holding.smile.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import com.holding.smile.tools.Constants;
import com.holding.smile.tools.JSONUtil;

public class JGoods implements Serializable {
	private static final long	serialVersionUID	= -1956085742090097519L;

	/**
	 * 省钱排序号
	 */
	private Integer				seqorder;
	/**
	 * 时间排序号
	 */
	private Integer				seqorder_time;
	/**
	 * 物品ID
	 */
	private Integer				i;

	/**
	 * 品牌
	 */
	private String				b;

	/**
	 * 名称
	 */
	private String				n;

	/**
	 * 当前价格
	 */
	private String				pn;

	/**
	 * 原价
	 */
	private String				po;

	/**
	 * 省
	 */
	private String				s;

	/**
	 * 发布日期
	 */
	private String				d;

	/**
	 * 图片
	 */
	private String[]			p;

	/**
	 * 描述
	 */
	private String				desc;

	public JGoods() {
	}

	public JGoods(SolrGood solr) {
		i = solr.getId();
		n = solr.getName();
		if (solr.getBrand_cn() == null) {
			b = solr.getBrand_en();
		} else {
			b = solr.getBrand_cn();
		}
		if (solr.getPurchasing_price() != null) {
			pn = String.valueOf(new BigDecimal(solr.getPurchasing_price()).setScale(0,
					BigDecimal.ROUND_HALF_UP));
		}
		if (solr.getLocal_price() != null) {
			po = String.valueOf(new BigDecimal(solr.getLocal_price()).setScale(0,
					BigDecimal.ROUND_HALF_UP));
		}
		if (solr.getLocal_price() != null && solr.getPurchasing_price() != null) {
			s = String.valueOf(Integer.parseInt(po) - Integer.parseInt(pn));
		}
		if (solr.getPost_time() != null) {
			d = solr.getPost_time().replace("T", " ").replace("Z", "");
		}
		seqorder = solr.getSeqorder();
		seqorder_time = solr.getSeqorder_time();
		desc = solr.getDescription();

		String[] olds = JSONUtil.getJson2Entity(solr.getImg_urls_m(), String[].class);
		if (olds != null && olds.length > 0) {
			p = new String[olds.length];
			String preUrl = Constants.getImageUrlPre();
			for (int i = 0; i < olds.length; i++) {
				p[i] = preUrl + olds[i];
			}
		}

	}

	/**
	 * 提供精确的减法运算。
	 * 
	 * @param v1
	 *            被减数
	 * @param v2
	 *            减数
	 * @return 两个参数的差
	 */
	private double sub(double v1, double v2) {
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.subtract(b2).doubleValue();
	}

	public Integer getSeqorder() {
		return seqorder;
	}

	public void setSeqorder(Integer seqorder) {
		this.seqorder = seqorder;
	}

	public Integer getI() {
		return i;
	}

	public void setI(Integer i) {
		this.i = i;
	}

	public String getB() {
		return b;
	}

	public void setB(String b) {
		this.b = b;
	}

	public String getN() {
		return n;
	}

	public void setN(String n) {
		this.n = n;
	}

	public String getPn() {
		return pn;
	}

	public void setPn(String pn) {
		this.pn = pn;
	}

	public String getPo() {
		return po;
	}

	public void setPo(String po) {
		this.po = po;
	}

	public String getS() {
		return s;
	}

	public void setS(String s) {
		this.s = s;
	}

	public String getD() {
		return d;
	}

	public void setD(String d) {
		this.d = d;
	}

	public String[] getP() {
		return p;
	}

	public void setP(String[] p) {
		this.p = p;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public Integer getSeqorder_time() {
		return seqorder_time;
	}

	public void setSeqorder_time(Integer seqorder_time) {
		this.seqorder_time = seqorder_time;
	}

}

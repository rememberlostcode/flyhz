
package com.holding.smile.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.holding.smile.R;
import com.holding.smile.dto.RtnValueDto;
import com.holding.smile.dto.ValidateDto;
import com.holding.smile.entity.SortType;
import com.holding.smile.protocol.PBrandJGoods;
import com.holding.smile.protocol.PCartItem;
import com.holding.smile.protocol.PCategory;
import com.holding.smile.protocol.PFindPwd;
import com.holding.smile.protocol.PGoods;
import com.holding.smile.protocol.PIdcards;
import com.holding.smile.protocol.PIndexBrands;
import com.holding.smile.protocol.PIndexJGoods;
import com.holding.smile.protocol.POrder;
import com.holding.smile.protocol.POrderList;
import com.holding.smile.protocol.PSort;
import com.holding.smile.protocol.PSortTypes;
import com.holding.smile.protocol.PUser;
import com.holding.smile.protocol.PVersion;
import com.holding.smile.tools.CodeValidator;
import com.holding.smile.tools.Constants;
import com.holding.smile.tools.JSONUtil;
import com.holding.smile.tools.StrUtils;
import com.holding.smile.tools.URLUtil;

public class DataService {

	/**
	 * 是否从网络获取数据
	 */
	public static boolean	getDataFromNet	= true;

	private String			prefix_url;
	private String			jGoods_index_url;
	private String			jGoods_version_url;
	private String			jGoods_index_single_url;
	private String			jGoods_recommend_url;
	private String			jGoods_cate_url;
	private String			jGoods_brand_url;
	private String			jGoods_brand_more_url;
	private String			jGoods_sorttype_url;
	private String			jGoods_detail_url;
	private String			jGoods_sort_url;
	private String			jGoods_search_url;
	private String			jGoods_search_more_url;
	private String			user_info;
	private String			idcard_list;
	private String			order_list_url;
	private String			cart_list_url;
	private String			order_status_url;
	private String			user_find_backpwd_url;
	private String			pay_status_url;

	public DataService(Context context) {
		prefix_url = context.getString(R.string.prefix_url);
		jGoods_index_url = context.getString(R.string.jGoods_index_url);
		jGoods_version_url = context.getString(R.string.jGoods_version_url);
		jGoods_index_single_url = context.getString(R.string.jGoods_index_single_url);
		jGoods_recommend_url = context.getString(R.string.jGoods_recommend_url);
		jGoods_cate_url = context.getString(R.string.jGoods_cate_url);
		jGoods_brand_url = context.getString(R.string.jGoods_brand_url);
		jGoods_brand_more_url = context.getString(R.string.jGoods_brand_more_url);
		jGoods_sorttype_url = context.getString(R.string.jGoods_sorttype_url);
		jGoods_detail_url = context.getString(R.string.jGoods_detail_url);
		jGoods_sort_url = context.getString(R.string.jGoods_sort_url);
		jGoods_search_url = context.getString(R.string.jGoods_search_refresh_url);
		jGoods_search_more_url = context.getString(R.string.jGoods_search_more_url);
		user_info = context.getString(R.string.user_info);
		idcard_list = context.getString(R.string.idcard_list);
		order_list_url = context.getString(R.string.order_list_url);
		cart_list_url = context.getString(R.string.cart_list_url);
		order_status_url = context.getString(R.string.order_status_url);
		user_find_backpwd_url = context.getString(R.string.user_find_backpwd_url);
		pay_status_url = context.getString(R.string.pay_status_url);
	}

	/**
	 * 首页初始化
	 * 
	 * @return
	 */
	public RtnValueDto getIndexListInit(Integer cid) {
		if (CodeValidator.isNetworkError()) {
			return CodeValidator.getNetworkErrorRtnValueDto();
		}
		RtnValueDto obj = getIndexJGoods(cid);
		return obj;
	}

	/**
	 * 首页推荐品牌商品
	 * 
	 * @return
	 */
	public RtnValueDto getRecommendBrandsListInit(Integer cid) {
		if (CodeValidator.isNetworkError()) {
			return CodeValidator.getNetworkErrorRtnValueDto();
		}
		RtnValueDto obj = getRecommendBrands(cid);
		return obj;
	}

	/**
	 * 单一品牌查看全部
	 * 
	 * @return
	 */
	public RtnValueDto getBrandJGoodsListInit(Integer bid, Integer cid, String seqorderType) {
		if (CodeValidator.isNetworkError()) {
			return CodeValidator.getNetworkErrorRtnValueDto();
		}
		RtnValueDto obj = getBrandGoods(bid, cid, seqorderType, null);
		return obj;
	}

	/**
	 * 获取排序类型列表
	 * 
	 * @param bid品牌ID
	 * @param cid分类ID
	 * @param seqorderType排序类型
	 * @param seqorderValue序号
	 * @return
	 */
	public List<SortType> getSortTypeList() {
		List<SortType> results = new ArrayList<SortType>();
		if (CodeValidator.isNetworkError()) {
			return results;
		}
		String rStr = URLUtil.getStringByGet(this.prefix_url + this.jGoods_sorttype_url, null);
		if (StrUtils.isNotEmpty(rStr)) {
			PSortTypes pst = JSONUtil.getJson2Entity(rStr, PSortTypes.class);
			if (pst != null)
				results = pst.getData();
		}
		return results;
	}

	/**
	 * 单一品牌查看更多
	 * 
	 * @param bid品牌ID
	 * @param cid分类ID
	 * @param seqorderType排序类型
	 * @param start序号
	 *            （即当前页面总记录数）
	 * @return
	 */
	public RtnValueDto getBrandJGoodsListMore(Integer bid, Integer cid, String seqorderType,
			Integer start) {
		if (CodeValidator.isNetworkError()) {
			return CodeValidator.getNetworkErrorRtnValueDto();
		}
		RtnValueDto obj = getBrandGoods(bid, cid, seqorderType, start);
		return obj;
	}

	/**
	 * 搜索物品初始化
	 * 
	 * @param keywords
	 *            关键词
	 * @return
	 */
	public RtnValueDto getJGoodsSearchListInit(String keywords) {
		if (CodeValidator.isNetworkError()) {
			return CodeValidator.getNetworkErrorRtnValueDto();
		}
		RtnValueDto obj = null;
		if (keywords != null && !"".equals(keywords.trim())) {
			obj = searchGoods(keywords, null, null);
		}
		return obj;
	}

	/**
	 * 搜索物品刷新
	 * 
	 * @param keywords
	 *            关键词
	 * @param seqorderType
	 *            排序方式
	 * @return
	 */
	public RtnValueDto getJGoodsSearchListRefresh(String keywords, String seqorderType) {
		if (CodeValidator.isNetworkError()) {
			return CodeValidator.getNetworkErrorRtnValueDto();
		}
		RtnValueDto obj = null;
		if (keywords != null && !"".equals(keywords.trim())) {
			obj = searchGoods(keywords, seqorderType, null);
		}
		return obj;
	}

	/**
	 * 搜索物品更多
	 * 
	 * @param keywords关键词
	 * @param seqorderType排序方式
	 * @param start序号
	 *            （即当前页面总记录数）
	 * @return
	 */
	public RtnValueDto getJGoodsSearchListMore(String keywords, String seqorderType, Integer start) {
		if (CodeValidator.isNetworkError()) {
			return CodeValidator.getNetworkErrorRtnValueDto();
		}
		RtnValueDto obj = null;
		if (keywords != null && !"".equals(keywords.trim())) {
			obj = searchGoods(keywords, seqorderType, start);
		}
		return obj;
	}

	/**
	 * 首页商品
	 * 
	 * @return
	 */
	public RtnValueDto getIndexJGoods(Integer cid) {
		if (CodeValidator.isNetworkError()) {
			return CodeValidator.getNetworkErrorRtnValueDto();
		}
		RtnValueDto rvd = new RtnValueDto();
		HashMap<String, String> param = new HashMap<String, String>();
		if (cid != null)
			param.put("cid", String.valueOf(cid));

		String rStr = URLUtil.getStringByGet(this.prefix_url + this.jGoods_index_url, param);
		if (rStr != null && !"".equals(rStr)) {
			try {
				PIndexJGoods indexJGoods = JSONUtil.getJson2Entity(rStr, PIndexJGoods.class);
				if (indexJGoods != null) {
					rvd.setIndexData(indexJGoods.getData());
					rvd.setCode(indexJGoods.getCode());
				}
			} catch (Exception e) {
				e.printStackTrace();
				ValidateDto vd = new ValidateDto();
				vd.setMessage(Constants.MESSAGE_EXCEPTION);
				rvd.setValidate(vd);
			}
		} else {
			ValidateDto vd = new ValidateDto();
			vd.setMessage(Constants.MESSAGE_NET);
			rvd.setValidate(vd);
		}
		return rvd;
	}

	/**
	 * 首页商品品牌
	 * 
	 * @return
	 */
	public RtnValueDto getIndexBrands(Integer cid) {
		if (CodeValidator.isNetworkError()) {
			return CodeValidator.getNetworkErrorRtnValueDto();
		}
		RtnValueDto rvd = null;
		HashMap<String, String> param = new HashMap<String, String>();
		if (cid != null)
			param.put("cid", String.valueOf(cid));

		String rStr = URLUtil.getStringByGet(this.prefix_url + this.jGoods_index_single_url, param);
		if (rStr != null && !"".equals(rStr)) {
			try {
				PIndexBrands indexBrands = JSONUtil.getJson2Entity(rStr, PIndexBrands.class);
				if (indexBrands != null) {
					rvd = new RtnValueDto();
					rvd.setIndexBrandsData(indexBrands.getData());
					rvd.setCode(indexBrands.getCode());
				}
			} catch (Exception e) {
				e.printStackTrace();
				Log.e("dataService", e.getMessage());
			}
		}
		return rvd;
	}

	/**
	 * 首页推荐品牌商品
	 * 
	 * @return
	 */
	public RtnValueDto getRecommendBrands(Integer cid) {
		if (CodeValidator.isNetworkError()) {
			return CodeValidator.getNetworkErrorRtnValueDto();
		}
		RtnValueDto rvd = null;
		HashMap<String, String> param = new HashMap<String, String>();
		if (cid != null)
			param.put("cid", String.valueOf(cid));
		String rStr = URLUtil.getStringByGet(this.prefix_url + this.jGoods_recommend_url, param);

		if (rStr != null && !"".equals(rStr)) {
			try {
				PBrandJGoods brandJGoods = JSONUtil.getJson2Entity(rStr, PBrandJGoods.class);
				if (brandJGoods != null) {
					rvd = new RtnValueDto();
					rvd.setBrandData(brandJGoods.getData());
					rvd.setCode(brandJGoods.getCode());
				}
			} catch (Exception e) {
				e.printStackTrace();
				Log.e("dataService", e.getMessage());
			}
		}
		return rvd;
	}

	/**
	 * 单一品牌查询
	 * 
	 * @param bid
	 * @param cid
	 * @param seqorderType
	 * @param start序号
	 *            （即当前页面总记录数）
	 * @return
	 */

	public RtnValueDto getBrandGoods(Integer bid, Integer cid, String seqorderType, Integer start) {
		if (CodeValidator.isNetworkError()) {
			return CodeValidator.getNetworkErrorRtnValueDto();
		}
		RtnValueDto rvd = null;
		String url = this.prefix_url;
		HashMap<String, String> param = new HashMap<String, String>();
		if (bid != null)
			param.put("bid", String.valueOf(bid));
		if (cid != null)
			param.put("cid", String.valueOf(cid));
		if (StrUtils.isNotEmpty(seqorderType))
			param.put("seqorderType", seqorderType.trim());
		if (start != null) {
			param.put("start", String.valueOf(start));
			url += this.jGoods_brand_more_url;
		} else {
			url += this.jGoods_brand_url;
		}
		String rStr = URLUtil.getStringByGet(url, param);

		if (rStr != null && !"".equals(rStr)) {
			try {
				PGoods pst = JSONUtil.getJson2Entity(rStr, PGoods.class);
				if (pst != null) {
					rvd = new RtnValueDto();
					rvd.setData(pst.getData());
					rvd.setCode(pst.getCode());
				}
			} catch (Exception e) {
				e.printStackTrace();
				Log.e("DataService", e.getMessage());
			}
		}
		return rvd;
	}

	/**
	 * 搜索物品
	 * 
	 * @param keywords
	 *            关键词
	 * @param seqorderType
	 * @param start序号
	 *            （即当前页面总记录数）
	 * @return
	 */
	public RtnValueDto searchGoods(String keywords, String seqorderType, Integer start) {
		if (CodeValidator.isNetworkError()) {
			return CodeValidator.getNetworkErrorRtnValueDto();
		}
		RtnValueDto rvd = null;
		String url = prefix_url;
		HashMap<String, String> param = new HashMap<String, String>();
		if (StrUtils.isNotEmpty(keywords)) {
			param.put("keywords", keywords.trim());
		}
		if (StrUtils.isNotEmpty(seqorderType)) {
			param.put("seqorderType", seqorderType.trim());
		}
		if (start != null) {
			param.put("start", String.valueOf(start));
			url += this.jGoods_search_more_url;
		} else {
			url += this.jGoods_search_url;
		}
		String rStr = URLUtil.getStringByGet(url, param);

		if (rStr != null && !"".equals(rStr)) {
			try {
				PGoods pst = JSONUtil.getJson2Entity(rStr, PGoods.class);
				if (pst != null) {
					rvd = new RtnValueDto();
					rvd.setData(pst.getData());
					rvd.setCode(pst.getCode());
				}
			} catch (Exception e) {
				e.printStackTrace();
				Log.e("DataService", e.getMessage());
			}
		}
		return rvd;
	}

	/**
	 * 商品分类
	 * 
	 * @return
	 */
	public RtnValueDto getCategorys() {
		if (CodeValidator.isNetworkError()) {
			return CodeValidator.getNetworkErrorRtnValueDto();
		}
		RtnValueDto rvd = new RtnValueDto();
		String rStr = URLUtil.getStringByGet(this.prefix_url + this.jGoods_cate_url, null);

		PCategory pc = null;
		if (rStr != null && !"".equals(rStr)) {
			try {
				pc = JSONUtil.getJson2Entity(rStr, PCategory.class);
				if (pc != null) {
					rvd.setCode(pc.getCode());
					rvd.setCateData(pc.getData());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return rvd;
	}

	/**
	 * 商品排行列表
	 * 
	 * @return
	 */
	public RtnValueDto getSortList() {
		if (CodeValidator.isNetworkError()) {
			return CodeValidator.getNetworkErrorRtnValueDto();
		}
		RtnValueDto rvd = new RtnValueDto();
		String rStr = URLUtil.getStringByGet(this.prefix_url + this.jGoods_sort_url, null);

		if (rStr != null && !"".equals(rStr)) {
			try {
				PSort pc = JSONUtil.getJson2Entity(rStr, PSort.class);
				if (pc != null) {
					rvd.setSortData(pc.getData());
					rvd.setCode(pc.getCode());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return rvd;
	}

	/**
	 * 商品排行榜列表
	 * 
	 * @return
	 */
	public RtnValueDto getJGoodsSortList(String sortUrl) {
		if (CodeValidator.isNetworkError()) {
			return CodeValidator.getNetworkErrorRtnValueDto();
		}
		RtnValueDto rvd = new RtnValueDto();
		String rStr = "";
		if (StrUtils.isNotEmpty(sortUrl)) {
			rStr = URLUtil.getStringByGet(prefix_url + sortUrl, null);
		}

		if (rStr != null && !"".equals(rStr)) {
			try {
				PGoods pst = JSONUtil.getJson2Entity(rStr, PGoods.class);
				if (pst != null) {
					rvd.setData(pst.getData());
					rvd.setCode(pst.getCode());
				}
			} catch (Exception e) {
				e.printStackTrace();
				ValidateDto vd = new ValidateDto();
				vd.setMessage(Constants.MESSAGE_EXCEPTION);
			}
		} else {
			ValidateDto vd = new ValidateDto();
			vd.setMessage(Constants.MESSAGE_NET);
			rvd.setValidate(vd);
		}
		return rvd;
	}

	/**
	 * 商品详情
	 * 
	 * @param bs
	 * @return
	 */
	public RtnValueDto getGoodsDetail(String bs) {
		if (CodeValidator.isNetworkError()) {
			return CodeValidator.getNetworkErrorRtnValueDto();
		}
		RtnValueDto rvd = new RtnValueDto();
		String url = this.prefix_url + this.jGoods_detail_url;
		HashMap<String, String> param = new HashMap<String, String>();
		if (bs != null)
			param.put("bs", String.valueOf(bs));

		String rStr = URLUtil.getStringByGet(url, param);
		if (rStr != null && !"".equals(rStr)) {
			try {
				PGoods details = JSONUtil.getJson2Entity(rStr, PGoods.class);
				if (details != null) {
					rvd.setData(details.getData());
					rvd.setCode(details.getCode());
				}
			} catch (Exception e) {
				e.printStackTrace();
				ValidateDto vd = new ValidateDto();
				vd.setMessage(Constants.MESSAGE_EXCEPTION);
				rvd.setValidate(vd);
			}
		} else {
			ValidateDto vd = new ValidateDto();
			vd.setMessage(Constants.MESSAGE_NET);
			rvd.setValidate(vd);
		}
		return rvd;
	}

	public RtnValueDto getUserInfo() {
		if (CodeValidator.isNetworkError()) {
			return CodeValidator.getNetworkErrorRtnValueDto();
		}
		RtnValueDto rvd = new RtnValueDto();
		String url = prefix_url + user_info;

		String rStr = URLUtil.getStringByGet(url, null);
		if (rStr != null && !"".equals(rStr)) {
			try {
				PUser user = JSONUtil.getJson2Entity(rStr, PUser.class);
				if (user != null) {
					rvd.setUserData(user.getData());
					rvd.setCode(user.getCode());
				}
			} catch (Exception e) {
				e.printStackTrace();
				ValidateDto vd = new ValidateDto();
				vd.setMessage(Constants.MESSAGE_EXCEPTION);
				rvd.setValidate(vd);
			}
		} else {
			ValidateDto vd = new ValidateDto();
			vd.setMessage(Constants.MESSAGE_NET);
			rvd.setValidate(vd);
		}
		return rvd;
	}

	/**
	 * 获取当前用户的收件人身份证信息
	 * 
	 * @return
	 */
	public RtnValueDto getIdcardsList() {
		if (CodeValidator.isNetworkError()) {
			return CodeValidator.getNetworkErrorRtnValueDto();
		}
		RtnValueDto rvd = new RtnValueDto();
		String url = prefix_url + idcard_list;
		String rStr = URLUtil.getStringByGet(url, null);
		// String rStr =
		// "{\"data\":[{\"id\":\"1\",\"name\":\"张斌\",\"idcard\":\"330424198711111111\",\"photo\":\"/cocah_intern/200526_s.jpeg\"},{\"id\":\"2\",\"name\":\"张斌2\",\"idcard\":\"330424198712222222\",\"photo\":\"/cocah_intern/200574_s.jpeg\"}]}";
		if (rStr != null && !"".equals(rStr)) {
			try {
				PIdcards idcards = JSONUtil.getJson2Entity(rStr, PIdcards.class);
				if (idcards != null) {
					rvd.setIdcardsData(idcards.getData());
					rvd.setCode(idcards.getCode());
				}
			} catch (Exception e) {
				e.printStackTrace();
				ValidateDto vd = new ValidateDto();
				vd.setMessage(Constants.MESSAGE_EXCEPTION);
				rvd.setValidate(vd);
			}
		} else {
			ValidateDto vd = new ValidateDto();
			vd.setMessage(Constants.MESSAGE_NET);
			rvd.setValidate(vd);
		}
		return rvd;
	}

	public RtnValueDto getOrdersList(String status) {
		if (CodeValidator.isNetworkError()) {
			return CodeValidator.getNetworkErrorRtnValueDto();
		}
		RtnValueDto rvd = new RtnValueDto();

		HashMap<String, String> param = new HashMap<String, String>();
		if (status != null && !"".equals(status)) {
			param.put("status", status);
		}

		String url = prefix_url + order_list_url;
		String rStr = URLUtil.getStringByGet(url, param);
		if (rStr != null && !"".equals(rStr)) {
			try {
				POrderList orders = JSONUtil.getJson2Entity(rStr, POrderList.class);
				if (orders != null) {
					rvd.setOrderListData(orders.getData());
					rvd.setCode(orders.getCode());
				}
			} catch (Exception e) {
				e.printStackTrace();
				ValidateDto vd = new ValidateDto();
				vd.setMessage(Constants.MESSAGE_EXCEPTION);
			}
		} else {
			ValidateDto vd = new ValidateDto();
			vd.setMessage(Constants.MESSAGE_NET);
			rvd.setValidate(vd);
		}
		return rvd;
	}

	/**
	 * 获取订单的状态信息
	 * 
	 * @param number
	 * @return
	 */
	public RtnValueDto getOrderStatus(String number) {
		if (CodeValidator.isNetworkError()) {
			return CodeValidator.getNetworkErrorRtnValueDto();
		}
		RtnValueDto rvd = new RtnValueDto();

		HashMap<String, String> param = new HashMap<String, String>();
		if (StrUtils.isNotEmpty(number)) {
			param.put("num", number);
		}

		String url = prefix_url + order_status_url;
		String rStr = URLUtil.getStringByGet(url, param);
		if (rStr != null && !"".equals(rStr)) {
			try {
				POrder order = JSONUtil.getJson2Entity(rStr, POrder.class);
				if (order != null) {
					rvd.setOrderData(order.getData());
					rvd.setCode(order.getCode());
				}
			} catch (Exception e) {
				e.printStackTrace();
				ValidateDto vd = new ValidateDto();
				vd.setMessage(Constants.MESSAGE_EXCEPTION);
			}
		} else {
			ValidateDto vd = new ValidateDto();
			vd.setMessage(Constants.MESSAGE_NET);
			rvd.setValidate(vd);
		}
		return rvd;
	}

	/**
	 * 获取购物车列表
	 * 
	 * @return
	 */
	public RtnValueDto getCartItemList() {
		if (CodeValidator.isNetworkError()) {
			return CodeValidator.getNetworkErrorRtnValueDto();
		}
		RtnValueDto rvd = null;
		String rvdString = URLUtil.getStringByGet(this.prefix_url + this.cart_list_url, null);
		if (rvdString != null) {
			PCartItem pc = JSONUtil.getJson2Entity(rvdString, PCartItem.class);
			if (pc != null) {
				rvd = new RtnValueDto();
				rvd.setCartListData(pc.getData());
				rvd.setCode(pc.getCode());
			}
		}
		return rvd;
	}

	/**
	 * 找回密码
	 * 
	 * @param username
	 * @return
	 */
	public RtnValueDto findBackPwd(String username) {
		if (CodeValidator.isNetworkError()) {
			return CodeValidator.getNetworkErrorRtnValueDto();
		}
		RtnValueDto rvd = null;
		HashMap<String, String> param = new HashMap<String, String>();
		param.put("username", username);
		String rvdString = URLUtil.getStringByGet(this.prefix_url + this.user_find_backpwd_url,
				param);
		if (rvdString != null) {
			PFindPwd pc = JSONUtil.getJson2Entity(rvdString, PFindPwd.class);
			if (pc != null) {
				rvd = new RtnValueDto();
				rvd.setAtData(pc.getData());
				rvd.setCode(pc.getCode());
			}
		}
		return rvd;
	}

	/**
	 * 查询订单支付结果
	 * 
	 * @param tid
	 *            淘宝订单号
	 * @param number
	 *            订单号(可能多个)
	 * @return RtnValueDto
	 */
	public RtnValueDto getPayStatus(String tid, String number) {
		if (CodeValidator.isNetworkError()) {
			return CodeValidator.getNetworkErrorRtnValueDto();
		}
		RtnValueDto rvd = null;
		HashMap<String, String> param = new HashMap<String, String>();
		param.put("tid", tid);
		param.put("number", number);
		String rvdString = URLUtil.getStringByGet(this.prefix_url + this.user_find_backpwd_url,
				param);
		if (rvdString != null) {
			PFindPwd pc = JSONUtil.getJson2Entity(rvdString, PFindPwd.class);
			if (pc != null) {
				rvd = new RtnValueDto();
				rvd.setAtData(pc.getData());
				rvd.setCode(pc.getCode());
			}
		}
		return rvd;
	}

	public RtnValueDto getLastestVersion() {
		if (CodeValidator.isNetworkError()) {
			return CodeValidator.getNetworkErrorRtnValueDto();
		}
		RtnValueDto rvd = null;
		String rvdString = URLUtil.getStringByGet(this.prefix_url + this.jGoods_version_url, null);
		if (rvdString != null) {
			PVersion pv = JSONUtil.getJson2Entity(rvdString, PVersion.class);
			if (pv != null) {
				rvd = new RtnValueDto();
				rvd.setVersionData(pv.getData());
				rvd.setCode(pv.getCode());
			}
		}
		return rvd;
	}
}

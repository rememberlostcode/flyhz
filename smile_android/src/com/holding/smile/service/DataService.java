
package com.holding.smile.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;

import com.holding.smile.R;
import com.holding.smile.dto.RtnValueDto;
import com.holding.smile.dto.ValidateDto;
import com.holding.smile.entity.Category;
import com.holding.smile.entity.JGoods;
import com.holding.smile.entity.SortType;
import com.holding.smile.protocol.PBrandJGoods;
import com.holding.smile.protocol.PCartItem;
import com.holding.smile.protocol.PCategory;
import com.holding.smile.protocol.PGoods;
import com.holding.smile.protocol.PIdcards;
import com.holding.smile.protocol.PIndexBrands;
import com.holding.smile.protocol.PIndexJGoods;
import com.holding.smile.protocol.POrder;
import com.holding.smile.protocol.POrderList;
import com.holding.smile.protocol.PSort;
import com.holding.smile.protocol.PSortTypes;
import com.holding.smile.protocol.PUser;
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

	public DataService(Context context) {
		prefix_url = context.getString(R.string.prefix_url);
		jGoods_index_url = context.getString(R.string.jGoods_index_url);
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
	}

	/**
	 * 首页初始化
	 * 
	 * @return
	 */
	public RtnValueDto getIndexListInit(Integer cid) {
		RtnValueDto obj = getIndexJGoods(cid);
		return obj;
	}

	/**
	 * 首页推荐品牌商品
	 * 
	 * @return
	 */
	public RtnValueDto getRecommendBrandsListInit(Integer cid) {
		RtnValueDto obj = getRecommendBrands(cid);
		return obj;
	}

	/**
	 * 单一品牌查看全部
	 * 
	 * @return
	 */
	public RtnValueDto getBrandJGoodsListInit(Integer bid, Integer cid, String seqorderType) {
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
	 * @param seqorderValue序号
	 * @return
	 */
	public RtnValueDto getBrandJGoodsListMore(Integer bid, Integer cid, String seqorderType,
			Integer seqorderValue) {
		RtnValueDto obj = getBrandGoods(bid, cid, seqorderType, seqorderValue);
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
	public RtnValueDto getJGoodsSearchListRefresh(String keywords,String seqorderType) {
		RtnValueDto obj = null;
		if (keywords != null && !"".equals(keywords.trim())) {
			obj = searchGoods(keywords, seqorderType, null);
		}
		return obj;
	}

	/**
	 * 搜索物品更多
	 * 
	 * @param keywords
	 *            关键词
	 * @param jGoodsLast
	 *            最后一个物品
	 * @param mLocation
	 *            经纬度
	 * @return
	 */
	public RtnValueDto getJGoodsSearchListMore(String keywords, String seqorderType, Integer seqorderValue) {
		RtnValueDto obj = null;
		if (keywords != null && !"".equals(keywords.trim())) {
			obj = searchGoods(keywords, seqorderType, seqorderValue);
		}
		return obj;
	}

	/**
	 * 首页商品
	 * 
	 * @return
	 */
	public RtnValueDto getIndexJGoods(Integer cid) {
		RtnValueDto rvd = new RtnValueDto();
		HashMap<String, String> param = new HashMap<String, String>();
		if (cid != null)
			param.put("cid", String.valueOf(cid));

		String rStr = URLUtil.getStringByGet(this.prefix_url + this.jGoods_index_url, param);
		if (rStr != null && !"".equals(rStr)) {
			try {
				PIndexJGoods indexJGoods = JSONUtil.getJson2Entity(rStr, PIndexJGoods.class);
				if (indexJGoods != null)
					rvd.setIndexData(indexJGoods.getData());
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
		RtnValueDto rvd = new RtnValueDto();
		HashMap<String, String> param = new HashMap<String, String>();
		if (cid != null)
			param.put("cid", String.valueOf(cid));

		String rStr = URLUtil.getStringByGet(this.prefix_url + this.jGoods_index_single_url, param);
		if (rStr != null && !"".equals(rStr)) {
			try {
				PIndexBrands indexBrands = JSONUtil.getJson2Entity(rStr, PIndexBrands.class);
				if (indexBrands != null)
					rvd.setIndexBrandsData(indexBrands.getData());
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
	 * 首页推荐品牌商品
	 * 
	 * @return
	 */
	public RtnValueDto getRecommendBrands(Integer cid) {
		RtnValueDto rvd = new RtnValueDto();
		HashMap<String, String> param = new HashMap<String, String>();
		if (cid != null)
			param.put("cid", String.valueOf(cid));
		String rStr = URLUtil.getStringByGet(this.prefix_url + this.jGoods_recommend_url, param);

		if (rStr != null && !"".equals(rStr)) {
			try {
				PBrandJGoods brandJGoods = JSONUtil.getJson2Entity(rStr, PBrandJGoods.class);
				if (brandJGoods != null)
					rvd.setBrandData(brandJGoods.getData());
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
	 * 单一品牌查询
	 * 
	 * @param bid
	 * @param cid
	 * @param seqorderType
	 * @param seqorderValue
	 * @return
	 */

	public RtnValueDto getBrandGoods(Integer bid, Integer cid, String seqorderType,
			Integer seqorderValue) {
		RtnValueDto rvd = new RtnValueDto();
		List<JGoods> results = new ArrayList<JGoods>();
		String url = this.prefix_url;
		HashMap<String, String> param = new HashMap<String, String>();
		if (bid != null)
			param.put("bid", String.valueOf(bid));
		if (cid != null)
			param.put("cid", String.valueOf(cid));
		if (StrUtils.isNotEmpty(seqorderType))
			param.put("seqorderType", seqorderType.trim());
		if (seqorderValue != null) {
			param.put("seqorderValue", String.valueOf(seqorderValue));
			url += this.jGoods_brand_more_url;
		} else {
			url += this.jGoods_brand_url;
		}
		String rStr = URLUtil.getStringByGet(url, param);

		if (rStr != null && !"".equals(rStr)) {
			try {
				PGoods pst = JSONUtil.getJson2Entity(rStr, PGoods.class);
				results = pst.getData();
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
		rvd.setData(results);
		return rvd;
	}

	/**
	 * 搜索物品
	 * 
	 * @param keywords
	 *            关键词
	 * @param seqorderType
	 * @param seqorderValue
	 * @return
	 */
	public RtnValueDto searchGoods(String keywords, String seqorderType, Integer seqorderValue) {
		RtnValueDto rvd = new RtnValueDto();
		List<JGoods> results = new ArrayList<JGoods>();
		String url = prefix_url;
		HashMap<String, String> param = new HashMap<String, String>();
		if (StrUtils.isNotEmpty(keywords)) {
			param.put("keywords", keywords.trim());
		}
		if (StrUtils.isNotEmpty(seqorderType)) {
			param.put("seqorderType", seqorderType.trim());
		}
		if (seqorderValue != null) {
			param.put("seqorderValue", String.valueOf(seqorderValue));
			url += this.jGoods_search_more_url;
		} else {
			url += this.jGoods_search_url;
		}
		String rStr = URLUtil.getStringByGet(url, param);

		if (rStr != null && !"".equals(rStr)) {
			try {
				PGoods pst = JSONUtil.getJson2Entity(rStr, PGoods.class);
				results = pst.getData();
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
		rvd.setData(results);
		return rvd;
	}

	/**
	 * 商品分类
	 * 
	 * @return
	 */
	public RtnValueDto getCategorys() {
		RtnValueDto rvd = new RtnValueDto();
		List<Category> results = new ArrayList<Category>();
		String rStr = URLUtil.getStringByGet(this.prefix_url + this.jGoods_cate_url, null);

		if (rStr != null && !"".equals(rStr)) {
			try {
				PCategory pc = JSONUtil.getJson2Entity(rStr, PCategory.class);
				results = pc.getData();
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
		rvd.setCateData(results);
		return rvd;
	}

	/**
	 * 商品排行列表
	 * 
	 * @return
	 */
	public RtnValueDto getSortList() {
		RtnValueDto rvd = new RtnValueDto();
		String rStr = URLUtil.getStringByGet(this.prefix_url + this.jGoods_sort_url, null);

		if (rStr != null && !"".equals(rStr)) {
			try {
				PSort pc = JSONUtil.getJson2Entity(rStr, PSort.class);
				if (pc != null)
					rvd.setSortData(pc.getData());
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
	 * 商品排行榜列表
	 * 
	 * @return
	 */
	public RtnValueDto getJGoodsSortList(String sortUrl) {
		RtnValueDto rvd = new RtnValueDto();
		String rStr = "";
		if (StrUtils.isNotEmpty(sortUrl)) {
			rStr = URLUtil.getStringByGet(prefix_url + sortUrl, null);
		}

		if (rStr != null && !"".equals(rStr)) {
			try {
				PGoods pst = JSONUtil.getJson2Entity(rStr, PGoods.class);
				rvd.setData(pst.getData());
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
		RtnValueDto rvd = new RtnValueDto();
		String url = this.prefix_url + this.jGoods_detail_url;
		HashMap<String, String> param = new HashMap<String, String>();
		if (bs != null)
			param.put("bs", String.valueOf(bs));

		String rStr = URLUtil.getStringByGet(url, param);
		if (rStr != null && !"".equals(rStr)) {
			try {
				PGoods details = JSONUtil.getJson2Entity(rStr, PGoods.class);
				if (details != null)
					rvd.setData(details.getData());
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
		RtnValueDto rvd = null;
		String rvdString = URLUtil.getStringByGet(this.prefix_url + this.cart_list_url, null);
		if (rvdString != null) {
			PCartItem pc = JSONUtil.getJson2Entity(rvdString, PCartItem.class);
			if (pc != null && pc.getData() != null && pc.getCode() == 200000) {
				rvd = new RtnValueDto();
				rvd.setCartListData(pc.getData());
				rvd.setCode(200000);
			}
		}
		return rvd;
	}
}

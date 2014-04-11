
package com.holding.smile.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.holding.smile.R;
import com.holding.smile.dto.BrandJGoods;
import com.holding.smile.dto.RtnValueDto;
import com.holding.smile.dto.ValidateDto;
import com.holding.smile.entity.Category;
import com.holding.smile.entity.JGoods;
import com.holding.smile.entity.SortType;
import com.holding.smile.tools.Constants;
import com.holding.smile.tools.JSONUtil;
import com.holding.smile.tools.StrUtils;
import com.holding.smile.tools.URLUtil;

public class DataService {

	/**
	 * 是否从网络获取数据
	 */
	public static boolean	getDataFromNet	= true;

	private String			jGoodsInitJson;
	private String			jGoodsRefreshJson;
	private String			jGoodsMoreJson;

	private String			jGoodsTwoInitJson;
	private String			jGoodsTwoRefreshJson;
	private String			jGoodsTwoMoreJson;

	private String			jGoodsSearchInitJson;
	private String			jGoodsSearchRefreshJson;
	private String			jGoodsSearchMoreJson;

	private String			brandJGoodsInitJson;

	private String			prefix_url;
	private String			jGoods_recommend_url;
	private String			jGoods_brand_recommend_url;
	private String			jGoods_cate_url;
	private String			jGoods_brand_url;
	private String			jGoods_brand_more_url;
	private String			jGoods_sorttype_url;

	public DataService(Context context) {
		jGoodsInitJson = setJson("jGoodsInitJson.json");
		jGoodsRefreshJson = setJson("jGoodsRefreshJson.json");
		jGoodsMoreJson = setJson("jGoodsMoreJson.json");

		jGoodsTwoInitJson = setJson("jGoodsTwoInitJson.json");
		jGoodsTwoRefreshJson = setJson("jGoodsTwoRefreshJson.json");
		jGoodsTwoMoreJson = setJson("jGoodsTwoMoreJson.json");

		jGoodsSearchInitJson = setJson("jGoodsSearchInitJson.json");
		jGoodsSearchRefreshJson = setJson("jGoodsSearchRefreshJson.json");
		jGoodsSearchMoreJson = setJson("jGoodsSearchMoreJson.json");

		brandJGoodsInitJson = setJson("brandJGoodsInitJson.json");

		prefix_url = context.getString(R.string.prefix_url);
		jGoods_recommend_url = context.getString(R.string.jGoods_recommend_url);
		jGoods_brand_recommend_url = context.getString(R.string.jGoods_brand_recommend_url);
		jGoods_cate_url = context.getString(R.string.jGoods_cate_url);
		jGoods_brand_url = context.getString(R.string.jGoods_brand_url);
		jGoods_brand_more_url = context.getString(R.string.jGoods_brand_more_url);
		jGoods_sorttype_url = context.getString(R.string.jGoods_sorttype_url);
	}

	private String setJson(String fileName) {
		String result = "";
		try {
			InputStream is = DataService.class.getClassLoader().getResourceAsStream(
					"testdata/" + fileName);
			InputStreamReader read = new InputStreamReader(is);
			BufferedReader reader = new BufferedReader(read);
			StringBuffer sb = new StringBuffer();
			String eachLine = null;
			while ((eachLine = reader.readLine()) != null) {
				sb.append(eachLine);
			}
			result = sb.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 首页初始化
	 * 
	 * @return
	 */
	public RtnValueDto getRecommendBrandsListInit(Integer cid) {
		RtnValueDto obj = null;
		if (getDataFromNet) {
			obj = getRecommendBrands(cid);
		} else {
			obj = JSONUtil.changeJson2RtnValueDto(brandJGoodsInitJson);
		}
		return obj;
	}

	/**
	 * 首页活动区初始化
	 * 
	 * @return
	 */
	public RtnValueDto getRecommendJGoodsListInit() {
		RtnValueDto obj = null;
		if (getDataFromNet) {
			obj = getRecommendGoods();
		} else {
			obj = JSONUtil.changeJson2RtnValueDto(jGoodsInitJson);
		}
		return obj;
	}

	/**
	 * 首页最新物品刷新
	 * 
	 * @param jGoodsFirst
	 *            第一个物品
	 * @return
	 */
	public RtnValueDto getJGoodsListRefresh(JGoods jGoodsFirst) {
		RtnValueDto obj = null;
		if (getDataFromNet) {
			if (jGoodsFirst != null && jGoodsFirst.getSt() != null) {
				obj = getLastestGoods(null, jGoodsFirst.getSt(), true);
			} else {
				obj = getLastestGoods(null, null, true);
			}
		} else {
			obj = JSONUtil.changeJson2RtnValueDto(jGoodsRefreshJson);
		}
		return obj;
	}

	/**
	 * 单一品牌查看全部
	 * 
	 * @return
	 */
	public RtnValueDto getBrandJGoodsListInit(Integer bid, Integer cid, String seqorderType) {
		RtnValueDto obj = null;
		if (getDataFromNet) {
			obj = getBrandGoods(bid, cid, seqorderType, null);
		} else {
			obj = JSONUtil.changeJson2RtnValueDto(jGoodsTwoInitJson);
		}
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
		if (getDataFromNet) {
			String rStr = URLUtil.getStringByGet(this.prefix_url + this.jGoods_sorttype_url, null);

			if (rStr != null && !"".equals(rStr)) {
				JSONObject datas;
				try {
					datas = new JSONObject(rStr);
					String data = datas.getString("data");
					SortType[] sortTypes = JSONUtil.getJson2Entity(data, SortType[].class);
					if (sortTypes != null) {
						for (int i = 0; i < sortTypes.length; i++) {
							results.add(sortTypes[i]);
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
					return null;
				}
			}
		} else {
			SortType sort = new SortType();
			sort.setN("销售量");
			sort.setV("sales");
			results.add(sort);

			sort = new SortType();
			sort.setN("折扣");
			sort.setV("discount");
			results.add(sort);
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
		RtnValueDto obj = null;
		if (getDataFromNet) {
			obj = getBrandGoods(bid, cid, seqorderType, seqorderValue);
		} else {
			obj = JSONUtil.changeJson2RtnValueDto(jGoodsTwoMoreJson);
		}
		return obj;
	}

	/**
	 * 搜索物品初始化
	 * 
	 * @param keywords
	 *            关键词
	 * @param mLocation
	 *            经纬度
	 * @return
	 */
	public RtnValueDto getJGoodsSearchListInit(String keywords) {
		RtnValueDto obj = null;
		if (getDataFromNet) {
			if (keywords != null && !"".equals(keywords.trim())) {
				obj = getLastestGoods(keywords, null, true);
			}
		} else {
			obj = JSONUtil.changeJson2RtnValueDto(jGoodsSearchInitJson);
		}
		return obj;
	}

	/**
	 * 搜索物品刷新
	 * 
	 * @param keywords
	 *            关键词
	 * @param jGoodsFirst
	 *            第一个物品
	 * @param mLocation
	 *            经纬度
	 * @return
	 */
	public RtnValueDto getJGoodsSearchListRefresh(String keywords, JGoods jGoodsFirst) {
		RtnValueDto obj = null;
		if (getDataFromNet) {
			if (keywords != null && !"".equals(keywords.trim())) {
				if (jGoodsFirst != null && jGoodsFirst.getSt() != null) {
					obj = getLastestGoods(keywords, jGoodsFirst.getSt(), true);
				} else {
					obj = getLastestGoods(keywords, null, true);
				}
			}
		} else {
			obj = JSONUtil.changeJson2RtnValueDto(jGoodsSearchRefreshJson);
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
	public RtnValueDto getJGoodsSearchListMore(String keywords, JGoods jGoodsLast) {
		RtnValueDto obj = null;
		if (getDataFromNet) {
			if (keywords != null && !"".equals(keywords.trim())) {
				if (jGoodsLast != null && jGoodsLast.getSt() != null) {
					obj = getLastestGoods(keywords, jGoodsLast.getSt(), false);
				} else {
					obj = getLastestGoods(keywords, null, false);
				}
			}
		} else {
			obj = JSONUtil.changeJson2RtnValueDto(jGoodsSearchMoreJson);
		}
		return obj;
	}

	/**
	 * 首页推荐品牌商品
	 * 
	 * @return
	 */
	public RtnValueDto getRecommendBrands(Integer cid) {
		RtnValueDto rvd = new RtnValueDto();
		List<BrandJGoods> results = new ArrayList<BrandJGoods>();
		HashMap<String, String> param = new HashMap<String, String>();
		if (cid != null)
			param.put("cid", String.valueOf(cid));
		String rStr = URLUtil.getStringByGet(this.prefix_url + this.jGoods_brand_recommend_url,
				param);

		if (rStr != null && !"".equals(rStr)) {
			JSONObject datas;
			try {
				datas = new JSONObject(rStr);
				// .replaceAll("\"\\\\[", "[").replaceAll("\\\\]\"", "]")
				String data = datas.getString("data");
				BrandJGoods[] goods = JSONUtil.getJson2Entity(data, BrandJGoods[].class);
				if (goods != null) {
					for (int i = 0; i < goods.length; i++) {
						results.add(goods[i]);
					}
				}
			} catch (JSONException e) {
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
		rvd.setBrandData(results);
		return rvd;
	}

	/**
	 * 首页活动区商品
	 * 
	 * @return
	 */
	public RtnValueDto getRecommendGoods() {
		RtnValueDto rvd = new RtnValueDto();
		List<JGoods> results = new ArrayList<JGoods>();
		String rStr = URLUtil.getStringByGet(this.prefix_url + this.jGoods_recommend_url, null);

		if (rStr != null && !"".equals(rStr)) {
			JSONObject datas;
			try {
				datas = new JSONObject(rStr);
				// .replaceAll("\"\\\\[", "[").replaceAll("\\\\]\"", "]")
				String data = datas.getString("data");
				JGoods[] goods = JSONUtil.getJson2Entity(data, JGoods[].class);
				if (goods != null) {
					for (int i = 0; i < goods.length; i++) {
						results.add(goods[i]);
					}
				}
			} catch (JSONException e) {
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
			JSONObject datas;
			try {
				datas = new JSONObject(rStr);
				String data = datas.getString("data");
				JGoods[] goods = JSONUtil.getJson2Entity(data, JGoods[].class);
				if (goods != null) {
					for (int i = 0; i < goods.length; i++) {
						results.add(goods[i]);
					}
				}
			} catch (JSONException e) {
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
	 * 最新物品
	 * 
	 * @param seqorder
	 *            序号
	 * @param refresh
	 *            是否是刷新，true是，否则是更多
	 * @return
	 */
	public RtnValueDto getLastestGoods(String keywords, Integer seqorder, boolean refresh) {
		RtnValueDto rvd = new RtnValueDto();
		List<JGoods> results = new ArrayList<JGoods>();
		String rStr = "";
		HashMap<String, String> param = new HashMap<String, String>();

		int rows = Constants.MORE_NUM;
		if (refresh) {
			rows = Constants.INIT_NUM;
		}

		if (keywords != null && !"".equals(keywords)) {
			try {
				param.put("q",
						"name%3A" + URLEncoder.encode(keywords, "UTF-8") + "+OR+brand_cn%3A"
								+ URLEncoder.encode(keywords, "UTF-8") + "+OR+description%3A"
								+ URLEncoder.encode(keywords, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		} else {
			param.put("q", "*%3A*");
		}
		if (seqorder != null) {
			if (refresh) {
				param.put("fq", "seqorder_time%3A%5B" + (seqorder + 1) + "+TO+*%5D");
			} else {
				param.put("fq", "seqorder_time%3A%5B*+TO+" + (seqorder - 1) + "%5D");
			}
		}
		param.put("sort", "seqorder_time+desc");
		param.put("wt", "json");
		param.put("fl", Constants.PARAM_STRING);
		param.put("rows", String.valueOf(rows));
		rStr = URLUtil.getStringByGet(Constants.SERVER_URL + Constants.SEARCH_URL, param);

		if (rStr != null && !"".equals(rStr)) {
			JSONObject datas;
			try {
				datas = new JSONObject(rStr);
				// .replaceAll("\"\\\\[", "[").replaceAll("\\\\]\"", "]")
				String docs = datas.getJSONObject("response").getString("docs");
				int numFound = datas.getJSONObject("response").getInt("numFound");
				JGoods[] goods = JSONUtil.getJson2Entity(docs, JGoods[].class);
				if (goods != null) {
					for (int i = 0; i < goods.length; i++) {
						results.add(goods[i]);
					}
				}

				ValidateDto vd = new ValidateDto();
				if (results.size() == 0) {
					if (refresh) {
						vd.setMessage(Constants.MESSAGE_NODATA);
					} else {
						vd.setMessage(Constants.MESSAGE_LAST);
					}
				}
				if (refresh && numFound > Constants.INIT_NUM) {
					vd.setOption("3");
				}
				rvd.setValidate(vd);
			} catch (JSONException e) {
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
	 * 最省钱物品
	 * 
	 * @param seqorder
	 *            序号
	 * @param refresh
	 *            是否是刷新，true是，否则是更多
	 * @return
	 */
	public RtnValueDto getMostEconomicalGoods(Integer seqorder, boolean refresh) {
		RtnValueDto rvd = new RtnValueDto();
		List<JGoods> results = new ArrayList<JGoods>();
		String rStr = "";
		HashMap<String, String> param = new HashMap<String, String>();

		int rows = Constants.MORE_NUM;
		if (refresh) {
			rows = Constants.INIT_NUM;
		}

		param.put("q", "*%3A*");
		if (seqorder != null) {
			if (refresh) {
				param.put("fq", "seqorder%3A%5B" + (seqorder + 1) + "+TO+*%5D");
			} else {
				param.put("fq", "seqorder%3A%5B*+TO+" + (seqorder - 1) + "%5D");
			}
		}
		param.put("sort", "seqorder+desc");
		param.put("wt", "json");
		param.put("fl", Constants.PARAM_STRING);
		param.put("rows", String.valueOf(rows));
		rStr = URLUtil.getStringByGet(Constants.SERVER_URL + Constants.SEARCH_URL, param);

		if (rStr != null && !"".equals(rStr.trim())) {
			JSONObject datas;
			try {
				datas = new JSONObject(rStr);
				String docs = datas.getJSONObject("response").getString("docs");
				int numFound = datas.getJSONObject("response").getInt("numFound");
				JGoods[] goods = JSONUtil.getJson2Entity(docs, JGoods[].class);
				if (goods != null) {
					for (int i = 0; i < goods.length; i++) {
						results.add(goods[i]);
					}
				}

				ValidateDto vd = new ValidateDto();
				if (results.size() == 0) {
					if (refresh) {
						vd.setMessage(Constants.MESSAGE_NODATA);
					} else {
						vd.setMessage(Constants.MESSAGE_LAST);
					}
				}
				if (refresh && numFound > Constants.MORE_NUM) {
					vd.setOption("3");
				}
				rvd.setValidate(vd);
			} catch (JSONException e) {
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
			JSONObject datas;
			try {
				datas = new JSONObject(rStr);
				String data = datas.getString("data");
				Category[] cates = JSONUtil.getJson2Entity(data, Category[].class);
				if (cates != null) {
					for (int i = 0; i < cates.length; i++) {
						results.add(cates[i]);
					}
				}
			} catch (JSONException e) {
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
}

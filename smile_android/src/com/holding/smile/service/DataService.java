
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

import com.holding.smile.dto.RtnValueDto;
import com.holding.smile.dto.ValidateDto;
import com.holding.smile.entity.JGoods;
import com.holding.smile.entity.SolrGood;
import com.holding.smile.tools.Constants;
import com.holding.smile.tools.JSONUtil;
import com.holding.smile.tools.URLUtil;

public class DataService {

	/**
	 * 是否从网络获取数据
	 */
	public static boolean	getDataFromNet	= false;

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
	public RtnValueDto getBrandJGoodsListInit() {
		RtnValueDto obj = null;
		if (getDataFromNet) {
			// obj = getLastestGoods(null, null, true);
		} else {
			obj = JSONUtil.changeJson2RtnValueDto(brandJGoodsInitJson);
		}
		return obj;
	}

	/**
	 * 首页最新物品初始化
	 * 
	 * @return
	 */
	public RtnValueDto getJGoodsListInit() {
		RtnValueDto obj = null;
		if (getDataFromNet) {
			obj = getLastestGoods(null, null, true);
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
			if (jGoodsFirst != null && jGoodsFirst.getSeqorder_time() != null) {
				obj = getLastestGoods(null, jGoodsFirst.getSeqorder_time(), true);
			} else {
				obj = getLastestGoods(null, null, true);
			}
		} else {
			obj = JSONUtil.changeJson2RtnValueDto(jGoodsRefreshJson);
		}
		return obj;
	}

	/**
	 * 首页最新物品更多
	 * 
	 * @param jGoodsLast
	 *            最后一个物品
	 * @return
	 */
	public RtnValueDto getJGoodsListMore(JGoods jGoodsLast) {
		RtnValueDto obj = null;
		if (getDataFromNet) {
			if (jGoodsLast != null && jGoodsLast.getSeqorder_time() != null) {
				obj = getLastestGoods(null, jGoodsLast.getSeqorder_time(), false);
			} else {
				obj = getLastestGoods(null, null, false);
			}
		} else {
			obj = JSONUtil.changeJson2RtnValueDto(jGoodsMoreJson);
		}
		return obj;
	}

	/**
	 * 首页省钱物品初始化
	 * 
	 * @return
	 */
	public RtnValueDto getJGoodsTwoListInit() {
		RtnValueDto obj = null;
		if (getDataFromNet) {
			obj = getMostEconomicalGoods(null, true);
		} else {
			obj = JSONUtil.changeJson2RtnValueDto(jGoodsTwoInitJson);
		}
		return obj;
	}

	/**
	 * 首页省钱物品刷新
	 * 
	 * @param jGoodsFirst
	 *            第一个物品
	 * @return
	 */
	public RtnValueDto getJGoodsTwoListRefresh(JGoods jGoodsFirst) {
		RtnValueDto obj = null;
		if (getDataFromNet) {
			if (jGoodsFirst != null && jGoodsFirst.getSeqorder() != null) {
				obj = getMostEconomicalGoods(jGoodsFirst.getSeqorder(), true);
			} else {
				obj = getMostEconomicalGoods(null, true);
			}
		} else {
			obj = JSONUtil.changeJson2RtnValueDto(jGoodsTwoRefreshJson);
		}
		return obj;
	}

	/**
	 * 首页省钱物品更多
	 * 
	 * @param jGoodsLast
	 *            最后一个物品
	 * @return
	 */
	public RtnValueDto getJGoodsTwoListMore(JGoods jGoodsLast) {
		RtnValueDto obj = null;
		if (getDataFromNet) {
			if (jGoodsLast != null && jGoodsLast.getSeqorder() != null) {
				obj = getMostEconomicalGoods(jGoodsLast.getSeqorder(), false);
			} else {
				obj = getMostEconomicalGoods(null, false);
			}
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
				if (jGoodsFirst != null && jGoodsFirst.getSeqorder_time() != null) {
					obj = getLastestGoods(keywords, jGoodsFirst.getSeqorder_time(), true);
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
				if (jGoodsLast != null && jGoodsLast.getSeqorder_time() != null) {
					obj = getLastestGoods(keywords, jGoodsLast.getSeqorder_time(), false);
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
				SolrGood[] solr = JSONUtil.getJson2Entity(docs, SolrGood[].class);
				if (solr != null) {
					for (int i = 0; i < solr.length; i++) {
						results.add(new JGoods(solr[i]));
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
				SolrGood[] solr = JSONUtil.getJson2Entity(docs, SolrGood[].class);
				if (solr != null) {
					for (int i = 0; i < solr.length; i++) {
						results.add(new JGoods(solr[i]));
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
}

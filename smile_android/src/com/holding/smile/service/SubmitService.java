
package com.holding.smile.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;

import com.holding.smile.R;
import com.holding.smile.dto.RtnValueDto;
import com.holding.smile.dto.ValidateDto;
import com.holding.smile.entity.Consignee;
import com.holding.smile.entity.Idcard;
import com.holding.smile.protocol.PConsignee;
import com.holding.smile.tools.Constants;
import com.holding.smile.tools.FileUtils;
import com.holding.smile.tools.JSONUtil;
import com.holding.smile.tools.URLUtil;

public class SubmitService {

	private String	prefix_url;
	private String	address_add;
	private String	address_modify;
	private String	address_remove;
	private String	password_reset;
	private String	set_suer_info;
	private String	idcard_save;
	private String	idcard_delete;

	public SubmitService(Context context) {
		prefix_url = context.getString(R.string.prefix_url);
		address_add = context.getString(R.string.address_add);
		address_modify = context.getString(R.string.address_modify);
		address_remove = context.getString(R.string.address_remove);
		password_reset = context.getString(R.string.reset_password);
		set_suer_info = context.getString(R.string.set_suer_info);

		idcard_save = context.getString(R.string.idcard_save);
		idcard_delete = context.getString(R.string.idcard_delete);
	}

	/**
	 * 编辑收货人地址
	 * 
	 * @param consignee
	 *            收货人地址信息
	 * @return
	 */
	public RtnValueDto consigneeEdit(Consignee consignee) {
		if (consignee == null) {
			return null;
		}
		RtnValueDto rvd = new RtnValueDto();
		HashMap<String, String> param = new HashMap<String, String>();
		if (consignee.getId() != null) {
			param.put("id", String.valueOf(consignee.getId()));
		}
		if (consignee.getConturyId() != null) {
			param.put("conturyId", String.valueOf(consignee.getConturyId()));
		}
		if (consignee.getProvinceId() != null) {
			param.put("provinceId", String.valueOf(consignee.getProvinceId()));
		}
		if (consignee.getCityId() != null) {
			param.put("cityId", String.valueOf(consignee.getCityId()));
		}
		if (consignee.getDistrictId() != null) {
			param.put("districtId", String.valueOf(consignee.getDistrictId()));
		}
		if (consignee.getAddress() != null) {
			param.put("address", String.valueOf(consignee.getAddress()));
		}
		if (consignee.getName() != null) {
			param.put("name", String.valueOf(consignee.getName()));
		}
		if (consignee.getMobilephone() != null) {
			param.put("mobilephone", String.valueOf(consignee.getMobilephone()));
		}
		if (consignee.getZipcode() != null) {
			param.put("zipcode", String.valueOf(consignee.getZipcode()));
		}

		String url = "";
		if (consignee.getId() != null) {
			url = prefix_url + address_modify;
		} else {
			url = prefix_url + address_add;
		}
		String rStr = URLUtil.getStringByGet(url, param);

		if (rStr != null && !"".equals(rStr)) {
			try {
				PConsignee pcons = JSONUtil.getJson2Entity(rStr, PConsignee.class);
				if (pcons != null) {
					rvd.setConsignee(pcons.getData());
					rvd.setCode(pcons.getCode());
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
	 * 删除收货人地址
	 * 
	 * @param conid
	 *            主键ID
	 * @return
	 */
	public RtnValueDto addressRemove(Integer conid) {
		if (conid == null) {
			return null;
		}
		RtnValueDto rvd = new RtnValueDto();
		HashMap<String, String> param = new HashMap<String, String>();

		param.put("conid", String.valueOf(conid));

		String url = prefix_url + address_remove;
		String rStr = URLUtil.getStringByGet(url, param);

		if (rStr != null && !"".equals(rStr)) {
			try {
				rvd = JSONUtil.getJson2Entity(rStr, RtnValueDto.class);
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
	 * 修改密码
	 * 
	 * @param oldPwd
	 *            原密码
	 * @param newPwd
	 *            新密码
	 * @return
	 */
	public RtnValueDto passwordReset(String oldPwd, String newPwd) {
		if (oldPwd == null || newPwd == null || "".equals(oldPwd) || "".equals(newPwd)) {
			return null;
		}
		RtnValueDto rvd = new RtnValueDto();
		HashMap<String, String> param = new HashMap<String, String>();

		param.put("opwd", oldPwd);
		param.put("npwd", newPwd);

		String url = prefix_url + password_reset;
		String rStr = URLUtil.getStringByGet(url, param);

		if (rStr != null && !"".equals(rStr)) {
			try {
				rvd = JSONUtil.getJson2Entity(rStr, RtnValueDto.class);
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
	 * 设置用户信息
	 * 
	 * @param field
	 *            参数名
	 * @param value
	 *            参数值
	 * @return
	 */
	public RtnValueDto setUserInfo(String field, String value) {
		if (field == null || value == null || "".equals(field)) {
			return null;
		}
		RtnValueDto rvd = new RtnValueDto();
		HashMap<String, String> param = new HashMap<String, String>();

		param.put("field", field);
		param.put("fval", value);

		String url = prefix_url + set_suer_info;
		String rStr = URLUtil.getStringByGet(url, param);

		if (rStr != null && !"".equals(rStr)) {
			try {
				rvd = JSONUtil.getJson2Entity(rStr, RtnValueDto.class);
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

	public RtnValueDto idcardSave(Idcard idcard, String filePath) throws Exception {
		if (idcard == null) {
			return null;
		}
		RtnValueDto rvd = new RtnValueDto();
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		if (idcard.getId() != null) {
			params.add(new BasicNameValuePair("id", String.valueOf(idcard.getId())));
		}
		if (idcard.getName() != null) {
			params.add(new BasicNameValuePair("name", String.valueOf(idcard.getName())));
		}
		if (idcard.getIdcard() != null) {
			params.add(new BasicNameValuePair("idcard", String.valueOf(idcard.getIdcard())));
		}

		String url = "";
		url = prefix_url + idcard_save;
		// String rStr = URLUtil.getStringByGet(url, param);
		String rStr = FileUtils.uploadIdcardPhoto(url, params, filePath);

		if (rStr != null && !"".equals(rStr)) {
			try {
				rvd = JSONUtil.getJson2Entity(rStr, RtnValueDto.class);
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

	public RtnValueDto idcardRemove(Integer dicadrId) {
		if (dicadrId == null) {
			return null;
		}
		RtnValueDto rvd = new RtnValueDto();
		HashMap<String, String> param = new HashMap<String, String>();

		param.put("id", String.valueOf(dicadrId));

		String url = prefix_url + idcard_delete;
		String rStr = URLUtil.getStringByGet(url, param);

		if (rStr != null && !"".equals(rStr)) {
			try {
				rvd = JSONUtil.getJson2Entity(rStr, RtnValueDto.class);
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
}

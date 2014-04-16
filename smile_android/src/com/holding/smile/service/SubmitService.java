
package com.holding.smile.service;

import java.util.HashMap;

import android.content.Context;

import com.holding.smile.R;
import com.holding.smile.dto.RtnValueDto;
import com.holding.smile.dto.ValidateDto;
import com.holding.smile.entity.Consignee;
import com.holding.smile.protocol.PBrandJGoods;
import com.holding.smile.tools.Constants;
import com.holding.smile.tools.JSONUtil;
import com.holding.smile.tools.URLUtil;

public class SubmitService {

	private String	prefix_url;
	private String	address_add;
	private String	address_modify;
	private String	address_remove;
	private String	address_set;

	public SubmitService(Context context) {
		prefix_url = context.getString(R.string.prefix_url);
		address_add = context.getString(R.string.address_add);
		address_modify = context.getString(R.string.address_modify);
		address_remove = context.getString(R.string.address_remove);
		address_set = context.getString(R.string.address_set);
	}

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
}

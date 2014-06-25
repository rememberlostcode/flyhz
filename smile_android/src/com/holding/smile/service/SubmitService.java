
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
import com.holding.smile.entity.Idcard;
import com.holding.smile.entity.SUser;
import com.holding.smile.protocol.PCartItemDetail;
import com.holding.smile.protocol.POrder;
import com.holding.smile.protocol.PProduct;
import com.holding.smile.protocol.PUser;
import com.holding.smile.tools.CodeValidator;
import com.holding.smile.tools.Constants;
import com.holding.smile.tools.FileUtils;
import com.holding.smile.tools.JSONUtil;
import com.holding.smile.tools.StrUtils;
import com.holding.smile.tools.URLUtil;

public class SubmitService {

	private String	prefix_url;
	private String	password_reset;
	private String	set_suer_info;
	private String	idcard_save;
	private String	idcard_delete;
	private String	register_url;

	private String	order_inform_url;
	private String	order_updateQty_url;
	private String	order_confirm_url;
	private String	cart_add_url;
	private String	cart_setQty_url;
	private String	cart_remove_url;
	private String	order_close_url;

	public SubmitService(Context context) {
		prefix_url = context.getString(R.string.prefix_url);
		password_reset = context.getString(R.string.reset_password);
		set_suer_info = context.getString(R.string.set_suer_info);

		idcard_save = context.getString(R.string.idcard_save);
		idcard_delete = context.getString(R.string.idcard_delete);
		register_url = context.getString(R.string.register_url);

		order_inform_url = context.getString(R.string.order_inform_url);
		order_updateQty_url = context.getString(R.string.order_updateQty_url);
		order_confirm_url = context.getString(R.string.order_confirm_url);
		order_close_url = context.getString(R.string.order_close_url);

		cart_add_url = context.getString(R.string.cart_add_url);
		cart_setQty_url = context.getString(R.string.cart_setQty_url);
		cart_remove_url = context.getString(R.string.cart_remove_url);
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
		if(CodeValidator.isNetworkError()){
			return CodeValidator.getNetworkErrorRtnValueDto();
		}
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
		if(CodeValidator.isNetworkError()){
			return CodeValidator.getNetworkErrorRtnValueDto();
		}
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

	/**
	 * 身份证保存
	 * 
	 * @param idcard
	 *            身份证信息
	 * @param filePath
	 *            身份证图片
	 * @return
	 * @throws Exception
	 */
	public RtnValueDto idcardSave(Idcard idcard, String filePath) throws Exception {
		if(CodeValidator.isNetworkError()){
			return CodeValidator.getNetworkErrorRtnValueDto();
		}
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
		if (idcard.getNumber() != null) {
			params.add(new BasicNameValuePair("number", String.valueOf(idcard.getNumber())));
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

	/**
	 * 身份证删除
	 * 
	 * @param dicadrId
	 *            身份证表主键ID
	 * @return
	 */
	public RtnValueDto idcardRemove(Integer dicadrId) {
		if(CodeValidator.isNetworkError()){
			return CodeValidator.getNetworkErrorRtnValueDto();
		}
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

	/**
	 * 注册
	 * 
	 * @param iuser
	 *            用户信息
	 * @return
	 */
	public RtnValueDto register(SUser iuser) {
		if(CodeValidator.isNetworkError()){
			return CodeValidator.getNetworkErrorRtnValueDto();
		}
		RtnValueDto rvd = null;
		if (iuser != null && iuser.getUsername() != null && iuser.getPassword() != null) {
			HashMap<String, String> param = new HashMap<String, String>();
			param.put("username", iuser.getUsername());
			param.put("password", iuser.getPassword());
			param.put("email", iuser.getEmail());
			String rvdString = URLUtil.getStringByGet(prefix_url + register_url, param);
			if (rvdString != null) {
				PUser user = JSONUtil.getJson2Entity(rvdString, PUser.class);
				rvd = new RtnValueDto();
				if (user != null && user.getData() != null && user.getCode() == 200000) {
					rvd.setUserData(user.getData());
					rvd.setCode(user.getCode());
				} else {
					rvd.setCode(user.getCode());
				}
			}
		}
		return rvd;
	}

	/**
	 * 购买时生成的订单信息,pids与cartIds只传一种
	 * 
	 * @param pids
	 *            格式：pid_qty(如：12_1,12_2)
	 * @param cartIds
	 *            购物车ID列表
	 * @param addressId
	 *            地址ID
	 * @return
	 */
	public RtnValueDto getOrderInform(String pidQty, List<String> cartIds, Integer addressId) {
		if(CodeValidator.isNetworkError()){
			return CodeValidator.getNetworkErrorRtnValueDto();
		}
		RtnValueDto rvd = new RtnValueDto();
		List<String> listParam = new ArrayList<String>();
		HashMap<String, List<String>> param = new HashMap<String, List<String>>();
		if (StrUtils.isNotEmpty(pidQty)) {
			listParam.add(pidQty);
			param.put("pids", listParam);
		} else {
			if (cartIds != null && !cartIds.isEmpty()) {
				param.put("cartIds", cartIds);
			}
		}
		if (addressId != null) {
			listParam = new ArrayList<String>();
			listParam.add(addressId + "");
			param.put("cid", listParam);
		}
		String rStr = URLUtil.getStringByPostMulti(this.prefix_url + this.order_inform_url, param);

		if (rStr != null && !"".equals(rStr)) {
			try {
				POrder pc = JSONUtil.getJson2Entity(rStr, POrder.class);
				if (pc != null){
					rvd.setOrderData(pc.getData());
					rvd.setCode(pc.getCode());
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
	 * 直接购买时，更改购买数量
	 * 
	 * @param gid
	 *            商品ID
	 * @param qty
	 *            数量
	 * @return
	 */
	public RtnValueDto updateOrderQty(Integer pid, short qty) {
		if(CodeValidator.isNetworkError()){
			return CodeValidator.getNetworkErrorRtnValueDto();
		}
		RtnValueDto rvd = new RtnValueDto();
		HashMap<String, String> param = new HashMap<String, String>();
		if (pid != null) {
			param.put("pid", String.valueOf(pid));
		}
		if (qty != 0) {
			param.put("qty", String.valueOf(qty));
		}
		String rStr = URLUtil.getStringByPost(this.prefix_url + this.order_updateQty_url, param);

		if (rStr != null && !"".equals(rStr)) {
			try {
				PProduct pc = JSONUtil.getJson2Entity(rStr, PProduct.class);
				if (pc != null){
					rvd.setCode(pc.getCode());
					rvd.setProductData(pc.getData());
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
	 * 确认订单后生成订单信息,pids与cartIds只传一种
	 * 
	 * @param pids
	 *            格式：pid_qty(如：12_1,12_2)
	 * @param cartIds
	 *            购物车ID列表
	 * @param addressId
	 *            地址ID
	 * @return
	 */
	public RtnValueDto confirmOrder(String pidQty, List<String> cartIds, Integer addressId) {
		if(CodeValidator.isNetworkError()){
			return CodeValidator.getNetworkErrorRtnValueDto();
		}
		RtnValueDto rvd = new RtnValueDto();
		List<String> listParam = new ArrayList<String>();
		HashMap<String, List<String>> param = new HashMap<String, List<String>>();
		if (StrUtils.isNotEmpty(pidQty)) {
			listParam.add(pidQty);
			param.put("pids", listParam);
		} else {
			if (cartIds != null && !cartIds.isEmpty()) {
				param.put("cartIds", cartIds);
			}
		}
		if (addressId != null) {
			listParam = new ArrayList<String>();
			listParam.add(addressId + "");
			param.put("cid", listParam);
		}
		String rStr = URLUtil.getStringByPostMulti(this.prefix_url + this.order_confirm_url, param);

		if (rStr != null && !"".equals(rStr)) {
			try {
				POrder pc = JSONUtil.getJson2Entity(rStr, POrder.class);
				if (pc != null){
					rvd.setCode(pc.getCode());
					rvd.setOrderData(pc.getData());
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
	 * 添加购物车
	 * 
	 * @param pid
	 * @param qty
	 * @return
	 */
	public RtnValueDto addCart(Integer pid, short qty) {
		if(CodeValidator.isNetworkError()){
			return CodeValidator.getNetworkErrorRtnValueDto();
		}
		RtnValueDto rvd = null;
		HashMap<String, String> param = new HashMap<String, String>();
		if (pid != null) {
			param.put("pid", String.valueOf(pid));
		}
		if (qty != 0) {
			param.put("qty", String.valueOf(qty));
		}
		String rvdString = URLUtil.getStringByPost(this.prefix_url + this.cart_add_url, param);
		if (rvdString != null) {
			PCartItemDetail pc = JSONUtil.getJson2Entity(rvdString, PCartItemDetail.class);
			if (pc != null && pc.getCode() == 200000) {
				rvd = new RtnValueDto();
				rvd.setCartData(pc.getData());
				rvd.setCode(200000);
			}
		}
		return rvd;
	}

	/**
	 * 移除出购物车
	 * 
	 * @param pid
	 * @param qty
	 * @return
	 */
	public RtnValueDto removeCart(Integer itemId) {
		if(CodeValidator.isNetworkError()){
			return CodeValidator.getNetworkErrorRtnValueDto();
		}
		RtnValueDto rvd = null;
		HashMap<String, String> param = new HashMap<String, String>();
		if (itemId != null) {
			param.put("id", String.valueOf(itemId));
		}
		String rvdString = URLUtil.getStringByPost(this.prefix_url + this.cart_remove_url, param);
		if (rvdString != null) {
			PCartItemDetail pc = JSONUtil.getJson2Entity(rvdString, PCartItemDetail.class);
			if (pc != null && pc.getCode() == 200000) {
				rvd = new RtnValueDto();
				rvd.setCartData(pc.getData());
				rvd.setCode(pc.getCode());
			}
		}
		return rvd;
	}

	/**
	 * 购物车中修改购买数量
	 * 
	 * @param pid
	 * @param qty
	 * @return
	 */
	public RtnValueDto updateCartQty(Integer itemId, short qty) {
		if(CodeValidator.isNetworkError()){
			return CodeValidator.getNetworkErrorRtnValueDto();
		}
		RtnValueDto rvd = null;
		HashMap<String, String> param = new HashMap<String, String>();
		if (itemId != null) {
			param.put("id", String.valueOf(itemId));
		}
		if (qty != 0) {
			param.put("qty", String.valueOf(qty));
		}
		String rvdString = URLUtil.getStringByPost(this.prefix_url + this.cart_setQty_url, param);
		if (rvdString != null) {
			PCartItemDetail pc = JSONUtil.getJson2Entity(rvdString, PCartItemDetail.class);
			if (pc != null && pc.getCode() == 200000) {
				rvd = new RtnValueDto();
				rvd.setCartData(pc.getData());
				rvd.setCode(pc.getCode());
			}
		}
		return rvd;
	}

	public RtnValueDto closeOrder(Integer orderId) {
		if(CodeValidator.isNetworkError()){
			return CodeValidator.getNetworkErrorRtnValueDto();
		}
		RtnValueDto rvd = null;
		HashMap<String, String> param = new HashMap<String, String>();
		if (orderId != null) {
			param.put("id", String.valueOf(orderId));
		}
		String rvdString = URLUtil.getStringByPost(this.prefix_url + this.order_close_url, param);
		if (rvdString != null) {
			rvd = JSONUtil.getJson2Entity(rvdString, RtnValueDto.class);
			rvd.setCode(200000);
		}
		return rvd;
	}
}

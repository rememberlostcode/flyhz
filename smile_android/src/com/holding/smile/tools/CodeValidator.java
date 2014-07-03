
package com.holding.smile.tools;

import android.content.Context;

import com.holding.smile.activity.MyApplication;
import com.holding.smile.dto.RtnValueDto;

/**
 * 
 * code验证工具类
 * 
 * 200000=操作成功 101001=收件人为空 101002=登陆用户为空 101003=收件人名称为空 101004=收件人姓名超过16个字符
 * 101005=收件人国家为空 101006=收件人省份为空 101007=收件人城市为空 101008=收件人县区为空 101009=收件人地址不能为空
 * 101010=收件人地址超过128个字符 101011=邮编为空 101012=邮编无效 101013=手机号为空 101014=手机号无效
 * 101015=待设置字段为空 101016=邮箱长度超过64个字符 101017=邮箱格式无效 101018=用户密码为空
 * 101019=用户密码超过64个字符 101020=身份证照片保存失败 101021=商品为空 101022=商品数量错误 101023=购物车商品为空
 * 101024=旧密码为空 101025=新密码为空 101026=输入旧密码不对 101027=旧密码超过64个字符 101028=新密码超过64个字符
 * 101029=用户邮箱不能为空
 * 
 * 201001=订单商品为空 201002=订单号不能为空 201003=订单为空 201004=关闭订单失败 400000=程序异常
 * 
 * 111110=操作对象为空 111111=主键ID为空 111112=款号为空 120001=品牌ID为空 120002=身份证照片为空
 * 122221=身份证照片保存出错 122222=身份证照片附件保存出错 130001=订单状态修改失败 130002=用户ID不能为空
 * 130003=订单ID不能为空 130004=订单内容不能为空 140001=用户名与密码不能为空 140002=用户名为空
 * 140003=用户名长度不能大于32个字符 140004=用户名已存在 140005=密码为空 140006=电子邮箱输入不正确
 * 140007=用户验证码不能为空
 * 
 * @author zhangb
 * 
 */
public class CodeValidator {
	private CodeValidator() {
	}

	/**
	 * 处理code并进行提示
	 * 
	 * @param context
	 * @param rtnValueDto
	 * @return 如果code等于200000则返回true
	 */
	public static boolean dealCode(Context context, RtnValueDto rtnValueDto) {
		boolean res = false;
		if (rtnValueDto == null) {// rtnValueDto等于null时可能是网络问题，需要检查网络
			if (MyApplication.isHasNetwork()) {
				ToastUtils.showShort(context, "暂无数据！");
			} else {
				ToastUtils.showShort(context, "网络异常，请检查网络！");
			}
		} else {
			if (rtnValueDto.getCode() == null) {
				ToastUtils.showShort(context, "程序异常！");
			} else {
				switch (rtnValueDto.getCode()) {
					case 200000:
						// 200000正常的，目前不需要任何操作
						res = true;
						break;
					case 101002:
						ToastUtils.showShort(context, "用户名不能为空 ！");
						break;
					case 101013:
						ToastUtils.showShort(context, "手机号不能为空 ！");
						break;
					case 101014:
						ToastUtils.showShort(context, "手机号无效！");
						break;
					case 101015:
						ToastUtils.showShort(context, "值不能为空！");
						break;
					case 101016:
						ToastUtils.showShort(context, "邮箱长度不能超过64个字符！");
						break;
					case 101017:
						ToastUtils.showShort(context, "邮箱格式无效！");
						break;
					case 101018:
						ToastUtils.showShort(context, "用户密码为空！");
						break;
					case 101019:
						ToastUtils.showShort(context, "用户密码超过限制长度！");
						break;
					case 101020:
						ToastUtils.showShort(context, "身份证照片保存失败！");
						break;
					case 101021:
						ToastUtils.showShort(context, "商品不能为空！");
						break;
					case 101022:
						ToastUtils.showShort(context, "商品数量错误 ！");
						break;
					case 101023:
						ToastUtils.showShort(context, "购物车商品为空！");
						break;
					case 101024:
						ToastUtils.showShort(context, "旧密码不能为空！");
						break;
					case 101025:
						ToastUtils.showShort(context, "新密码不能为空！");
						break;
					case 101026:
						ToastUtils.showShort(context, "密码错误！");
						break;
					case 101027:
						ToastUtils.showShort(context, "原密码超过长度限制 ！");
						break;
					case 101028:
						ToastUtils.showShort(context, "新密码超过长度限制 ！");
						break;
					case 101029:
						ToastUtils.showShort(context, "用户邮箱不能为空 ！");
						break;
					case 201001:
						ToastUtils.showShort(context, "订单商品不能为空！");
						break;
					case 201002:
						ToastUtils.showShort(context, "订单号不能为空 ！");
						break;
					case 201004:
						ToastUtils.showShort(context, "关闭订单失败，请稍候重试！");
						break;
					case 120002:
						ToastUtils.showShort(context, "身份证照片不能为空！");
						break;
					case 140001:
						ToastUtils.showShort(context, "用户名与密码不能为空！");
						break;
					case 140002:
						ToastUtils.showShort(context, "用户名为空！");
						break;
					case 140003:
						ToastUtils.showShort(context, "用户名长度不超过限制 ！");
						break;
					case 140004:
						ToastUtils.showShort(context, "用户名已存在 ！");
						break;
					case 140005:
						ToastUtils.showShort(context, "密码为空！");
						break;
					case 140006:
						ToastUtils.showShort(context, "电子邮箱输入不正确！");
						break;
					case 140007:
						ToastUtils.showShort(context, "用户验证码不能为空！");
						break;
					case 400000:
						ToastUtils.showShort(context, "等待淘宝订单查询中，请稍候！");
						break;
					case 400001:
						ToastUtils.showShort(context, "淘宝支付金额与订单金额不符，请联系客服！");
						break;
					case 888888:
						ToastUtils.showShort(context, "网络异常，请检查网络设置！");
						break;
					case 888889:
						ToastUtils.showShort(context, "连接超时，请稍候重试！");
						break;
					case 999999:
						ToastUtils.showShort(context, "连接异常，请稍候重试！");
						break;
					case 600001:
						// nodejs 缺少bid参数导致的
						ToastUtils.showShort(context, "未获取当前的品牌，请点击后退键，重新打开！");
						break;
					case 600002:
						// nodejs 缺少start参数导致的
						ToastUtils.showShort(context, "未获取当前的商品数量，请点击后退键，重新打开！");
						break;
					case 200001:
						ToastUtils.showShort(context, "操作失败，请稍候重试！");
						break;
					default:
						ToastUtils.showShort(context, "程序异常！");
						break;
				}
			}
		}
		return res;
	}

	/**
	 * 判断是否网络异常
	 * 
	 * @return
	 */
	public static boolean isNetworkError() {
		return !MyApplication.isHasNetwork();
	}

	/**
	 * 获得网络异常时的RtnValueDto
	 * 
	 * @return
	 */
	public static RtnValueDto getNetworkErrorRtnValueDto() {
		RtnValueDto rtnValueDto = new RtnValueDto();
		rtnValueDto.setCode(888888);
		return rtnValueDto;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}

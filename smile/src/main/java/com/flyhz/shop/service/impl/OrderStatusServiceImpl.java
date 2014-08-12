
package com.flyhz.shop.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.flyhz.framework.lang.JPush;
import com.flyhz.framework.lang.RedisRepository;
import com.flyhz.framework.lang.SolrData;
import com.flyhz.framework.lang.TaobaoData;
import com.flyhz.framework.lang.ValidateException;
import com.flyhz.framework.lang.mail.MailRepository;
import com.flyhz.framework.util.Constants;
import com.flyhz.shop.dto.OrderSimpleDto;
import com.flyhz.shop.dto.UserDto;
import com.flyhz.shop.persistence.dao.IdcardDao;
import com.flyhz.shop.persistence.dao.LogisticsDao;
import com.flyhz.shop.persistence.dao.OrderDao;
import com.flyhz.shop.persistence.dao.UserDao;
import com.flyhz.shop.persistence.entity.IdcardModel;
import com.flyhz.shop.persistence.entity.OrderModel;
import com.flyhz.shop.service.common.OrderStatusService;

@Service
public class OrderStatusServiceImpl implements OrderStatusService {
	private Logger			log	= LoggerFactory.getLogger(OrderStatusServiceImpl.class);

	@Resource
	private OrderDao		orderDao;
	@Resource
	private UserDao			userDao;
	@Resource
	private IdcardDao		idcardDao;
	@Resource
	private RedisRepository	redisRepository;
	@Resource
	private SolrData		solrData;
	@Resource
	private LogisticsDao	logisticsDao;
	@Resource
	private MailRepository	mailRepository;
	@Resource
	private TaobaoData		taobaoData;

	@Override
	public void closeOrderById(String[] numbers) {
		// TODO Auto-generated method stub
		try {
			for (int g = 0; g < numbers.length; g++) {
				if (StringUtils.isNotBlank(numbers[g])) {
					OrderModel orderModel = orderDao.getModelByNumber(numbers[g]);
					// 如果是等待发货才需要处理
					if (g == 0
							&& orderModel != null
							&& !orderModel.getStatus()
												.equals(Constants.OrderStateCode.HAVE_BEEN_CLOSED.code)) {
						redisRepository.reBuildOrderToRedis(orderModel.getUserId(), orderModel.getId(),
								Constants.OrderStateCode.HAVE_BEEN_CLOSED.code);
					}
				}
			}
		} catch (ValidateException e) {
			log.error(e.getMessage());
		}
	}

	@Override
	public void paymentValidateAmountAndIdcard(String[] numbers, BigDecimal payment,String taobaoReceiverName) {
		// TODO Auto-generated method stub
		try {
			List<OrderModel> ordersList = new ArrayList<OrderModel>();
			BigDecimal total = new BigDecimal(0);
			OrderModel orderModel = null;
			String smileStatus = Constants.OrderStateCode.HAVE_BEEN_PAID.code;

			// 买家已付款，需要验证身份证是否存在
			IdcardModel idcardModel = new IdcardModel();
			
			for (int g = 0; g < numbers.length; g++) {
				if (StringUtils.isNotBlank(numbers[g])) {
					orderModel = orderDao.getModelByNumber(numbers[g]);
					if (orderModel != null) {
						// 累加订单总额
						total = total.add(orderModel.getTotal());

						if (idcardModel.getUserId() == null) {
							idcardModel.setUserId(orderModel.getUserId());
						}

						// 添加到集合，后面统一更新状态
						ordersList.add(orderModel);
					}
				}
			}
			
			//判断金额是否相等
			if (total.equals(payment)) {
//				smileStatus = Constants.OrderStateCode.HAVE_BEEN_PAID.code;// 金额相等，状态设置成“已付款”
				// 缺失身份证
				smileStatus = Constants.OrderStateCode.THE_LACK_OF_IDENTITY_CARD.code;

				List<IdcardModel> list = idcardDao.getModelList(idcardModel);
				if (list == null || list.size() == 0) {
				} else {
					for (int k = 0; k < list.size(); k++) {
						log.info(taobaoReceiverName + "===?" + list.get(k).getName());
						if (list.get(k) != null && taobaoReceiverName.equals(list.get(k).getName())) {
							// 存在相同名字的身份证，状态改为“等待发货”
							smileStatus = Constants.OrderStateCode.WAITING_FOR_DELIVERY.code;
							break;
						}
					}
				}

				// 循环更新数据库订单状态
				for (int i = 0; i < ordersList.size(); i++) {
					
					ordersList.get(i).setStatus(smileStatus);
					
					orderDao.updateStatusByNumber(ordersList.get(i));
					redisRepository.reBuildOrderToRedis(ordersList.get(i).getUserId(),
							ordersList.get(i).getId(), ordersList.get(i).getStatus());
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

	@Override
	public void sendGoods(String[] numbers) {
		// TODO Auto-generated method stub
		OrderModel orderModel = null;
		for (int g = 0; g < numbers.length; g++) {
			if (StringUtils.isNotBlank(numbers[g])) {

				orderModel = new OrderModel();
				orderModel.setNumber(numbers[g]);
				orderModel.setStatus(Constants.OrderStateCode.SHIPPED_ABROAD_CLEARANCE.code);

				OrderSimpleDto orderDto = orderDao.getOrderByNumber(orderModel.getNumber());

				if (orderDto != null) {

					orderDao.updateStatusByNumber(orderModel);
					solrData.submitOrder(orderDto.getUserId(), orderDto.getId(),
							orderModel.getStatus(), new Date(), null);

					UserDto user = userDao.getUserById(orderDto.getUserId());

					if (Constants.OrderStateCode.HAVE_BEEN_PAID.code.equals(orderModel.getStatus())) {// 已付款的发送邮件
						// 用户邮箱存在则发送邮件
						if (user != null && StringUtils.isNotBlank(user.getEmail())) {
							Map<String, Object> modelMap = new HashMap<String, Object>();
							modelMap.put("orderId", orderModel.getNumber());
							modelMap.put("total", orderModel.getTotal());
							modelMap.put("username", user.getUsername());
							mailRepository.sendWithTemplate(user.getEmail(), "订单支付成功",
									"velocity/mailvm/pay_success_mail.vm", modelMap);
						}
					} else if (Constants.OrderStateCode.THE_LACK_OF_IDENTITY_CARD.code.equals(orderModel.getStatus())) {// 缺少身份证的发送消息
						// 获取用户信息并得到registrationID发送通知
						if (user != null && user.getId() != null
								&& user.getRegistrationID() != null) {
							JPush jpush = new JPush();
							Map<String, String> extras = new HashMap<String, String>();
							extras.put("orderId", orderDto.getId().toString());
							jpush.sendAndroidNotificationWithRegistrationID(
									"由于海关需要，您的订单收件人缺少必要身份证，您需要上传！", extras,
									user.getRegistrationID());
						}
					} else if (Constants.OrderStateCode.SHIPPED_ABROAD_CLEARANCE.code.equals(orderModel.getStatus())) {// 已发货的发送消息
						// 获取用户信息并得到registrationID发送通知
						if (user != null && user.getId() != null
								&& user.getRegistrationID() != null) {
							JPush jpush = new JPush();
							Map<String, String> extras = new HashMap<String, String>();
							extras.put("orderId", orderDto.getId().toString());
							jpush.sendAndroidNotificationWithRegistrationID("您的订单已发货！", extras,
									user.getRegistrationID());
						}
					}
				}
			}
		}
	}

	@Override
	public void receiveGoods(String[] numbers) {
		// TODO Auto-generated method stub

	}

}

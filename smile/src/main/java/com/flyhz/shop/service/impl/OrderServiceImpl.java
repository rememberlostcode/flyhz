
package com.flyhz.shop.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
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
import com.flyhz.framework.util.DateUtil;
import com.flyhz.framework.util.JSONUtil;
import com.flyhz.framework.util.RandomString;
import com.flyhz.framework.util.StringUtil;
import com.flyhz.shop.dto.ConsigneeDetailDto;
import com.flyhz.shop.dto.IdentitycardDto;
import com.flyhz.shop.dto.LogisticsDto;
import com.flyhz.shop.dto.OrderDetailDto;
import com.flyhz.shop.dto.OrderDto;
import com.flyhz.shop.dto.OrderPayDto;
import com.flyhz.shop.dto.OrderSimpleDto;
import com.flyhz.shop.dto.ProductDto;
import com.flyhz.shop.dto.UserDto;
import com.flyhz.shop.dto.VoucherDto;
import com.flyhz.shop.persistence.dao.ConsigneeDao;
import com.flyhz.shop.persistence.dao.IdcardDao;
import com.flyhz.shop.persistence.dao.LogisticsDao;
import com.flyhz.shop.persistence.dao.OrderDao;
import com.flyhz.shop.persistence.dao.ProductDao;
import com.flyhz.shop.persistence.dao.UserDao;
import com.flyhz.shop.persistence.entity.ConsigneeModel;
import com.flyhz.shop.persistence.entity.IdcardModel;
import com.flyhz.shop.persistence.entity.LogisticsModel;
import com.flyhz.shop.persistence.entity.OrderModel;
import com.flyhz.shop.persistence.entity.UserModel;
import com.flyhz.shop.service.OrderService;

@Service
public class OrderServiceImpl implements OrderService {
	private Logger			log	= LoggerFactory.getLogger(OrderServiceImpl.class);
	@Resource
	private OrderDao		orderDao;
	@Resource
	private UserDao			userDao;
	@Resource
	private IdcardDao		idcardDao;
	@Resource
	private ConsigneeDao	consigneeDao;
	@Resource
	private ProductDao		productDao;
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
	public OrderDto generateOrder(Integer userId, Integer consigneeId, String[] productIds,
			boolean flag) throws ValidateException {
		if (userId == null)
			throw new ValidateException(101002);
		if (productIds == null || productIds.length <= 0)
			throw new ValidateException(101021);

		UserDto user = userDao.getUserById(userId);
		if (user == null)
			throw new ValidateException(101002);

		ConsigneeModel consignee = new ConsigneeModel();
		consignee.setId(consigneeId);
		consignee.setUserId(userId);
		ConsigneeDetailDto consigneeDto = consigneeDao.getConsigneeByModel(consignee);
		if (consigneeDto != null) {
			consigneeDto.setUser(user);
			IdcardModel idcard = new IdcardModel();
			idcard.setUserId(userId);
			idcard.setName(consigneeDto.getName());
			IdcardModel idcardData = idcardDao.getModelByName(idcard);
			if (idcardData != null) {
				IdentitycardDto identitycard = new IdentitycardDto();
				identitycard.setName(idcardData.getName());
				identitycard.setNumber(idcardData.getNumber());
				identitycard.setUrl(idcardData.getUrl());
				consigneeDto.setIdentitycard(identitycard);
			}
		}

		// 处理商品信息
		List<OrderDetailDto> orderDetails = new ArrayList<OrderDetailDto>();
		BigDecimal total = new BigDecimal(0);
		BigDecimal logisticsPriceTotal = new BigDecimal(0);
		int allqty = 0;
		for (String pidstr : productIds) {
			if (StringUtils.isBlank(pidstr))
				continue;
			try {
				String[] pid_qty = pidstr.split("_");// 格式是pid_qty,如：1_2
				if (pid_qty.length != 2)
					continue;
				int qty = Integer.parseInt(pid_qty[1]);
				if (qty <= 0)
					continue;

				ProductDto product = redisRepository.getProductFromRedis(String.valueOf(pid_qty[0]));
				if (product != null) {
					OrderDetailDto orderDetailDto = new OrderDetailDto();
					orderDetailDto.setProduct(product);
					orderDetailDto.setQty((short) qty);
					if (product.getPurchasingPrice() != null) {
						BigDecimal detailTotal = product.getPurchasingPrice().multiply(
								BigDecimal.valueOf(qty));
						orderDetailDto.setTotal(detailTotal);
						allqty += qty;
						total = total.add(detailTotal);
						
						//处理邮费
						orderDetailDto.setLogisticsPriceEvery(Constants.logisticsPriceEvery);//每种商品单个物流费用
						orderDetailDto.setLogisticsPriceTotal(Constants.logisticsPriceEvery.multiply(
								BigDecimal.valueOf(qty)));//每种商品总有物流费用
						logisticsPriceTotal = logisticsPriceTotal.add(orderDetailDto.getLogisticsPriceTotal());//累加订单总物流费用
					}
					orderDetails.add(orderDetailDto);
				}
			} catch (Exception e) {
				log.error("生成订单出错：", e.getMessage());
				continue;
			}
		}

		if (orderDetails.isEmpty())
			throw new ValidateException(201001);

		Date date = new Date();
		OrderDto orderDto = new OrderDto();
		orderDto.setDetails(orderDetails);
		orderDto.setConsignee(consigneeDto);
		
		orderDto.setLogisticsPriceTotal(logisticsPriceTotal);
		total = total.add(logisticsPriceTotal);//总价加上物流费用
		
		orderDto.setTotal(total);
		orderDto.setQty(allqty);
		orderDto.setUser(user);
		orderDto.setTime(DateUtil.dateToStr(date));

		// 优惠卷
		List<VoucherDto> vouchers = null;
		orderDto.setVouchers(vouchers);
		String detail = null;

		String number = null;
		if (flag) {
			number = RandomString.generateRandomStringTime();
			orderDto.setNumber(number);
		}

		if (flag) {
			OrderModel order = new OrderModel();
			order.setNumber(number);
			order.setUserId(userId);
			order.setStatus(Constants.OrderStateCode.FOR_PAYMENT.code);// 默认待付款
			detail = JSONUtil.getEntity2Json(orderDto);
			order.setDetail(detail);
			order.setTotal(total);
			order.setGmtCreate(date);
			order.setGmtModify(date);
			orderDao.generateOrder(order);
			log.debug("====={}", order.getId());
			orderDto.setId(order.getId());
			orderDto.setStatus(order.getStatus());
			redisRepository.buildOrderToRedis(userId, orderDto.getId(), order.getStatus(),
					order.getGmtModify(), JSONUtil.getEntity2Json(orderDto));

		}
		return orderDto;
	}

	@Override
	public String getOrder(Integer userId, Integer orderId) throws ValidateException {
		return redisRepository.getOrderFromRedis(userId, orderId);
	}

	@Override
	public List<OrderDto> listOrders(Integer userId, String status) throws ValidateException {
		if (userId == null)
			throw new ValidateException(101002);
		List<OrderDto> orderDtoList = new ArrayList<OrderDto>();
		String json = null;
		List<OrderSimpleDto> idsList = solrData.getOrderIdsFromSolr(userId, status);
		for (OrderSimpleDto order : idsList) {
			json = getOrder(userId, order.getId());
			if (json != null && StringUtil.isNotBlank(json)) {
				OrderDto orderDto = JSONUtil.getJson2Entity(json, OrderDto.class);
				if (orderDto != null) {
					orderDto.setStatus(order.getStatus());// 把订单的状态塞入dto
					orderDto.setLogisticsDto(order.getLogisticsDto());
					orderDtoList.add(orderDto);
				}
			}
		}
		return orderDtoList;
	}

	@Override
	public boolean pay(Integer userId, String number) throws ValidateException {
		if (userId == null)
			throw new ValidateException(101002);
		if (StringUtils.isBlank(number))
			throw new ValidateException(201002);
		boolean flag = false;
		OrderModel orderModel = new OrderModel();
		orderModel.setNumber(number);
		orderModel.setUserId(userId);
		orderModel = orderDao.getModel(orderModel);
		if (orderModel != null && orderModel.getStatus() != null
				&& Constants.OrderStateCode.HAVE_BEEN_PAID.code.equals(orderModel.getStatus())) {// 表示已付款
			flag = true;
			redisRepository.reBuildOrderToRedis(userId, orderModel.getId(),
					Constants.OrderStateCode.HAVE_BEEN_PAID.code);
		}
		return flag;
	}

	public void closeOrder(Integer userId, Integer id) throws ValidateException {
		if (userId == null)
			throw new ValidateException(101002);
		if (id == null)
			throw new ValidateException(201003);
		String status = Constants.OrderStateCode.HAVE_BEEN_CLOSED.code;
		OrderModel orderModel = new OrderModel();
		orderModel.setId(id);
		orderModel.setUserId(userId);
		orderModel.setStatus(status);
		orderModel.setGmtModify(new Date());
		int num = orderDao.update(orderModel);
		if (num == 1) {
			redisRepository.reBuildOrderToRedis(userId, orderModel.getId(), status);
		} else {
			throw new ValidateException(201004);
		}
	}

	@Override
	public OrderPayDto getOrderPay(OrderPayDto orderPayDto) {
		if (orderPayDto == null || orderPayDto.getUserId() == null
				|| (orderPayDto.getId() == null && StringUtils.isBlank(orderPayDto.getNumber())))
			return null;
		return orderDao.getOrderPay(orderPayDto);
	}

	public OrderSimpleDto getOrderDtoByNumber(String number) {
		OrderSimpleDto orderDto = orderDao.getOrderByNumber(number);
		if (orderDto != null) {
			LogisticsModel logisticsModel = logisticsDao.getLogisticsByOrderNumber(number);
			if (logisticsModel != null) {
				LogisticsDto logisticsDto = new LogisticsDto();
				logisticsDto.setCompanyName(logisticsModel.getCompanyName());
				logisticsDto.setLogisticsStatus(logisticsModel.getLogisticsStatus());
				logisticsDto.setTid(logisticsModel.getTid());
				if (StringUtils.isNotBlank(logisticsModel.getContent())) {
					String[] lls = logisticsModel.getContent().split("@#@");
					logisticsDto.setTransitStepInfoList(Arrays.asList(lls));
				}
				orderDto.setLogisticsDto(logisticsDto);
			}
		}
		return orderDto;
	}

	public void sendPaySuccess(String number) {
		if (StringUtils.isNotBlank(number)) {
			OrderModel orderModel = new OrderModel();
			orderModel.setNumber(number);
			orderModel = orderDao.getModel(orderModel);
			if (orderModel != null) {
				UserModel userModel = userDao.getModelById(orderModel.getUserId());
				// 用户邮箱存在则发送邮件
				if (userModel != null && StringUtils.isNotBlank(userModel.getEmail())) {
					Map<String, Object> modelMap = new HashMap<String, Object>();
					modelMap.put("orderId", orderModel.getNumber());
					modelMap.put("total", orderModel.getTotal());
					modelMap.put("username", userModel.getUsername());
					mailRepository.sendWithTemplate(userModel.getEmail(), "订单支付成功",
							"velocity/mailvm/pay_success_mail.vm", modelMap);
				}
			}
		}
	}

	public String getOrderPayStatusByTid(String numbers, Long tid) throws ValidateException {
		if(numbers==null || StringUtils.isBlank(numbers)){
			throw new ValidateException(201002);
		}
		if(tid == null){
			throw new ValidateException(700001);
		}
		
		List<OrderModel> ordersList = new ArrayList<OrderModel>();
		String[] number = numbers.replaceAll(" ", "").split(",");
		String smileStatus = "10";
		
//		Integer userId = null;
//		BigDecimal total = new BigDecimal(0);
		for (int i = 0; i < number.length; i++) {
			OrderModel orderModel = orderDao.getModelByNumber(number[i]);

			if (orderModel != null) {
				//如果任一订单是已付款后面的状态，直接返回状态
				if (Constants.OrderStateCode.HAVE_BEEN_PAID.code.equals(orderModel.getStatus())
						|| Constants.OrderStateCode.SHIPPED_ABROAD_CLEARANCE.code.equals(orderModel.getStatus())
						|| Constants.OrderStateCode.HAS_BEEN_COMPLETED.code.equals(orderModel.getStatus())
						|| Constants.OrderStateCode.HAVE_BEEN_CLOSED.code.equals(orderModel.getStatus())) {// 如果状态已经是已付款/卖家已发货则直接返回状态
					return orderModel.getStatus();
				}// mysql显示未付款时，需要调用淘宝接口查看
				
//				if (userId == null) {
//					userId = orderModel.getUserId();
//				}
				
//				//累加订单总额
//				total = total.add(orderModel.getTotal());
//				log.info("total = "+ total);

//				//最后一单，处理查看淘宝的订单信息
//				if (i == number.length - 1) {
//					Trade trade = taobaoData.getTradeByTid(tid);
//					if (trade == null) {
//						throw new ValidateException(400000);
//					} else {
//						String status = trade.getStatus();
//						BigDecimal payment = new BigDecimal(trade.getPayment());
//						log.info("淘宝状态：" + status);
//						if (total.equals(payment)) {
//							if ("WAIT_BUYER_PAY".equals(status)) {// 等待买家付款
//								// 未付款
//								smileStatus = Constants.OrderStateCode.FOR_PAYMENT.code;
//							} else if ("WAIT_SELLER_SEND_GOODS".equals(status)) {// 等待卖家发货,即:买家已付款
//								// 已付款
//								smileStatus = Constants.OrderStateCode.HAVE_BEEN_PAID.code;
//								String taobaoReceiverName = taobaoData.getReceiverName(tid);
//								if(StringUtils.isBlank(taobaoReceiverName)){
//									throw new ValidateException(700002);
//								}
//								// 买家已付款，需要验证身份证是否存在
//								IdcardModel im = new IdcardModel();
//								im.setUserId(userId);
//								List<IdcardModel> list = idcardDao.getModelList(im);
//								// 缺失身份证
//								smileStatus = Constants.OrderStateCode.THE_LACK_OF_IDENTITY_CARD.code;
//								if (list == null || list.size() == 0) {
//								} else {
//									for(int k=0;k<list.size();k++){
//										if(list.get(k)!=null && StringUtils.isNotBlank(list.get(k).getName()) 
//												&& taobaoReceiverName.equals(list.get(k).getName())){
//											// 存在相同名字的身份证，状态改为“等待发货”
//											smileStatus = Constants.OrderStateCode.WAITING_FOR_DELIVERY.code;
//											break;
//										}
//									}
//								}
//							} else if ("WAIT_BUYER_CONFIRM_GOODS".equals(status)) {// 等待买家确认收货,即:卖家已发货
//								// 已发货
//								smileStatus = Constants.OrderStateCode.SHIPPED_ABROAD_CLEARANCE.code;
//							} else if ("TRADE_FINISHED".equals(status)) {// 交易成功
//								smileStatus = Constants.OrderStateCode.HAS_BEEN_COMPLETED.code;
//							} else {
//								// 未知状态
//								log.error("淘宝是暂不处理的状态：" + status);
//								throw new ValidateException(400000);
//							}
//						} else {
//							log.error("淘宝订单价格和smile系统订单价格不相等" + payment + "?=" + total);
//							throw new ValidateException(400001);
//						}
//					}
//				}//淘宝结束
			}
			
			//设置状态
//			orderModel.setStatus(smileStatus);
//			ordersList.add(orderModel);
		}
		
		//循环更新数据库订单状态
		for(int i=0;i<ordersList.size();i++){
			orderDao.updateStatusByNumber(ordersList.get(i));
		}
		
		return smileStatus;
	}

	public void updateStatusByNumber(OrderModel orderModel) {
		log.info("定时器改变订单" + orderModel.getNumber() + "状态为" + orderModel.getStatus());
		orderDao.updateStatusByNumber(orderModel);
	}

	public void updateStatusByNumberForMessage(OrderModel orderModel) {
		log.info("淘宝消息改变订单" + orderModel.getNumber() + "状态为" + orderModel.getStatus());
		OrderSimpleDto orderDto = orderDao.getOrderByNumber(orderModel.getNumber());
		if (orderDto != null) {
			orderDao.updateStatusByNumber(orderModel);
			// 更新订单的状态（以及有物流信息的时更新物流）
			solrData.submitOrder(orderDto.getUserId(), orderDto.getId(), orderModel.getStatus(),
					new Date(), null);
			if (Constants.OrderStateCode.HAVE_BEEN_PAID.code.equals(orderModel.getStatus())) {// 已付款的发送邮件
				sendPaySuccess(orderModel.getNumber());
			} else if (Constants.OrderStateCode.THE_LACK_OF_IDENTITY_CARD.code.equals(orderModel.getStatus())) {// 缺少身份证的发送消息
				// 获取用户信息并得到registrationID发送通知
				UserDto user = userDao.getUserById(orderDto.getUserId());
				if (user != null && user.getId() != null && user.getRegistrationID() != null) {
					JPush jpush = new JPush();
					Map<String, String> extras = new HashMap<String, String>();
					extras.put("orderId", orderDto.getId().toString());
					jpush.sendAndroidNotificationWithRegistrationID("由于海关需要，您的订单收件人缺少必要身份证，您需要上传！", extras,
							user.getRegistrationID());
				}
			} else if (Constants.OrderStateCode.SHIPPED_ABROAD_CLEARANCE.code.equals(orderModel.getStatus())) {// 已发货的发送消息
				// 获取用户信息并得到registrationID发送通知
				UserDto user = userDao.getUserById(orderDto.getUserId());
				if (user != null && user.getId() != null && user.getRegistrationID() != null) {
					JPush jpush = new JPush();
					Map<String, String> extras = new HashMap<String, String>();
					extras.put("orderId", orderDto.getId().toString());
					jpush.sendAndroidNotificationWithRegistrationID("您的订单已发货！", extras,
							user.getRegistrationID());
				}
			}
		}
	}

	// /**
	// * 根据订单编号获取mysql数据库物流信息
	// * @param orderNumber
	// * @return
	// */
	// private LogisticsDto getLogisticsDto(String orderNumber){
	// LogisticsModel logisticsModel =
	// logisticsDao.getLogisticsByOrderNumber(orderNumber);
	// LogisticsDto logisticsDto = null;
	// if (logisticsModel != null) {
	// logisticsDto = new LogisticsDto();
	// log.info("淘宝订单" + logisticsModel.getTid() + "已有物流信息:" +
	// logisticsModel.getContent());
	// logisticsDto.setId(logisticsModel.getId());
	// logisticsDto.setCompanyName(logisticsModel.getCompanyName());
	// logisticsDto.setLogisticsStatus(logisticsModel.getLogisticsStatus());
	// logisticsDto.setTid(logisticsModel.getTid());
	// logisticsDto.setAddress(logisticsModel.getAddress());
	// if (StringUtils.isNotBlank(logisticsModel.getContent())) {
	// String[] lls = logisticsModel.getContent().split("@#@");
	// logisticsDto.setTransitStepInfoList(Arrays.asList(lls));
	// }
	// }
	// return logisticsDto;
	// }
}
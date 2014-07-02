
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
import com.taobao.api.domain.Trade;

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
			redisRepository.buildOrderToRedis(userId, orderDto.getId(),
					JSONUtil.getEntity2Json(orderDto));

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

	public String getOrderPayStatusByTid(Integer orderId, Long tid) throws ValidateException {
		if(orderId==null){
			throw new ValidateException(201002);
		}
		if(tid == null){
			throw new ValidateException(700001);
		}
		OrderModel orderModel = orderDao.getModelById(orderId);
		String smileStatus = "10";
		if (Constants.OrderStateCode.HAVE_BEEN_PAID.code.equals(orderModel.getStatus())
				|| Constants.OrderStateCode.SHIPPED_ABROAD_CLEARANCE.code.equals(orderModel.getStatus())
				|| Constants.OrderStateCode.HAS_BEEN_COMPLETED.code.equals(orderModel.getStatus())
				|| Constants.OrderStateCode.HAVE_BEEN_CLOSED.code.equals(orderModel.getStatus())) {// 如果状态已经是已付款/卖家已发货则直接返回状态
			return orderModel.getStatus();
		} else {// mysql显示未付款时，需要调用淘宝接口查看
			Trade trade = taobaoData.getTradeByTid(tid);
			if (trade == null) {
				throw new ValidateException(400000);
			} else {
				BigDecimal Payment = new BigDecimal(trade.getPayment());
				if(orderModel.getTotal().equals(Payment)){
					String status = trade.getStatus();
					if ("WAIT_BUYER_PAY".equals(status)) {// 等待买家付款
						// 未付款
						smileStatus = Constants.OrderStateCode.FOR_PAYMENT.code;
					} else if ("WAIT_SELLER_SEND_GOODS".equals(status)) {// 等待卖家发货,即:买家已付款
						// 已付款
						smileStatus = Constants.OrderStateCode.HAVE_BEEN_PAID.code;
	
						// 买家已付款，需要验证身份证是否存在
						IdcardModel im = new IdcardModel();
						im.setUserId(orderModel.getUserId());
						List<IdcardModel> list = idcardDao.getModelList(im);
						if (list == null || list.size() == 0) {
							// 缺失身份证
							smileStatus = Constants.OrderStateCode.THE_LACK_OF_IDENTITY_CARD.code;
						} else {
							// 等待发货
							smileStatus = Constants.OrderStateCode.WAITING_FOR_DELIVERY.code;
						}
					} else if ("WAIT_BUYER_CONFIRM_GOODS".equals(status)) {// 等待买家确认收货,即:卖家已发货
						// 已发货
						smileStatus = Constants.OrderStateCode.SHIPPED_ABROAD_CLEARANCE.code;
					} else if ("TRADE_FINISHED".equals(status)) {// 交易成功
						smileStatus = Constants.OrderStateCode.HAS_BEEN_COMPLETED.code;
					} else {
						// 未知状态
						throw new ValidateException(400000);
					}
				} else {
					throw new ValidateException(400001);
				}
			}
		}
		orderModel.setStatus(smileStatus);
		orderDao.updateStatusByNumber(orderModel);
		return smileStatus;
	}

	public void updateStatusByNumber(OrderModel orderModel) {
		orderDao.updateStatusByNumber(orderModel);
		if (Constants.OrderStateCode.HAVE_BEEN_PAID.code.equals(orderModel.getStatus())) {// 已付款的发送邮件
			sendPaySuccess(orderModel.getNumber());
		}
	}
}
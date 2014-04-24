
package com.flyhz.shop.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.flyhz.framework.lang.RedisRepository;
import com.flyhz.framework.lang.SolrData;
import com.flyhz.framework.lang.ValidateException;
import com.flyhz.framework.util.JSONUtil;
import com.flyhz.framework.util.RandomString;
import com.flyhz.framework.util.StringUtil;
import com.flyhz.shop.dto.ConsigneeDetailDto;
import com.flyhz.shop.dto.OrderDetailDto;
import com.flyhz.shop.dto.OrderDto;
import com.flyhz.shop.dto.OrderPayDto;
import com.flyhz.shop.dto.ProductDto;
import com.flyhz.shop.dto.UserDto;
import com.flyhz.shop.dto.VoucherDto;
import com.flyhz.shop.persistence.dao.ConsigneeDao;
import com.flyhz.shop.persistence.dao.OrderDao;
import com.flyhz.shop.persistence.dao.ProductDao;
import com.flyhz.shop.persistence.dao.UserDao;
import com.flyhz.shop.persistence.entity.ConsigneeModel;
import com.flyhz.shop.persistence.entity.OrderModel;
import com.flyhz.shop.service.OrderService;

@Service
public class OrderServiceImpl implements OrderService {
	private Logger			log	= LoggerFactory.getLogger(OrderServiceImpl.class);
	@Resource
	private OrderDao		orderDao;
	@Resource
	private UserDao			userDao;
	@Resource
	private ConsigneeDao	consigneeDao;
	@Resource
	private ProductDao		productDao;
	@Resource
	private RedisRepository	redisRepository;
	@Resource
	private SolrData		solrData;

	@Override
	public OrderDto generateOrder(Integer userId, Integer consigneeId, String[] productIds,
			boolean flag) throws ValidateException {
		if (userId == null)
			throw new ValidateException("您没有登录！");
		if (productIds == null || productIds.length <= 0)
			throw new ValidateException("产品ID为空！");

		UserDto user = userDao.getUserById(userId);
		if (user == null)
			throw new ValidateException("您还不是会员，请注册后再购买！");

		ConsigneeModel consignee = new ConsigneeModel();
		consignee.setId(consigneeId);
		consignee.setUserId(userId);
		ConsigneeDetailDto consigneeDto = consigneeDao.getConsigneeByModel(consignee);
		if (flag) {
			if (consigneeDto == null)
				throw new ValidateException("收件人地址为空！");
		}
		if (consigneeDto != null)
			consigneeDto.setUser(user);

		// 处理商品信息
		List<OrderDetailDto> orderDetails = new ArrayList<OrderDetailDto>();
		BigDecimal total = new BigDecimal(0);
		for (String pidstr : productIds) {
			if (StringUtils.isBlank(pidstr))
				continue;
			try {
				String[] pid_qty = pidstr.split("_");// 格式是pid_qty,如：1_2
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
			throw new ValidateException("产品为空！");

		OrderDto orderDto = new OrderDto();
		orderDto.setDetails(orderDetails);
		orderDto.setConsignee(consigneeDto);
		orderDto.setTotal(total);
		orderDto.setUser(user);

		// 优惠卷
		List<VoucherDto> vouchers = null;
		orderDto.setVouchers(vouchers);
		String detail = null;

		String number = null;
		if (flag) {
			number = RandomString.generateRandomString8();
			orderDto.setNumber(number);
		}

		if (flag) {
			OrderModel order = new OrderModel();
			order.setNumber(number);
			order.setUserId(userId);
			order.setStatus("0");// 0表示待支付，1表示支付，2表示关闭
			detail = JSONUtil.getEntity2Json(orderDto);
			order.setDetail(detail);
			order.setTotal(total);
			Date date = new Date();
			order.setGmtCreate(date);
			order.setGmtModify(date);
			orderDao.generateOrder(order);
			log.debug("====={}", order.getId());
			orderDto.setId(order.getId());
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
		List<OrderDto> orderDtoList = new ArrayList<OrderDto>();
		String json = null;
		List<Integer> idsList = solrData.getOrderIdsFromSolr(userId, status);
		for (Integer orderId : idsList) {
			json = getOrder(userId, orderId);
			if (json != null && StringUtil.isNotBlank(json)) {
				OrderDto orderDto = JSONUtil.getJson2Entity(json, OrderDto.class);
				if (orderDto != null) {
					orderDtoList.add(orderDto);
				}
			}
		}
		return orderDtoList;
	}

	@Override
	public boolean pay(Integer userId, String number) throws ValidateException {
		if (userId == null)
			throw new ValidateException("您没有登录！");
		if (StringUtils.isBlank(number))
			throw new ValidateException("订单ID不能为空！");
		boolean flag = false;
		OrderModel orderModel = new OrderModel();
		orderModel.setNumber(number);
		orderModel.setUserId(userId);
		orderModel = orderDao.getModel(orderModel);
		if (orderModel != null && orderModel.getStatus() != null
				&& "1".equals(orderModel.getStatus())) {// 表示已付款
			flag = true;
			redisRepository.reBuildOrderToRedis(userId, orderModel.getId());
		}
		return flag;
	}

	@Override
	public OrderPayDto getOrderPay(OrderPayDto orderPayDto) {
		if (orderPayDto == null || orderPayDto.getUserId() == null || orderPayDto.getId() == null)
			return null;
		return orderDao.getOrderPay(orderPayDto);
	}

}
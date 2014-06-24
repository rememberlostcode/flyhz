
package com.flyhz.shop.persistence;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;

import com.flyhz.framework.lang.SolrData;
import com.flyhz.framework.lang.TaobaoData;
import com.flyhz.framework.util.Constants;
import com.flyhz.framework.util.TaobaoTokenUtil;
import com.flyhz.shop.dto.LogisticsDto;
import com.flyhz.shop.dto.OrderSimpleDto;
import com.flyhz.shop.persistence.dao.LogisticsDao;
import com.flyhz.shop.persistence.dao.OrderDao;
import com.flyhz.shop.persistence.entity.OrderModel;
import com.flyhz.shop.service.OrderService;
import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.domain.Trade;
import com.taobao.api.domain.TransitStepInfo;
import com.taobao.api.request.LogisticsTraceSearchRequest;
import com.taobao.api.request.TradeGetRequest;
import com.taobao.api.request.TradesSoldGetRequest;
import com.taobao.api.response.LogisticsTraceSearchResponse;
import com.taobao.api.response.TradeGetResponse;
import com.taobao.api.response.TradesSoldGetResponse;

public class TaobaoDataImpl implements TaobaoData {
	private final String	url	= "http://gw.api.taobao.com/router/rest";
	private String			appkey;
	private String			secret;
	private String			sessionKey;
	private String			sellerNick;
	private static String					propertiesFilePath	= "C:/Users/silvermoon/taobao.properties";

	@Resource
	@Value(value = "${smile.taobao.file}")
	private String			taobaoPropertiesFilePath;
	@Resource
	@Value(value = "${smile.taobao.flag}")
	private String			taobaoFlag;

	@Resource
	private SolrData		solrData;
	@Resource
	private OrderService	orderService;
	@Resource
	private OrderDao		orderDao;
	@Resource
	private LogisticsDao	logisticsDao;
	
	private final Long PAGE_SIZE = 100L;

	/**
	 * 初始化参数	
	 */
	private void init() {
		if("1".equals(taobaoFlag)){//判断是否打开淘宝接口调用
			propertiesFilePath = taobaoPropertiesFilePath;
			appkey = TaobaoTokenUtil.getAppKey();
			secret = TaobaoTokenUtil.getAppSecret();
			sessionKey = TaobaoTokenUtil.getAccessToken();
			sellerNick = TaobaoTokenUtil.getSellerNick();
		}
	}

	public void synchronizationLogistics() {
		if("1".equals(taobaoFlag)){
			init();
			
			Long page = 1L;
			Long totalPage = 1L;

			while (page <= totalPage) {
				System.out.println("第" + page + "页");
				TaobaoClient client = new DefaultTaobaoClient(url, appkey, secret);
				TradesSoldGetRequest req = new TradesSoldGetRequest();
				req.setFields("status,tid,receiver_address,receiver_city,receiver_district,receiver_mobile,receiver_name,receiver_state,receiver_zip");
				req.setPageSize(PAGE_SIZE);
		
				try {
					TradesSoldGetResponse response = client.execute(req, sessionKey);
					List<Trade> trades = response.getTrades();
					totalPage = response.getTotalResults()/PAGE_SIZE + 1L;
		
					boolean isAdd = true;
					String buyerMessage = null;
					String[] numbers = null;
					// for (int i = 0; shippings != null && i < shippings.size(); i++) {
					// Long tid = shippings.get(i).getTid();
					for (int i = 0; trades != null && i < trades.size(); i++) {
						Long tid = trades.get(i).getTid();
						String status = trades.get(i).getStatus();
						// 先获取买家留言
						buyerMessage = getOrderNumber(client, tid);
						if (StringUtils.isNotBlank(buyerMessage)) {
							//详细收货地址
							StringBuilder address = new StringBuilder(30);
							address.append(trades.get(i).getReceiverState());
							address.append(trades.get(i).getReceiverCity());
							address.append(trades.get(i).getReceiverDistrict());
							address.append(trades.get(i).getReceiverAddress());
							address.append(" " + trades.get(i).getReceiverName());
							address.append(" " + trades.get(i).getReceiverZip());
							
							// 通过留言获得smile订单号
							numbers = buyerMessage.split(",");
							/*****************************
							 * 处理订单状态 start*********************************** 交易状态：
							 * TRADE_NO_CREATE_PAY(没有创建支付宝交易) WAIT_BUYER_PAY(等待买家付款)
							 * WAIT_SELLER_SEND_GOODS(等待卖家发货,即:买家已付款)
							 * SELLER_CONSIGNED_PART（卖家部分发货）
							 * WAIT_BUYER_CONFIRM_GOODS(等待买家确认收货,即:卖家已发货)
							 * TRADE_BUYER_SIGNED(买家已签收,货到付款专用) TRADE_FINISHED(交易成功)
							 * TRADE_CLOSED(交易关闭) TRADE_CLOSED_BY_TAOBAO(交易被淘宝关闭)
							 * ALL_WAIT_PAY(包含：WAIT_BUYER_PAY、TRADE_NO_CREATE_PAY)
							 * ALL_CLOSED(包含：TRADE_CLOSED、TRADE_CLOSED_BY_TAOBAO)
							 * WAIT_PRE_AUTH_CONFIRM(余额宝0元购合约中)/
							 */
							OrderModel orderModel = new OrderModel();
							if ("WAIT_BUYER_PAY".equals(status)) {// 等待买家付款
								for (int g = 0; g < numbers.length; g++) {
									if (StringUtils.isNotBlank(numbers[g])) {
										orderModel.setNumber(numbers[g]);
										orderModel.setStatus(Constants.OrderStateCode.FOR_PAYMENT.code);
										orderDao.updateStatusByNumber(orderModel);
									}
								}
								continue;
							} else if ("WAIT_SELLER_SEND_GOODS".equals(status)) {// 等待卖家发货,即:买家已付款
								for (int g = 0; g < numbers.length; g++) {
									if (StringUtils.isNotBlank(numbers[g])) {
										orderModel.setNumber(numbers[g]);
										orderModel.setStatus(Constants.OrderStateCode.HAVE_BEEN_PAID.code);
										orderDao.updateStatusByNumber(orderModel);
									}
								}
								continue;
							} else if ("SELLER_CONSIGNED_PART".equals(status)) {// 卖家部分发货
								for (int g = 0; g < numbers.length; g++) {
									if (StringUtils.isNotBlank(numbers[g])) {
										orderModel.setNumber(numbers[g]);
										orderModel.setStatus(Constants.OrderStateCode.SHIPPED_ABROAD_CLEARANCE.code);
										orderDao.updateStatusByNumber(orderModel);
									}
								}
							} else if ("WAIT_BUYER_CONFIRM_GOODS".equals(status)) {// 等待买家确认收货,即:卖家已发货
								for (int g = 0; g < numbers.length; g++) {
									if (StringUtils.isNotBlank(numbers[g])) {
										orderModel.setNumber(numbers[g]);
										orderModel.setStatus(Constants.OrderStateCode.SHIPPED_ABROAD_CLEARANCE.code);
										orderDao.updateStatusByNumber(orderModel);
									}
								}
							} else if ("TRADE_BUYER_SIGNED".equals(status)) {// 买家已签收,货到付款专用
								for (int g = 0; g < numbers.length; g++) {
									if (StringUtils.isNotBlank(numbers[g])) {
										orderModel.setNumber(numbers[g]);
										orderModel.setStatus(Constants.OrderStateCode.HAS_BEEN_COMPLETED.code);
										orderDao.updateStatusByNumber(orderModel);
									}
								}
							} else if ("TRADE_FINISHED".equals(status)) {// 交易成功
								for (int g = 0; g < numbers.length; g++) {
									if (StringUtils.isNotBlank(numbers[g])) {
										orderModel.setNumber(numbers[g]);
										orderModel.setStatus(Constants.OrderStateCode.HAS_BEEN_COMPLETED.code);
										orderDao.updateStatusByNumber(orderModel);
									}
								}
								continue;
							} else if ("TRADE_CLOSED".equals(status)) {// 交易关闭
								for (int g = 0; g < numbers.length; g++) {
									if (StringUtils.isNotBlank(numbers[g])) {
										orderModel.setNumber(numbers[g]);
										orderModel.setStatus(Constants.OrderStateCode.HAVE_BEEN_CLOSED.code);
										orderDao.updateStatusByNumber(orderModel);
									}
								}
								continue;
							} else if ("TRADE_CLOSED_BY_TAOBAO".equals(status)) {
								continue;
							} else if ("ALL_WAIT_PAY".equals(status)) {
								continue;
							} else if ("ALL_CLOSED".equals(status)) {
								continue;
							} else if ("WAIT_PRE_AUTH_CONFIRM".equals(status)) {
								continue;
							} else {
								continue;
							}
		
							/***************************** 处理订单状态 end ***********************************/
		
							/***************************** 处理物流 start *************************************/
							for (int j = 0; j < numbers.length; j++) {
								OrderSimpleDto orderDto = orderService.getOrderDtoByNumber(numbers[j]);
								LogisticsTraceSearchResponse res = null;
								try {
									res = this.getTraceList(client, tid);
								} catch (ApiException e) {
									e.printStackTrace();
								}
								if (res != null) {
									List<TransitStepInfo> traceList = res.getTraceList();
									if (res != null && traceList != null && traceList.size() > 0) {
										List<String> llist = new ArrayList<String>();
										for (int k = 0; k < traceList.size(); k++) {
											llist.add(traceList.get(k).getStatusTime() + " "
													+ traceList.get(k).getStatusDesc());
										}
		
										if (orderDto.getLogisticsDto() == null) {
											orderDto.setLogisticsDto(new LogisticsDto());
											isAdd = true;
										} else {
											isAdd = false;
										}
		
										orderDto.getLogisticsDto().setNumber(numbers[j]);
										orderDto.getLogisticsDto().setCompanyName(res.getCompanyName());
										orderDto.getLogisticsDto().setTid(res.getTid());
										orderDto.getLogisticsDto().setLogisticsStatus(res.getStatus());
										orderDto.getLogisticsDto().setTransitStepInfoList(llist);
										orderDto.getLogisticsDto().setAddress(address.toString());
		
										StringBuilder content = new StringBuilder(50);
										for (String c : orderDto.getLogisticsDto().getTransitStepInfoList()) {
											content.append(c);
											content.append("@#@");
										}
										content.delete(content.length() - 3, content.length());
										orderDto.getLogisticsDto().setContent(content.toString());
		
										if (isAdd) {
											Date date = new Date();
											orderDto.getLogisticsDto().setGmtCreate(date);
											orderDto.getLogisticsDto().setGmtModify(date);
											logisticsDao.insertLogistics(orderDto.getLogisticsDto());
										} else {
											orderDto.getLogisticsDto().setGmtModify(new Date());
											logisticsDao.updateLogistics(orderDto.getLogisticsDto());
										}
									}
									solrData.submitOrder(orderDto.getUserId(), orderDto.getId(),
											orderDto.getStatus(), orderDto.getGmtModify(),
											orderDto.getLogisticsDto());
								}
							}
							/***************************** 处理物流 end ***********************************/
						}
					}
		
				} catch (ApiException e) {
					System.out.println("淘宝同步物流信息失败！");
					e.printStackTrace();
				}
				page ++;
			}
		} else {
			System.out.println("淘宝接口没有打开，已忽略调用！");
			return;
		}
	}

	/**
	 * 根据物流编号获取物流详情
	 * 
	 * @param client
	 * @param tid
	 * @return
	 * @throws ApiException
	 */
	private LogisticsTraceSearchResponse getTraceList(TaobaoClient client, Long tid)
			throws ApiException {
		if (tid == null) {
			return null;
		}
		LogisticsTraceSearchRequest req = new LogisticsTraceSearchRequest();
		req.setTid(tid);
		req.setSellerNick(sellerNick);
		return client.execute(req);
	}

	/**
	 * 根据订单编号获取买家留言（即smile订单号，多个时以英文逗号分隔）
	 * 
	 * @param client
	 * @param tid
	 * @return
	 * @throws ApiException
	 */
	private String getOrderNumber(TaobaoClient client, Long tid) throws ApiException {
		TradeGetRequest req = new TradeGetRequest();
		req.setFields("buyer_message");// 买家留言
		req.setTid(tid);
		TradeGetResponse response = client.execute(req, sessionKey);
		if (response.getTrade() == null) {
			return null;
		}
		return response.getTrade().getBuyerMessage();
	}

	public static void main(String[] args) throws ApiException {
		TaobaoDataImpl tb = new TaobaoDataImpl();
		tb.synchronizationLogistics();
		System.exit(1);
	}

	public static String getPropertiesFilePath() {
		return propertiesFilePath;
	}
	
	public Trade getTradeByTid(Long tid){
		Trade trade = null;
		if("1".equals(taobaoFlag)){
			init();
			TaobaoClient client=new DefaultTaobaoClient(url, appkey, secret);
			TradeGetRequest req=new TradeGetRequest();
			req.setFields("status,payment,post_fee,buyer_nick");
			req.setTid(tid);
			try {
				TradeGetResponse response = client.execute(req , sessionKey);
				trade = response.getTrade();
			} catch (ApiException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("淘宝接口没有打开，已忽略调用！");
		}
		return trade;
	}
}


package com.flyhz.shop.persistence;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.flyhz.framework.lang.SolrData;
import com.flyhz.framework.lang.TaobaoData;
import com.flyhz.framework.util.Constants;
import com.flyhz.framework.util.TaobaoTokenUtil;
import com.flyhz.shop.dto.LogisticsDto;
import com.flyhz.shop.dto.OrderSimpleDto;
import com.flyhz.shop.persistence.dao.IdcardDao;
import com.flyhz.shop.persistence.dao.LogisticsDao;
import com.flyhz.shop.persistence.dao.OrderDao;
import com.flyhz.shop.persistence.entity.IdcardModel;
import com.flyhz.shop.persistence.entity.OrderModel;
import com.flyhz.shop.service.OrderService;
import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.domain.Shipping;
import com.taobao.api.domain.Trade;
import com.taobao.api.domain.TransitStepInfo;
import com.taobao.api.internal.tmc.Message;
import com.taobao.api.internal.tmc.MessageHandler;
import com.taobao.api.internal.tmc.MessageStatus;
import com.taobao.api.internal.tmc.TmcClient;
import com.taobao.api.request.LogisticsOrdersDetailGetRequest;
import com.taobao.api.request.LogisticsTraceSearchRequest;
import com.taobao.api.request.TradeGetRequest;
import com.taobao.api.request.TradesSoldGetRequest;
import com.taobao.api.response.LogisticsOrdersDetailGetResponse;
import com.taobao.api.response.LogisticsTraceSearchResponse;
import com.taobao.api.response.TradeGetResponse;
import com.taobao.api.response.TradesSoldGetResponse;
import com.taobao.top.link.LinkException;

public class TaobaoDataImpl implements TaobaoData {
	private Logger				log					= LoggerFactory.getLogger(TaobaoDataImpl.class);

	private final String		url					= "http://gw.api.taobao.com/router/rest";
	private String				appkey;
	private String				appSecret;
	private String				sessionKey;
	private String				sellerNick;
	/**
	 * 淘宝配置文件全路径，taobao.properties
	 */
	public static String		propertiesFilePath;

	@Resource
	@Value(value = "${smile.taobao.file}")
	private String				taobaoPropertiesFilePath;
	@Resource
	@Value(value = "${smile.taobao.flag}")
	private String				taobaoFlag;

	@Resource
	private SolrData			solrData;
	@Resource
	private OrderService		orderService;
	@Resource
	private LogisticsDao		logisticsDao;
	@Resource
	private IdcardDao idcardDao;
	@Resource
	private OrderDao orderDao;

	private final Long			PAGE_SIZE			= 100L;

	private static boolean		isBeenInitialized	= false;

	private static TmcClient	client;

	/**
	 * 初始化参数
	 */
	private boolean checkAndInit() {
		if ("1".equals(taobaoFlag)) {
			if (!isBeenInitialized) {// 判断是否打开淘宝接口调用
				log.info("淘宝初始化参数开始...");
				if (StringUtils.isBlank(propertiesFilePath)) {
					propertiesFilePath = taobaoPropertiesFilePath;
				}

				appkey = TaobaoTokenUtil.getAppKey();
				appSecret = TaobaoTokenUtil.getAppSecret();
				sessionKey = TaobaoTokenUtil.getAccessToken();
				sellerNick = TaobaoTokenUtil.getSellerNick();

				isBeenInitialized = true;
				log.info("淘宝初始化参数结束");
			} else {
				// log.info("淘宝参数已经初始化，不需要重新初始化");
			}
			return true;
		} else {
			log.info("淘宝接口没有打开，已忽略调用！");
			return false;
		}
	}

	public void synchronizationLogistics() {
		if (checkAndInit()) {
			log.info("同步淘宝订单及物流信息开始...");

			Long page = 1L;
			Long totalPage = 1L;

			while (page <= totalPage) {
				log.info("第" + page + "页");
				TaobaoClient client = new DefaultTaobaoClient(url, appkey, appSecret);
				TradesSoldGetRequest req = new TradesSoldGetRequest();
				req.setFields("status,tid");
				req.setPageSize(PAGE_SIZE);

				try {
					TradesSoldGetResponse response = client.execute(req, sessionKey);
					List<Trade> trades = response.getTrades();
					totalPage = response.getTotalResults() / PAGE_SIZE + 1L;

					boolean isAdd = true;
					String buyerMessage = null;
					String[] numbers = null;
					for (int i = 0; trades != null && i < trades.size(); i++) {
						Long tid = trades.get(i).getTid();
						String status = trades.get(i).getStatus();
						log.info(i + ".淘宝订单tid:" + tid + ",status:" + status);

						/*****************************
						 * 处理订单状态 start*********************************** 交易状态：
						 * TRADE_NO_CREATE_PAY(没有创建支付宝交易) WAIT_BUYER_PAY(等待买家付款)
						 * WAIT_SELLER_SEND_GOODS(等待卖家发货,即:买家已付款)
						 * SELLER_CONSIGNED_PART（卖家部分发货）
						 * WAIT_BUYER_CONFIRM_GOODS(等待买家确认收货,即:卖家已发货)
						 * TRADE_BUYER_SIGNED(买家已签收,货到付款专用) TRADE_FINISHED(交易成功)
						 * TRADE_CLOSED(交易关闭) TRADE_CLOSED_BY_TAOBAO(交易被淘宝关闭)
						 * ALL_WAIT_PAY(包含：WAIT_BUYER_PAY 、TRADE_NO_CREATE_PAY)
						 * ALL_CLOSED(包含：TRADE_CLOSED、 TRADE_CLOSED_BY_TAOBAO)
						 * WAIT_PRE_AUTH_CONFIRM(余额宝0元购合约中)/
						 */

						// 只有 等待买家付款、买家已付款、卖家部分发货、卖家已发货、买家已签收、交易成功、交易关闭、交易被淘宝关闭
						if ("WAIT_BUYER_PAY".equals(status)
								|| "WAIT_SELLER_SEND_GOODS".equals(status)
								|| "SELLER_CONSIGNED_PART".equals(status)
								|| "WAIT_BUYER_CONFIRM_GOODS".equals(status)
								|| "TRADE_BUYER_SIGNED".equals(status)
								|| "TRADE_FINISHED".equals(status) || "TRADE_CLOSED".equals(status)
								|| "TRADE_CLOSED_BY_TAOBAO".equals(status)) {
							// 先获取买家留言
							try {
								buyerMessage = getOrderNumber(client, tid);
							} catch (ApiException e) {
								log.error(e.getMessage());
							}
						} else {
							buyerMessage = null;
							continue;
						}

						if (StringUtils.isNotBlank(buyerMessage)) {
							// 通过留言获得smile订单号
							numbers = buyerMessage.split(",");
							OrderModel orderModel = new OrderModel();

							if ("WAIT_BUYER_PAY".equals(status)) {// 等待买家付款
								for (int g = 0; g < numbers.length; g++) {
									if (StringUtils.isNotBlank(numbers[g])) {
										orderModel.setNumber(numbers[g]);
										orderModel.setStatus(Constants.OrderStateCode.FOR_PAYMENT.code);
										orderService.updateStatusByNumber(orderModel);
									}
								}
								continue;
							} else if ("WAIT_SELLER_SEND_GOODS".equals(status)) {// 买家已付款,即:等待卖家发货
								for (int g = 0; g < numbers.length; g++) {
									if (StringUtils.isNotBlank(numbers[g])) {
										orderModel.setNumber(numbers[g]);
										
										// 已付款
										orderModel.setStatus(Constants.OrderStateCode.HAVE_BEEN_PAID.code);
										OrderSimpleDto orderDto = orderDao.getOrderByNumber(orderModel.getNumber());
										
										// 买家已付款，需要验证身份证是否存在
										IdcardModel im = new IdcardModel();
										im.setUserId(orderDto.getUserId());
										List<IdcardModel> list = idcardDao.getModelList(im);
										if (list == null || list.size() == 0) {
											// 缺失身份证
											orderModel.setStatus(Constants.OrderStateCode.THE_LACK_OF_IDENTITY_CARD.code);
										} else {
											// 等待发货
											orderModel.setStatus(Constants.OrderStateCode.WAITING_FOR_DELIVERY.code);
										}
										orderService.updateStatusByNumber(orderModel);
									}
								}
								continue;
							} else if ("SELLER_CONSIGNED_PART".equals(status)) {// 卖家部分发货
								for (int g = 0; g < numbers.length; g++) {
									if (StringUtils.isNotBlank(numbers[g])) {
										orderModel.setNumber(numbers[g]);
										orderModel.setStatus(Constants.OrderStateCode.SHIPPED_ABROAD_CLEARANCE.code);
										orderService.updateStatusByNumber(orderModel);
									}
								}
							} else if ("WAIT_BUYER_CONFIRM_GOODS".equals(status)) {// 卖家已发货,即:等待买家确认收货
								for (int g = 0; g < numbers.length; g++) {
									if (StringUtils.isNotBlank(numbers[g])) {
										orderModel.setNumber(numbers[g]);
										orderModel.setStatus(Constants.OrderStateCode.SHIPPED_ABROAD_CLEARANCE.code);
										orderService.updateStatusByNumber(orderModel);
									}
								}
							} else if ("TRADE_BUYER_SIGNED".equals(status)) {// 买家已签收,货到付款专用
								for (int g = 0; g < numbers.length; g++) {
									if (StringUtils.isNotBlank(numbers[g])) {
										orderModel.setNumber(numbers[g]);
										orderModel.setStatus(Constants.OrderStateCode.HAS_BEEN_COMPLETED.code);
										orderService.updateStatusByNumber(orderModel);
									}
								}
							} else if ("TRADE_FINISHED".equals(status)) {// 交易成功
								for (int g = 0; g < numbers.length; g++) {
									if (StringUtils.isNotBlank(numbers[g])) {
										orderModel.setNumber(numbers[g]);
										orderModel.setStatus(Constants.OrderStateCode.HAS_BEEN_COMPLETED.code);
										orderService.updateStatusByNumber(orderModel);
									}
								}
								continue;
							} else if ("TRADE_CLOSED".equals(status)) {// 交易关闭
								for (int g = 0; g < numbers.length; g++) {
									if (StringUtils.isNotBlank(numbers[g])) {
										orderModel.setNumber(numbers[g]);
										orderModel.setStatus(Constants.OrderStateCode.HAVE_BEEN_CLOSED.code);
										orderService.updateStatusByNumber(orderModel);
									}
								}
								continue;
							} else if ("TRADE_CLOSED_BY_TAOBAO".equals(status)) {
								for (int g = 0; g < numbers.length; g++) {
									if (StringUtils.isNotBlank(numbers[g])) {
										orderModel.setNumber(numbers[g]);
										orderModel.setStatus(Constants.OrderStateCode.HAVE_BEEN_CLOSED.code);
										orderService.updateStatusByNumber(orderModel);
									}
								}
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
							log.info("同步淘宝物流...");
							for (int j = 0; j < numbers.length; j++) {
								OrderSimpleDto orderDto = orderService.getOrderDtoByNumber(numbers[j]);
								if (orderDto != null) {
									// 详细收货地址
									String address = getLogisticsOrderAddress(client, tid);

									LogisticsTraceSearchResponse res = null;
									try {
										res = this.getTraceList(client, tid);
									} catch (ApiException e) {
										e.printStackTrace();
									}
									if (res != null) {
										List<TransitStepInfo> traceList = res.getTraceList();
										if (res != null && traceList != null
												&& traceList.size() > 0) {
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
											orderDto.getLogisticsDto().setCompanyName(
													res.getCompanyName());
											orderDto.getLogisticsDto().setTid(res.getTid());
											orderDto.getLogisticsDto().setLogisticsStatus(
													res.getStatus());
											orderDto.getLogisticsDto()
													.setTransitStepInfoList(llist);
											orderDto.getLogisticsDto().setAddress(address);

											StringBuilder content = new StringBuilder(50);
											for (String c : orderDto.getLogisticsDto()
																	.getTransitStepInfoList()) {
												content.append(c);
												content.append("@#@");
											}
											content.delete(content.length() - 3, content.length());
											orderDto.getLogisticsDto().setContent(
													content.toString());

											if (isAdd) {
												Date date = new Date();
												orderDto.getLogisticsDto().setGmtCreate(date);
												orderDto.getLogisticsDto().setGmtModify(date);
												logisticsDao.insertLogistics(orderDto.getLogisticsDto());
												log.info("插入物流信息，系统订单号为"
														+ orderDto.getLogisticsDto().getNumber()
														+ "，淘宝订单号为"
														+ orderDto.getLogisticsDto().getTid());
											} else {
												orderDto.getLogisticsDto().setGmtModify(new Date());
												logisticsDao.updateLogistics(orderDto.getLogisticsDto());
												log.info("更新物流信息，系统订单号为"
														+ orderDto.getLogisticsDto().getNumber()
														+ "，淘宝订单号为"
														+ orderDto.getLogisticsDto().getTid());
											}
										}
										solrData.submitOrder(orderDto.getUserId(),
												orderDto.getId(), orderDto.getStatus(),
												orderDto.getGmtModify(), orderDto.getLogisticsDto());
									}
								}
							}
							log.info("同步淘宝物流结束");
							/***************************** 处理物流 end ***********************************/
						}
					}

				} catch (ApiException e) {
					log.info("淘宝同步物流信息失败！");
					e.printStackTrace();
				}
				page++;
			}
			log.info("同步订单及淘宝物流信息完成");
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
		// taobao.trade.get 获取单笔交易的部分信息(性能高)
		TradeGetRequest req = new TradeGetRequest();
		req.setFields("buyer_message");// 买家留言
		req.setTid(tid);
		TradeGetResponse response = client.execute(req, sessionKey);
		if (response.getTrade() == null) {
			return "";
		}
		return response.getTrade().getBuyerMessage();
	}

	/**
	 * 根据淘宝订单ID获取买家收件地址
	 * 
	 * @param client
	 * @param tid
	 * @return
	 * @throws ApiException
	 */
	public String getLogisticsOrderAddress(TaobaoClient client, Long tid) throws ApiException {
		LogisticsOrdersDetailGetRequest req = new LogisticsOrdersDetailGetRequest();
		req.setFields("receiver_name,receiver_mobile,receiver_phone,receiver_location");
		req.setTid(tid);
		LogisticsOrdersDetailGetResponse response = client.execute(req, sessionKey);
		if (response.getShippings() != null && response.getShippings().size() > 0) {
			Shipping shipping = response.getShippings().get(0);
			StringBuilder address = new StringBuilder(30);
			address.append(shipping.getLocation().getState());
			address.append(shipping.getLocation().getCity());
			address.append(shipping.getLocation().getDistrict());
			address.append(shipping.getLocation().getAddress());
			address.append(" " + shipping.getLocation().getZip());
			address.append(" " + shipping.getReceiverName());
			return address.toString();
		}
		return "";
	}

	public static void main(String[] args) throws ApiException {
	}

	public static String getPropertiesFilePath() {
		return propertiesFilePath;
	}

	public Trade getTradeByTid(Long tid) {
		Trade trade = null;
		if (checkAndInit()) {
			TaobaoClient client = new DefaultTaobaoClient(url, appkey, appSecret);
			TradeGetRequest req = new TradeGetRequest();
			req.setFields("status,payment,post_fee,buyer_nick,buyer_message");
			req.setTid(tid);
			try {
				TradeGetResponse response = client.execute(req, sessionKey);
				trade = response.getTrade();
			} catch (ApiException e) {
				e.printStackTrace();
			}
		}
		return trade;
	}

	private static boolean	isRunning	= false;

	public void stopMessageHandler() {
		if (client != null && client.isOnline()) {
			log.info("淘宝消息进程正在关闭！");
			client.close();
			log.info("淘宝消息进程已关闭！");
		} else {
			log.info("淘宝消息进程没有启用！");
		}
	}

	public void startMessageHandler() {
		if (checkAndInit()) {
			if (!isRunning) {
				log.info("淘宝消息进程即将启动！");
				log.info("appkey=" + appkey + ",appSecret=" + appSecret);
				client = new TmcClient("ws://mc.api.taobao.com/", appkey, appSecret,
						"smile");
				client.setMessageHandler(new MessageHandler() {
					public void onMessage(Message message, MessageStatus status) {
						try {
							log.info(message.getContent());// {"buyer_nick":"sandbox_cilai_c","payment":"120.00","oid":192364827791084,"tid":192364827791084,"type":"guarantee_trade","seller_nick":"sandbox_c_20"}
							log.info(message.getTopic());// taobao_trade_TradeCreate
							if ("taobao_item_ItemUpshelf".equals(message.getTopic())) {

								JSONObject jobject = new JSONObject(message.getContent());
								log.info("有商品上架了,id=" + jobject.getString("num_iid"));

							} else if ("taobao_item_ItemDownshelf".equals(message.getTopic())) {

								JSONObject jobject = new JSONObject(message.getContent());
								log.info("有商品下架了,id=" + jobject.getString("num_iid"));

							} else if ("taobao_trade_TradeBuyerPay".equals(message.getTopic())) {

								JSONObject jobject = new JSONObject(message.getContent());
								log.info("买家付完款，或万人团买家付完尾款,tid=" + jobject.getString("tid"));

								// 获取smile系统订单编号
								TaobaoClient client = new DefaultTaobaoClient(url, appkey,
										appSecret);
								String number = getOrderNumber(client,
										Long.valueOf(jobject.getString("tid")));

								if (StringUtils.isNotBlank(number)) {
									String[] numbers = number.split(",");
									// 修改订单状态
									OrderModel orderModel = new OrderModel();
									for (int g = 0; g < numbers.length; g++) {
										if (StringUtils.isNotBlank(numbers[g])) {
											orderModel.setStatus(Constants.OrderStateCode.HAVE_BEEN_PAID.code);
											orderModel.setNumber(number);
											orderService.updateStatusByNumberForMessage(orderModel);
										}
									}
								}

							} else if ("taobao_trade_TradeSuccess".equals(message.getTopic())) {

								JSONObject jobject = new JSONObject(message.getContent());
								log.info("交易成功消息,tid=" + jobject.getString("tid"));

								// 获取smile系统订单编号
								TaobaoClient client = new DefaultTaobaoClient(url, appkey,
										appSecret);
								String number = getOrderNumber(client,
										Long.valueOf(jobject.getString("tid")));

								if (StringUtils.isNotBlank(number)) {
									String[] numbers = number.split(",");
									// 修改订单状态
									OrderModel orderModel = new OrderModel();
									for (int g = 0; g < numbers.length; g++) {
										if (StringUtils.isNotBlank(numbers[g])) {
											orderModel.setStatus(Constants.OrderStateCode.HAS_BEEN_COMPLETED.code);
											orderModel.setNumber(number);
											orderService.updateStatusByNumberForMessage(orderModel);
										}
									}
								}
							} else if ("taobao_trade_TradeSellerShip".equals(message.getTopic())) {

								JSONObject jobject = new JSONObject(message.getContent());
								log.info("卖家发货消息,tid=" + jobject.getString("tid"));

								// 获取smile系统订单编号
								TaobaoClient client = new DefaultTaobaoClient(url, appkey,
										appSecret);
								String number = getOrderNumber(client,
										Long.valueOf(jobject.getString("tid")));

								if (StringUtils.isNotBlank(number)) {
									String[] numbers = number.split(",");
									// 修改订单状态
									OrderModel orderModel = new OrderModel();
									for (int g = 0; g < numbers.length; g++) {
										if (StringUtils.isNotBlank(numbers[g])) {
											orderModel.setStatus(Constants.OrderStateCode.SHIPPED_ABROAD_CLEARANCE.code);
											orderModel.setNumber(number);
											orderService.updateStatusByNumberForMessage(orderModel);
										}
									}
								}
							}

							log.info("一次消息结束");
							// 默认不抛出异常则认为消息处理成功
						} catch (Exception e) {
							e.printStackTrace();
							status.fail();// 消息处理失败回滚，服务端需要重发
						}
					}
				});
				try {
					client.connect();
					isRunning = true;
					log.info("淘宝消息进程运行成功！");
				} catch (LinkException e) {
					log.info("淘宝消息进程运行失败！");
					log.error(e.getMessage());
				}
			} else {
				log.warn("淘宝消息进程正在运行中，不用重复启动！");
			}
		}
	}
}

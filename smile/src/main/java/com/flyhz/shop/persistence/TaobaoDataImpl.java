
package com.flyhz.shop.persistence;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;

import com.flyhz.framework.lang.SolrData;
import com.flyhz.framework.lang.TaobaoData;
import com.flyhz.framework.util.Constants;
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
	private String			url			= "http://gw.api.taobao.com/router/rest";
	private String			appkey		= "21799805";
	private String			secret		= "ac6da6b7525a4c0390f5e4d0dd18d148";
	private String			sessionKey	= "61023272ec1ff3cab047f128366df05778511ec59c48d62822504044";

	@Resource
	private SolrData		solrData;
	@Resource
	private OrderService	orderService;
	@Resource
	private OrderDao orderDao;
	@Resource
	private LogisticsDao	logisticsDao;

	public void synchronizationLogistics() {
		// TaobaoClient client = new DefaultTaobaoClient(url, appkey, secret);
		// LogisticsOrdersDetailGetRequest req = new
		// LogisticsOrdersDetailGetRequest();
		// req.setFields("tid,buyer_nick,receiver_name,receiver_mobile,receiver_phone,receiver_location");

		TaobaoClient client = new DefaultTaobaoClient(url, appkey, secret);
		TradesSoldGetRequest req = new TradesSoldGetRequest();
		req.setFields("status,tid");

		try {
			// 获取所以已经发货的
			// LogisticsOrdersDetailGetResponse response = client.execute(req,
			// sessionKey);
			// List<Shipping> shippings = response.getShippings();
			TradesSoldGetResponse response = client.execute(req, sessionKey);
			List<Trade> trades = response.getTrades();

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
					// 通过留言获得smile订单号
					numbers = buyerMessage.split(",");
					/*****************************处理订单状态 start***********************************
					 * 交易状态：
					 * TRADE_NO_CREATE_PAY(没有创建支付宝交易) 
					 * WAIT_BUYER_PAY(等待买家付款)
					 * WAIT_SELLER_SEND_GOODS(等待卖家发货,即:买家已付款)
					 * SELLER_CONSIGNED_PART（卖家部分发货）
					 * WAIT_BUYER_CONFIRM_GOODS(等待买家确认收货,即:卖家已发货)
					 * TRADE_BUYER_SIGNED(买家已签收,货到付款专用) 
					 * TRADE_FINISHED(交易成功)
					 * TRADE_CLOSED(交易关闭) 
					 * TRADE_CLOSED_BY_TAOBAO(交易被淘宝关闭)
					 * ALL_WAIT_PAY(包含：WAIT_BUYER_PAY、TRADE_NO_CREATE_PAY)
					 * ALL_CLOSED(包含：TRADE_CLOSED、TRADE_CLOSED_BY_TAOBAO)
					 * WAIT_PRE_AUTH_CONFIRM(余额宝0元购合约中)/
					 */
					OrderModel orderModel = new OrderModel();
					if("WAIT_BUYER_PAY".equals(status)){//等待买家付款
						for(int g=0;g<numbers.length;g++){
							if(StringUtils.isNotBlank(numbers[g])){
								orderModel.setNumber(numbers[g]);
								orderModel.setStatus(Constants.OrderStateCode.FOR_PAYMENT.code);
								orderDao.updateStatusByNumber(orderModel);
							}
						}
						continue;
					} else if("WAIT_SELLER_SEND_GOODS".equals(status)) {//等待卖家发货,即:买家已付款
						for(int g=0;g<numbers.length;g++){
							if(StringUtils.isNotBlank(numbers[g])){
								orderModel.setNumber(numbers[g]);
								orderModel.setStatus(Constants.OrderStateCode.HAVE_BEEN_PAID.code);
								orderDao.updateStatusByNumber(orderModel);
							}
						}
						continue;
					} else if("SELLER_CONSIGNED_PART".equals(status)) {//卖家部分发货
						for(int g=0;g<numbers.length;g++){
							if(StringUtils.isNotBlank(numbers[g])){
								orderModel.setNumber(numbers[g]);
								orderModel.setStatus(Constants.OrderStateCode.SHIPPED_ABROAD_CLEARANCE.code);
								orderDao.updateStatusByNumber(orderModel);
							}
						}
					} else if("WAIT_BUYER_CONFIRM_GOODS".equals(status)) {//等待买家确认收货,即:卖家已发货
						for(int g=0;g<numbers.length;g++){
							if(StringUtils.isNotBlank(numbers[g])){
								orderModel.setNumber(numbers[g]);
								orderModel.setStatus(Constants.OrderStateCode.SHIPPED_ABROAD_CLEARANCE.code);
								orderDao.updateStatusByNumber(orderModel);
							}
						}
					} else if("TRADE_BUYER_SIGNED".equals(status)) {//买家已签收,货到付款专用
						for(int g=0;g<numbers.length;g++){
							if(StringUtils.isNotBlank(numbers[g])){
								orderModel.setNumber(numbers[g]);
								orderModel.setStatus(Constants.OrderStateCode.HAS_BEEN_COMPLETED.code);
								orderDao.updateStatusByNumber(orderModel);
							}
						}
					} else if("TRADE_FINISHED".equals(status)) {//交易成功
						for(int g=0;g<numbers.length;g++){
							if(StringUtils.isNotBlank(numbers[g])){
								orderModel.setNumber(numbers[g]);
								orderModel.setStatus(Constants.OrderStateCode.HAS_BEEN_COMPLETED.code);
								orderDao.updateStatusByNumber(orderModel);
							}
						}
						continue;
					} else if("TRADE_CLOSED".equals(status)) {//交易关闭
						for(int g=0;g<numbers.length;g++){
							if(StringUtils.isNotBlank(numbers[g])){
								orderModel.setNumber(numbers[g]);
								orderModel.setStatus(Constants.OrderStateCode.HAVE_BEEN_CLOSED.code);
								orderDao.updateStatusByNumber(orderModel);
							}
						}
						continue;
					} else if("TRADE_CLOSED_BY_TAOBAO".equals(status)) {
						continue;
					} else if("ALL_WAIT_PAY".equals(status)) {
						continue;
					} else if("ALL_CLOSED".equals(status)) {
						continue;
					} else if("WAIT_PRE_AUTH_CONFIRM".equals(status)) {
						continue;
					} else {
						continue;
					}
					
					
					/*****************************处理订单状态 end***********************************/

					/***************************** 处理物流 start *************************************/
					for (int j = 0; j < numbers.length; j++) {
						OrderSimpleDto orderDto = orderService.getOrderDtoByNumber(numbers[j]);
						LogisticsTraceSearchResponse res = null;
						try {
							res = getTraceList(client, tid);
						} catch (ApiException e) {
							e.printStackTrace();
						}
						if(res != null){
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
		req.setSellerNick("taobao_zb2012");
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
}

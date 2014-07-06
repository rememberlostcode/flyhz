
package com.flyhz.shop;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.taobao.api.internal.tmc.Message;
import com.taobao.api.internal.tmc.MessageHandler;
import com.taobao.api.internal.tmc.MessageStatus;
import com.taobao.api.internal.tmc.TmcClient;
import com.taobao.top.link.LinkException;

public class TaobaoSdkMessageTest {
	private static Logger	log			= LoggerFactory.getLogger(TaobaoSdkMessageTest.class);
	/**
	 * 是否在运行中
	 */
	private static boolean	isRunning	= false;

	public static void startMessageHandler(String appkey, String appSecret) {
		if (!isRunning) {
			TmcClient client = new TmcClient("ws://mc.api.taobao.com/", appkey, appSecret, "smile");
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
						} else if ("taobao_trade_TradeSuccess".equals(message.getTopic())) {
							JSONObject jobject = new JSONObject(message.getContent());
							log.info("交易成功消息,tid=" + jobject.getString("tid"));
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
			} catch (LinkException e) {
				log.error(e.getMessage());
			}
		}
	}

	/**
	 * @param args
	 * @throws LinkException
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws LinkException, InterruptedException {

		TmcClient client = new TmcClient("ws://mc.api.taobao.com/", "21799805",
				"ac6da6b7525a4c0390f5e4d0dd18d148", "smile");
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
					} else if ("taobao_trade_TradeSuccess".equals(message.getTopic())) {
						JSONObject jobject = new JSONObject(message.getContent());
						log.info("交易成功消息,tid=" + jobject.getString("tid"));
					}
					log.info("一次消息结束");
					// 默认不抛出异常则认为消息处理成功
				} catch (Exception e) {
					e.printStackTrace();
					status.fail();// 消息处理失败回滚，服务端需要重发
				}
			}
		});
		client.connect();
		Thread.sleep(1000000); // 测试使用，为了观察效果

	}

}

package com.flyhz.shop;

import org.json.JSONObject;

import com.taobao.api.internal.tmc.Message;
import com.taobao.api.internal.tmc.MessageHandler;
import com.taobao.api.internal.tmc.MessageStatus;
import com.taobao.api.internal.tmc.TmcClient;
import com.taobao.top.link.LinkException;

public class TaobaoSdkMessage {

	public void startMessageHandler(String appkey,String appSecret){
		TmcClient client = new TmcClient("ws://mc.api.taobao.com/", appkey, appSecret, "smile");
		client.setMessageHandler(new MessageHandler() {
			public void onMessage(Message message, MessageStatus status) {
				try {
					System.out.println(message.getContent());//{"buyer_nick":"sandbox_cilai_c","payment":"120.00","oid":192364827791084,"tid":192364827791084,"type":"guarantee_trade","seller_nick":"sandbox_c_20"}
					System.out.println(message.getTopic());//taobao_trade_TradeCreate
					if("taobao_item_ItemUpshelf".equals(message.getTopic())){
						JSONObject jobject = new JSONObject(message.getContent());
						System.out.println("有商品上架了,id="+jobject.getString("num_iid"));
					} else if("taobao_item_ItemDownshelf".equals(message.getTopic())){
						JSONObject jobject = new JSONObject(message.getContent());
						System.out.println("有商品下架了,id="+jobject.getString("num_iid"));
					}
					
					System.out.println("一次消息结束");
					// 默认不抛出异常则认为消息处理成功
				} catch (Exception e) {
					e.printStackTrace();
					status.fail();// 消息处理失败回滚，服务端需要重发
				}
			}
		});
		try {
			client.connect();
		} catch (LinkException e) {
			e.printStackTrace();
		}
	}
	/**
	 * @param args
	 * @throws LinkException
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws LinkException,
			InterruptedException {

		TmcClient client = new TmcClient("ws://mc.api.taobao.com/", "21799805", "ac6da6b7525a4c0390f5e4d0dd18d148", "smile");
		client.setMessageHandler(new MessageHandler() {
			public void onMessage(Message message, MessageStatus status) {
				try {
					System.out.println(message.getContent());//{"buyer_nick":"sandbox_cilai_c","payment":"120.00","oid":192364827791084,"tid":192364827791084,"type":"guarantee_trade","seller_nick":"sandbox_c_20"}
					System.out.println(message.getTopic());//taobao_trade_TradeCreate
					if("taobao_item_ItemUpshelf".equals(message.getTopic())){
						JSONObject jobject = new JSONObject(message.getContent());
						System.out.println("有商品上架了,id="+jobject.getString("num_iid"));
					} else if("taobao_item_ItemDownshelf".equals(message.getTopic())){
						JSONObject jobject = new JSONObject(message.getContent());
						System.out.println("有商品下架了,id="+jobject.getString("num_iid"));
					}
					
					System.out.println("一次消息结束");
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

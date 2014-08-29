
package com.holding.smile.activity;

import java.math.BigDecimal;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.holding.smile.R;
import com.holding.smile.dto.RtnValueDto;
import com.holding.smile.tools.Constants;
import com.holding.smile.tools.StrUtils;
import com.holding.smile.tools.TbUtil;

/**
 * 
 * 类说明：webview跳转至淘宝网
 * 
 * @author robin 2014-4-29下午1:16:56
 * 
 */
public class WebViewActivity extends Activity implements OnClickListener {
	private String		number;		// 订单号
	private BigDecimal	amount;		// 总额
	private String		viewhtml;		// 页面HTML
	private Long		tid;			// 淘宝订单号
	private int			count	= 0;

	@SuppressLint({ "SetJavaScriptEnabled", "NewApi" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.web_view);
		ImageView backBtn = (ImageView) findViewById(R.id.btn_back);
		backBtn.setOnClickListener(this);

		try {
			Intent intent = this.getIntent();
			number = intent.getExtras().getString("number");
			amount = (BigDecimal) intent.getExtras().getSerializable("amount");
			WebView web = (WebView) findViewById(R.id.webView);

			if (StrUtils.isNotEmpty(number) && amount != null) {
				// 获取webView控件
				TbUtil.setWebView(web);
				WebSettings webSettings = TbUtil.getWebView().getSettings();
				// 允许使用JavaScript
				webSettings.setJavaScriptEnabled(true);
				// 设置支持缩放
				webSettings.setBuiltInZoomControls(true);
				// 设置默认字体大小
				webSettings.setDefaultFontSize(5);
				// 设置允许自动加载图片
				webSettings.setLoadsImagesAutomatically(true);
				// 设置JS能自动打开新窗口
				webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
				// 设置DOM树是否能更新(缓存页面是否能更新)
				webSettings.setDomStorageEnabled(true);
				// 设置滚动条样式
				TbUtil.getWebView().setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
				// 绑定webview接口
				TbUtil.getWebView().addJavascriptInterface(new InJavaScriptLocalObj(), "localObj");
				// 网页链接不以浏览器方式打开
				TbUtil.getWebView().setWebViewClient(new WebViewClient() {
					@Override
					public void onPageStarted(WebView view, String url, Bitmap favicon) {
						Log.i(MyApplication.LOG_TAG + "onPageStarted", view.toString());
						super.onPageStarted(view, url, favicon);
					}

					@Override
					public boolean shouldOverrideUrlLoading(WebView view, String url) {
						Log.i(MyApplication.LOG_TAG, url);
						return super.shouldOverrideUrlLoading(view, url);
					}

					@Override
					public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
						Log.i(MyApplication.LOG_TAG + "shouldInterceptRequest", view.toString());
						if (url.indexOf("http://api.m.taobao.com/rest/h5ApiUpdate.do?callback=mtopjsonp1&type=jsonp&api=mtop.trade.buildOrder.ex") > -1
								&& count == 0) {
							count++;
							view.reload();
						} else if (url.indexOf("http://login.m.taobao.com/login.htm?v=0&ttid=h5@iframe") > -1) {
							// 设置返回和刷新按钮不显示
							view.loadUrl("javascript:document.getElementsByTagName('section')[1].style.display='none';");
							view.loadUrl("javascript:document.getElementsByTagName('section')[3].style.display='none';");
						} else if (url.indexOf("http://cdn.mmstat.com/aplus-proxy.html?v=20130115") > -1) {
							// 设置首页按钮不显示
							view.loadUrl("javascript:document.getElementsByClassName('back')[0].style.display='none';");
						} else if (url.indexOf("http://maliprod.alipay.com/w/trade_pay.do?alipay_trade_no=") > -1) {
							tid = Long.parseLong(url.substring(url.indexOf("pay_order_id=") + 13,
									url.lastIndexOf("&")));
						}
						return super.shouldInterceptRequest(view, url);
					}

					@Override
					public void onPageFinished(WebView view, String url) {
						Log.i(MyApplication.LOG_TAG + "onPageFinished", view.toString());
						// 设置webview头部显示url
						TextView showVurl = (TextView) findViewById(R.id.show_vurl);
						showVurl.setText(url);
						super.onPageFinished(view, url);
						// 给卖家留言设置订单编号和数量
						if (url.indexOf("buyNow=true&v=0&skuId=&quantity=") > -1) {
							// 加载js
							StringBuffer jsStringBuffer = new StringBuffer();
							jsStringBuffer.append("javascript:$(document).ready(function(){");
							jsStringBuffer.append("setTimeout(function(){");
							jsStringBuffer.append("$('.c-btn-aw').eq(0).css('display','none');");
							jsStringBuffer.append("$('.numTxt').eq(0).attr('readonly','readonly');");
							jsStringBuffer.append("$('.c-form-txt-normal').eq(1).attr('value','")
											.append(Constants.TBHG_PREFIX).append(number)
											.append("');");
							jsStringBuffer.append("$('.c-form-txt-normal').eq(1).attr('placeholder','');");
							jsStringBuffer.append("$('.c-form-txt-normal').eq(1).attr('readonly','readonly');");
							jsStringBuffer.append("},1000);");
							jsStringBuffer.append("});");
							view.loadUrl(jsStringBuffer.toString());
						} else if (url.indexOf("http://login.m.taobao.com/login.htm") > -1) {
							// 设置首页按钮不显示
							StringBuffer jsStringBuffer = new StringBuffer();
							jsStringBuffer.append("javascript:$(document).ready(function(){");
							jsStringBuffer.append("$('.back').eq(0).css('display','none');");
							jsStringBuffer.append("});");
						} else if (url.indexOf("https://mclient.alipay.com/cashierPay.htm?awid=") > -1) {
							view.loadUrl("javascript:window.localObj.showSource(document.body.innerHTML);");
							try {
								Thread.sleep(500);
								if (viewhtml != null) {
									if (viewhtml.indexOf("J-success am-message am-message-success fn-hide") < 0) {
										// 沉睡500ms后获取订单状态
										Thread.sleep(500);
										// 获取订单状态
										RtnValueDto rtnValue = MyApplication.getInstance()
																			.getDataService()
																			.getPayStatus(tid,
																					number);
										if (rtnValue != null
												&& rtnValue.getOrderPayDto() != null
												&& isPaySuccess(rtnValue.getOrderPayDto()
																		.getStatus())) {
											new Handler().postDelayed(new Runnable() {
												public void run() {
													// Activity跳转
													Intent intentNew = new Intent();
													intentNew.setClass(WebViewActivity.this,
															MyOrdersActivity.class);
													startActivity(intentNew);
													finish();
												}
											}, 1000);
										}
									} else if (viewhtml.indexOf("J-error am-message am-message-error fn-hide") > -1) {
										onPageFinished(view, url);
									}
								} else {
									onPageFinished(view, url);
								}
							} catch (InterruptedException e1) {
								e1.printStackTrace();
							}
						}
					}

					@Override
					public void onReceivedError(WebView view, int errorCode, String description,
							String failingUrl) {
						Log.i(MyApplication.LOG_TAG + "onReceivedError", view.toString());
						super.onReceivedError(view, errorCode, description, failingUrl);
					}
				});
				TbUtil.setNumber(amount.intValue());
				// TbUtil.cshTb();
				TbUtil.getWebView()
						.loadUrl(
								"http://h5.m.taobao.com/awp/base/buy.htm?itemId=39544967909&item_num_id=39544967909&_input_charset=utf-8&buyNow=true&v=0&skuId=#!/awp/core/buy.htm?itemId=39544967909&item_num_id=39544967909&_input_charset=utf-8&buyNow=true&v=0&skuId=&quantity="
										+ amount.intValue());
			} else {
				Toast.makeText(this, "订单号或金额为空！", Toast.LENGTH_SHORT).show();
				setResult(RESULT_CANCELED, null);
				finish();
			}
		} catch (Exception e) {
			Log.e(MyApplication.LOG_TAG, "去淘宝支付时出错：" + e.getMessage());
			Toast.makeText(this, "订单号或金额不能为空！", Toast.LENGTH_SHORT).show();
			setResult(RESULT_CANCELED, null);
			finish();
			return;
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.btn_back: {
				setResult(RESULT_CANCELED, null);
				finish();
				break;
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && TbUtil.getWebView().canGoBack()) {
			TbUtil.getWebView().goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		if (TbUtil.getWebView() != null) {
			TbUtil.getWebView().removeAllViews();
			TbUtil.getWebView().destroy();
			TbUtil.setWebView(null);
		}
		super.onDestroy();
	}

	final class InJavaScriptLocalObj {
		public void showSource(String html) {
			viewhtml = html;
		}
	}

	private boolean isPaySuccess(String status) {
		boolean flag = false;
		if (!Constants.OrderStateCode.FOR_PAYMENT.code.equals(status)
				|| !Constants.OrderStateCode.HAVE_BEEN_CLOSED.code.equals(status)
				|| !Constants.OrderStateCode.DELETED.code.equals(status)) {
			flag = true;
		}
		return flag;
	}
}

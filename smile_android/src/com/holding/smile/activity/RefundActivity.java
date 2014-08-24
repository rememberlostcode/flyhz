
package com.holding.smile.activity;

import com.holding.smile.R;
import com.holding.smile.tools.StrUtils;
import com.holding.smile.tools.TbUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
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

public class RefundActivity extends Activity implements OnClickListener {
	private String	tbOrderId;	// 淘宝订单号
	private Integer	refund;	// 待退金额

	@SuppressLint({ "NewApi", "SetJavaScriptEnabled" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.refund_view);
		ImageView backBtn = (ImageView) findViewById(R.id.btn_back);
		backBtn.setOnClickListener(this);

		try {
			Intent intent = this.getIntent();
			tbOrderId = intent.getExtras().getString("tbOrderId");
			refund = Integer.parseInt(intent.getExtras().getString("refund"));
			// tbOrderId = "778517876963659";
			// refund = 8;
			WebView web = (WebView) findViewById(R.id.refundView);
			if (StrUtils.isNotEmpty(tbOrderId) && refund != null) {
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
						super.onPageStarted(view, url, favicon);
					}

					@Override
					public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
						return super.shouldInterceptRequest(view, url);
					}

					@Override
					public void onPageFinished(WebView view, String url) {
						// 设置webview头部显示url
						TextView showVurl = (TextView) findViewById(R.id.refund_url);
						showVurl.setText(url);
						super.onPageFinished(view, url);
						// TbUtil.setWebView(view);
						// 给卖家留言设置订单编号和数量
						if (url.indexOf("h5.m.taobao.com/gaia/apply.html") > -1) {
							// 加载js
							StringBuffer jsStringBuffer = new StringBuffer();
							jsStringBuffer.append("javascript:$(document).ready(function(){");
							// jsStringBuffer.append("setTimeout(function(){");
							jsStringBuffer.append("$('.money').eq(0).html('").append(refund)
											.append("');");
							// jsStringBuffer.append("$('.money').eq(2).text('").append(refund)
							// .append("');");
							jsStringBuffer.append("$('input[name=refundFee]').eq(0).attr('value','")
											.append(refund).append("');");
							jsStringBuffer.append("$('input[name=refundFee]').eq(1).attr('value','")
											.append(refund).append("');");
							// jsStringBuffer.append("},1000);");
							jsStringBuffer.append("});");
							view.loadUrl(jsStringBuffer.toString());
							view.loadUrl("javascript:window.localObj.showSource(document.body.innerHTML);");
						} else if (url.indexOf("http://login.m.taobao.com/login.htm") > -1) {
							// 设置首页按钮不显示
							StringBuffer jsStringBuffer = new StringBuffer();
							jsStringBuffer.append("javascript:$(document).ready(function(){");
							jsStringBuffer.append("$('.back').eq(0).css('display','none');");
							jsStringBuffer.append("});");
						}
					}

					@Override
					public void onReceivedError(WebView view, int errorCode, String description,
							String failingUrl) {
						super.onReceivedError(view, errorCode, description, failingUrl);
					}
				});
				TbUtil.getWebView().loadUrl(
						"http://h5.m.taobao.com/gaia/apply.html?spm=0.0.0.0&bizOrderId="
								+ tbOrderId);
			} else {
				Toast.makeText(this, "淘宝订单号或退款金额为空！", Toast.LENGTH_SHORT).show();
				setResult(RESULT_CANCELED, null);
				finish();
			}
		} catch (Exception e) {
			// Log.e(MyApplication.LOG_TAG, "去淘宝支付时出错：" + e.getMessage());
			Toast.makeText(this, "淘宝订单号或退款金额为空！", Toast.LENGTH_SHORT).show();
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
			System.out.println(html);
			// viewhtml = html;
		}
	}
}

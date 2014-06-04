
package com.holding.smile.activity;

import java.math.BigDecimal;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.holding.smile.R;
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

	private String		number;	// 订单号
	private BigDecimal	amount;	// 总额

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.web_view);
		ImageView backBtn = (ImageView) findViewById(R.id.btn_back);
		TextView headerNum = (TextView) findViewById(R.id.number);
		TextView headerAmount = (TextView) findViewById(R.id.amount);
		backBtn.setOnClickListener(this);

		try {
			Intent intent = this.getIntent();
			number = intent.getExtras().getString("number");
			amount = (BigDecimal) intent.getExtras().getSerializable("amount");

			if (StrUtils.isNotEmpty(number) && amount != null) {

				headerNum.setText("订单号：" + number);
				headerAmount.setText("合计：￥" + amount.doubleValue() + "元");

				// 获取webView控件
				if (TbUtil.getWebView() == null) {
					TbUtil.setWebView((WebView) findViewById(R.id.webView));
				}
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
				// 网页链接不以浏览器方式打开
				TbUtil.getWebView().setWebViewClient(new WebViewClient() {
					@Override
					public void onPageStarted(WebView view, String url, Bitmap favicon) {
						super.onPageStarted(view, url, favicon);
					}

					@Override
					public void onPageFinished(WebView view, String url) {
						super.onPageFinished(view, url);
						// 设置隐藏返回按钮
						view.loadUrl("javascript:document.getElementsByTagName('li')[1].style.display='none';");
						// 给卖家留言设置订单编号和数量
						if (url.indexOf("buyNow=true") >= 0) {
							StringBuffer jsStringBuffer = new StringBuffer();
							jsStringBuffer.append("javascript:window.onload = function(){");
							// 立即购买:卖家留言设置订单编号
							jsStringBuffer.append("document.getElementsByTagName('input')[1].setAttribute('value','");
							jsStringBuffer.append(number);
							jsStringBuffer.append("');");
							// 设置卖家留言属性值为空
							jsStringBuffer.append("document.getElementsByTagName('input')[1].setAttribute('placeholder','');");
							// 立即购买:设置数量为订单总价
							jsStringBuffer.append("document.getElementsByName('quantity')[0].setAttribute('value','");
							jsStringBuffer.append(amount.intValue());
							jsStringBuffer.append("');");
							// 修改总价显示
							jsStringBuffer.append("document.getElementsByTagName('section')[3].lastChild.lastChild.lastChild.innerHTML = '￥");
							jsStringBuffer.append(amount.intValue());
							jsStringBuffer.append(".00';");
							// 合计修改价格数量
							jsStringBuffer.append("document.getElementsByTagName('section')[7].lastChild.lastChild.innerHTML = '￥");
							jsStringBuffer.append(amount.intValue());
							jsStringBuffer.append(".00';");
							// 实付款修改价格
							jsStringBuffer.append("document.getElementById('final').innerHTML = '￥");
							jsStringBuffer.append(amount.intValue());
							jsStringBuffer.append(".00';");

							jsStringBuffer.append("};");
							view.loadUrl(jsStringBuffer.toString());
						}
						// 设置不显示提示信息
						view.loadUrl("javascript:window.SmartbannerJSON.mainDetail='';");
						view.loadUrl("javascript:window.SmartbannerJSON.addcart='';");
					}

					@Override
					public void onReceivedError(WebView view, int errorCode, String description,
							String failingUrl) {
						super.onReceivedError(view, errorCode, description, failingUrl);
					}

				});
				TbUtil.cshTb();
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
}

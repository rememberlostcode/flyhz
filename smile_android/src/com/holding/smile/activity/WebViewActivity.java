
package com.holding.smile.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.holding.smile.R;

/**
 * 
 * 类说明：webview跳转至淘宝网
 * 
 * @author robin 2014-4-29下午1:16:56
 * 
 */
public class WebViewActivity extends BaseActivity implements OnClickListener {

	private WebView	webView;
	private String	number;	// 订单号

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.web_view);

		Intent intent = this.getIntent();
		number = intent.getExtras().getString("number");

		// 获取webView控件
		webView = (WebView) findViewById(R.id.webView);
		WebSettings webSettings = webView.getSettings();
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
		webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		// 加载服务器页面
		// webView.loadUrl("http://h5.m.taobao.com/awp/core/detail.htm?id=37176776067&spm=0.0.0.0");
		// 加载本地页面
		// webView.loadUrl("file:///android_asset/www/index.html");
		// 网页链接不以浏览器方式打开
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
			}
		});
		String url = context.getString(R.string.taobaodian_url);
		webView.loadUrl(url);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
			webView.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}

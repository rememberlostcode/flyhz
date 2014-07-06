
package com.holding.smile.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.Toast;

import com.holding.smile.R;
import com.holding.smile.tools.StrUtils;

/**
 * 类说明：为活动页面设计，实现html5与android的交互
 * @author robin
 * 
 */
public class HtmlUIActivity extends Activity implements OnClickListener {

	private final static String	TAG	= "HtmlUIActivity";
	private WebView				webView;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.htmlui_view);
		ImageView backView = (ImageView) findViewById(R.id.btn_back);
		backView.setOnClickListener(this);

		webView = (WebView) findViewById(R.id.htmlui);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);// 不使用缓存
		webView.addJavascriptInterface(new JsObject(), "jsObj");

		// String url = "file:///android_asset/index.html";
		String url = "";
		Intent intent = this.getIntent();
		String pathUrl = intent.getExtras().getString("url");
		if (StrUtils.isNotEmpty(pathUrl)) {
			url = pathUrl;
			webView.loadUrl(url);
		} else {
			Log.e(TAG, "加载活动页面出错，url为空！");
			Toast toast = Toast.makeText(this, "服务器连接失败!", Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
			return;
		}

	}

	/**
	 * 这个类提供出了一个视图和业务层通信的接口。HTML 中，通过这个类的实例，间接与业务 Bean 通信。 为什么不直接将业务类提供给
	 * webView, 让 HTML 中直接访问到这个类。而多出这样 "插件"~~ 我想：目前的这样一种架构，Activity
	 * 甚至有些类似于控制器的概念了。有点像 struts 中的 Action。 在使用了 struts 框架的项目架构中，Action
	 * 也是被划分到视图层的。它和JSP页面共同完成准备数据和页面跳转的工作。 因此，这里我们也不应该让 HTML 中的 JS
	 * 直接与业务层耦合。实现表现层和业务层的解耦
	 */
	private class JsObject {
		/**
		 * 调用详情页
		 */
		@SuppressWarnings("unused")
		public void call(String gid, String bs) {
			Intent intent = new Intent(HtmlUIActivity.this, GoodsDetailActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.putExtra("gid", Integer.valueOf(gid));
			intent.putExtra("bs", bs);
			startActivity(intent);
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
		if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
			webView.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (webView != null) {
			webView.removeAllViews();
			webView.destroy();
			webView = null;
		}
	}
}

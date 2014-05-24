
package com.holding.smile.activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;

import com.holding.smile.R;
import com.holding.smile.dto.RtnValueDto;

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
		webView = (WebView) findViewById(R.id.htmlui);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.addJavascriptInterface(new JsPlugin(), "jsPlugin");
		webView.loadUrl("file:///android_asset/index.html");
	}

	/**
	 * 这个类提供出了一个视图和业务层通信的接口。HTML 中，通过这个类的实例，间接与业务 Bean 通信。 为什么不直接将业务类提供给
	 * webView, 让 HTML 中直接访问到这个类。而多出这样 "插件"~~ 我想：目前的这样一种架构，Activity
	 * 甚至有些类似于控制器的概念了。有点像 struts 中的 Action。 在使用了 struts 框架的项目架构中，Action
	 * 也是被划分到视图层的。它和JSP页面共同完成准备数据和页面跳转的工作。 因此，这里我们也不应该让 HTML 中的 JS
	 * 直接与业务层耦合。实现表现层和业务层的解耦
	 */
	private class JsPlugin {
		/**
		 * 此方法将执行 JS 代码，调用 JS 函数：show() 实现，将活动产品列表信息展示到 HTML 页面上
		 */
		@SuppressWarnings("unused")
		public void getGoodsList() {
			RtnValueDto rtnValue = MyApplication.getInstance().getDataService().getIndexJGoods(1);
			try {
				JSONArray array = new JSONArray();
				// for (JGoods contact : contacts) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("id", 1);
				jsonObject.put("gid", 12);
				jsonObject.put("name", "coach");
				array.put(jsonObject);
				// }
				String json = array.toString();
				webView.loadUrl("javascript:show('" + json + "')");
			} catch (JSONException e) {
				Log.i(TAG, e.toString());
			}
		}

		/**
		 * 调用详情页
		 */
		@SuppressWarnings("unused")
		public void call(String gid, String bs) {
			Intent intent = new Intent(HtmlUIActivity.this, GoodsDetailActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.putExtra("gid", gid);
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

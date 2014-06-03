package com.holding.smile.tools;

import android.webkit.WebView;

public class TbUtil {
	private static WebView webView;
	static {
		System.loadLibrary("jni_curl");
	}
	
	synchronized public static native void init();

	synchronized public static native void init(String url);

	synchronized public static native void cleanup();
	
	public static native void cshTb();

	public static WebView getWebView() {
		return webView;
	}

	public static void setWebView(WebView webView) {
		TbUtil.webView = webView;
	}
}

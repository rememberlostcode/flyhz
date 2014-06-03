package com.holding.smile.tools;

public class TbUtil {
	static {
		System.loadLibrary("jni_curl");
	}
	synchronized public static native void init();

	synchronized public static native void init(String url);

	synchronized public static native void cleanup();
	
	public static native void cshTb();
}

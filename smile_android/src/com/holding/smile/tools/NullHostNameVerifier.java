
package com.holding.smile.tools;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import com.holding.smile.activity.MyApplication;

import android.util.Log;

public class NullHostNameVerifier implements HostnameVerifier {

	public boolean verify(String hostname, SSLSession session) {
		Log.i(MyApplication.getClassName(this.getClass().getName()), "RestUtilImpl:Approving certificate for " + hostname);
		return true;
	}
}

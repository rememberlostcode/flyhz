package com.holding.smile.tools;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import android.util.Log;

public class NullHostNameVerifier implements HostnameVerifier {

	public boolean verify(String hostname, SSLSession session) {
		Log.i("RestUtilImpl", "Approving certificate for " + hostname);
		return true;
	}
}

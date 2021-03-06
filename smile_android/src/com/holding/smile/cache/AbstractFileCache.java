
package com.holding.smile.cache;

import java.io.File;

import android.content.Context;
import android.util.Log;

import com.holding.smile.activity.MyApplication;
import com.holding.smile.tools.FileHelper;

public abstract class AbstractFileCache {

	private String	dirString;

	public AbstractFileCache(Context context) {

		dirString = getCacheDir();
		boolean ret = FileHelper.createDirectory(dirString);
		Log.e(MyApplication.getClassName(this.getClass().getName()), "FileHelper.createDirectory:" + dirString + ", ret = " + ret);
	}

	public File getFile(String url) {
		File f = new File(getSavePath(url));
		if (f == null || !f.exists()) {
			FileHelper.createDirectory(dirString);
			f = new File(getSavePath(url));
		}
		return f;
	}

	public abstract String getSavePath(String url);

	public abstract String getCacheDir();

	public void clear() {
		FileHelper.deleteDirectory(dirString);
	}

}

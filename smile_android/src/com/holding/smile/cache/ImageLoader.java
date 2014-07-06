
package com.holding.smile.cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import com.holding.smile.activity.MyApplication;
import com.holding.smile.tools.StrUtils;

public class ImageLoader {

	private MemoryCache				memoryCache	= new MemoryCache();
	private AbstractFileCache		fileCache;
	private Map<ImageView, String>	imageViews	= Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
	// 线程池
	private ExecutorService			executorService;

	public ImageLoader(Context context) {
		fileCache = new FileCache(context);
		executorService = Executors.newFixedThreadPool(5);
	}

	// 最主要的方法
	public void DisplayImage(String url, ImageView imageView, boolean isLoadOnlyFromCache) {
		if (!StrUtils.isNotEmpty(url))
			return;

		imageViews.put(imageView, url);

		// 先从内存缓存中查找
		Bitmap bitmap = memoryCache.get(url);
		if (bitmap != null)
			imageView.setImageBitmap(bitmap);
		else if (!isLoadOnlyFromCache) {
			// 若没有的话则开启新线程加载图片
			queuePhoto(url, imageView);
		}
	}

	/**
	 * 先从缓存中获取图片，没有再去网络下载并加入缓存中
	 * 
	 * @param url
	 * @return
	 */
	public Bitmap getBitmapFromCache(String url) {
		if (!StrUtils.isNotEmpty(url))
			return null;
		// 先从内存缓存中查找
		Bitmap bitmap = memoryCache.get(url);
		if (bitmap != null) {
			return bitmap;
		} else {
			bitmap = getBitmap(url, false);
			if (bitmap != null)
				memoryCache.put(url, bitmap);
			return bitmap;
		}
	}

	private void queuePhoto(String url, ImageView imageView) {
		PhotoToLoad p = new PhotoToLoad(url, imageView);
		executorService.submit(new PhotosLoader(p));
	}

	/**
	 * 
	 * @param url
	 * @param bigFlag
	 *            为true取原图，false按比例取图片
	 * @return
	 */
	public Bitmap getBitmap(String url, boolean bigFlag) {
		int maxWidth = 0;
		File f = null;
		if (bigFlag) {
			f = fileCache.getFile(url + "big");
		} else {
			float density = MyApplication.getInstance().getDensity();
			maxWidth = (int) (MyApplication.getInstance().getScreenWidth() * density);
			f = fileCache.getFile(url);
		}

		// 先从文件缓存中查找是否有
		Bitmap b = null;
		if (f != null && f.exists()) {
			b = decodeFile(f, maxWidth, 0);
		}
		if (b != null) {
			return b;
		}
		// 最后从指定的url中下载图片
		try {
			Bitmap bitmap = null;
			URL imageUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
			conn.setConnectTimeout(30000);
			conn.setReadTimeout(30000);
			conn.setInstanceFollowRedirects(true);
			InputStream is = conn.getInputStream();
			OutputStream os = new FileOutputStream(f);
			CopyStream(is, os);
			os.close();
			bitmap = decodeFile(f, maxWidth, 0);
			return bitmap;
		} catch (Exception ex) {
			Log.e(MyApplication.LOG_TAG,
					"getBitmap catch Exception...\nmessage = " + ex.getMessage());
			return null;
		}
	}

	// decode这个图片并且按比例缩放以减少内存消耗，虚拟机对每张图片的缓存大小也是有限制的
	private Bitmap decodeFile(File f, int maxWidth, int maxHeight) {
		try {
			// decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);

			// Find the correct scale value. It should be the power of 2.
			/**
			 * int REQUIRED_SIZE = maxWidth; int width_tmp = o.outWidth,
			 * height_tmp = o.outHeight; int scale = 1; while (true) { if
			 * (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
			 * break; width_tmp /= 2; height_tmp /= 2; scale *= 2; // }
			 */
			int scale = calcScaleRatio(o, maxWidth, maxHeight);
			if (scale < 1) {
				scale = 1;
			}

			// decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
		} catch (FileNotFoundException e) {
		}
		return null;
	}

	public static int calcScaleRatio(BitmapFactory.Options options, int maxWidth, int maxHeight) {
		double ratio = 1D;
		if (maxWidth > 0 && maxHeight <= 0) {
			// 限定宽度，高度不做限制
			ratio = java.lang.Math.ceil(options.outWidth / maxWidth);
		} else if (maxHeight > 0 && maxWidth <= 0) {
			// 限定高度，不限制宽度
			ratio = java.lang.Math.ceil(options.outHeight / maxHeight);
		} else if (maxWidth > 0 && maxHeight > 0) {
			// 高度和宽度都做了限制，这时候我们计算在这个限制内能容纳的最大的图片尺寸，不会使图片变形
			double _widthRatio = java.lang.Math.ceil(options.outWidth / maxWidth);
			double _heightRatio = (double) java.lang.Math.ceil(options.outHeight / maxHeight);
			// ratio = _widthRatio > _heightRatio ? _widthRatio : _heightRatio;
			ratio = _widthRatio < _heightRatio ? _widthRatio : _heightRatio;
		}
		return (int) ratio;
	}

	// Task for the queue
	private class PhotoToLoad {
		public String		url;
		public ImageView	imageView;

		public PhotoToLoad(String u, ImageView i) {
			url = u;
			imageView = i;
		}
	}

	class PhotosLoader implements Runnable {
		PhotoToLoad	photoToLoad;

		PhotosLoader(PhotoToLoad photoToLoad) {
			this.photoToLoad = photoToLoad;
		}

		@Override
		public void run() {
			if (imageViewReused(photoToLoad))
				return;
			Bitmap bmp = getBitmap(photoToLoad.url, false);
			memoryCache.put(photoToLoad.url, bmp);
			if (imageViewReused(photoToLoad))
				return;
			BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
			// 更新的操作放在UI线程中
			Activity a = (Activity) photoToLoad.imageView.getContext();
			a.runOnUiThread(bd);
		}
	}

	/**
	 * 防止图片错位
	 * 
	 * @param photoToLoad
	 * @return
	 */
	boolean imageViewReused(PhotoToLoad photoToLoad) {
		String tag = imageViews.get(photoToLoad.imageView);
		if (tag == null || !tag.equals(photoToLoad.url))
			return true;
		return false;
	}

	/**
	 * 
	 * 类说明：用于在UI线程中更新界面
	 * 
	 * @author robin 2014-4-15上午10:00:02
	 * 
	 */
	class BitmapDisplayer implements Runnable {
		Bitmap		bitmap;
		PhotoToLoad	photoToLoad;

		public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
			bitmap = b;
			photoToLoad = p;
		}

		@Override
		public void run() {
			if (imageViewReused(photoToLoad))
				return;
			if (bitmap != null)
				photoToLoad.imageView.setImageBitmap(bitmap);

		}
	}

	/**
	 * 清理内存缓存
	 */
	public void clearMemoryCache() {
		memoryCache.clear();
		fileCache.clear();
	}

	/**
	 * 清理文件缓存
	 */
	public void clearFileCache() {
		fileCache.clear();
	}

	public static void CopyStream(InputStream is, OutputStream os) {
		final int buffer_size = 1024;
		try {
			byte[] bytes = new byte[buffer_size];
			for (;;) {
				int count = is.read(bytes, 0, buffer_size);
				if (count == -1)
					break;
				os.write(bytes, 0, count);
			}
		} catch (Exception ex) {
			Log.e(MyApplication.LOG_TAG, "CopyStream catch Exception...");
		}
	}
}
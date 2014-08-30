
package com.holding.smile.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.holding.smile.activity.MyApplication;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

public class ImgUtil {

	private static ImgUtil	singleton	= new ImgUtil();

	public static ImgUtil getInstance() {
		return singleton;
	}

	/**
	 * 建立HTTP请求，并获取Bitmap对象。
	 * 
	 * @param imageUrl
	 *            图片的URL地址
	 * @return 解析后的Bitmap对象
	 */
	public void downloadBitmapFromNet2Sdcard(String imageUrl, File file) {
		HttpURLConnection con = null;
		try {
			URL url = new URL(imageUrl);
			con = (HttpURLConnection) url.openConnection();
			con.setConnectTimeout(5 * 1000);
			con.setRequestMethod("GET");
			con.setDoInput(true);
			if (con.getResponseCode() == 200) {
				InputStream is = con.getInputStream();
				FileOutputStream fos = new FileOutputStream(file);
				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len = is.read(buffer)) != -1) {
					fos.write(buffer, 0, len);
				}
				is.close();
				fos.close();
			} else {
				Log.d(MyApplication.getClassName(this.getClass().getName()), "Get bitmap failed");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (con != null) {
				con.disconnect();
			}
		}
	}

	public Bitmap getBitmap(String imageUrl) {
		Bitmap bitmap = null;
		// HttpURLConnection con = null;
		// try {
		// String name = MD5.getMD5(imageUrl);
		// File file = new File(MyApplication.getInstance().getCache(), name);
		// if (!file.exists()) {
		// downloadBitmapFromNet2Sdcard(imageUrl, file);
		// }
		// bitmap = getBitmapFromSdcard(file.getAbsolutePath());
		// } catch (Exception e) {
		// e.printStackTrace();
		// } finally {
		// if (con != null) {
		// con.disconnect();
		// }
		// }
		return bitmap;
	}

	public Bitmap getBitmapFromSdcard(String pathName) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Config.ARGB_8888;
		options.inPurgeable = true;// 允许可清除
		options.inInputShareable = true;// 以上options的两个属性必须联合使用才会有效果
		Bitmap bitmap = BitmapFactory.decodeFile(pathName, options);
		return bitmap;
	}

	public static Bitmap getImageBitmapFromLocal(String path) {
		// Uri uri = getImageURIFromLocal(path,
		// MyApplication.getInstance().getCache());
		// return uri2Bitmap(uri);
		return null;
	}

	public static Bitmap getImageBitmapFromLocalDir(String dir) {
		Uri uri = getImageURIFromLocalDir(dir);
		return uri2Bitmap(uri);
	}

	public static Bitmap getImageBitmap(String path) {
		// Uri uri = getImageURI(path, MyApplication.getInstance().getCache());
		// return uri2Bitmap(uri);
		return null;
	}

	private static Bitmap uri2Bitmap(Uri uri) {
		if (uri != null) {
			BitmapFactory.Options options = new BitmapFactory.Options();

			// options.inJustDecodeBounds = true;
			// BitmapFactory.decodeFile(uri.getPath(), options);
			// options.inSampleSize = computeSampleSize(options, -1, 128*128);
			// options.inJustDecodeBounds = false;

			options.inPreferredConfig = Config.ARGB_8888;
			options.inPurgeable = true;// 允许可清除
			options.inInputShareable = true;// 以上options的两个属性必须联合使用才会有效果

			Bitmap bitmap = BitmapFactory.decodeFile(uri.getPath(), options);
			return bitmap;
		}
		return null;
	}

	public static Uri getImageURIFromLocal(String path, File cache) {
		try {
			if (path == null || "".equals(path.trim())) {
				return null;
			}
			String name = MD5.getMD5(path);
			File file = new File(cache, name);
			// 如果图片存在本地缓存目录，则不去服务器下载
			if (file.exists()) {
				return Uri.fromFile(file);// Uri.fromFile(path)这个方法能得到文件的URI
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}

	private static Uri getImageURIFromLocalDir(String dir) {
		try {
			if (dir == null || "".equals(dir.trim())) {
				return null;
			}
//			String name = MD5.getMD5(dir);
			File file = new File(dir);
			// 如果图片存在本地缓存目录，则不去服务器下载
			if (file.exists()) {
				return Uri.fromFile(file);// Uri.fromFile(path)这个方法能得到文件的URI
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}

	/*
	 * 从网络上获取图片，如果图片在本地存在的话就直接拿，如果不存在再去服务器上下载图片 这里的path是图片的地址
	 */
	public static Uri getImageURI(String path, File cache) {
		HttpURLConnection conn = null;
		try {
			String name = MD5.getMD5(path);
			File file = new File(cache, name);
			// 如果图片存在本地缓存目录，则不去服务器下载
			if (file.exists()) {
				return Uri.fromFile(file);// Uri.fromFile(path)这个方法能得到文件的URI
			} else {
				// 从网络上获取图片
				URL url = new URL(path);
				conn = (HttpURLConnection) url.openConnection();
				conn.setConnectTimeout(5000);
				conn.setRequestMethod("GET");
				conn.setDoInput(true);
				if (conn.getResponseCode() == 200) {

					InputStream is = conn.getInputStream();
					FileOutputStream fos = new FileOutputStream(file);
					byte[] buffer = new byte[1024];
					int len = 0;
					while ((len = is.read(buffer)) != -1) {
						fos.write(buffer, 0, len);
					}
					is.close();
					fos.close();
					// 返回一个URI对象
					return Uri.fromFile(file);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
		return null;
	}

	public static int computeSampleSize(BitmapFactory.Options options, int minSideLength,
			int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);

		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}

		return roundedSize;
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength,
			int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;

		int lowerBound = (maxNumOfPixels == -1) ? 1
				: (int) java.lang.Math.ceil(java.lang.Math.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) java.lang.Math.min(
				java.lang.Math.floor(w / minSideLength), java.lang.Math.floor(h / minSideLength));

		if (upperBound < lowerBound) {
			// return the larger one when there is no overlapping zone.
			return lowerBound;
		}

		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}
}


package com.holding.smile.tools;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.holding.smile.activity.MyApplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;

/**
 * 
 * 图片工具类
 * 
 * @author robin
 * 
 */
public class BitmapUtils {

	public static int	width	= 480;
	public static int	height	= 800;

	/**
	 * 从文件解析出Bitmap格式的图片
	 * 
	 * @param path
	 * @param maxWidth
	 * @param maxHeight
	 * @return
	 */
	public static Bitmap decodeFile(String path, int maxWidth, int maxHeight) {
		// if (path.lastIndexOf(".png") > -1 || path.lastIndexOf(".PNG") > -1) {
		// try {
		// return BitmapFactory.decodeStream(new FileInputStream(new
		// File(path)));
		// } catch (FileNotFoundException e) {
		// return null;
		// }
		// }
		if (path == null || "".equals(path)) {
			return null;
		}
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		Bitmap image = null;
		image = BitmapFactory.decodeFile(path, options);
		int sampleSize = calcScaleRatio(options, maxWidth, maxHeight);
		if (sampleSize < 1) {
			sampleSize = 1;
		}
		options.inSampleSize = sampleSize;
		options.inJustDecodeBounds = false;
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		image = BitmapFactory.decodeFile(path, options);

		// 缩放后再旋转为正
		int degree = readPicDegree(path);
		if (degree != 0)
			image = rotateBitmap(image, degree);
		return image;
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

	/**
	 * 翻转图
	 * 
	 * @param path
	 * @param degree
	 *            旋转角度
	 * @param maxWidth
	 * @param maxHeight
	 * @return
	 */
	public static Bitmap getRotateBitmap(String path, int degree, int maxWidth, int maxHeight) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		Bitmap bitmap = null;
		bitmap = BitmapFactory.decodeFile(path, options);
		int sampleSize = calcScaleRatio(options, maxWidth, maxHeight);
		if (sampleSize < 1) {
			sampleSize = 1;
		}
		options.inSampleSize = sampleSize;
		options.inJustDecodeBounds = false;
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		bitmap = BitmapFactory.decodeFile(path, options);

		// 缩放后再旋转为正
		if (degree != 0)
			bitmap = rotateBitmap(bitmap, degree);
		return bitmap;

	}

	/**
	 * 获取图像的字节大小
	 * 
	 * @param bm
	 * @return
	 */
	public static byte[] bitmap2Bytes(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}

	/**
	 * 获取得翻转度
	 * 
	 * @param imagePath
	 * @return
	 */
	public static int readPicDegree(String imagePath) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(imagePath);
			int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
				case ExifInterface.ORIENTATION_ROTATE_90:
					degree = 90;
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:
					degree = 180;
					break;
				case ExifInterface.ORIENTATION_ROTATE_270:
					degree = 270;
					break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}

	/**
	 * 旋转图片
	 * 
	 * @param bitmap
	 * @param rotate
	 *            旋转角度
	 * @return
	 */
	public static Bitmap rotateBitmap(Bitmap bitmap, int rotate) {
		if (bitmap == null)
			return null;

		Matrix mtx = new Matrix();
		mtx.postRotate(rotate);
		// 创建新的图片
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
				bitmap.getHeight(), mtx, true);
		return resizedBitmap;

	}

	/**
	 * 保持长宽比缩小Bitmap
	 * 
	 * @param bitmap
	 * @param maxWidth
	 * @param maxHeight
	 * @return
	 */
	public static Bitmap resizeBitmap(Bitmap bitmap, int maxWidth, int maxHeight) {

		int originWidth = bitmap.getWidth();
		int originHeight = bitmap.getHeight();

		// no need to resize
		if (originWidth < maxWidth && originHeight < maxHeight) {
			return bitmap;
		}

		int width = originWidth;
		int height = originHeight;

		// 若图片过宽, 则保持长宽比缩放图片
		if (originWidth > maxWidth) {
			width = maxWidth;

			double i = originWidth * 1.0 / maxWidth;
			height = (int) Math.floor(originHeight / i);

			bitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
		}

		// 若图片过长, 则从上端截取
		if (height > maxHeight) {
			height = maxHeight;
			bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height);
		}

		// Log.i(MyApplication.getClassName(this.getClass().getName()), width + " width");
		// Log.i(MyApplication.getClassName(this.getClass().getName()), height + " height");

		return bitmap;
	}

	/**
	 * 质量压缩方法
	 * 
	 * @param bitmap
	 * @return
	 */
	public static ByteArrayOutputStream compressBitmap(Bitmap bitmap, int size) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);// 质量压缩方法，先压缩10%，100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while ((baos.toByteArray().length / 1024) > size) { // 循环判断如果压缩后图片是否大于1M,大于继续压缩
			baos.reset();// 重置baos即清空baos
			options -= 10;// 每次都减少10
			bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
		}

		// 释放占用的内存
		if (bitmap != null && !bitmap.isRecycled()) {
			bitmap.recycle();
			bitmap = null;

		}
		return baos;
	}

	/**
	 * 图片按比例大小压缩方法
	 * 
	 * @param srcPath
	 * @param sWidth
	 * @param sHeight
	 * @param size
	 *            压缩至size指定大小
	 * @return
	 */
	public static ByteArrayOutputStream getCompressBitmap(String srcPath, int sWidth, int sHeight,
			int size) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空

		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		// 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
		// float hh = 800f;// 这里设置高度为800f
		// float ww = 480f;// 这里设置宽度为480f
		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;// be=1表示不缩放
		if (w > h && w > sWidth) {// 如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / sWidth);
		} else if (w < h && h > sHeight) {// 如果高度高的话根据宽度固定大小缩放
			be = (int) (newOpts.outHeight / sHeight);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// 设置缩放比例
		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
		// 缩放后再旋转为正
		int degree = BitmapUtils.readPicDegree(srcPath);
		if (degree != 0)
			bitmap = BitmapUtils.rotateBitmap(bitmap, degree);
		return compressBitmap(bitmap, size);// 压缩好比例大小后再进行质量压缩至指定大小size
	}

	public static String pngToJPEG(String imagePath, String outputPath) {
		if (outputPath == null)
			outputPath = imagePath.substring(0, imagePath.lastIndexOf(".") + 1) + "jpg";
		FileOutputStream fos = null;
		FileInputStream fis = null;
		try {
			File file = new File(outputPath);
			fos = new FileOutputStream(file);
			fis = new FileInputStream(new File(imagePath));
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = fis.read(buffer)) != -1) {
				fos.write(buffer, 0, len);
			}
			fos.flush();
		} catch (FileNotFoundException exception) {
			Log.e(MyApplication.getClassName(BitmapUtils.class.getName()), "debug_log:" + exception.toString());
		} catch (IOException exception) {
			Log.e(MyApplication.getClassName(BitmapUtils.class.getName()), "debug_log:" + exception.toString());
		} finally {
			try {
				if (fos != null)
					fos.close();
				if (fis != null)
					fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return outputPath;
	}
}

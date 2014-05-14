
package com.flyhz.avengers.util.image;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 从网络获取图片到本地
 * 
 * @author zhangb
 * 
 */
public class ImageUtil {
	Logger					log			= LoggerFactory.getLogger(ImageUtil.class);
	/**
	 * 文件的根目录
	 */
	public String			rootPath	= "E:/tmp/images";
	/**
	 * 最大连接次数限制
	 */
	public static final int	MAX_NUM		= 3;

	private ImageUtil() {

	}

	/**
	 * 
	 * @param rootPath
	 *            保存到本地磁盘的根目录
	 */
	public ImageUtil(String rootPath) {
		this.rootPath = rootPath;
	}

	/**
	 * 下载图片
	 * 
	 * @param image
	 */
	public void downloadImage(Image image) {
		if (image.getNumber() <= MAX_NUM) {
			ImageUtil imageUtil = new ImageUtil();
			byte[] btImg = imageUtil.getImageFromNetByUrl(image);
			if (null != btImg && btImg.length > 0) {
				image.setSize(btImg.length);
				SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
				String dateString = df.format(new Date());
				if (image.getFileName() == null || "".equals(image.getFileName().trim())) {
					image.setStaticPath("/" + dateString.substring(0, 8) + "/" + dateString + "_"
							+ new Random().nextInt(1000) + ".jpg");
					image.setFileName(dateString + "_" + new Random().nextInt(1000) + ".jpg");
					image.setFilePath(rootPath + image.getStaticPath());
				} else {
					image.setStaticPath("/" + dateString.substring(0, 8) + "/"
							+ image.getFileName());
					image.setFilePath(rootPath + image.getStaticPath());
				}
				imageUtil.writeImageToDisk(btImg, image);
			}
			// else {
			// addError(image, "连接成功但未获取到图片流");
			// }
		}
	}

	/**
	 * 将图片写入到磁盘
	 * 
	 * @param img
	 *            图片数据流
	 * @param fileName
	 *            文件保存时的名称
	 */
	private void writeImageToDisk(byte[] img, Image image) {
		File dirFile = new File(image.getFilePath().substring(0,
				image.getFilePath().lastIndexOf("/")));
		// 检查目录是否存在
		if (!dirFile.exists()) {
			dirFile.mkdirs();
		}
		// 检查目录写权限
		if (!dirFile.canWrite()) {
			addError(image, "文件根目录没有写权限。");
			return;
		}
		FileOutputStream fops = null;
		try {
			File dir = new File(rootPath);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			File file = new File(image.getFilePath());
			fops = new FileOutputStream(file);
			fops.write(img);
			fops.flush();
			fops.close();
			image.setFinshed(true);
		} catch (FileNotFoundException e) {
			log.error(e.getMessage());
			addError(image, e.getMessage());
		} catch (IOException e) {
			log.error(e.getMessage());
			addError(image, e.getMessage());
		} finally {
			if (fops != null) {
				try {
					fops.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 根据地址获得数据的字节流
	 * 
	 * @param strUrl
	 *            网络连接地址
	 * @return
	 */
	private byte[] getImageFromNetByUrl(Image image) {
		String strUrl = image.getUrl();
		if (strUrl == null || "".equals(strUrl)) {
			addError(image, "url为空");
		}
		InputStream inStream = null;
		byte[] btImg = null;
		try {
			URL url = new URL(strUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(5 * 1000);
			inStream = conn.getInputStream();// 通过输入流获取图片数据
			btImg = readInputStream(inStream);// 得到图片的二进制数据
			return btImg;
		} catch (SocketTimeoutException e) {
			log.error(e.getMessage());
			addError(image, e.getMessage());
		} catch (IOException e) {
			log.error(e.getMessage());
			addError(image, e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage());
			addError(image, e.getMessage());
		} finally {
			if (inStream != null) {
				try {
					inStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return btImg;
	}

	/**
	 * 从输入流中获取数据
	 * 
	 * @param inStream
	 *            输入流
	 * @return
	 * @throws Exception
	 */
	private byte[] readInputStream(InputStream inStream) throws Exception {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		byte[] outBuffer = new byte[1024];
		int len = 0;
		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		outBuffer = outStream.toByteArray().clone();
		inStream.close();
		return outBuffer;
	}

	private void addError(Image image, String errorMessage) {
		image.getMessage().add(errorMessage);
		image.setNumber(image.getNumber() + 1);
		if (image.getNumber() <= MAX_NUM) {
			ImagePool.getNextImage(image);
		} else {
			String msg = "超过了限制的尝试次数，结束本次下载！";
			image.getMessage().add(msg);
			log.warn(msg);
		}
	}

	public static void main(String[] args) {
		Image image = new Image("http://s7d2.scene7.com/is/image/Coach/99864_b4baj_a0?$pd_main$");

		// image.setUrl("http://www.baidu.com/img/bdlogo.gif");
		ImageUtil imageUtil = new ImageUtil("E:/tmp/images");
		imageUtil.downloadImage(image);
		System.exit(0);
	}
}
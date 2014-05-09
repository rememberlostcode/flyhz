
package com.flyhz.avengers.util.image;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 访问图片线程
 * 
 * @author zhangb 2014年5月8日 下午3:39:10
 * 
 */
public class ImageThread extends Thread {
	Logger			log	= LoggerFactory.getLogger(ImageThread.class);
	private Image	image;
	private String	rootPath;

	/**
	 * 初始化一个图片线程
	 * 
	 * @param threadName
	 * @param image
	 */
	public ImageThread(String threadName, Image image) {
		super(threadName);
		this.image = image;
		this.rootPath = "E:/tmp/images";
	}

	/**
	 * 初始化一个图片线程
	 * 
	 * @param threadName
	 *            线程名字
	 * @param image
	 *            图片
	 * @param rootPath
	 *            下载后保存的根目录
	 */
	public ImageThread(String threadName, Image image, String rootPath) {
		super(threadName);
		this.image = image;
		this.rootPath = rootPath;
	}

	@Override
	public void run() {
		ImageUtil imageUtil = new ImageUtil(rootPath);
		imageUtil.downloadImage(image);
		if (image.isFinshed()) {
			log.info("图片已经写入到磁盘目录：" + image.getFilePath());
		} else {
			// log.error("图片下载失败，" + image.getMessage());
		}
	}

	public static void main(String[] args) {
		Image image = new Image("http://s7d2.scene7.com/is/image/Coach/99864_b4baj_a0?$pd_main$");
		new ImageThread("test", image).start();
	}
}

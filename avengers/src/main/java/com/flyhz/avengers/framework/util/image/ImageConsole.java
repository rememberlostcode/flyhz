
package com.flyhz.avengers.framework.util.image;

import java.util.LinkedList;

public class ImageConsole {
	private static ImageMitiThread	it;

	private ImageConsole() {
	}

	public static void main(String[] rags) {
		startDownloadThread();

		addImage(new Image("http://s7d2.scene7.com/is/image/Coach/99864_b4baj_a0?$pd_main$"));
		addImage(new Image("http://s7d2.scene7.com/is/image/Coach/99864_b4baj_a0?$pd_main$"));
		addImage(new Image("http://s7d2.scene7.com/is/image/Coach/99864_b4baj_a0?$pd_main$"));
		addImage(new Image("http://s7d2.scene7.com/is/image/Coa"));
		addImage(new Image("http://s7d2.scene7.com/is/image/Coach/99864_b4baj_a0?$pd_main$"));
		addImage(new Image("http://s7d2.scene7.com/is/image/Coach/99864_b4baj_a0?$pd_main$"));
		addImage(new Image("http://s7d2.scene7.com/is/image/Coach/99864_b4baj_a0?$pd_main$"));
		addImage(new Image("http://s7d2.scene7.com/is/image/Coach/99864_b4baj_a0?$pd_main$"));

		// it.stopDownloadThread();
	}

	/**
	 * 开始下载图片进程
	 */
	public static void startDownloadThread() {
		if (it == null) {
			it = new ImageMitiThread();
			it.start();
		}
	}

	/**
	 * 添加图片下载
	 * 
	 * @param image
	 *            url不能为空
	 */
	public static void addImage(Image image) {
		ImagePool.getNextImage(image);
	}

	/**
	 * 获取已结束下载的图片（包括成功和失败的）
	 * 
	 * @return
	 */
	public static LinkedList<Image> getFinshedImage() {
		return ImagePool.getFinshedImage(null);
	}

}


package com.flyhz.avengers.util.image;

import java.util.LinkedList;

public class ImageConsole {

	public static void main(String[] rags) {
		start();

		addImage(new Image("http://s7d2.scene7.com/is/image/Coach/99864_b4baj_a0?$pd_main$"));
		addImage(new Image("http://s7d2.scene7.com/is/image/Coach/99864_b4baj_a0?$pd_main$"));
		addImage(new Image("http://s7d2.scene7.com/is/image/Coach/99864_b4baj_a0?$pd_main$"));
		addImage(new Image("http://s7d2.scene7.com/is/image/Coa"));
		addImage(new Image("http://s7d2.scene7.com/is/image/Coach/99864_b4baj_a0?$pd_main$"));

		// it.stopDownloadThread();
	}

	public static void start() {
		ImageMitiThread it = new ImageMitiThread();
		it.start();
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
	 * 获取已完成下载的图片
	 * 
	 * @return
	 */
	public static LinkedList<Image> getFinshedImage() {
		return ImagePool.getFinshedImage(null);
	}

}

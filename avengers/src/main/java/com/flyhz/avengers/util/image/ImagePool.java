
package com.flyhz.avengers.util.image;

import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 图片池
 * 
 * @author zhangb 2014年5月9日 上午10:25:46
 * 
 */
public class ImagePool {
	Logger								log			= LoggerFactory.getLogger(ImagePool.class);
	private static LinkedList<Image>	imagesList	= new LinkedList<Image>();

	/**
	 * 添加到线程的图片集合
	 * 
	 * @param image
	 */
	public static synchronized void addImage(Image image) {
		imagesList.add(image);
	}

	/**
	 * 获取下一个图片
	 * 
	 * @return
	 */
	public static synchronized Image getNextImage() {
		Image image = null;
		if (imagesList.size() > 0) {
			image = imagesList.get(0);
			imagesList.remove(0);
		}
		return image;
	}
}

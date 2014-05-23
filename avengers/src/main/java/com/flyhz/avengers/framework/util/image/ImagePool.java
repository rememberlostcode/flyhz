
package com.flyhz.avengers.framework.util.image;

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
	Logger								log					= LoggerFactory.getLogger(ImagePool.class);
	/**
	 * 待下载图片
	 */
	private static LinkedList<Image>	imagesList			= new LinkedList<Image>();
	/**
	 * 已下载图片
	 */
	private static LinkedList<Image>	imagesFinshedList	= new LinkedList<Image>();
	private static int					threadNum			= 0;

	/**
	 * 获取下一个待下载的图片/添加一个待下载的图片
	 * 
	 * @param image
	 *            参数是null时，返回下一个图片；不是null时，image添加到线程的图片数组
	 * @return
	 */
	public static synchronized Image getNextImage(Image image) {
		if (image == null) {
			if (imagesList.size() > 0) {
				image = imagesList.get(0);
				imagesList.remove(0);
			}
			return image;
		} else {
			imagesList.add(image);
			return null;
		}
	}

	/**
	 * 获取下一个已完成下载的图片/添加已完成下载的图片
	 * 
	 * @param image
	 *            参数是null时，返回所有已完成下载/下载失败的图片；不是null时，image添加到已完成下载的图片数组
	 * @return
	 */
	public static synchronized LinkedList<Image> getFinshedImage(Image image) {
		if (image == null) {
			@SuppressWarnings("unchecked")
			LinkedList<Image> list = (LinkedList<Image>) imagesFinshedList.clone();
			imagesFinshedList.clear();
			return list;
		} else {
			imagesFinshedList.add(image);
			return null;
		}
	}

	/**
	 * 加减当前线程数并获取当前线程数
	 * 
	 * @param number
	 *            加减的值，一般是1或者-1；要获得当前的线程数值使用0
	 * @return
	 */
	public static synchronized int getThreadNum(int number) {
		threadNum += number;
		return threadNum;
	}
}

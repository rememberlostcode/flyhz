
package com.flyhz.avengers.framework;

import java.util.LinkedList;

import com.flyhz.avengers.util.image.Image;

public interface ResultFilter {

	/**
	 * 开始处理图片
	 */
	public void startDownloadThread();

	/**
	 * 添加图片下载
	 * 
	 * @param image
	 *            url不能为空
	 */
	public void addImage(Image image);

	/**
	 * 获取已结束下载的图片（包括成功和失败的）
	 * 
	 * @return
	 */
	public LinkedList<Image> getFinshedImage();
}

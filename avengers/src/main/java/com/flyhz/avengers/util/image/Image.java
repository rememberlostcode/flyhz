
package com.flyhz.avengers.util.image;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Image implements Serializable {
	private static final long	serialVersionUID	= 3105757482563141700L;

	/**
	 * 图片相对路径（包括文件名）
	 */
	private String				staticPath;
	/**
	 * 图片的url路径
	 */
	private String				url;
	/**
	 * 图片绝对路径
	 */
	private String				filePath;
	/**
	 * 图片文件名
	 */
	private String				fileName;
	/**
	 * 图片的大小，单位字节
	 */
	private Integer				size;

	private boolean				isFinshed;
	private List<String>		message;
	private int					number;

	@SuppressWarnings("unused")
	private Image() {
	}

	/**
	 * 初始化一个image对象
	 * 
	 * @param url
	 *            图片的url路径
	 */
	public Image(String url) {
		this.url = url;
		this.message = new ArrayList<String>();
		this.number = 1;
		this.isFinshed = false;
	}

	/**
	 * 获取图片相对路径（包括文件名）
	 * 
	 * @return
	 */
	public String getStaticPath() {
		return staticPath;
	}

	/**
	 * 设置图片相对路径（包括文件名）
	 * 
	 * @param staticPath
	 */
	public void setStaticPath(String staticPath) {
		this.staticPath = staticPath;
	}

	/**
	 * 获取图片的url路径
	 * 
	 * @return
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * 设置图片的url路径
	 * 
	 * @param url
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * 获取图片绝对路径
	 * 
	 * @return
	 */
	public String getFilePath() {
		return filePath;
	}

	/**
	 * 设置图片绝对路径
	 * 
	 * @param filePath
	 */
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	/**
	 * 获取图片的大小，单位字节
	 * 
	 * @return
	 */
	public Integer getSize() {
		return size;
	}

	/**
	 * 设置图片的大小，单位字节
	 * 
	 * @param size
	 */
	public void setSize(Integer size) {
		this.size = size;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public boolean isFinshed() {
		return isFinshed;
	}

	public void setFinshed(boolean isFinshed) {
		this.isFinshed = isFinshed;
	}

	public List<String> getMessage() {
		return message;
	}

	public void setMessage(List<String> message) {
		this.message = message;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

}


package com.flyhz.avengers.framework.util.image;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 图片
 * 
 * @author zhangb 2014年5月10日 下午12:52:56
 * 
 */
public class Image implements Serializable {
	private static final long	serialVersionUID	= 3105757482563141700L;

	private Integer				id;
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
	/**
	 * 是否成功下载完成
	 */
	private boolean				isFinshed;
	/**
	 * 下载记录消息
	 */
	private List<String>		message;
	/**
	 * 已下载次数
	 */
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

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	/**
	 * 获得图片文件名称
	 * 
	 * @return
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * 设置图片文件名称
	 * 
	 * @param fileName
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * 是否成功下载完成
	 * 
	 * @return
	 */
	public boolean isFinshed() {
		return isFinshed;
	}

	/**
	 * 设置是否成功下载完成
	 * 
	 * @param isFinshed
	 */
	public void setFinshed(boolean isFinshed) {
		this.isFinshed = isFinshed;
	}

	/**
	 * 下载记录消息
	 * 
	 * @return
	 */
	public List<String> getMessage() {
		return message;
	}

	/**
	 * 添加下载记录消息
	 * 
	 * @param message
	 */
	public void setMessage(List<String> message) {
		this.message = message;
	}

	/**
	 * 获得已下载次数
	 * 
	 * @return
	 */
	public int getNumber() {
		return number;
	}

	/**
	 * 设置已下载次数
	 * 
	 * @param number
	 */
	public void setNumber(int number) {
		this.number = number;
	}

}

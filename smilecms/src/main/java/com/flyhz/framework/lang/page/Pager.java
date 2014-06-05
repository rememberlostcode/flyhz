
package com.flyhz.framework.lang.page;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(value = { "pageSort", "pagers" })
public class Pager {

	private static final ThreadLocal<Pager>	pagers		= new ThreadLocal<Pager>();

	private int								totalRowsAmount;						// 总数
	private int								pageSize	= 10;						// 每页行数
	private int								totalPages;							// 总页数
	private int								currentPage	= 1;						// 当前页码
	private int								num			= 0;						// 显示数字
	private int								sznum		= 0;
	private String							sortWay		= ToolConstants.ASC;		// 排序方式
	private String							sortName;								// 排序字段，为数据库表字段名
	private String							pageId		= null;					// dao方法
	private String							userUrl;

	public static Pager currentPager() {
		return pagers.get();
	}

	public static void removeCurrentPager() {
		pagers.remove();
	}

	public String getSortWay() {
		return sortWay;
	}

	public void setSortWay(String sortWay) {
		this.sortWay = sortWay;
	}

	public String getSortName() {
		return sortName;
	}

	public void setSortName(String sortName) {
		this.sortName = sortName;
	}

	public String getUserUrl() {
		return userUrl;
	}

	public void setUserUrl(String userUrl) {
		this.userUrl = userUrl;
	}

	/**
	 * 构造函数。
	 * 
	 * @param totalRows
	 *            总行数
	 */
	public Pager() {
		pagers.set(this);
	}

	/**
	 * 设置总行数。
	 * 
	 * @param i
	 *            总行数。
	 */
	public void setTotalRowsAmount(int rows) {
		if (rows < 0) {
			totalRowsAmount = 0;
		} else {
			totalRowsAmount = rows;
		}
		if (totalRowsAmount % pageSize == 0) {
			totalPages = totalRowsAmount / pageSize;
		} else {
			totalPages = totalRowsAmount / pageSize + 1;
		}
	}

	/**
	 * 
	 * 显示当前数字
	 */
	public int getNum() {
		if (sznum == pageSize) {
			sznum = 0;
		}
		sznum++;
		if (currentPage != 1) {
			num = pageSize * (currentPage - 1) + sznum;
		} else {
			num++;
		}
		return num;

	}

	/**
	 * 设置当前页数。
	 * 
	 * @param i
	 */
	public void setCurrentPage(int curPage) {
		this.currentPage = curPage;

	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getPageSize() {
		return pageSize;
	}

	public int getTotalPages() {
		return totalPages;
	}

	public int getTotalRowsAmount() {
		return totalRowsAmount;
	}

	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}

	public String getPageId() {
		return pageId;
	}

	public void setPageId(String pageId) {
		this.pageId = pageId;
	}

	public void setPageClass(Class<?> clazz) {
		if (clazz != null) {
			this.pageId = clazz.getName();
		}
	}

}

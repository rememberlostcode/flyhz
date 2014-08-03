
package com.flyhz.avengers.framework.lang;

public enum AVTable {

	/**
	 * <table>
	 * <tbody>
	 * <tr>
	 * <td>av_fetch</td>
	 * <td>crawl完成后的url结果</td>
	 * </tr>
	 * <tr>
	 * <td>rowkey</td>
	 * <td>url</td>
	 * <td>爬到的URL</td>
	 * </tr>
	 * <tr>
	 * <td>family:column</td>
	 * <td>i:id</td>
	 * <td>id是自增序列</td>
	 * </tr>
	 * 
	 * <tr>
	 * <td>av_crawl</td>
	 * <td>已爬过的url临时表</td>
	 * </tr>
	 * </tbody>
	 * </table>
	 */
	av_fetch, av_crawl, av_domain, av_page, av_color;

	public enum AVFamily {
		i;
	}

	public enum AVColumn {
		// id
		id,
		// url
		url,
		// maxid
		maxid,
		// content
		c,
		bid,
		/**
		 * encoding
		 */
		e,
		// root
		r,
		// color
		co;
	}
}

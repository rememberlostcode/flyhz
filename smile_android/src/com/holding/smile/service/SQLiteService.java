
package com.holding.smile.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.holding.smile.tools.DBHelper;
import com.holding.smile.tools.DateUtil;

/**
 * 本地DB工具 ，先要初始化public SQLiteService(Context context) ，不用后需要调用closeDB()来关闭
 * 
 * @author zhangb 2014年2月25日 上午10:56:54
 * 
 */
public class SQLiteService {

	private DBHelper		helper;
	private SQLiteDatabase	db;

	/**
	 * 初始化DB
	 * 
	 * @param context
	 *            不能为null
	 */
	public SQLiteService(Context context) {
		if (context == null) {
			try {
				throw new Exception("SQLiteService:context is not init!");
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			if (helper == null || db == null) {
				helper = new DBHelper(context);
				db = helper.getWritableDatabase();
			}
		}
	}

	/**
	 * 关闭DB
	 */
	public void closeDB() {
		if (db != null) {
			db.close();
		}
	}

	/**
	 * 添加搜索词
	 * 
	 * @param content
	 * @return
	 */
	public boolean addSearch(String content) {
		try {
			Cursor cursor = db.query("SEARCH", new String[] { "ID", "CONTENT", "TIME", "COUNT" },
					"CONTENT = ?", new String[] { String.valueOf(content) }, null, null, null, "1");

			while (cursor.moveToNext()) {
				db.execSQL("UPDATE SEARCH SET COUNT=COUNT+1 WHERE CONTENT=?",
						new Object[] { content });
				return true;
			}
			db.execSQL("INSERT INTO SEARCH (CONTENT,TIME,COUNT)VALUES(?, ?, 1)", new Object[] {
					content, DateUtil.dateToStr(new Date()) });
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 获取最近的搜索词，搜得越多越在前面，最多10个
	 * 
	 * @param content
	 * @return
	 */
	public List<String> getSearchWords(String content) {
		List<String> list = new ArrayList<String>();
		Cursor cursor = null;
		if (content == null || content.trim().equals("")) {
			cursor = db.rawQuery(
					"SELECT S.CONTENT FROM SEARCH S ORDER BY S.COUNT DESC,S.TIME DESC LIMIT 10",
					null);
		} else {
			cursor = db.rawQuery(
					"SELECT S.CONTENT FROM SEARCH S WHERE S.CONTENT LIKE ? ORDER BY S.COUNT DESC,S.TIME DESC LIMIT 10",
					new String[] { content + "%" });
		}
		while (cursor != null && cursor.moveToNext()) {
			list.add(cursor.getString(0));
		}
		return list;
	}
}

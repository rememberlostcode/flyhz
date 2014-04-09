
package com.holding.smile.tools;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 本地DB工具类
 * 
 * @author zhangb 2014年2月25日 上午10:42:55
 * 
 */
public class DBHelper extends SQLiteOpenHelper {

	private static final String	DATABASE_NAME		= "smile.db";
	private static final int	DATABASE_VERSION	= 1;

	public DBHelper(Context context) {
		// CursorFactory设置为null,使用默认值
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/**
	 * 数据库第一次被创建时onCreate会被调用
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		createTable(db);
	}

	/**
	 * 如果DATABASE_VERSION值大于本地的值（说明现有数据库版本不同）,即会调用onUpgrade
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// 版本更新后的数据库变动
		db.execSQL("DROP TABLE IF EXISTS SEARCH");
		createTable(db);

	}

	/**
	 * 建表
	 * 
	 * @param db
	 */
	public void createTable(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE SEARCH"
				+ "(ID INTEGER PRIMARY KEY AUTOINCREMENT, CONTENT VARCHAR,TIME TIMESTAMP,COUNT INTEGER)");
	}
}

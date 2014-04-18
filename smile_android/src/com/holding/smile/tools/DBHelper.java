
package com.holding.smile.tools;

import java.util.List;

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
	private static final int	DATABASE_VERSION	= 6;

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
		createTable(db);
	}

	/**
	 * 建表
	 * 
	 * @param db
	 */
	public void createTable(SQLiteDatabase db) {
		db.execSQL("drop table if exists search");
		db.execSQL("drop table if exists user");
		db.execSQL("drop table if exists contury");
		db.execSQL("drop table if exists province");
		db.execSQL("drop table if exists city");
		db.execSQL("drop table if exists district");
		db.execSQL("drop table if exists consignee");

		db.execSQL("create table search (id integer primary key autoincrement, content varchar,time timestamp,count integer)");
		db.execSQL("create table user (cuid integer primary key autoincrement,username varchar,token varchar,mobilephone varchar,identitycard varchar,qq varchar,email varchar,weibo varchar,weixin varchar,flag varchar)");
		db.execSQL("create table contury (id integer primary key autoincrement,name varchar,sort integer)");
		db.execSQL("create table province (id integer primary key autoincrement,name varchar,sort integer,type varchar,conturyid integer)");
		db.execSQL("create table city (id integer primary key autoincrement,name varchar,sort integer,proid integer)");
		db.execSQL("create table district (id integer primary key autoincrement,name varchar,sort integer,cityid integer)");
		db.execSQL("create table consignee (id integer primary key autoincrement,name varchar,contury_id integer,province_id integer,cityid integer,district_id integer,address varchar,zipcode varchar,mobilephone varchar,idcard varchar,user_id integer,is_default varchar)");

		/********** 省市区 start **********/
		List<String> conList = ProvinceCityUtil.getConturyList();
		List<String> pList = ProvinceCityUtil.getProvinceList();
		List<String> cList = ProvinceCityUtil.getCityList();
		List<String> dList = ProvinceCityUtil.getDistrictList();
		for (String sql : conList) {
			db.execSQL(sql);
		}
		for (String sql : pList) {
			db.execSQL(sql);
		}
		for (String sql : cList) {
			db.execSQL(sql);
		}
		for (String sql : dList) {
			db.execSQL(sql);
		}

		ProvinceCityUtil.cleanData();
		/********** 省市区 end **********/
	}
}

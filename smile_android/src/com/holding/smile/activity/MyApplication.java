
package com.holding.smile.activity;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.holding.smile.R;
import com.holding.smile.cache.ImageLoader;
import com.holding.smile.entity.SUser;
import com.holding.smile.service.DataService;
import com.holding.smile.service.LoginService;
import com.holding.smile.service.SQLiteService;
import com.holding.smile.service.SubmitService;

/**
 * 
 * 类说明：
 * 
 * @author robin 2014-2-25下午12:21:41
 * 
 */
public class MyApplication extends Application {

	public static final String		LOG_TAG	= "smile";
	private static MyApplication	singleton;

	private static ImageLoader		mImageLoader;

	/**
	 * 屏幕宽度
	 */
	private Integer					screenWidth;
	/**
	 * 屏幕高度
	 */
	private Integer					screenHeight;
	/**
	 * 屏幕密度
	 */
	private Float					density;

	private DataService				dataService;
	private SubmitService			submitService;

	/**
	 * 本地数据库操作service
	 */
	private SQLiteService			sqliteService;

	private LoginService			loginService;

	public static String			jgoods_img_url;

	/**
	 * 当前登录用户
	 */
	private SUser					currentUser;

	@Override
	public void onCreate() {
		super.onCreate();
		singleton = this;

		Context context = getApplicationContext();
		dataService = new DataService(context);
		submitService = new SubmitService(context);
		loginService = new LoginService(context);
		mImageLoader = new ImageLoader(context);
		sqliteService = new SQLiteService(context);// 初始化本地DB

		jgoods_img_url = getString(R.string.jGoods_img_url);
	}

	public static ImageLoader getImageLoader() {
		return mImageLoader;
	}

	public DataService getDataService() {
		return dataService;
	}

	public SubmitService getSubmitService() {
		return submitService;
	}

	public LoginService getLoginService() {
		return loginService;
	}

	public static MyApplication getInstance() {
		return singleton;
	}

	public Integer getScreenWidth() {
		return screenWidth;
	}

	public void setScreenWidth(Integer screenWidth) {
		this.screenWidth = screenWidth;
	}

	public Integer getScreenHeight() {
		return screenHeight;
	}

	public void setScreenHeight(Integer screenHeight) {
		this.screenHeight = screenHeight;
	}

	public Float getDensity() {
		return density;
	}

	public void setDensity(Float density) {
		this.density = density;
	}

	public SUser getCurrentUser() {
		return currentUser;
	}

	public void setCurrentUser(SUser currentUser) {
		this.currentUser = currentUser;
	}

	public SQLiteService getSqliteService() {
		return sqliteService;
	}

	public void setSqliteService(SQLiteService sqliteService) {
		this.sqliteService = sqliteService;
	}

	/**
	 * 退出应用程序
	 */
	public void appExit() {
		try {
			/* 与按下HOME键效果一样 begin */
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.addCategory(Intent.CATEGORY_HOME);
			this.startActivity(intent);
		} catch (Exception e) {
		}
	}

}

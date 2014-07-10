
package com.holding.smile.activity;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import cn.jpush.android.api.JPushInterface;

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

	private static Date				sessionTime;
	public static final String		LOG_TAG			= "smile";
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
	private static String			tb_url;

	/**
	 * 当前登录用户
	 */
	private SUser					currentUser;

	// JSESSIONID=6CCC2F179859F7D98D2F8E35CEBD5CF4
	private String					sessionId;
	private String 					registrationID;

	/** 任务线程池 */
	private static ExecutorService	threadPool;

	private static boolean			isHasNetwork	= true;

	@Override
	public void onCreate() {
		super.onCreate();
		singleton = this;
		threadPool = Executors.newFixedThreadPool(2);

		Context context = getApplicationContext();
		dataService = new DataService(context);
		submitService = new SubmitService(context);
		loginService = new LoginService(context);
		mImageLoader = new ImageLoader(context);
		sqliteService = new SQLiteService(context);// 初始化本地DB

		jgoods_img_url = getString(R.string.prefix_url) + getString(R.string.img_static_url);
		
		JPushInterface.setDebugMode(true); 	// 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);     		// 初始化 JPush
	}

	public static ImageLoader getImageLoader() {
		return mImageLoader;
	}

	public static ExecutorService getThreadPool() {
		return threadPool;
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

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public SQLiteService getSqliteService() {
		return sqliteService;
	}

	public void setSqliteService(SQLiteService sqliteService) {
		this.sqliteService = sqliteService;
	}

	public static String getTb_url() {
		return tb_url;
	}

	public static void setTb_url(String tb_url) {
		MyApplication.tb_url = tb_url;
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

	public static Date getSessionTime() {
		return sessionTime;
	}

	public static void setSessionTime(Date sessionTime) {
		MyApplication.sessionTime = sessionTime;
	}

	public static boolean isHasNetwork() {
		return isHasNetwork;
	}

	public static void setHasNetwork(boolean isHasNetwork) {
		MyApplication.isHasNetwork = isHasNetwork;
	}

	public String getRegistrationID() {
		return registrationID;
	}

	public void setRegistrationID(String registrationID) {
		this.registrationID = registrationID;
	}

}

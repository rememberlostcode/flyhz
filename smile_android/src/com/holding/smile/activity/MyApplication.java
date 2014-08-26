
package com.holding.smile.activity;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import cn.jpush.android.api.JPushInterface;

import com.holding.smile.R;
//import com.holding.smile.cache.ImageLoader;
import com.holding.smile.entity.SUser;
import com.holding.smile.service.DataService;
import com.holding.smile.service.LoginService;
import com.holding.smile.service.SQLiteService;
import com.holding.smile.service.SubmitService;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

/**
 * 
 * 类说明：
 * 
 * @author robin 2014-2-25下午12:21:41
 * 
 */
public class MyApplication extends Application {

	private static Date				sessionTime;
	public static final String			LOG_TAG			= "smile";
	private static MyApplication		singleton;

	/**
	 * 屏幕宽度
	 */
	private Integer						screenWidth;
	/**
	 * 屏幕高度
	 */
	private Integer						screenHeight;
	/**
	 * 屏幕密度
	 */
	private Float						density;

	private DataService					dataService;
	private SubmitService				submitService;

	/**
	 * 本地数据库操作service
	 */
	private SQLiteService				sqliteService;

	private LoginService				loginService;

	public static String				jgoods_img_url;
	private static String				tb_url;

	/**
	 * 当前登录用户
	 */
	private SUser						currentUser;

	private String						sessionId;
	private String						registrationID;

	/** 任务线程池 */
	private static ExecutorService		threadPool;

	/**
	 * 是否有网络
	 */
	private static boolean				isHasNetwork	= true;

	/**
	 * 是否必须升级APP
	 */
	private static boolean				isMustUpdate	= false;

	/**
	 * DisplayImageOptions是用于设置图片显示的类
	 */
	public static DisplayImageOptions	options;

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(MyApplication.LOG_TAG, "MyApplication init...");
		singleton = this;
		threadPool = Executors.newFixedThreadPool(2);

		initImageLoader(getApplicationContext());

		Context context = getApplicationContext();
		dataService = new DataService(context);
		submitService = new SubmitService(context);
		loginService = new LoginService(context);
		sqliteService = new SQLiteService(context);// 初始化本地DB

		jgoods_img_url = getString(R.string.prefix_url) + getString(R.string.img_static_url);

		JPushInterface.setDebugMode(false); // 设置开启日志,发布时请关闭日志
		JPushInterface.init(this); // 初始化 JPush
	}

	// 初始化ImageLoader
	@SuppressWarnings("deprecation")
	public void initImageLoader(Context context) {
		Log.i(MyApplication.LOG_TAG, "初始化图片缓存工具...");
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).threadPriority(
				Thread.NORM_PRIORITY - 2)
																						.denyCacheImageMultipleSizesInMemory()
																						.memoryCache(
																								new LruMemoryCache(
																										2 * 1024 * 1024))
																						.discCacheSize(
																								2 * 1024 * 1024)
																						.discCacheFileNameGenerator(
																								new Md5FileNameGenerator())
																						.tasksProcessingOrder(
																								QueueProcessingType.LIFO)
																						.discCacheFileCount(
																								30)
																						.build();
		ImageLoader.getInstance().init(config);

		// 使用DisplayImageOptions.Builder()创建DisplayImageOptions
		options = new DisplayImageOptions.Builder().showStubImage(R.drawable.empty_photo) // 设置图片下载期间显示的图片
													.showImageForEmptyUri(R.drawable.empty_photo) // 设置图片Uri为空或是错误的时候显示的图片
													.showImageOnFail(R.drawable.empty_photo) // 设置图片加载或解码过程中发生错误显示的图片
													.cacheInMemory(true) // 设置下载的图片是否缓存在内存中
													.cacheOnDisc(true) // 设置下载的图片是否缓存在SD卡中
													// .displayer(new
													// RoundedBitmapDisplayer(20))
													// // 设置成圆角图片
													.build(); // 创建配置过得DisplayImageOption对象
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

	public static boolean isMustUpdate() {
		return isMustUpdate;
	}

	public static void setMustUpdate(boolean isMustUpdate) {
		MyApplication.isMustUpdate = isMustUpdate;
	}
}

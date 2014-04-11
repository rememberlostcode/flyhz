
package com.holding.smile.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;

import com.holding.smile.R;
import com.holding.smile.service.DataService;
import com.holding.smile.service.SQLiteService;
import com.holding.smile.tools.ImgUtil;
import com.holding.smile.tools.LruCache;

/**
 * 
 * 类说明：
 * 
 * @author robin 2014-2-25下午12:21:41
 * 
 */
public class MyApplication extends Application implements OnScrollListener {

	public static final String		LOG_TAG	= "smile";
	private static MyApplication	singleton;

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

	/**
	 * 本地数据库操作service
	 */
	private SQLiteService			sqliteService;

	@Override
	public void onCreate() {
		super.onCreate();
		singleton = this;

		Context context = getApplicationContext();
		dataService = new DataService(context);

		// 创建缓存目录，系统一运行就得创建缓存目录的，
		cache = new File(Environment.getExternalStorageDirectory(), "cache");
		if (!cache.exists()) {
			cache.mkdirs();
		}
		taskCollection = new HashSet<BitmapWorkerTask>();
		// 获取应用程序最大可用内存
		int maxMemory = (int) Runtime.getRuntime().maxMemory();
		int cacheSize = maxMemory / 8;
		// 设置图片缓存大小为程序最大可用内存的1/8
		imgMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				return bitmap.getRowBytes() * bitmap.getHeight();
				// return bitmap.getByteCount();
			}
		};

		// 初始化本地DB
		MyApplication.getInstance().setSqliteService(new SQLiteService(context));
	}

	public DataService getDataService() {
		return dataService;
	}

	public void setDataService(DataService dataService) {
		this.dataService = dataService;
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

	public File getCache() {
		return cache;
	}

	public void setCache(File cache) {
		this.cache = cache;
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

	/*
	 * ==========================================================================
	 */

	private File						cache;

	/**
	 * 记录所有正在下载或等待下载的任务。
	 */
	private Set<BitmapWorkerTask>		taskCollection;

	/**
	 * 图片缓存技术的核心类，用于缓存所有下载好的图片，在程序内存达到设定值时会将最少最近使用的图片移除掉。
	 */
	private LruCache<String, Bitmap>	imgMemoryCache;

	/**
	 * 第一张可见图片的下标
	 */
	private int							mFirstVisibleItem;

	/**
	 * 一屏有多少张图片可见
	 */
	private int							mVisibleItemCount;

	/**
	 * ListView的实例
	 */
	private List<AbsListView>			imgList;

	/**
	 * 给ImageView设置图片。首先从LruCache中取出图片的缓存，设置到ImageView上。如果LruCache中没有该图片的缓存，
	 * 就给ImageView设置一张默认图片。
	 * 
	 * @param imageUrl
	 *            图片的URL地址，用于作为LruCache的键。
	 * @param imageView
	 *            用于显示图片的控件。
	 */
	public void setImageView(String imageUrl, ImageView imageView) {

		Bitmap bitmap = getBitmapFromMemoryCache(imageUrl);
		if (bitmap != null) {
			imageView.setImageBitmap(bitmap);
		} else {
			imageView.setImageResource(R.drawable.empty_photo);
		}
	}

	/**
	 * 将一张图片存储到LruCache中。
	 * 
	 * @param key
	 *            LruCache的键，这里传入图片的URL地址。
	 * @param bitmap
	 *            LruCache的键，这里传入从网络上下载的Bitmap对象。
	 */
	public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
		if (key != null && key.indexOf("http://") < 0) {
			key = getApplicationContext().getString(R.string.jGoods_img_url) + key;
		}
		if (getBitmapFromMemoryCache(key) == null) {
			imgMemoryCache.put(key, bitmap);
		}
	}

	/**
	 * 从LruCache中获取一张图片，如果不存在就返回null。
	 * 
	 * @param key
	 *            LruCache的键，这里传入图片的URL地址。
	 * @return 对应传入键的Bitmap对象，或者null。
	 */
	public Bitmap getBitmapFromMemoryCache(String key) {// step-4
		if (key == null || key.equals(""))
			return null;
		if (key != null && key.indexOf("http://") < 0) {
			key = getApplicationContext().getString(R.string.jGoods_img_url) + key;
		}
		return imgMemoryCache.get(key);
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// 仅当ListView静止时才去下载图片，ListView滑动时取消所有正在下载的任务

		if (scrollState == SCROLL_STATE_FLING) {
			// Log.d("==============", "开始滚动");
		} else if (scrollState == SCROLL_STATE_TOUCH_SCROLL) {
			// Log.d("==============", "正在滚动");
		} else if (scrollState == SCROLL_STATE_IDLE) {
			// Log.d("==============", "停止滚动");
		}
		if (scrollState == SCROLL_STATE_IDLE) {
			loadBitmaps(mFirstVisibleItem, mVisibleItemCount);
		} else {
			cancelAllTasks();
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
			int totalItemCount) {// step-1 2 5 6
		mFirstVisibleItem = firstVisibleItem;
		mVisibleItemCount = visibleItemCount;
		// 下载的任务应该由onScrollStateChanged里调用，但首次进入程序时onScrollStateChanged并不会调用，
		// 因此在这里为首次进入程序开启下载任务。
		if (visibleItemCount > 0) {
			loadBitmaps(firstVisibleItem, visibleItemCount);
		}
	}

	/**
	 * 加载Bitmap对象。此方法会在LruCache中检查所有屏幕中可见的ImageView的Bitmap对象，
	 * 如果发现任何一个ImageView的Bitmap对象不在缓存中，就会开启异步线程去下载图片。
	 * 
	 * @param firstVisibleItem
	 *            第一个可见的ImageView的下标
	 * @param visibleItemCount
	 *            屏幕中总共可见的元素数
	 */
	private void loadBitmaps(int firstVisibleItem, int visibleItemCount) {// step-3
		try {
			if (imgList != null && !imgList.isEmpty()) {
				for (AbsListView mImgList : imgList) {
					for (int i = 0; i < 0 + visibleItemCount; i++) {
						List<View> views = getAllChildViews(mImgList.getChildAt(i));
						for (View view : views) {
							if (view instanceof ImageView) {
								ImageView imageView = (ImageView) view;
								if (imageView.getTag() != null) {
									String imageUrl = (String) imageView.getTag();
									if (imageUrl != null && !"".equals(imageUrl.trim())) {
										Bitmap bitmap = getBitmapFromMemoryCache(imageUrl);
										if (bitmap == null) {
											BitmapWorkerTask task = new BitmapWorkerTask();
											taskCollection.add(task);
											task.execute(imageUrl);
										} else {
											if (imageView != null && bitmap != null) {
												imageView.setImageBitmap(bitmap);
											}
										}
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private List<View> getAllChildViews(View view) {
		List<View> allchildren = new ArrayList<View>();
		if (view instanceof ViewGroup) {
			ViewGroup vp = (ViewGroup) view;
			for (int i = 0; i < vp.getChildCount(); i++) {
				View viewchild = vp.getChildAt(i);
				allchildren.add(viewchild);
				allchildren.addAll(getAllChildViews(viewchild));
			}
		}
		return allchildren;
	}

	/**
	 * 取消所有正在下载或等待下载的任务。
	 */
	public void cancelAllTasks() {
		if (taskCollection != null && !taskCollection.isEmpty()) {
			for (BitmapWorkerTask task : taskCollection) {
				task.cancel(false);
			}
		}
	}

	/**
	 * 异步下载图片的任务。
	 * 
	 * @author guolin
	 */
	class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {

		/**
		 * 图片的URL地址
		 */
		private String	imageUrl;

		@Override
		protected Bitmap doInBackground(String... params) {
			imageUrl = params[0];
			if (imageUrl != null && imageUrl.indexOf("http://") < 0) {
				imageUrl = getApplicationContext().getString(R.string.jGoods_img_url) + imageUrl;
			}
			// 在后台开始下载图片
			Bitmap bitmap = ImgUtil.getInstance().getBitmap(imageUrl);
			if (bitmap != null) {
				// 图片下载完成后缓存到LrcCache中
				addBitmapToMemoryCache(imageUrl, bitmap);
			}
			return bitmap;
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {
			super.onPostExecute(bitmap);
			// 根据Tag找到相应的ImageView控件，将下载好的图片显示出来。
			if (imgList != null && !imgList.isEmpty()) {
				for (AbsListView mImgList : imgList) {
					ImageView imageView = (ImageView) mImgList.findViewWithTag(imageUrl);
					if (imageView != null && bitmap != null) {
						imageView.setImageBitmap(bitmap);
					}
				}
			}
			taskCollection.remove(this);
		}
	}

	public void setmImgList(AbsListView mImgList) {
		imgList = new ArrayList<AbsListView>();
		this.imgList.add(mImgList);
		mImgList.setOnScrollListener(this);
	}

	public void addImgList(AbsListView childView) {
		childView.setOnScrollListener(this);
		imgList.add(childView);
	}

}


package com.holding.smile.tools;

import java.lang.ref.SoftReference;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.holding.smile.activity.MyApplication;

public class ImageLoader {

	private static final String									TAG					= "ImageLoader";
	private static final int									MAX_CAPACITY		= 10;						// 一级缓存的最大空间
	private static final long									DELAY_BEFORE_PURGE	= 10 * 1000;				// 定时清理缓存

	// 二级缓存，采用的是软应用，只有在内存吃紧的时候软应用才会被回收，有效的避免了oom
	private ConcurrentHashMap<String, SoftReference<Bitmap>>	mSecondLevelCache	= new ConcurrentHashMap<String, SoftReference<Bitmap>>(
																							MAX_CAPACITY / 2);

	// 定时清理缓存
	private Runnable											mClearCache			= new Runnable() {
																						@Override
																						public void run() {
																							clear();
																						}
																					};
	private Handler												mPurgeHandler		= new Handler();

	Executor													mExecutor			= new Executor();

	public ImageLoader() {
		mExecutor.start();
	}

	private static final BlockingQueue<ImageLoadTask>	mTasks		= new LinkedBlockingQueue<ImageLoader.ImageLoadTask>();
	// 通过信号量控制同时执行的线程数
	Semaphore											mSemaphore	= new Semaphore(50);

	// 这里是任务的消费者，去任务队列取出下载任务，然后执行，当没有任务的时候消费者就等待
	class Executor extends Thread {
		@Override
		public void run() {
			while (true) {
				ImageLoadTask task = null;
				try {
					task = mTasks.take();
					if (task != null) {
						mSemaphore.acquire();
						task.execute();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// 重置缓存清理的timer
	private void resetPurgeTimer() {
		mPurgeHandler.removeCallbacks(mClearCache);
		mPurgeHandler.postDelayed(mClearCache, DELAY_BEFORE_PURGE);
	}

	/**
	 * 清理缓存
	 */
	private void clear() {
		mSecondLevelCache.clear();
	}

	/**
	 * 返回缓存，如果没有则返回null
	 * 
	 * @param url
	 * @return
	 */
	public Bitmap getBitmapFromCache(String url) {
		Bitmap bitmap = null;
		bitmap = MyApplication.getInstance().getBitmapFromMemoryCache(url);// 从缓存中拿
		if (bitmap != null) {
			return bitmap;
		}
		bitmap = getFromSecondLevelCache(url);// 从二级缓存中拿
		return bitmap;
	}

	/**
	 * 从二级缓存中拿
	 * 
	 * @param url
	 * @return
	 */
	private Bitmap getFromSecondLevelCache(String url) {
		Bitmap bitmap = null;
		SoftReference<Bitmap> softReference = mSecondLevelCache.get(url);
		if (softReference != null) {
			bitmap = softReference.get();
			if (bitmap == null) {// 由于内存吃紧，软引用已经被gc回收了
				mSecondLevelCache.remove(url);
			}
		}
		return bitmap;
	}

	/**
	 * 加载图片，如果缓存中有就直接从缓存中拿，缓存中没有就下载
	 * 
	 * @param url
	 * @param adapter
	 * @param holder
	 */
	int	i	= 0;

	public void loadImage(String url, BaseAdapter adapter, ImageView mImageView) {
		resetPurgeTimer();
		Bitmap bitmap = getBitmapFromCache(url);// 从缓存中读取
		if (bitmap == null) {
			// mImageView.setImageResource(R.drawable.empty_photo);// 缓存没有设为默认图片
			ImageLoadTask imageLoadTask = new ImageLoadTask(url, adapter);
			try {
				// 将任务放入队列中
				mTasks.put(imageLoadTask);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} else {
			mImageView.setImageBitmap(bitmap);// 设为缓存图片
		}
	}

	/**
	 * 放入缓存
	 * 
	 * @param url
	 * @param value
	 */
	public void addImage2Cache(String url, Bitmap value) {
		if (value == null || url == null) {
			return;
		}

	}

	public class ImageLoadTask extends AsyncTask<Object, Void, Bitmap> {
		String		url;
		BaseAdapter	adapter;

		public ImageLoadTask(String url, BaseAdapter adapter) {
			this.url = url;
			this.adapter = adapter;
		}

		@Override
		protected Bitmap doInBackground(Object... params) {
			Log.d(TAG, "func doInBackground-----");
			Bitmap drawable = ImgUtil.getInstance().getBitmap(url);// 获取网络图片
			return drawable;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			mSemaphore.release();
			if (result == null) {
				return;
			}
			// addImage2Cache(url, result);// 放入缓存
			MyApplication.getInstance().addBitmapToMemoryCache(url, result);
			adapter.notifyDataSetChanged();// 触发getView方法执行，这个时候getView实际上会拿到刚刚缓存好的图片
		}
	}

}

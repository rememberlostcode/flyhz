package com.holding.smile.activity;

import java.util.List;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.holding.smile.R;
import com.holding.smile.myview.ProgressWheel;
import com.holding.smile.tools.BitmapUtils;
import com.holding.smile.tools.ToastUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

/**
 * 查看大图
 * 
 * @author zhangb
 * 
 */
public class ImageViewActivity extends BaseActivity {
	PhotoView photoView;

	ProgressWheel progressWheel;

	private PhotoViewAttacher mAttacher;

	@SuppressLint({ "NewApi", "SdCardPath" })
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_imageview);
		photoView = (PhotoView) findViewById(R.id.photoView);
		progressWheel = (ProgressWheel) findViewById(R.id.progressWheel);

		Intent intent = getIntent();
		try {
			List<String> picList = intent.getExtras().getStringArrayList(
					"picList");
			Integer position = intent.getExtras().getInt("position");
			String picture = intent.getExtras().getString("picture");
			boolean local = intent.getBooleanExtra("local", false);// 是否是本地文件
			if (picture != null) {
				if (local) {
					photoView.setImageBitmap(BitmapUtils.decodeFile(picture,
							800, 800));
					progressWheel.setVisibility(View.GONE);
				} else {
					ImageLoader.getInstance().displayImage(MyApplication.jgoods_img_url + picture, photoView,
							MyApplication.options,
							new SimpleImageLoadingListener() {
								@Override
								public void onLoadingComplete(String imageUri,
										View view, Bitmap loadedImage) {
									progressWheel.setVisibility(View.GONE);
									mAttacher.update();
								}
							}, new ImageLoadingProgressListener() {
								@Override
								public void onProgressUpdate(String imageUri,
										View view, int current, int total) {
									progressWheel.setProgress(360 * current
											/ total);
								}
							});

				}
			} else if (picList != null && picList.size() > 0
					&& position != null && picList.size() > position
					&& picList.get(position) != null) {
				mAttacher = new PhotoViewAttacher(photoView);
				mAttacher
						.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
							@Override
							public void onPhotoTap(View view, float x, float y) {
								finish();
							}
						});

				if (local) {
					photoView.setImageBitmap(BitmapUtils.decodeFile(picList.get(position),
							800, 800));
					progressWheel.setVisibility(View.GONE);
				} else {
					ImageLoader.getInstance().displayImage(MyApplication.jgoods_img_url + picList.get(position), photoView,
							MyApplication.options,
							new SimpleImageLoadingListener() {
								@Override
								public void onLoadingComplete(String imageUri,
										View view, Bitmap loadedImage) {
									progressWheel.setVisibility(View.GONE);
									mAttacher.update();
								}
							}, new ImageLoadingProgressListener() {
								@Override
								public void onProgressUpdate(String imageUri,
										View view, int current, int total) {
									progressWheel.setProgress(360 * current
											/ total);
								}
							});

				}
			} else {
				progressWheel.setVisibility(View.GONE);
				ToastUtils.showShort(context, "抱歉，未找到原图 ！");
			}
		} catch (Exception e) {
			Log.e("参数转换异常：", e.getMessage());
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mAttacher != null) {
			mAttacher.cleanup();
		}
	}
}

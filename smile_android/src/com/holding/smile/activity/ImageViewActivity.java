
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
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

/**
 * 查看大图
 * @author zhangb
 *
 */
public class ImageViewActivity extends BaseActivity {
	PhotoView					photoView;

	ProgressWheel				progressWheel;

	private PhotoViewAttacher	mAttacher;

	@SuppressLint({ "NewApi", "SdCardPath" })
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_imageview);
		photoView = (PhotoView) findViewById(R.id.photoView);
		progressWheel = (ProgressWheel) findViewById(R.id.progressWheel);

		mAttacher = new PhotoViewAttacher(photoView);
		mAttacher.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
			@Override
			public void onPhotoTap(View view, float x, float y) {
				finish();
			}
		});

		Intent intent = getIntent();
		String imageUrl = "";
		try {
			List<String> picList = intent.getExtras().getStringArrayList("picList");
			boolean local = intent.getBooleanExtra("local", false);//是否是本地文件
			if (picList != null && picList.size() > 0) {
				if (local) {
					imageUrl = picList.get(0);
					photoView.setImageBitmap(BitmapUtils.decodeFile(imageUrl, 800, 800));
					progressWheel.setVisibility(View.GONE);
				} else {
					imageUrl = MyApplication.jgoods_img_url + picList.get(0);
					ImageLoader.getInstance().displayImage(imageUrl, photoView,MyApplication.options,
							new SimpleImageLoadingListener() {
								@Override
								public void onLoadingComplete(String imageUri, View view,
										Bitmap loadedImage) {
									progressWheel.setVisibility(View.GONE);
									mAttacher.update();
								}
							}, new ImageLoadingProgressListener() {
								@Override
								public void onProgressUpdate(String imageUri, View view,
										int current, int total) {
									progressWheel.setProgress(360 * current / total);
								}
							});
					
				}
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

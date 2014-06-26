
package com.holding.smile.adapter;

import java.util.List;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.support.v4.view.PagerAdapter;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

import com.holding.smile.activity.MyApplication;
import com.holding.smile.cache.ImageLoader;
import com.holding.smile.myview.TouchImageView;

/**
 * 
 * 类说明：页卡适配器
 * 
 * @author robin 2014-06-26下午4:05:44
 * 
 */
public class BigImgPagerAdapter extends PagerAdapter {
	private ImageLoader		mImageLoader	= MyApplication.getImageLoader();
	private List<View>		mListViews;
	private ImageView		currentImgView;
	private LayoutParams	para;

	public BigImgPagerAdapter(List<View> mListViews) {
		this.mListViews = mListViews;// 构造方法，参数是我们的页卡，这样比较方便。
	}

	public void setListViews(List<View> mListViews) {
		this.mListViews = mListViews;
		notifyDataSetChanged();
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView(mListViews.get(position));// 删除页卡
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) { // 这个方法用来实例化页卡
		View view = null;
		if (mListViews != null && !mListViews.isEmpty()) {
			view = mListViews.get(position);
			ImageView im = (TouchImageView) view;
			// if (im != null) {
			// // if (para == null) {
			// // para = im.getLayoutParams();
			// // para.width = MyApplication.getInstance().getScreenWidth();
			// // para.height = para.width;
			// // }
			// // im.setLayoutParams(para);
			// // im.setImageResource(R.drawable.empty_photo);
			// String url = im.getTag().toString();
			// mImageLoader.DisplayImage(url, im, false);
			// // currentImgView = im;
			// // currentImgView.setOnTouchListener(new MyTouchListener());
			// }
			container.addView(view, 0);// 添加页卡
		}
		return view;
	}

	@Override
	public int getCount() {
		if (mListViews != null)
			return mListViews.size();// 返回页卡的数量
		return 0;
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;// 官方提示这样写
	}

	/**
	 * 处理触摸事件
	 * 
	 * @author robin
	 * @date 2014-06-26 上午10:38:36
	 */
	private class MyTouchListener implements OnTouchListener {
		private PointF				startPoint		= new PointF(); // 移动前手指点击的坐标
		private PointF				midPoint		= new PointF(); // 移动前手指点击的坐标
		private Matrix				matrix			= new Matrix(); // 矩阵
		private Matrix				currentMatrix	= new Matrix(); // 每次缩放前都需要存储当前的矩阵
		private final static int	DRAG			= 1;			// 移动的标志
		private final static int	ZOOM			= 1;			// 缩放的标志
		private int					type			= 0;			// 代表当前状态
		private float				startDist		= 0;			// 开始时的距离

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			switch (event.getAction() & MotionEvent.ACTION_MASK) {// 屏蔽掉高8位 因为没用
																	// 可能会提高效率
				case MotionEvent.ACTION_DOWN:// 一个手指按下事件
					startPoint.set(event.getX(), event.getY());// 起点的坐标
					currentMatrix.set(matrix);// 每一次存储当前的矩阵
					type = DRAG;// 这个代表移动
					break;
				case MotionEvent.ACTION_MOVE:
					if (type == DRAG) {// 如果是移动的事件
						float dx = event.getX() - startPoint.x;// x轴移动的距离
						float dy = event.getY() - startPoint.y;// y轴移动的距离
						matrix.set(currentMatrix);
						matrix.postTranslate(dx, dy);
					} else if (type == ZOOM) {// 如果是缩放的事件
						float dist = distance(event);
						if (dist > 10f) {
							matrix.set(currentMatrix);
							float scale = dist / startDist;
							matrix.postScale(scale, scale, midPoint.x, midPoint.y);
						}
					}
					break;
				case MotionEvent.ACTION_POINTER_DOWN:// 第二个手指
					startDist = distance(event);// 两个手指之间的距离
					if (startDist > 10f) {
						currentMatrix.set(matrix);
						type = ZOOM;
						midPoint = midPoint(event);
					}
					break;
				case MotionEvent.ACTION_UP:
					break;
				case MotionEvent.ACTION_POINTER_UP:
					type = 0;// 弹起来初始化
					break;
				default:
					break;
			}
			currentImgView.setImageMatrix(matrix);
			return true;
		}

		private float distance(MotionEvent event) {
			float dx = event.getX(1) - event.getX(0);
			float dy = event.getY(1) - event.getY(0);
			return FloatMath.sqrt(dx * dx + dy * dy);
		}

		private PointF midPoint(MotionEvent event) {
			float x = (event.getX(1) + event.getX(0)) / 2;
			float y = (event.getY(1) + event.getY(0)) / 2;
			return new PointF(x, y);
		}
	}

}

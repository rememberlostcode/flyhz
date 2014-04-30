
package com.holding.smile.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.holding.smile.R;
import com.holding.smile.entity.SUser;

/**
 * 
 * 类说明：继承于Activity用于以后方便管理
 * 
 * @author robin 2014-2-25下午12:17:56
 * 
 */
public class BaseActivity extends Activity {

	public static final int	CATE_CODE			= 8;
	public static final int	MORE_CODE			= 9;
	public static final int	SEARCH_CODE			= 10;
	public static final int	UPLOAD_IMAGE_CODE	= 11;
	/**
	 * 编辑邮箱操作代码
	 */
	public static final int	EMAIL_CODE			= 13;
	/**
	 * 编辑手机号码操作代码
	 */
	public static final int	PHONE_CODE			= 14;
	/**
	 * 选择身份证照片操作代码
	 */
	public static final int	SELECT_PHOTO_IMAGE	= 15;
	/**
	 * 选择身份证照片编辑身份证操作代码
	 */
	public static final int	IDCARD_EDIT_CODE	= 16;
	/**
	 * 订单代码
	 */
	public static final int	ORDER_CODE			= 17;

	public Context			context;

	private LinearLayout	ly_content;
	// 内容区域的布局
	private View			contentView;
	protected int			reqCode				= 0;
	protected String		filepath;
	protected ProgressBar	progressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		context = getApplicationContext();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.base);
		ly_content = (LinearLayout) findViewById(R.id.content);
		progressBar = (ProgressBar) findViewById(R.id.load_progressbar);

		// 删除窗口背景
		getWindow().setBackgroundDrawable(null);

		if (MyApplication.getInstance().getScreenWidth() == null
				|| MyApplication.getInstance().getScreenHeight() == null
				|| MyApplication.getInstance().getDensity() == null) {
			Display display = getWindowManager().getDefaultDisplay();
			@SuppressWarnings("deprecation")
			int h = display.getHeight();
			@SuppressWarnings("deprecation")
			int w = display.getWidth();
			MyApplication.getInstance().setScreenWidth(w);
			MyApplication.getInstance().setScreenHeight(h);

			/** 屏幕密度 **/
			DisplayMetrics displayMetrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
			float density = displayMetrics.density;
			MyApplication.getInstance().setDensity(density);
		}

	}

	public void setVisible(int id) {
		View view = (View) findViewById(id);
		if (view != null) {
			int visibility = view.getVisibility();
			if (visibility != View.VISIBLE) {
				view.setVisibility(View.VISIBLE);
			}
		}
	}

	public void setHeadVisible() {
		setVisible(R.id.head);
	}

	public void setFootVisible() {
		setVisible(R.id.foot);
	}

	/**
	 * 显示头部标题
	 * 
	 * @return
	 */
	public TextView displayHeaderDescription() {
		setHeadVisible();
		int id = R.id.header_description;
		TextView textView = (TextView) findViewById(id);
		setVisible(id);
		return textView;
	}

	/**
	 * 显示头部回退键
	 * 
	 * @return
	 */
	public ImageView displayHeaderBack() {
		setHeadVisible();
		int id = R.id.btn_back;
		ImageView button = (ImageView) findViewById(id);
		setVisible(id);
		return button;
	}

	/**
	 * 显示头部右边部分
	 * 
	 * @return
	 */
	public TextView displayHeaderRight() {
		setHeadVisible();
		int id = R.id.header_right;
		TextView textView = (TextView) findViewById(id);
		setVisible(id);
		return textView;
	}

	/***
	 * 设置内容区域
	 * 
	 * @param resId
	 *            资源文件ID
	 */
	public void setContentLayout(int resId) {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		contentView = inflater.inflate(resId, null);
		// LayoutParams layoutParams = new LayoutParams();
		// contentView.setLayoutParams(layoutParams);
		// contentView.setBackgroundDrawable(null);
		if (null != ly_content) {
			ly_content.addView(contentView);
		}
	}

	/**
	 * 得到内容的View
	 * 
	 * @return
	 */
	public View getLyContentView() {
		return contentView;
	}

	protected static final int	MENU_SETTINGS		= Menu.FIRST;
	protected static final int	MENU_CHANGEACCOUNT	= Menu.FIRST + 1;
	protected static final int	MENU_QUIT			= Menu.FIRST + 2;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		/**
		 * 1. 如果不分组，就自定义为Menu.NONE 2. id:　这个很重要：onOptionsItemSelected(MenuItem
		 * item) 根据id来判断那个菜单被选中 3. 定义菜单的排列 3. 设置Title
		 */
		menu.add(Menu.NONE, MENU_SETTINGS, Menu.NONE, R.string.action_settings);
		// menu.add(Menu.NONE, MENU_CHANGEACCOUNT, Menu.NONE,
		// R.string.action_changeaccount);
		// menu.add(Menu.NONE, MENU_QUIT, Menu.NONE, R.string.action_quit);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}

	public void onClick(View v) {

	}

	// 01-菜单-end
	@Override
	protected void onStart() {
		super.onStart();
		Log.e("====", "start onStart~~~");
	}

	// 当按HOME键时，然后再次启动应用时，我们要恢复先前状态
	@Override
	protected void onRestart() {
		super.onRestart();
		Log.e("====", "start onRestart~~~");
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.e("====", "start onResume~~~");
	}

	// 当我们按HOME键时
	@Override
	protected void onPause() {
		super.onPause();
		Log.e("====", "start onPause~~~");
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.e("====", "start onStop~~~");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// MyApplication.getInstance().finishActivity(this);
		Log.e("====", "start onDestroy~~~");
	}

	long	waitTime	= 2000;
	long	touchTime	= 0;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// if (event.getAction() == KeyEvent.ACTION_DOWN &&
		// KeyEvent.KEYCODE_BACK == keyCode) {
		// long currentTime = System.currentTimeMillis();
		// if ((currentTime - touchTime) >= waitTime) {
		// Toast.makeText(this, "再按一次返回键回到桌面", Toast.LENGTH_SHORT).show();
		// touchTime = currentTime;
		// } else {
		// /* 与按下HOME键效果一样 begin */
		// Intent intent = new Intent(Intent.ACTION_MAIN);
		// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// 注意
		// intent.addCategory(Intent.CATEGORY_HOME);
		// this.startActivity(intent);
		// /* 与按下HOME键效果一样 end */
		// return true;
		// }
		// return true;
		// }
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 开始异步执行任务，开始后会调用loadData方法
	 */
	protected void startTask() {
		progressBar.setVisibility(View.VISIBLE);
		LoadTask lt = new LoadTask();
		lt.execute();
	}

	/**
	 * LoadTask调用的默认方法
	 */
	public void loadData() {
	}

	/**
	 * 异步执行任务，比如载入数据；使用时需要在activity中覆盖loadData方法，在loadData方法中编写获取数据代码
	 * 
	 * @author zhangb 2014年2月17日 下午2:19:10
	 * 
	 */
	protected class LoadTask extends AsyncTask<String, Integer, String> {
		@Override
		protected String doInBackground(String... params) {
			loadData();
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (!this.isCancelled()) {// 执行完后停止任务
				this.cancel(true);
			}
		}
	}

	public View displayFooterMain(int idNow) {
		setFootVisible();
		int id = R.id.mainfooter;
		LinearLayout view = (LinearLayout) findViewById(id);
		setVisible(id);

		int count = view.getChildCount();
		for (int i = 0; i < count; i++) {
			int o = view.getChildAt(i).getId();
			if (o == idNow) {
				view.getChildAt(i).setSelected(true);
			} else {
				if (o == R.id.mainfooter_one) {// 首页
					view.getChildAt(i).setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent();
							intent.setClass(context, MainActivity.class);
							intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
							startActivity(intent);
							finish();
							overridePendingTransition(0, 0);
						}
					});
				} else if (o == R.id.mainfooter_two) {// 排行榜
					view.getChildAt(i).setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent();
							intent.setClass(context, SortActivity.class);
							intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
							startActivity(intent);
							overridePendingTransition(0, 0);
						}
					});
				} else if (o == R.id.mainfooter_three) {// 搜索
					view.getChildAt(i).setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent();
							intent.setClass(context, SearchGoodsActivity.class);
							intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
							startActivity(intent);
							overridePendingTransition(0, 0);
						}
					});
				} else if (o == R.id.mainfooter_four) {// 购物车
					view.getChildAt(i).setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							SUser user = MyApplication.getInstance().getCurrentUser();
							Intent intent = new Intent();
							if (user == null || MyApplication.getInstance().getSessionId() == null) {
								intent.putExtra("class", ShoppingCartActivity.class);
								intent.setClass(context, LoginActivity.class);
							} else {
								intent.setClass(context, ShoppingCartActivity.class);
							}
							intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
							startActivity(intent);
							overridePendingTransition(0, 0);
						}
					});
				} else if (o == R.id.mainfooter_more) {// 更多
					view.getChildAt(i).setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							SUser user = MyApplication.getInstance().getCurrentUser();
							Intent intent = new Intent();
							if (user == null || MyApplication.getInstance().getSessionId() == null) {
								intent.putExtra("class", MySmileActivity.class);
								intent.setClass(context, LoginActivity.class);
							} else {
								intent.setClass(context, MySmileActivity.class);
							}
							intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
							startActivity(intent);
							overridePendingTransition(0, 0);
						}
					});
				}
			}
		}
		return view;
	}

	public View displayFooterMainBuyBtn() {
		setFootVisible();
		int id = R.id.mainfooter_buy;
		View view = (View) findViewById(id);
		setVisible(id);
		return view;
	}

	/**
	 * 进入购物车时显示
	 * 
	 * @return
	 */
	public View displayFooterMainTotal() {
		setFootVisible();
		int id = R.id.calculate_total;
		View view = (View) findViewById(id);
		setVisible(id);
		return view;
	}

}
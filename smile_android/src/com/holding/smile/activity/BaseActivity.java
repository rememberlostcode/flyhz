
package com.holding.smile.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.widget.TextView;

import com.holding.smile.R;
import com.holding.smile.entity.SUser;
import com.holding.smile.myview.HKDialogLoading;
import com.holding.smile.tools.ToastUtils;

/**
 * 
 * 类说明：继承于Activity用于以后方便管理
 * 
 * @author robin 2014-2-25下午12:17:56
 * 
 */
public class BaseActivity extends Activity {

	public static final int		CATE_CODE				= 8;
	public static final int		MORE_CODE				= 9;
	public static final int		SEARCH_CODE				= 10;
	public static final int		UPLOAD_IMAGE_CODE		= 11;
	/**
	 * 个人设置操作代码
	 */
	public static final int		SETTINGS_CODE			= 151;
	/**
	 * 编辑邮箱操作代码
	 */
	public static final int		EMAIL_CODE				= 13;
	/**
	 * 编辑手机号码操作代码
	 */
	public static final int		PHONE_CODE				= 14;
	/**
	 * 选择身份证照片操作代码
	 */
	public static final int		SELECT_PHOTO_IMAGE		= 15;
	/**
	 * 选择身份证照片操作代码
	 */
	public static final int		SELECT_PHOTO_IMAGE_BACK	= 150;
	/**
	 * 选择身份证照片编辑身份证操作代码
	 */
	public static final int		IDCARD_EDIT_CODE		= 16;
	/**
	 * 订单代码
	 */
	public static final int		ORDER_CODE				= 17;

	public Context				context;

	private LinearLayout		ly_content;
	// 内容区域的布局
	private View				contentView;
	protected int				reqCode					= 0;
	protected String			filepath;

	protected HKDialogLoading	dialogLoading;

	// private boolean canClosed = false;
	// private Timer time;
	// private TimerTask loadingTimerTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		context = getApplicationContext();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.base);
		ly_content = (LinearLayout) findViewById(R.id.content);

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
	 * 显示头部右边部分(绿色背景的文字按钮，可设置文字)
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

	/**
	 * 显示头部右边部分（图片按钮，需自己设置图片；ImageView.setImageResource(R.drawable.leibie);）
	 * 
	 * @return
	 */
	public ImageView displayHeaderRightBtn() {
		setHeadVisible();
		int id = R.id.header_right_btn;
		ImageView imageView = (ImageView) findViewById(id);
		setVisible(id);
		return imageView;
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
		// menu.add(Menu.NONE, MENU_SETTINGS, Menu.NONE,
		// R.string.action_settings);
		// menu.add(Menu.NONE, MENU_CHANGEACCOUNT, Menu.NONE,
		// R.string.action_changeaccount);
		// menu.add(Menu.NONE, MENU_QUIT, Menu.NONE, R.string.action_quit);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}

	// 01-菜单-end
	@Override
	protected void onStart() {
		super.onStart();
		Log.e(MyApplication.LOG_TAG, "start onStart~~~");
	}

	// 当按HOME键时，然后再次启动应用时，我们要恢复先前状态
	@Override
	protected void onRestart() {
		super.onRestart();
		Log.e(MyApplication.LOG_TAG, "start onRestart~~~");
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.e(MyApplication.LOG_TAG, "start onResume~~~");
		// 先验证是否是需要登录后才可以访问的activity
		if (this.getClass().equals(ShoppingCartActivity.class)
				|| this.getClass().equals(MySmileActivity.class)
				|| this.getClass().equals(MyOrdersActivity.class)
				|| this.getClass().equals(PersonalSettingsActivity.class)
				|| this.getClass().equals(IdcardManagerActivity.class)
				|| this.getClass().equals(EmailActivity.class)
				|| this.getClass().equals(PhoneActivity.class)
				|| this.getClass().equals(ResetPwdActivity.class)) {
			if (!MyApplication.getInstance().getLoginService().isSessionInvalidated()) {
				Intent intent = new Intent();
				intent.putExtra("class", this.getClass());
				intent.setClass(context, LoginActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				overridePendingTransition(0, 0);
			}
		}
	}

	// 当我们按HOME键时
	@Override
	protected void onPause() {
		super.onPause();
		// Log.e(MyApplication.LOG_TAG, "start onPause~~~");
	}

	@Override
	protected void onStop() {
		super.onStop();
		// Log.e(MyApplication.LOG_TAG, "start onStop~~~");
	}

	@Override
	protected void onDestroy() {
		// if (connectionReceiver != null) {
		// // 取消监听
		// unregisterReceiver(connectionReceiver);
		// }

		if (dialogLoading != null && dialogLoading.isShowing()) {
			dialogLoading.dismiss();
		}
		dialogLoading = null;

		// if (time != null) {
		// time.cancel();
		// }
		// if (loadingTimerTask != null) {
		// loadingTimerTask.cancel();
		// }

		if (null != ly_content) {
			ly_content = null;
		}
		Log.e(MyApplication.LOG_TAG, this.getClass() + " start onDestroy~~~");

		super.onDestroy();
	}

	long	waitTime	= 2000;
	long	touchTime	= 0;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// if (returnDesktop(keyCode, event)) {
		// return true;
		// }
		return super.onKeyDown(keyCode, event);
	}

	public boolean returnDesktop(int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN && KeyEvent.KEYCODE_BACK == keyCode) {
			long currentTime = System.currentTimeMillis();
			if ((currentTime - touchTime) >= waitTime) {
				ToastUtils.showShort(context, "再按一次退出应用！");
				touchTime = currentTime;
			} else {
				/* 与按下HOME键效果一样 begin */
				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// 注意
				intent.addCategory(Intent.CATEGORY_HOME);
				this.startActivity(intent);
				/* 与按下HOME键效果一样 end */
				return true;
			}
			return true;
		}
		return false;
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
							intent.setClass(context, MainSmileActivity.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(intent);
							overridePendingTransition(0, 0);
							finish();
						}
					});
				} else if (o == R.id.mainfooter_two) {// 排行榜
					view.getChildAt(i).setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent();
							intent.setClass(context, SortActivity.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
							intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(intent);
							overridePendingTransition(0, 0);
						}
					});
				} else if (o == R.id.mainfooter_four) {// 购物车
					Integer shoppingCount = MyApplication.getInstance().getSqliteService()
															.getUserShoppingCount();
					if (shoppingCount != null && !shoppingCount.equals(0)) {
						TextView mfft = (TextView) findViewById(R.id.mainfooter_four_text);
						mfft.setText(shoppingCount + "");
						mfft.setVisibility(View.VISIBLE);
					}

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
							intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
							intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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

	/**
	 * 显示隐藏结算总额
	 * 
	 * @param visibility
	 * @return
	 */
	public View showOrhideFooterMainTotal(int visibility) {
		int id = R.id.calculate_total;
		View view = (View) findViewById(id);
		view.setVisibility(visibility);
		return view;
	}

	/**
	 * 结算付款时显示
	 * 
	 * @return
	 */
	public View displayFooterMainOrderInform() {
		setFootVisible();
		int id = R.id.footer_order_inform;
		View view = (View) findViewById(id);
		setVisible(id);
		return view;
	}

	/**
	 * 进入我的订单时显示
	 * 
	 * @return
	 */
	public View displayFooterMainOrder() {
		setFootVisible();
		int id = R.id.footer_my_orders;
		View view = (View) findViewById(id);
		setVisible(id);
		return view;
	}

	/**
	 * 开始异步执行任务，开始后会调用loadData方法
	 */
	protected void startTask() {
		showLoading();

		ly_content.postDelayed(new Runnable() {
			@Override
			public void run() {
				loadData();
			}
		}, 1000);
	}

	public void onClick(View v) {
	}

	/**
	 * LoadTask调用的默认方法
	 */
	public synchronized void loadData() {
		System.out.println("-------------------空方法-------------------");
	}

	/**
	 * 显示loading图片，最少显示一秒
	 */
	public void showLoading() {
		if (dialogLoading == null)
			dialogLoading = new HKDialogLoading(this, R.style.HKDialog);
		if (dialogLoading != null && !dialogLoading.isShowing())
			dialogLoading.show(); // 显示加载中对话框
		// canClosed = false;
		// seconds = 0;
		//
		// time = new Timer(true);
		// loadingTimerTask = new TimerTask() {
		// int countTime = 10;
		//
		// public void run() {
		// if (canClosed || countTime <= 0) {
		// try {
		// time.cancel();
		// loadingTimerTask.cancel();
		// dialogLoading.dismiss();
		// } catch (Exception e) {
		// Log.e(MyApplication.LOG_TAG, e.getMessage());
		// }
		// } else {
		// seconds++;
		// countTime--;
		// }
		// }
		//
		// };
		// time.schedule(loadingTimerTask, 1000, 1000);
	}

	/**
	 * 关闭loading图片，关闭会有延迟，最长延迟一秒
	 */
	public void closeLoading() {
		// if (seconds > 0) {// 持续时间已经有1秒了，立即关闭
		// closeImmediatelyLoading();
		// } else {
		// canClosed = true;
		// }
		try {
			if (dialogLoading != null && dialogLoading.isShowing()) {
				dialogLoading.dismiss();
			}
		} catch (Exception e) {
			Log.e(MyApplication.LOG_TAG, e.getMessage());
		}
	}

	/**
	 * 立即关闭loading图片
	 */
	public void closeImmediatelyLoading() {
		// canClosed = true;
		try {
			// time.cancel();
			// loadingTimerTask.cancel();
			dialogLoading.dismiss();
		} catch (Exception e) {
			Log.e(MyApplication.LOG_TAG, e.getMessage());
		}
	}

}
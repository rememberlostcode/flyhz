
package com.holding.smile.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import cn.jpush.android.api.InstrumentedActivity;
import cn.jpush.android.api.JPushInterface;

import com.holding.smile.R;
import com.holding.smile.dto.RtnValueDto;
import com.holding.smile.entity.JVersion;
import com.holding.smile.entity.SUser;
import com.holding.smile.service.LoginService;
import com.holding.smile.tools.CodeValidator;
import com.holding.smile.tools.UpdateManager;
import com.holding.smile.tools.jpush.ExampleUtil;

public class IndexActivity extends InstrumentedActivity {

	public static boolean		isForeground	= false;
	private static final int	AUTO_LOGIN		= 2;
	private static final int	CHECK_VERSION	= 3;

	private Button				welcomeImage;
	private Button				updateImage;
	private UpdateManager		mUpdateManager;
	private JVersion			jversion;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.index);
		welcomeImage = (Button) findViewById(R.id.index_welcome);
		welcomeImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(IndexActivity.this, MainSmileActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});

		updateImage = (Button) findViewById(R.id.index_update);
		updateImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mUpdateManager == null) {
					mUpdateManager = new UpdateManager(IndexActivity.this);
				}
				try {
					mUpdateManager.setApkUrl(MyApplication.jgoods_img_url
							+ jversion.getVersionApk());
					mUpdateManager.checkUpdateInfo();
				} catch (Exception e) {
					Log.e(MyApplication.LOG_TAG, e.getMessage());
				}
			}
		});

		// 网络状态
		MyApplication.getInstance().initReceiver();

		// jpush消息接收器
		registerMessageReceiver(); // used for receive msg
	}

	// 初始化 JPush。如果已经初始化，但没有登录成功，则执行重新登录。
	private void init() {
		JPushInterface.init(MyApplication.getInstance());
		try {
			String registrationID = JPushInterface.getRegistrationID(MyApplication.getInstance());
			if (registrationID != null) {
				Log.i(MyApplication.LOG_TAG, "registrationID=" + registrationID);
				MyApplication.getInstance().setRegistrationID(registrationID);
			} else {
				Log.w(MyApplication.LOG_TAG, "未取得registrationID");
			}
		} catch (Exception e) {
			Log.e(MyApplication.LOG_TAG, e.getMessage());
		}

		// JPushInterface.resumePush(MyApplication.getInstance());
	}

	@Override
	public void onStart() {
		super.onStart();
		Log.e(MyApplication.LOG_TAG, "IndexActivity start onStart~~~");

		/* 自动登录另起一个线程 */
		mUIHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				init();
				SUser user = MyApplication.getInstance().getSqliteService().getScurrentUser();
				if (user != null) {
					MyApplication.getInstance().setCurrentUser(user);
				}
				if (user != null && user.getUsername() != null && user.getToken() != null) {
					Message msg = mUIHandler.obtainMessage(AUTO_LOGIN);
					msg.obj = user;
					msg.sendToTarget();
				}
			}
		}, 200);

		mUIHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				RtnValueDto rvd = MyApplication.getInstance().getDataService().getLastestVersion();
				if (rvd != null)
					Log.i(MyApplication.LOG_TAG + ".version", rvd.getCode() + "");
				if (CodeValidator.dealCode(getApplicationContext(), rvd)) {
					try {
						// 获取packagemanager的实例
						PackageManager packageManager = getPackageManager();
						// getPackageName()是你当前类的包名，0代表是获取版本信息
						PackageInfo packInfo;
						packInfo = packageManager.getPackageInfo(getPackageName(), 0);
						String version = packInfo.versionName;

						jversion = rvd.getVersionData();

						if (!version.equals(jversion.getVersionNew())) {
							// 这里来检测版本是否需要更新
							Log.i(MyApplication.LOG_TAG,
									"检测到新版本需要更新：" + version + "->" + jversion.getVersionNew());
							mUpdateManager = new UpdateManager(IndexActivity.this);
							mUpdateManager.setApkUrl(MyApplication.jgoods_img_url
									+ jversion.getVersionApk());

							String[] nowVersions = jversion.getVersionNew().split("\\.");
							String[] oldVersions = version.split("\\.");
							if (nowVersions.length > 1
									&& oldVersions.length > 1
									&& (!nowVersions[0].equals(oldVersions[0]) || !nowVersions[1].equals(oldVersions[1]))) {
								MyApplication.setMustUpdate(true);
								welcomeImage.setVisibility(View.GONE);
								updateImage.setVisibility(View.VISIBLE);
							} else {
								welcomeImage.setVisibility(View.VISIBLE);
								updateImage.setVisibility(View.GONE);
							}

							mUpdateManager.checkUpdateInfo();
						} else {
							Log.i(MyApplication.LOG_TAG, "未检测到新版本");
							Intent intent = new Intent(IndexActivity.this, MainSmileActivity.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(intent);
						}
					} catch (NameNotFoundException e) {
						Log.e(MyApplication.LOG_TAG, e.getMessage());
					}
				}
			}
		}, 1000);

		// Message msg = mUIHandler.obtainMessage(CHECK_VERSION);
		// msg.sendToTarget();
	}

	@Override
	public void onResume() {
		isForeground = true;
		super.onResume();
	}

	@Override
	public void onPause() {
		isForeground = false;
		super.onPause();
	}

	@Override
	public void onDestroy() {
		unregisterReceiver(mMessageReceiver);
		super.onDestroy();
	}

	/*************************** jpush start *********************/
	// for receive customer msg from jpush server
	private MessageReceiver		mMessageReceiver;
	public static final String	MESSAGE_RECEIVED_ACTION	= "com.example.jpushdemo.MESSAGE_RECEIVED_ACTION";
	public static final String	KEY_TITLE				= "title";
	public static final String	KEY_MESSAGE				= "message";
	public static final String	KEY_EXTRAS				= "extras";

	public void registerMessageReceiver() {
		mMessageReceiver = new MessageReceiver();
		IntentFilter filter = new IntentFilter();
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		filter.addAction(MESSAGE_RECEIVED_ACTION);
		registerReceiver(mMessageReceiver, filter);
	}

	public class MessageReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
				String messge = intent.getStringExtra(KEY_MESSAGE);
				String extras = intent.getStringExtra(KEY_EXTRAS);
				StringBuilder showMsg = new StringBuilder();
				showMsg.append(KEY_MESSAGE + " : " + messge + "\n");
				if (!ExampleUtil.isEmpty(extras)) {
					showMsg.append(KEY_EXTRAS + " : " + extras + "\n");
				}
			}
		}
	}

	/*************************** jpush end *********************/

	@SuppressLint("HandlerLeak")
	private final Handler	mUIHandler	= new Handler() {

											@Override
											public void handleMessage(Message msg) {
												switch (msg.what) {
													case AUTO_LOGIN: {
														if (msg.obj != null) {
															SUser user = (SUser) msg.obj;
															LoginService loginService = MyApplication.getInstance()
																										.getLoginService();
															RtnValueDto rvd = loginService.autoLogin();
															if (rvd != null)
																Log.i(MyApplication.LOG_TAG,
																		rvd.getCode() + "");
															if (CodeValidator.dealCode(
																	getApplicationContext(), rvd)) {
																if (rvd != null
																		&& rvd.getUserData() != null) {
																	Log.i(MyApplication.LOG_TAG,
																			"自动登录成功！欢迎您,"
																					+ user.getUsername());
																}
															}
														}
														break;
													}
													case CHECK_VERSION: {

														break;
													}
												}
											}
										};

}

package com.holding.smile.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.holding.smile.R;
import com.holding.smile.dto.RtnValueDto;
import com.holding.smile.entity.SUser;
import com.holding.smile.service.LoginService;
import com.holding.smile.tools.CodeValidator;
import com.holding.smile.tools.MD5;
import com.holding.smile.tools.ToastUtils;

/**
 * 思路： 1、初始化组件 2、添加按钮点击监听事件 3、获取用户输入的数据并转换成JSON对象 4、设置请求数据url和实体数据，并执行请求
 * 5、获取服务端响应状态 6、若响应状态为200，响应成功，则获取响应实体 7、通过读入对象获取响应的实体的字符串对象，并转换成JSON对象
 * 8、获取result值，并判断，若为true,表示登陆成功，false表示用户登录失败
 * 9、登陆成功则通过intent跳转到test界面（真正工程则跳转到主界面） 10、登陆失败用Toast提醒用户名不存在或者密码错误
 * 
 * @author Administrator
 * 
 */
@SuppressWarnings("rawtypes")
public class LoginActivity extends BaseActivity implements OnClickListener {

	private Class		goingActivityClass;
	private EditText	userAccount;			// 用户的账号
	private EditText	userPwd;				// 用户密码
	private Button		btnLogining;			// 登陆按钮
	private Button		btnGetBackPwd;			// 取回密码按钮
	private Button		btnToRegister;			// 注册登录按钮
	private ImageView	autoImage;				// 自动登录标记

	private boolean		isClose		= false;
	private boolean		isAutoLogin	= true;
	private Integer		gid;
	private SUser user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentLayout(R.layout.login);

		try {
			Intent intent = getIntent();
			if (intent.getExtras() != null && intent.getExtras().getSerializable("class") != null) {
				goingActivityClass = (Class) (intent.getExtras().getSerializable("class"));
			}
			if (intent.getExtras() != null && intent.getExtras().getBoolean("isClose")) {
				isClose = intent.getExtras().getBoolean("isClose");
			}
			if (intent.getExtras() != null && intent.getExtras().getSerializable("gid") != null) {
				gid = (Integer) (intent.getExtras().getSerializable("gid"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		ImageView backBtn = displayHeaderBack();
		backBtn.setOnClickListener(this);

		displayHeaderDescription().setText(R.string.btn_login);

		initView();
	}

	/**
	 * 初始化所有控件
	 */
	private void initView() {
		userAccount = (EditText) findViewById(R.id.login_account);
		userPwd = (EditText) findViewById(R.id.login_pwd);
		btnLogining = (Button) findViewById(R.id.self_login);
		btnGetBackPwd = (Button) findViewById(R.id.login_btn_getbackpwd);
		btnToRegister = (Button) findViewById(R.id.login_btn_to_register);

		userAccount.setSelection(userAccount.getText().toString().trim().length());

		btnLogining.setOnClickListener(this);
		btnGetBackPwd.setOnClickListener(this);
		btnToRegister.setOnClickListener(this);

		autoImage = (ImageView) findViewById(R.id.auto_image);
		autoImage.setOnClickListener(this);

		SUser user = MyApplication.getInstance().getSqliteService().getScurrentUser();
		if (user != null) {
			userAccount.setText(user.getUsername());
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_back: {
				finish();
				break;
			}
			case R.id.self_login: {
				String username = userAccount.getText().toString();// 获取用户输入的账号
				String password = userPwd.getText().toString();// 获取用户输入的密码
				if (username == null || "".equals(username.trim())) {
					ToastUtils.showShort(context, "用户名不能为空！");
					return;
				}
				if (password == null || "".equals(password.trim())) {
					ToastUtils.showShort(context, "密码不能为空！");
					return;
				}
				password = MD5.getMD5(password);
				/* 关闭软键盘 */
				InputMethodManager inputMgr = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
				inputMgr.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
				user = new SUser();
				user.setUsername(username);
				user.setPassword(password);
				
				startTask();
				break;
			}
			case R.id.login_btn_to_register: {
				Intent intent = new Intent(context, RegisterActivity.class);
				startActivity(intent);
				overridePendingTransition(0, 0);
				break;
			}
			case R.id.auto_image: {
				if (isAutoLogin) {
					autoImage.setImageResource(R.drawable.new_cb_normal);
					isAutoLogin = false;
				} else {
					autoImage.setImageResource(R.drawable.new_cb_checked);
					isAutoLogin = true;
				}
				break;
			}
			case R.id.login_btn_getbackpwd: {
				Intent intent = new Intent(context, FindBackPwdActivity.class);
				String username = userAccount.getText().toString();// 获取用户输入的账号
				intent.putExtra("username", username);
				startActivity(intent);
				overridePendingTransition(0, 0);
				break;
			}
		}
		super.onClick(v);
	}
	
	@Override
	public synchronized void loadData() {
		Message msg = mUIHandler.obtainMessage(LOGIN_BY_SELF);
		msg.obj = user;
		msg.sendToTarget();
	}

	private static final int	LOGIN_BY_SELF	= 1;
	@SuppressLint("HandlerLeak")
	private Handler				mUIHandler		= new Handler() {
													@Override
													public void handleMessage(Message msg) {
														switch (msg.what) {
															case LOGIN_BY_SELF: {
																if (msg.obj != null) {
																	SUser user = (SUser) msg.obj;
																	LoginService loginService = MyApplication.getInstance()
																												.getLoginService();
																	if (MyApplication.getInstance()
																						.getRegistrationID() != null) {
																		user.setRegistrationID(MyApplication.getInstance()
																											.getRegistrationID());
																	}
																	RtnValueDto rvd = loginService.login(
																			user, isAutoLogin);
																	closeLoading();
																	if (CodeValidator.dealCode(
																			context, rvd)) {
																		if (!isClose) {
																			Intent intent = null;
																			if (goingActivityClass != null) {
																				intent = new Intent(
																						context,
																						goingActivityClass);
																				intent.putExtra(
																						"gid", gid);
																				goingActivityClass = null;
																				gid = null;
																			} else {
																				intent = new Intent(
																						context,
																						MainSmileActivity.class);
																			}
																			startActivity(intent);
																		}
																		finish();
																	} else {
																		initView();
																	}
																}
																break;
															}
														}
													}
												};

}

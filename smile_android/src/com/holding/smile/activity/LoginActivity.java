
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
import android.widget.LinearLayout;

import com.holding.smile.R;
import com.holding.smile.dto.RtnLoginDto;
import com.holding.smile.entity.SUser;
import com.holding.smile.service.DataService;
import com.holding.smile.service.LoginService;
import com.holding.smile.tools.MD5;

/**
 * 思路： 1、初始化组件 2、添加按钮点击监听事件 3、获取用户输入的数据并转换成JSON对象 4、设置请求数据url和实体数据，并执行请求
 * 5、获取服务端响应状态 6、若响应状态为200，响应成功，则获取响应实体 7、通过读入对象获取响应的实体的字符串对象，并转换成JSON对象
 * 8、获取result值，并判断，若为true,表示登陆成功，false表示用户登录失败
 * 9、登陆成功则通过intent跳转到test界面（真正工程则跳转到主界面） 10、登陆失败用Toast提醒用户名不存在或者密码错误
 * 
 * @author Administrator
 * 
 */
public class LoginActivity extends BaseActivity {

	private EditText		userAccount;	// 用户的账号
	private EditText		userPwd;		// 用户密码
	private Button			btnLogining;	// 登陆按钮
	private Button			btnGetBackPwd;	// 取回密码按钮
	private Button			btnSetLogin;	// 登录设置按钮
	private Button			btnToRegister;	// 注册登录按钮

	private LinearLayout	loginBySelf;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentLayout(R.layout.login);
		if (!DataService.getDataFromNet) {
			loadData();
		} else {
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
			finish();
		}

	}

	@Override
	public void loadData() {
		SUser user = MyApplication.getInstance().getSqliteService().getScurrentUser();
		if (user != null) {
			MyApplication.getInstance().setCurrentUser(user);
		}
		if (user != null && user.getUsername() != null && user.getToken() != null) {
			Message msg = mUIHandler.obtainMessage(AUTO_LOGIN);
			msg.obj = user;
			msg.sendToTarget();
		} else {
			loginViewInit();
		}
	}

	/**
	 * 登录界面初始化
	 * 
	 * @param savedInstanceState
	 */
	private void loginViewInit() {
		initView();// 初始化界面控件
		addListener();// 添加监听响应事件
	}

	/**
	 * 初始化所有控件
	 */
	private void initView() {
		userAccount = (EditText) findViewById(R.id.login_account);
		userPwd = (EditText) findViewById(R.id.login_pwd);
		btnLogining = (Button) findViewById(R.id.self_login);
		btnGetBackPwd = (Button) findViewById(R.id.login_btn_getbackpwd);
		btnSetLogin = (Button) findViewById(R.id.login_btn_setlogin);
		btnToRegister = (Button) findViewById(R.id.login_btn_to_register);

		userAccount.setSelection(userAccount.getText().toString().trim().length());

		loginBySelf = (LinearLayout) findViewById(R.id.login_by_self);

		loginBySelf.setVisibility(View.VISIBLE);
	}

	private void addListener() {
		btnLogining.setOnClickListener(new OnClickListener() {// 添加登陆按钮监听事件
			@Override
			public void onClick(View v) {
				String username = userAccount.getText().toString();// 获取用户输入的账号
				String password = userPwd.getText().toString();// 获取用户输入的密码
				password = MD5.getMD5(password);
				/* 关闭软键盘 */
				InputMethodManager inputMgr = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
				inputMgr.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
				SUser user = new SUser();
				user.setUsername(username);
				user.setPassword(password);
				Message msg = mUIHandler.obtainMessage(LOGIN_BY_SELF);
				msg.obj = user;
				msg.sendToTarget();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	private static final int	AUTO_LOGIN		= 0;
	private static final int	LOGIN_BY_SELF	= 1;
	@SuppressLint("HandlerLeak")
	private Handler				mUIHandler		= new Handler() {
													@Override
													public void handleMessage(Message msg) {
														switch (msg.what) {
															case AUTO_LOGIN: {
																if (msg.obj != null) {
																	SUser user = (SUser) msg.obj;
																	LoginService loginService = MyApplication.getInstance()
																												.getLoginService();
																	RtnLoginDto rtnLoginDto = loginService.autoLogin(user);
																	if (rtnLoginDto == null
																			|| rtnLoginDto.getCode() == null
																			|| rtnLoginDto.getData() == null) {
																		loginViewInit();
																	} else {
																		Intent intent = new Intent(
																				context,
																				MainActivity.class);
																		startActivity(intent);
																		finish();
																	}
																}
																break;
															}
															case LOGIN_BY_SELF: {
																if (msg.obj != null) {
																	SUser user = (SUser) msg.obj;
																	LoginService loginService = MyApplication.getInstance()
																												.getLoginService();
																	RtnLoginDto rtnLoginDto = loginService.login(user);
																	if (rtnLoginDto == null
																			|| rtnLoginDto.getCode() == null
																			|| rtnLoginDto.getData() == null) {
																		Intent intent = new Intent(
																				context,
																				MainActivity.class);
																		startActivity(intent);
																		finish();
																		// loginViewInit();
																		// Toast.makeText(context,
																		// "登录失败，请稍后重试！",
																		// Toast.LENGTH_SHORT).show();
																	} else {
																		Intent intent = new Intent(
																				context,
																				MainActivity.class);
																		startActivity(intent);
																		finish();
																	}
																}
																break;
															}
														}
													}
												};

}

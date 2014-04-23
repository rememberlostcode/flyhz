
package com.holding.smile.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.holding.smile.R;
import com.holding.smile.dto.RtnValueDto;
import com.holding.smile.entity.SUser;
import com.holding.smile.service.LoginService;

/**
 * 注册
 * 
 * @author zhangb 2014年4月23日 下午4:29:47
 * 
 */
public class RegisterActivity extends BaseActivity implements OnClickListener {

	private Class		goingActivityClass;
	private EditText	userAccount;		// 用户的账号
	private EditText	userPwd;			// 用户密码
	private EditText	userPwd2;			// 用户密码
	private Button		btnToRegister;		// 注册登录按钮

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentLayout(R.layout.register);
		loginViewInit();

		try {
			Intent intent = getIntent();
			if (intent.getExtras() != null && intent.getExtras().getSerializable("class") != null) {
				goingActivityClass = (Class) (intent.getExtras().getSerializable("class"));

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 登录界面初始化
	 * 
	 * @param savedInstanceState
	 */
	private void loginViewInit() {
		initView();// 初始化界面控件
	}

	/**
	 * 初始化所有控件
	 */
	private void initView() {
		userAccount = (EditText) findViewById(R.id.register_account);
		userPwd = (EditText) findViewById(R.id.register_pwd);
		userPwd2 = (EditText) findViewById(R.id.register_pwd2);
		btnToRegister = (Button) findViewById(R.id.self_register);
		userAccount.setSelection(userAccount.getText().toString().trim().length());

		btnToRegister.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.self_register: {
				Toast.makeText(context, "点击了注册", Toast.LENGTH_SHORT).show();
				break;
			}
		}
		super.onClick(v);
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
																	RtnValueDto rvd = loginService.login(user);
																	if (rvd == null
																			|| rvd.getCode() == null
																			|| rvd.getUserData() == null) {
																		loginViewInit();
																		Toast.makeText(context,
																				"登录失败！",
																				Toast.LENGTH_SHORT)
																				.show();
																	} else {
																		Intent intent = null;
																		if (goingActivityClass != null) {
																			intent = new Intent(
																					context,
																					goingActivityClass);
																			goingActivityClass = null;
																		} else {
																			intent = new Intent(
																					context,
																					MainActivity.class);
																		}
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

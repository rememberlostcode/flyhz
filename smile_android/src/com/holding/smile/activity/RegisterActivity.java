
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
import android.widget.Toast;

import com.holding.smile.R;
import com.holding.smile.dto.RtnValueDto;
import com.holding.smile.entity.SUser;

/**
 * 注册
 * 
 * @author zhangb 2014年4月23日 下午4:29:47
 * 
 */
public class RegisterActivity extends BaseActivity implements OnClickListener {

	private EditText	userAccount;	// 用户的账号
	private EditText	userPwd;		// 用户密码
	private EditText	userPwd2;		// 用户密码
	private Button		btnToRegister;	// 注册登录按钮

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentLayout(R.layout.register);
		loginViewInit();
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
				String username = userAccount.getText().toString();// 获取用户输入的账号
				String password = userPwd.getText().toString();// 获取用户输入的密码
				String password2 = userPwd2.getText().toString();// 获取用户输入的密码

				if (password == null || "".equals(password.trim())) {
					Toast.makeText(context, "密码不能为空！", Toast.LENGTH_SHORT).show();
					return;
				}
				if (!password.equals(password2.trim())) {
					Toast.makeText(context, "两个密码不一致！", Toast.LENGTH_SHORT).show();
					return;
				}

				/* 关闭软键盘 */
				InputMethodManager inputMgr = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
				inputMgr.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
				SUser user = new SUser();
				user.setUsername(username);
				user.setPassword(password);
				Message msg = mUIHandler.obtainMessage(REGISTER_BY_SELF);
				msg.obj = user;
				msg.sendToTarget();
				break;
			}
		}
		super.onClick(v);
	}

	private static final int	REGISTER_BY_SELF	= 1;
	@SuppressLint("HandlerLeak")
	private Handler				mUIHandler			= new Handler() {
														@Override
														public void handleMessage(Message msg) {
															switch (msg.what) {
																case REGISTER_BY_SELF: {
																	if (msg.obj != null) {
																		SUser user = (SUser) msg.obj;
																		RtnValueDto rvd = MyApplication.getInstance()
																										.getSubmitService()
																										.register(
																												user);

																		if (rvd == null
																				|| rvd.getCode() == null
																				|| rvd.getUserData() == null) {
																			loginViewInit();
																			Toast.makeText(
																					context,
																					"注册失败！",
																					Toast.LENGTH_SHORT)
																					.show();
																		} else {
																			Toast.makeText(
																					context,
																					"注册成功！",
																					Toast.LENGTH_SHORT)
																					.show();
																			Intent intent = new Intent(
																					context,
																					LoginActivity.class);
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


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
import com.holding.smile.tools.CodeValidator;
import com.holding.smile.tools.StrUtils;
import com.holding.smile.tools.ToastUtils;

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
	private EditText	userEmail;		// 用户的账号
	private Button		btnToRegister;	// 注册登录按钮

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentLayout(R.layout.register);
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
		userEmail = (EditText) findViewById(R.id.register_email);

		btnToRegister.setOnClickListener(this);
	}

	@Override
	public void onStart(){
		super.onStart();
		ImageView backBtn = displayHeaderBack();
		backBtn.setOnClickListener(this);
		
		displayHeaderDescription().setText(R.string.btn_register);
		
		initView();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_back: {
				finish();
				break;
			}
			case R.id.self_register: {
				String username = userAccount.getText().toString();// 获取用户输入的账号
				String password = userPwd.getText().toString();// 获取用户输入的密码
				String password2 = userPwd2.getText().toString();// 获取用户输入的密码
				String email = userEmail.getText().toString();// 获取用户输入的密码

				if (username == null || "".equals(username.trim())) {
					ToastUtils.showShort(context, "用户名不能为空！");
					return;
				}
				if (username.length() < 6 || username.length() > 12) {
					ToastUtils.showShort(context, "用户名只能6到12个字符！");
					return;
				}

				if (password == null || "".equals(password.trim())) {
					ToastUtils.showShort(context, "密码不能为空！");
					return;
				}
				if (password.length() < 6 || password.length() > 12) {
					ToastUtils.showShort(context, "密码只能6到12个字符！");
					return;
				}
				if (!password.equals(password2.trim())) {
					ToastUtils.showShort(context, "两个密码不一致！");
					return;
				}

				if (!StrUtils.chaeckPassword(password)) {
					ToastUtils.showShort(context, "密码太简单了，至少需要字母加数字！");
					return;
				}

				if (!"".equals(email.trim()) && !StrUtils.checkEmail(email)) {
					ToastUtils.showShort(context, "输入的邮箱格式不正确！");
					return;
				}
				/* 关闭软键盘 */
				InputMethodManager inputMgr = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
				inputMgr.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
				SUser user = new SUser();
				user.setUsername(username);
				user.setPassword(password);
				user.setEmail(email);
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
																		showLoading();
																		RtnValueDto rvd = MyApplication.getInstance()
																										.getSubmitService()
																										.register(
																												user);
																		closeImmediatelyLoading();
																		if (!CodeValidator.dealCode(
																				context, rvd)) {
																			initView();
																		} else {
																			ToastUtils.showShort(
																					context,
																					"注册成功！");
																			Intent intent = new Intent(
																					context,
																					LoginActivity.class);
																			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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

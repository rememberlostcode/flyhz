
package com.holding.smile.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.holding.smile.R;
import com.holding.smile.dto.RtnValueDto;
import com.holding.smile.entity.SUser;

/**
 * 个人设置
 * 
 * @author zhangb 2014年4月15日 下午2:11:06
 * 
 */
public class PersonalSettingsActivity extends BaseActivity implements OnClickListener {

	private LinearLayout	addressLayout;
	private LinearLayout	emailLayout;
	private LinearLayout	mobileLayout;
	private LinearLayout	userPwdLayout;
	private Button			logoutButton;

	private TextView		emailTextView;
	private TextView		phoneTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentLayout(R.layout.personal_settings);
		ImageView backBtn = displayHeaderBack();
		backBtn.setOnClickListener(this);

		TextView textView = displayHeaderDescription();
		textView.setText("个人设置");

		addressLayout = (LinearLayout) findViewById(R.id.user_info_address_layout);
		emailLayout = (LinearLayout) findViewById(R.id.user_info_email_layout);
		mobileLayout = (LinearLayout) findViewById(R.id.user_info_phone_layout);
		userPwdLayout = (LinearLayout) findViewById(R.id.user_info_pwd_layout);
		logoutButton = (Button) findViewById(R.id.setting_logout_button);

		addressLayout.setOnClickListener(this);
		emailLayout.setOnClickListener(this);
		mobileLayout.setOnClickListener(this);
		userPwdLayout.setOnClickListener(this);
		logoutButton.setOnClickListener(this);

		emailTextView = (TextView) findViewById(R.id.user_info_email);
		phoneTextView = (TextView) findViewById(R.id.user_info_phone);

		startTask();
	}

	@Override
	public void loadData() {
		RtnValueDto user = MyApplication.getInstance().getDataService().getUserInfo();
		if (user != null) {
			Message msg = mUIHandler.obtainMessage(1);
			msg.obj = user;
			msg.sendToTarget();
		} else {
			Toast.makeText(context, "暂无数据", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_back: {
				setResult(RESULT_CANCELED, null);
				finish();
				break;
			}
			case R.id.user_info_address_layout: {
				Intent intent = new Intent();
				intent.setClass(context, DeliveryAddressActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				startActivity(intent);
				break;
			}
			case R.id.user_info_email_layout: {
				Toast.makeText(context, "修改邮箱", Toast.LENGTH_SHORT).show();
				break;
			}
			case R.id.user_info_phone_layout: {
				Toast.makeText(context, "修改手机", Toast.LENGTH_SHORT).show();
				break;
			}
			case R.id.user_info_pwd_layout: {
				Intent intent = new Intent();
				intent.setClass(context, ResetPwdActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				startActivity(intent);
				break;
			}
			case R.id.setting_logout_button: {
				Toast.makeText(context, "注销", Toast.LENGTH_SHORT).show();
				break;
			}
		}
		super.onClick(v);
	}

	@SuppressLint("HandlerLeak")
	private final Handler	mUIHandler	= new Handler() {

											@Override
											public void handleMessage(Message msg) {
												progressBar.setVisibility(View.GONE);
												switch (msg.what) {
													case 1: {
														if (msg.obj != null) {
															RtnValueDto rvd = (RtnValueDto) (msg.obj);
															SUser user = rvd.getUserData();
															if (user != null) {
																if (user.getEmail() != null
																		&& !"".equals(user.getEmail())) {
																	emailTextView.setText(user.getEmail());
																}
																if (user.getMobilephone() != null
																		&& !"".equals(user.getMobilephone())) {
																	phoneTextView.setText(user.getMobilephone());
																}
															}
														} else {
															Toast.makeText(context, "暂无数据",
																	Toast.LENGTH_SHORT).show();
														}
														break;
													}
												}
											}
										};
}

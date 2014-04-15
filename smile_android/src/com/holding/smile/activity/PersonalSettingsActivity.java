
package com.holding.smile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.holding.smile.R;
import com.holding.smile.dto.RtnValueDto;

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentLayout(R.layout.personal_settings);
		ImageView backBtn = displayHeaderBack();
		backBtn.setOnClickListener(this);

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

	}

	@Override
	public void loadData() {
		RtnValueDto cates = MyApplication.getInstance().getDataService().getCategorys();
		if (cates != null) {
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
				Toast.makeText(context, "修改密码", Toast.LENGTH_SHORT).show();
				break;
			}
			case R.id.setting_logout_button: {
				Toast.makeText(context, "注销", Toast.LENGTH_SHORT).show();
				break;
			}
		}
		super.onClick(v);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}

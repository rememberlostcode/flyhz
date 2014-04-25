
package com.holding.smile.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.holding.smile.R;
import com.holding.smile.dto.RtnValueDto;
import com.holding.smile.entity.SUser;

/**
 * 更多
 * 
 * @author zhangb 2014年4月24日 上午10:53:39
 * 
 */
public class MySmileActivity extends BaseActivity implements OnClickListener {

	private LinearLayout	myOrdersLayout;
	private LinearLayout	myCouponLayout;
	private LinearLayout	settingLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentLayout(R.layout.my_smile);

		ImageView backBtn = displayHeaderBack();
		backBtn.setOnClickListener(this);

		TextView textView = displayHeaderDescription();
		textView.setText("我的SMILE");

		displayFooterMain(R.id.mainfooter_more);

		myOrdersLayout = (LinearLayout) findViewById(R.id.mysmile_myorders_layout);
		myCouponLayout = (LinearLayout) findViewById(R.id.mysmile_mycoupon_layout);
		settingLayout = (LinearLayout) findViewById(R.id.mysmile_setting_layout);

		myOrdersLayout.setOnClickListener(this);
		myCouponLayout.setOnClickListener(this);
		settingLayout.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_back: {
				setResult(RESULT_CANCELED, null);
				finish();
				break;
			}
			case R.id.mysmile_myorders_layout: {
				Intent intent = new Intent();
				intent.setClass(context, MyOrdersActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				startActivity(intent);
				overridePendingTransition(0, 0);
				break;
			}
			case R.id.mysmile_mycoupon_layout: {
				Toast.makeText(context, "点击了我的优惠券", Toast.LENGTH_SHORT).show();
				break;
			}
			case R.id.mysmile_setting_layout: {
				Intent intent = new Intent();
				intent.setClass(context, PersonalSettingsActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				startActivity(intent);
				overridePendingTransition(0, 0);
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

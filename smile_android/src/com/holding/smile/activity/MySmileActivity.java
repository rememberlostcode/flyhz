
package com.holding.smile.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.holding.smile.R;
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
				SUser user = MyApplication.getInstance().getCurrentUser();
				Intent intent = new Intent();
				if (user == null || MyApplication.getInstance().getSessionId() == null) {
					intent.putExtra("class", MyOrdersActivity.class);
					intent.setClass(context, LoginActivity.class);
				} else {
					intent.setClass(context, MyOrdersActivity.class);
				}
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
				SUser user = MyApplication.getInstance().getCurrentUser();
				Intent intent = new Intent();
				if (user == null || MyApplication.getInstance().getSessionId() == null) {
					intent.putExtra("class", PersonalSettingsActivity.class);
					intent.setClass(context, LoginActivity.class);
				} else {
					intent.setClass(context, PersonalSettingsActivity.class);
				}
				intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				startActivity(intent);
				overridePendingTransition(0, 0);
				break;
			}
		}
		super.onClick(v);
	}
}

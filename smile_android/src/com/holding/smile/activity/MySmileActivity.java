
package com.holding.smile.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.holding.smile.R;
import com.holding.smile.entity.SUser;
import com.holding.smile.tools.ToastUtils;

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
	private LinearLayout	clearCacheLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentLayout(R.layout.my_smile);

		ImageView backBtn = displayHeaderBack();
		backBtn.setOnClickListener(this);

		TextView textView = displayHeaderDescription();
		textView.setText(R.string.more);

		displayFooterMain(R.id.mainfooter_more);

		myOrdersLayout = (LinearLayout) findViewById(R.id.mysmile_myorders_layout);
		myCouponLayout = (LinearLayout) findViewById(R.id.mysmile_mycoupon_layout);
		myCouponLayout.setVisibility(View.GONE);
		settingLayout = (LinearLayout) findViewById(R.id.mysmile_setting_layout);
		clearCacheLayout = (LinearLayout) findViewById(R.id.mysmile_clearcache_layout);

		myOrdersLayout.setOnClickListener(this);
		myCouponLayout.setOnClickListener(this);
		settingLayout.setOnClickListener(this);
		clearCacheLayout.setOnClickListener(this);
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
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				overridePendingTransition(0, 0);
				break;
			}
			case R.id.mysmile_mycoupon_layout: {
				ToastUtils.showShort(context, "敬请期待！");
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
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				overridePendingTransition(0, 0);
				break;
			}
			case R.id.mysmile_clearcache_layout: {
				alert();
				break;
			}
		}
		super.onClick(v);
	}

	private void alert() {
		new AlertDialog.Builder(this).setTitle("提示")
										.setMessage("您确定要清理缓存吗？")
										.setPositiveButton("确定",
												new DialogInterface.OnClickListener() {
													public void onClick(DialogInterface dialog,
															int which) {

														MyApplication.getImageLoader()
																		.clearMemoryCache();
														ToastUtils.showShort(context, "清理缓存成功！");

													}
												}).setNegativeButton("取消", null).show();
	}
}

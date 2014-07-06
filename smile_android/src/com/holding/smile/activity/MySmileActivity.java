
package com.holding.smile.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.holding.smile.R;
import com.holding.smile.entity.SUser;
import com.holding.smile.tools.ClickUtil;
import com.holding.smile.tools.ToastUtils;

/**
 * 更多
 * 
 * @author zhangb 2014年4月24日 上午10:53:39
 * 
 */
public class MySmileActivity extends BaseActivity implements OnClickListener {

	/**
	 * 我的订单
	 */
	private LinearLayout	myOrdersLayout;
	/**
	 * 优惠券
	 */
	private LinearLayout	myCouponLayout;
	/**
	 * 个人设置
	 */
	private LinearLayout	settingLayout;
	/**
	 * 清除缓存
	 */
	private LinearLayout	clearCacheLayout;
	/**
	 * 联系我们
	 */
	private LinearLayout	contactUsLayoutLayout;

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
		contactUsLayoutLayout = (LinearLayout) findViewById(R.id.mysmile_contact_us_layout);

		myOrdersLayout.setOnClickListener(this);
		myCouponLayout.setOnClickListener(this);
		settingLayout.setOnClickListener(this);
		clearCacheLayout.setOnClickListener(this);
		contactUsLayoutLayout.setOnClickListener(this);
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
			case R.id.mysmile_contact_us_layout: {
				ClickUtil.sendEmail(this);
				break;
			}
		}
		super.onClick(v);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (returnDesktop(keyCode, event)) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
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

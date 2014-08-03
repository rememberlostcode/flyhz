
package com.holding.smile.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.holding.smile.R;
import com.holding.smile.entity.SUser;
import com.holding.smile.tools.ClickUtil;
import com.holding.smile.tools.Constants;
import com.holding.smile.tools.ToastUtils;

/**
 * 更多
 * 
 * @author zhangb 2014年4月24日 上午10:53:39
 * 
 */
public class MySmileActivity extends BaseActivity implements OnClickListener {

	/**
	 * 我的订单(全部)
	 */
	private LinearLayout	myOrdersLayout;
	/**
	 * 我的订单(待付款)
	 */
	private LinearLayout	myOrdersPayLayout;
	/**
	 * 我的订单(待收货)
	 */
	private LinearLayout	myOrdersResLayout;
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
	/**
	 * 缺少身份证
	 */
	private LinearLayout	missLayoutLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentLayout(R.layout.my_smile);
	}
	
	@Override
	public void onStart(){
		super.onStart();
		displayHeaderDescription().setText(R.string.more);
		
		if (MyApplication.getInstance().getCurrentUser() != null
				&& MyApplication.getInstance().getCurrentUser().getUsername() != null
				&& !"".equals(MyApplication.getInstance().getCurrentUser().getUsername())) {
			((TextView) findViewById(R.id.my_smile_account)).setText(MyApplication.getInstance()
																					.getCurrentUser()
																					.getUsername());
		}
		ImageView backBtn = displayHeaderBack();
		backBtn.setOnClickListener(this);

		displayFooterMain(R.id.mainfooter_more);

		myOrdersPayLayout = (LinearLayout) findViewById(R.id.mysmile_myorders_layout_pay);
		myOrdersPayLayout.setOnClickListener(this);
		myOrdersResLayout = (LinearLayout) findViewById(R.id.mysmile_myorders_layout_res);
		myOrdersResLayout.setOnClickListener(this);
		myOrdersLayout = (LinearLayout) findViewById(R.id.mysmile_myorders_layout);
		myOrdersLayout.setOnClickListener(this);
		
		myCouponLayout = (LinearLayout) findViewById(R.id.mysmile_mycoupon_layout);
		myCouponLayout.setVisibility(View.GONE);
		myCouponLayout.setOnClickListener(this);
		
		settingLayout = (LinearLayout) findViewById(R.id.mysmile_setting_layout);
		settingLayout.setOnClickListener(this);
		
		clearCacheLayout = (LinearLayout) findViewById(R.id.mysmile_clearcache_layout);
		clearCacheLayout.setOnClickListener(this);
		
		contactUsLayoutLayout = (LinearLayout) findViewById(R.id.mysmile_contact_us_layout);
		contactUsLayoutLayout.setOnClickListener(this);
		
		missLayoutLayout = (LinearLayout) findViewById(R.id.mysmile_idcards_layout);
		missLayoutLayout.setVisibility(View.VISIBLE);
		missLayoutLayout.setOnClickListener(this);
		if (MyApplication.getInstance().getCurrentUser() != null
				&& "1".equals(MyApplication.getInstance().getCurrentUser().getIsmissidcard())) {
			findViewById(R.id.mysmile_miss_idcard_text).setVisibility(View.VISIBLE);
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
			case R.id.mysmile_myorders_layout_pay: {
				SUser user = MyApplication.getInstance().getCurrentUser();
				Intent intent = new Intent();
				if (user == null || MyApplication.getInstance().getSessionId() == null) {
					intent.putExtra("class", MyOrdersActivity.class);
					intent.setClass(context, LoginActivity.class);
				} else {
					intent.setClass(context, MyOrdersActivity.class);
				}
				intent.putExtra("status", MyOrdersActivity.NEED_PAY);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				break;
			}
			case R.id.mysmile_myorders_layout_res: {
				SUser user = MyApplication.getInstance().getCurrentUser();
				Intent intent = new Intent();
				if (user == null || MyApplication.getInstance().getSessionId() == null) {
					intent.putExtra("class", MyOrdersActivity.class);
					intent.setClass(context, LoginActivity.class);
				} else {
					intent.setClass(context, MyOrdersActivity.class);
				}
				intent.putExtra("status", MyOrdersActivity.NEED_RECEIVE);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
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
				break;
			}
			case R.id.mysmile_mycoupon_layout: {
				ToastUtils.showShort(context, "敬请期待！");
				break;
			}
			case R.id.mysmile_setting_layout: {
				SUser user = MyApplication.getInstance().getCurrentUser();
				Intent intent = null;//new Intent();
				if (user == null || MyApplication.getInstance().getSessionId() == null) {
					intent = new Intent(this, LoginActivity.class);
					intent.putExtra("class", PersonalSettingsActivity.class);
				} else {
					intent = new Intent(this, PersonalSettingsActivity.class);
				}
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivityForResult(intent, SETTINGS_CODE);
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
			case R.id.mysmile_idcards_layout: {
				SUser user = MyApplication.getInstance().getCurrentUser();
				Intent intent = new Intent();
				if (user == null || MyApplication.getInstance().getSessionId() == null) {
					intent.putExtra("class", IdcardManagerActivity.class);
					intent.setClass(context, LoginActivity.class);
				} else {
					intent.setClass(context, IdcardManagerActivity.class);
				}
				intent.putExtra("status", Constants.OrderStateCode.FOR_PAYMENT);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				break;
			}
		}
		super.onClick(v);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == SETTINGS_CODE && resultCode == RESULT_OK) {
			Log.i(MyApplication.LOG_TAG, "关闭我页面...");
			finish();
		}
		super.onActivityResult(requestCode, resultCode, data);
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
																		.clearFileCache();
														ToastUtils.showShort(context, "清理缓存成功！");

													}
												}).setNegativeButton("取消", null).show();
	}
}

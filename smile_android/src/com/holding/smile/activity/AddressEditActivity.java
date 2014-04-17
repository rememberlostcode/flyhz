
package com.holding.smile.activity;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.holding.smile.R;
import com.holding.smile.dto.RtnValueDto;
import com.holding.smile.entity.City;
import com.holding.smile.entity.Consignee;
import com.holding.smile.entity.District;
import com.holding.smile.entity.Province;
import com.holding.smile.tools.Constants;
import com.holding.smile.tools.JSONUtil;

/**
 * 收货地址编辑
 * 
 * @author zhangb 2014年4月15日 下午3:22:11
 * 
 */
public class AddressEditActivity extends BaseActivity implements OnClickListener {
	private Integer					conturyId	= Constants.CONTURY_ID, provinceId, cityId,
			districtId;
	private Consignee				consignee;

	private Spinner					province_spinner;
	private Spinner					city_spinner;
	private Spinner					district_spinner;
	private ArrayAdapter<Province>	province_adapter;
	private ArrayAdapter<City>		city_adapter;
	private ArrayAdapter<District>	district_adapter;

	private EditText				addressAddress;
	private EditText				addressName;
	private EditText				addressMobile;
	private EditText				addressZipcode;
	private EditText				addressIdcard;
	private Button					addressSave;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentLayout(R.layout.address_edit);
		ImageView backBtn = displayHeaderBack();
		backBtn.setOnClickListener(this);

		TextView textView = displayHeaderDescription();
		textView.setText("地址编辑");

		addressAddress = (EditText) findViewById(R.id.address_address);
		addressName = (EditText) findViewById(R.id.address_name);
		addressMobile = (EditText) findViewById(R.id.address_mobile);
		addressZipcode = (EditText) findViewById(R.id.address_zipcode);
		addressIdcard = (EditText) findViewById(R.id.address_idcard);
		addressSave = (Button) findViewById(R.id.address_add_save);

		addressAddress.setOnClickListener(this);
		addressName.setOnClickListener(this);
		addressMobile.setOnClickListener(this);
		addressZipcode.setOnClickListener(this);
		addressIdcard.setOnClickListener(this);
		addressSave.setOnClickListener(this);

		try {
			Intent intent = getIntent();
			if (intent.getExtras() != null) {
				String consigneeJson = intent.getExtras().getString("consignee");
				consignee = JSONUtil.getJson2Entity(consigneeJson, Consignee.class);
				if (consignee != null) {
					provinceId = consignee.getProvinceId();
					cityId = consignee.getCityId();
					districtId = consignee.getDistrictId();
					addressAddress.setText(consignee.getAddress());
					addressName.setText(consignee.getName());
					addressMobile.setText(consignee.getMobilephone());
					addressZipcode.setText(consignee.getZipcode());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (consignee == null) {
			consignee = new Consignee();
		}

		province_spinner = (Spinner) findViewById(R.id.province_spinner);
		city_spinner = (Spinner) findViewById(R.id.city_spinner);
		district_spinner = (Spinner) findViewById(R.id.county_spinner);
		loadSpinner();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_back: {
				setResult(RESULT_CANCELED, null);
				finish();
				break;
			}
			case R.id.address_add_save: {
				if (conturyId == null) {
					Toast.makeText(context, "请选择所属国家", Toast.LENGTH_SHORT).show();
					break;
				}
				consignee.setConturyId(conturyId);

				if (provinceId == null) {
					Toast.makeText(context, "请选择所属省份", Toast.LENGTH_SHORT).show();
					break;
				}
				consignee.setProvinceId(provinceId);

				if (cityId == null) {
					Toast.makeText(context, "请选择所属城市", Toast.LENGTH_SHORT).show();
					break;
				}
				consignee.setCityId(cityId);

				if (districtId == null) {
					Toast.makeText(context, "请选择所属县城", Toast.LENGTH_SHORT).show();
					break;
				}
				consignee.setDistrictId(districtId);

				if ("".equals(addressAddress.getText().toString())) {
					Toast.makeText(context, "街道地址不能为空", Toast.LENGTH_SHORT).show();
					break;
				}
				consignee.setAddress(addressAddress.getText().toString());

				if ("".equals(addressName.getText().toString())) {
					Toast.makeText(context, "联系人不能为空", Toast.LENGTH_SHORT).show();
					break;
				}
				consignee.setName(addressName.getText().toString());

				if ("".equals(addressMobile.getText().toString())) {
					Toast.makeText(context, "联系电话不能为空", Toast.LENGTH_SHORT).show();
					break;
				}
				consignee.setMobilephone(addressMobile.getText().toString());

				if ("".equals(addressZipcode.getText().toString())) {
					Toast.makeText(context, "邮编不能为空", Toast.LENGTH_SHORT).show();
					break;
				}
				consignee.setZipcode(addressZipcode.getText().toString());

				/******************如果是新增需要把ID传回来*****************/
				RtnValueDto rvd = MyApplication.getInstance().getSubmitService().consigneeEdit(consignee);
				if(200000 == rvd.getCode()){
					Toast.makeText(context, "保存成功！", Toast.LENGTH_SHORT).show();
					Intent intent = new Intent();
					intent.putExtra("consignee", JSONUtil.getEntity2Json(consignee));
					setResult(RESULT_OK, intent);
					finish();
				} else {
					Toast.makeText(context, "保存失败，请重试！", Toast.LENGTH_SHORT).show();
				}
				break;
			}
		}
		super.onClick(v);
	}

	private void loadSpinner() {
		List<Province> pList = MyApplication.getInstance().getSqliteService()
											.getProvinces(conturyId);
		province_spinner.setPrompt("省");
		province_adapter = new ArrayAdapter<Province>(context,
				android.R.layout.simple_spinner_item, pList);
		province_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		province_spinner.setAdapter(province_adapter);

		province_spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				provinceId = ((Province) province_spinner.getSelectedItem()).getId();
				province_spinner.getSelectedItem().toString();
				city_spinner.setPrompt("城市");
				List<City> cList = MyApplication.getInstance().getSqliteService()
												.getCitys(provinceId);
				city_adapter = new ArrayAdapter<City>(context,
						android.R.layout.simple_spinner_item, cList);
				city_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				city_spinner.setAdapter(city_adapter);

				city_spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
						cityId = ((City) city_spinner.getSelectedItem()).getId();
						city_spinner.getSelectedItem().toString();
						district_spinner.setPrompt("区县");

						List<District> dList = MyApplication.getInstance().getSqliteService()
															.getDistricts(cityId);
						district_adapter = new ArrayAdapter<District>(context,
								android.R.layout.simple_spinner_item, dList);
						district_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
						district_spinner.setAdapter(district_adapter);

						district_spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

							@Override
							public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
									long arg3) {
								districtId = ((District) district_spinner.getSelectedItem()).getId();
							}

							@Override
							public void onNothingSelected(AdapterView<?> arg0) {

							}

						});
						if (districtId != null) {
							setSpinnerItemSelectedByValue(district_spinner, districtId);
						}
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {

					}

				});
				if (cityId != null) {
					setSpinnerItemSelectedByValue(city_spinner, cityId);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});
		if (provinceId != null) {
			setSpinnerItemSelectedByValue(province_spinner, provinceId);
		}
	}

	public static void setSpinnerItemSelectedByValue(Spinner spinner, Integer value) {
		SpinnerAdapter apsAdapter = spinner.getAdapter(); // 得到SpinnerAdapter对象
		if (apsAdapter != null) {
			int k = apsAdapter.getCount();
			for (int i = 0; i < k; i++) {
				if (apsAdapter.getItem(i) instanceof Province) {
					if (value == ((Province) (apsAdapter.getItem(i))).getId()) {
						spinner.setSelection(i, true);// 默认选中项
						break;
					}
				} else if (apsAdapter.getItem(i) instanceof City) {
					if (value.equals(((City) (apsAdapter.getItem(i))).getId())) {
						spinner.setSelection(i, true);// 默认选中项
						break;
					}
				} else if (apsAdapter.getItem(i) instanceof District) {
					if (value.equals(((District) (apsAdapter.getItem(i))).getId())) {
						spinner.setSelection(i, true);// 默认选中项
						break;
					}
				}
			}
		}
	}
}

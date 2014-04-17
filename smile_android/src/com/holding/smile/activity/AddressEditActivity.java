
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
import android.widget.TextView;
import android.widget.Toast;

import com.holding.smile.R;
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

		loadSpinner();

		Intent intent = getIntent();
		String consigneeJson = intent.getExtras().getString("consignee");
		consignee = JSONUtil.getJson2Entity(consigneeJson, Consignee.class);
		if (consignee == null) {
			consignee = new Consignee();
		} else {

			addressAddress.setText(consignee.getAddress());
			addressName.setText(consignee.getName());
			addressMobile.setText(consignee.getMobilephone());
			addressZipcode.setText(consignee.getZipcode());
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

				MyApplication.getInstance().getSubmitService().consigneeEdit(consignee);
				break;
			}
		}
		super.onClick(v);
	}

	private void loadSpinner() {
		List<Province> pList = MyApplication.getInstance().getSqliteService()
											.getProvinces(conturyId);
		province_spinner = (Spinner) findViewById(R.id.province_spinner);
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
				city_spinner = (Spinner) findViewById(R.id.city_spinner);
				if (true) {
					city_spinner = (Spinner) findViewById(R.id.city_spinner);
					city_spinner.setPrompt("城市");
					List<City> cList = MyApplication.getInstance().getSqliteService()
													.getCitys(provinceId);
					city_adapter = new ArrayAdapter<City>(context,
							android.R.layout.simple_spinner_item, cList);
					city_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					city_spinner.setAdapter(city_adapter);

					city_spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

						@Override
						public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
								long arg3) {
							cityId = ((City) city_spinner.getSelectedItem()).getId();
							city_spinner.getSelectedItem().toString();
							Log.v("test", "city: " + city_spinner.getSelectedItem().toString()
									+ cityId.toString());
							if (true) {
								district_spinner = (Spinner) findViewById(R.id.county_spinner);
								district_spinner.setPrompt("区县");

								List<District> dList = MyApplication.getInstance()
																	.getSqliteService()
																	.getDistricts(cityId);
								district_adapter = new ArrayAdapter<District>(context,
										android.R.layout.simple_spinner_item, dList);
								district_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
								district_spinner.setAdapter(district_adapter);

								district_spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

									@Override
									public void onItemSelected(AdapterView<?> arg0, View arg1,
											int arg2, long arg3) {
										districtId = ((District) district_spinner.getSelectedItem()).getId();
									}

									@Override
									public void onNothingSelected(AdapterView<?> arg0) {

									}

								});
							}
						}

						@Override
						public void onNothingSelected(AdapterView<?> arg0) {
							// TODO Auto-generated method stub

						}

					});
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});
	}
}

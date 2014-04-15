
package com.holding.smile.activity;

import java.util.List;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import com.holding.smile.R;
import com.holding.smile.entity.City;
import com.holding.smile.entity.District;
import com.holding.smile.entity.Province;

/**
 * 收货地址编辑
 * 
 * @author zhangb 2014年4月15日 下午3:22:11
 * 
 */
public class AddressEditActivity extends BaseActivity implements OnClickListener {
	private Spinner					province_spinner;
	private Spinner					city_spinner;
	private Spinner					county_spinner;
	private Integer					provinceId, cityId;

	private ArrayAdapter<Province>	province_adapter;
	private ArrayAdapter<City>		city_adapter;
	private ArrayAdapter<District>	county_adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentLayout(R.layout.address_edit);
		ImageView backBtn = displayHeaderBack();
		backBtn.setOnClickListener(this);

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
		}
		super.onClick(v);
	}

	private void loadSpinner() {
		List<Province> pList = MyApplication.getInstance().getSqliteService().getProvinces();
		province_spinner = (Spinner) findViewById(R.id.province_spinner);
		province_spinner.setPrompt("省");
		province_adapter = new ArrayAdapter<Province>(context,
				android.R.layout.simple_spinner_item, pList);
		province_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		province_spinner.setAdapter(province_adapter);

		province_spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				provinceId = ((Province) province_spinner.getSelectedItem()).getID();
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
							cityId = ((City) city_spinner.getSelectedItem()).getID();
							city_spinner.getSelectedItem().toString();
							Log.v("test", "city: " + city_spinner.getSelectedItem().toString()
									+ cityId.toString());
							if (true) {
								county_spinner = (Spinner) findViewById(R.id.county_spinner);
								county_spinner.setPrompt("区县");

								List<District> dList = MyApplication.getInstance()
																	.getSqliteService()
																	.getDistricts(cityId);
								county_adapter = new ArrayAdapter<District>(context,
										android.R.layout.simple_spinner_item, dList);
								county_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
								county_spinner.setAdapter(county_adapter);

								county_spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

									@Override
									public void onItemSelected(AdapterView<?> arg0, View arg1,
											int arg2, long arg3) {
										county_spinner.getSelectedItem().toString();
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


package com.holding.smile.activity;

import java.util.List;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
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
	private Button					addressIdcardButton;
	private CheckBox				addressDefault;
	private Button					addressSave;
	private Button					addressDelete;
	private ImageView				imageView;

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
		addressIdcardButton = (Button) findViewById(R.id.address_idcard_button);
		addressDefault = (CheckBox) findViewById(R.id.address_default);
		addressSave = (Button) findViewById(R.id.address_add_save);
		addressDelete = (Button) findViewById(R.id.address_delete);
		imageView = (ImageView) findViewById(R.id.address_idcard_img);

		addressAddress.setOnClickListener(this);
		addressName.setOnClickListener(this);
		addressMobile.setOnClickListener(this);
		addressZipcode.setOnClickListener(this);
		addressIdcardButton.setOnClickListener(this);
		addressSave.setOnClickListener(this);
		addressDelete.setOnClickListener(this);

		try {
			Intent intent = getIntent();
			if (intent.getExtras() != null
					&& intent.getExtras().getSerializable("consignee") != null) {
				consignee = (Consignee) (intent.getExtras().getSerializable("consignee"));
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
			addressDelete.setVisibility(View.GONE);
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

				/****************** 如果是新增需要把ID传回来 *****************/
				RtnValueDto rvd = MyApplication.getInstance().getSubmitService()
												.consigneeEdit(consignee);
				if (200000 == rvd.getCode()) {
					Toast.makeText(context, "保存成功！", Toast.LENGTH_SHORT).show();
					Intent intent = new Intent();
					intent.putExtra("consignee", consignee);
					setResult(RESULT_OK, intent);
					finish();
				} else {
					Toast.makeText(context, "保存失败，请重试！", Toast.LENGTH_SHORT).show();
				}
				break;
			}

			case R.id.address_delete: {
				RtnValueDto rvd = null;
				if (consignee != null && consignee.getId() != null) {
					rvd = MyApplication.getInstance().getSubmitService()
										.addressRemove(consignee.getId());
				}
				if (rvd != null && 200000 == rvd.getCode()) {
					Toast.makeText(context, "删除成功！", Toast.LENGTH_SHORT).show();
					Intent intent = new Intent();
					consignee.setIsDel(true);
					intent.putExtra("consignee", consignee);
					setResult(RESULT_OK, intent);
					finish();
				} else {
					if (consignee != null && consignee.getId() != null) {
						Toast.makeText(context, "保存失败，请重试！", Toast.LENGTH_SHORT).show();
					}
				}
				break;
			}
			case R.id.address_idcard_button: {
				Intent i = new Intent(Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(i, SELECT_PHOTO_IMAGE);
				break;
			}
		}
		super.onClick(v);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == SELECT_PHOTO_IMAGE && resultCode == RESULT_OK) {
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };

			Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null,
					null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			cursor.close();
			// BitmapFactory.decodeStream(BitmapUtils.getCompressBitmap(picturePath,
			// 800, 1280, 100));
			imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
		}
		super.onActivityResult(requestCode, resultCode, data);
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
						spinner.setSelection(i, true);
						break;
					}
				} else if (apsAdapter.getItem(i) instanceof City) {
					if (value.equals(((City) (apsAdapter.getItem(i))).getId())) {
						spinner.setSelection(i, true);
						break;
					}
				} else if (apsAdapter.getItem(i) instanceof District) {
					if (value.equals(((District) (apsAdapter.getItem(i))).getId())) {
						spinner.setSelection(i, true);
						break;
					}
				}
			}
		}
	}
}

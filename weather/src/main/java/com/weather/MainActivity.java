package com.weather;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParserException;

import com.weather.entity.City;
import com.weather.entity.District;
import com.weather.entity.Province;
import com.weather.json.entity.Result;
import com.weather.json.entity.Weather;
import com.weather.json.entity.Weather_data;
import com.weather.tool.HttpUtils;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private Spinner sp_province;//下拉列表省市
	private Spinner sp_city;//下拉列表城市
	private Spinner sp_district;//下拉列表地区
	private int currentPro;//当前省市
	private ArrayAdapter<Province> provinceAdapter;
	private ArrayAdapter<City> cityAdapter;
	private ArrayAdapter<District> districtAdapter;
	private List<Province> provinces;

	private TextView tvCity;
	private TextView tvPM25;
	private TextView tvDate;
	private ImageView ivpic11;
	private ImageView ivpic12;
	private TextView tvweek1;
	private TextView tvwea1;
	private TextView tvwind1;
	private TextView tvtemper1;

	private ImageView ivpic21;
	private ImageView ivpic22;
	private TextView tvweek2;
	private TextView tvwea2;
	private TextView tvwind2;
	private TextView tvtemper2;

	private ImageView ivpic31;
	private ImageView ivpic32;
	private TextView tvweek3;
	private TextView tvwea3;
	private TextView tvwind3;
	private TextView tvtemper3;

	Button send_message;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		sp_province = (Spinner) findViewById(R.id.spinner1);
		sp_city = (Spinner) findViewById(R.id.spinner2);
		sp_district = (Spinner) findViewById(R.id.spinner3);
		tvCity = (TextView) findViewById(R.id.tvCity);
		tvPM25 = (TextView) findViewById(R.id.tvPM25);
		tvDate = (TextView) findViewById(R.id.tvDate);
        //第二个城市
		ivpic21 = (ImageView) findViewById(R.id.ivpic21);
		ivpic22 = (ImageView) findViewById(R.id.ivpic22);

		tvweek2 = (TextView) findViewById(R.id.tvweek2);
		tvwea2 = (TextView) findViewById(R.id.tvwea2);
		tvwind2 = (TextView) findViewById(R.id.tvwind2);
		tvtemper2 = (TextView) findViewById(R.id.tvtemper2);

		// 第三个城市
		ivpic31 = (ImageView) findViewById(R.id.ivpic31);
		ivpic32 = (ImageView) findViewById(R.id.ivpic32);

		tvweek3 = (TextView) findViewById(R.id.tvweek3);
		tvwea3 = (TextView) findViewById(R.id.tvwea3);
		tvwind3 = (TextView) findViewById(R.id.tvwind3);
		tvtemper3 = (TextView) findViewById(R.id.tvtemper3);

		// 第一个城市
		ivpic11 = (ImageView) findViewById(R.id.ivpic11);
		ivpic12 = (ImageView) findViewById(R.id.ivpic12);

		tvweek1 = (TextView) findViewById(R.id.tvweek1);
		tvwea1 = (TextView) findViewById(R.id.tvwea1);
		tvwind1 = (TextView) findViewById(R.id.tvwind1);
		tvtemper1 = (TextView) findViewById(R.id.tvtemper1);

		send_message=(Button)findViewById(R.id.send);




		// 获取到地区信息
		try {
			provinces = HttpUtils.getProvinces(this);
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
		// adapter,填充信息
		provinceAdapter = new ArrayAdapter<Province>(this,
				android.R.layout.simple_spinner_item, android.R.id.text1,
				provinces);
		provinceAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp_province.setAdapter(provinceAdapter);

		cityAdapter = new ArrayAdapter<City>(this,
				android.R.layout.simple_spinner_item, android.R.id.text1,
				provinces.get(0).getCitys());
		cityAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp_city.setAdapter(cityAdapter);

		districtAdapter = new ArrayAdapter<District>(this,
				android.R.layout.simple_spinner_item, android.R.id.text1,
				provinces.get(0).getCitys().get(0).getDisList());
		districtAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp_district.setAdapter(districtAdapter);
		//当选择省份时，城市和地方列表会变化
		sp_province.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				currentPro = position;
				cityAdapter = new ArrayAdapter<City>(MainActivity.this,
						android.R.layout.simple_spinner_item,
						android.R.id.text1, provinces.get(position).getCitys());
				cityAdapter
						.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				sp_city.setAdapter(cityAdapter);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
		// 当选择城市时，地方列表会变化
		sp_city.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				districtAdapter = new ArrayAdapter<District>(MainActivity.this,
						android.R.layout.simple_spinner_item,
						android.R.id.text1, provinces.get(currentPro)
								.getCitys().get(position).getDisList());
				districtAdapter
						.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				sp_district.setAdapter(districtAdapter);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
		//当选择地方时，显示具体的天气情况
		sp_district.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// 选择的城市
				District dis = districtAdapter.getItem(position);
				// Log.i("i", dis.getName());
				new WeatherAsyncTask().execute(dis.getName());
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}

		});

		send_message.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, Send.class);
				startActivity(intent);
			}
		});

	}

	// 异步类，获取天气数据
	class WeatherAsyncTask extends AsyncTask<String, Void, Weather> {

		@Override
		protected Weather doInBackground(String... params) {
			String url = HttpUtils.getURl(params[0]);
			String jsonStr = HttpUtils.getJsonStr(url);
			Weather weather = HttpUtils.fromJson(jsonStr);
			Result r = weather.getResults().get(0);
			/*List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
			list = HttpUtils.toListMap(r);*/
			for(int i = 0;i<3;i++){
				Weather_data w = r.getWeather_data().get(i);
				w.setDayPicture(HttpUtils.getImage(w.getDayPictureUrl()));
				w.setNightPicture(HttpUtils.getImage(w.getNightPictureUrl()));
			}
			return weather;
		}

		@Override
		protected void onPostExecute(Weather result ) {
			Result res = result.getResults().get(0);
			Weather_data wa = res.getWeather_data().get(0);
			// System.out.println(res.getWeather_data());
			tvCity.setText("城市:" + res.getCurrentCity());
			String pm2_5 = "".equals(res.getPm25()) ? "75" : res.getPm25();
//			Log.i("i",res.getPm25()+"wwwww");
			tvPM25.setText("PM2.5:" + pm2_5);
			tvDate.setText("日期:" + result.getDate());
			// 网络获取的
//			ivpic11.setImageResource(R.drawable.d00);
//			ivpic12.setImageResource(R.drawable.d01);
			ivpic11.setImageBitmap(wa.getDayPicture());
			ivpic12.setImageBitmap(wa.getNightPicture());
			String str = wa.getDate();
			tvweek1.setText(str.substring(0, 2));
			tvwea1.setText(wa.getWeather());
			tvwind1.setText(wa.getWind());
			tvtemper1.setText(str.substring(14, str.length()-1));

			wa = res.getWeather_data().get(1);
			// System.out.println(wa2);

			tvtemper2.setText(wa.getTemperature());
			ivpic21.setImageBitmap(wa.getDayPicture());
			ivpic22.setImageBitmap(wa.getNightPicture());
			tvweek2.setText(wa.getDate());
			tvwea2.setText(wa.getWeather());
			tvwind2.setText(wa.getWind());
			tvtemper2.setText(wa.getTemperature());

			wa = res.getWeather_data().get(2);

			// System.out.println(wa4);
			ivpic31.setImageBitmap(wa.getDayPicture());
			ivpic32.setImageBitmap(wa.getNightPicture());
			tvweek3.setText(wa.getDate());
			tvwea3.setText(wa.getWeather());
			tvwind3.setText(wa.getWind());
			tvtemper3.setText(wa.getTemperature());
		}
	}


}

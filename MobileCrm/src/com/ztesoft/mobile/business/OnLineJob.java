/**
 */
package com.ztesoft.mobile.business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.ztesoft.mobile.R;
import com.ztesoft.mobile.core.BaseActivity;
import com.ztesoft.mobile.core.Caller;

/**
 * @author lei
 * 
 */
public class OnLineJob extends BaseActivity {

	private String url = "/myinfo/onlanjob/";

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState, R.layout.onlinejob);

		ListView list = (ListView) findViewById(R.id.listView01);

		ArrayList<HashMap<String, Object>> listItem = getAll();// 查询数据

		SimpleAdapter mSchedule = new SimpleAdapter(this, listItem,// 数据来源
				R.layout.simplelist_item, new String[] { "ItemID", "ItemTitle",
						"ItemText" }, new int[] { R.id.ItemID, R.id.ItemTitle,
						R.id.ItemText });

		list.setAdapter(mSchedule);
		TextView title = (TextView) findViewById(R.id.menuTitle);
		title.setText("未转化单列表");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	/**
	 * 取未转化单
	 * 
	 * @throws Exception
	 */
	private ArrayList<HashMap<String, Object>> getAll() {
		ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
		try {
			TypeToken targetType = new TypeToken<List<HashMap<String, String>>>() {
			};
			List<Map<String, String>> rst;
			rst = Caller.getJson(url, targetType);
			for (Iterator iterator = rst.iterator(); iterator.hasNext();) {
				Map map = (Map) iterator.next();
				HashMap<String, Object> map2 = new HashMap<String, Object>();
				map2.put("ItemText", map.get("offer_name"));
				map2.put("ItemTitle", "推荐号:" + map.get("recommend_id"));
				listItem.add(map2);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return listItem;
	}

	public void onClick(View v) {
		finish();
	}
}

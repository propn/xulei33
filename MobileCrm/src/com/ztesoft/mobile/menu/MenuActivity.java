package com.ztesoft.mobile.menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.ztesoft.mobile.R;
import com.ztesoft.mobile.core.App;
import com.ztesoft.mobile.core.BaseActivity;

/**
 * 
 * @author Thunder.xu
 * 
 */
public class MenuActivity extends BaseActivity {

	private int pid = 0;// 父菜单id,默认为根
	private Menu pMenu;// 父菜单对象
	List<Menu> menus;
	private GridView main;//
	private TextView info;//

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.menu_view);
		main = (GridView) findViewById(R.id.main);
		info = (TextView) findViewById(R.id.info);
		init();
	}

	private void init() {

		main.clearDisappearingChildren();
		main.setSelector(R.drawable.toolbar_menu_item);
		/** 设置菜单Adapter **/
		main.setAdapter(menuAdapter());
		/** 监听菜单选项 **/
		main.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int index,
					long arg3) {
				Menu m = menus.get(index);
				if (null == m) {
					return;
				}

				/* 叶子节点 */
				if (m.getActivity() != null && !m.getActivity().equals("")) {
					Intent intent = new Intent();
					intent.setClassName(app.getPackageName(),
							app.getPackageName() + m.getActivity());
					startActivity(intent);
					return;
				}
				/* 处理特殊菜单项 */
				switch (m.getId()) {
				case 100:/* 返回-进入上一级菜单 */
					pid = pMenu.getPid();
					init();
					break;
				case 200:/* 退出 */
					eixtPragram();
					break;
				default:/* 进行下一级菜单 */
					pid = m.getId();
					init();
					break;
				}

			}
		});
	}

	/**
	 * 构造菜单Adapter
	 * 
	 * @param menuNameArray
	 *            名称
	 * @param imageResourceArray
	 *            图片
	 * @return SimpleAdapter
	 */
	private SimpleAdapter menuAdapter() {

		pMenu = App.menuCache.get(pid);
		info.setText(pMenu.getName());

		menus = new ArrayList<Menu>();
		menus.addAll(pMenu.getMenu());

		ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();

		for (Iterator<Menu> it = menus.iterator(); it.hasNext();) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			Menu menu = (Menu) it.next();
			map.put("itemImage", menu.getImage());
			map.put("itemText", menu.getName());
			data.add(map);
		}

		/* 特色菜单 */
		if (pid == 0) {

		} else {
			/* 返回 */
			HashMap<String, Object> mBack = new HashMap<String, Object>();
			Menu menuBK = App.menuCache.get(100);
			menuBK.setPid(pid);
			mBack.put("itemImage", menuBK.getImage());
			mBack.put("itemText", menuBK.getName());
			data.add(mBack);
			menus.add(menuBK);

			/* 退出 */
			HashMap<String, Object> mClose = new HashMap<String, Object>();
			Menu menuClose = App.menuCache.get(200);
			menuClose.setPid(pid);
			mClose.put("itemImage", menuClose.getImage());
			mClose.put("itemText", menuClose.getName());
			data.add(mClose);
			menus.add(menuClose);
		}

		SimpleAdapter simperAdapter = new SimpleAdapter(this, data,
				R.layout.text_image_button, new String[] { "itemImage",
						"itemText" }, new int[] { R.id.image, R.id.name });
		return simperAdapter;
	}
}

package com.ztesoft.mobile.core;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.xml.sax.InputSource;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.ztesoft.mobile.R;
import com.ztesoft.mobile.menu.Menu;

/**
 * @author Thunder.xu
 * 
 */
public class App extends Application {

	public static String today = "";// 当前日期
	public static String lastUpdateDate = "2011-05-01";// 上次检查版本更新时间

	public static int versionCode = 0;// 当前版本号
	public static String versionName = "";

	public static String serverUrl = "http://localhost:8080/jersey16/rest";
	public static String apkUrl = "http://localhost:8080/MmtWeb/MobileCrm.apk";

	public static String userName = "userName";
	public static String passWord = "passWord";

	public static int mWidth = 0;// 屏幕高度
	public static int mHeight = 0;// 屏幕宽度

	public static Map<Integer, Menu> menuCache = new HashMap<Integer, Menu>();// 系统菜单缓存

	/**
	 * Tag used for DDMS logging
	 */
	public static String tag = "androidAPP";

	/**
	 * Singleton pattern
	 */
	private static App app;

	public static App getInstance() {
		return app;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		app = this;

		SharedPreferences settings = getSharedPreferences(tag,
				Context.MODE_WORLD_READABLE);

		serverUrl = settings.getString("serverUrl", "");
		userName = settings.getString("userName", "");
		passWord = settings.getString("passWord", "");

		// 上次检查更新日期
		lastUpdateDate = settings.getString("lastUpdateDate", "");
		// 当前时间
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		today = sdf.format(new Date());
		// 版本信息
		PackageManager manager = this.getPackageManager();
		PackageInfo info;
		try {
			info = manager.getPackageInfo(this.getPackageName(), 0);
			versionCode = info.versionCode;
			versionName = info.versionName;
		} catch (NameNotFoundException e) {
			Log.v(tag, e.getMessage());
		}
		try {
			parserXml();
		} catch (Exception e) {
			Log.v(tag, "menu.xml解析失败！");
		}
	}

	/**
	 * 
	 * 解析XML到菜单cache
	 * 
	 * @throws Exception
	 */
	private void parserXml() throws Exception {
		InputStream is = getResources().openRawResource(R.raw.menu);
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		int i = is.read();
		while (i != -1) {
			bo.write(i);
			i = is.read();
		}
		String xml = bo.toString();
		StringReader reader = new StringReader(xml);
		Map menus = XmlUtil.xmlToMap(new InputSource(reader));
		initMenu((Map) menus.get("menu"));
	}

	private static void initMenu(Map map) {
		Menu m = new Menu();
		int id = Integer.valueOf((String) map.get("id"));
		m.setId(id);
		m.setName(map.get("name") != null ? (String) map.get("name") : "");
		m.setPid(Integer.valueOf((String) map.get("pid")));

		String image = map.get("image") != null ? (String) map.get("image")
				: null;
		if (null != image) {
			m.setImage(app.getResId(image));
		}
		m.setActivity(map.get("activity") != null ? (String) map
				.get("activity") : "");
		Object obj = map.get("menu");// 下级菜单
		if (obj instanceof ArrayList) {// 有多个下级菜单
			// 多于一个子菜单情况
			List<Map<Integer, Menu>> menu = (List<Map<Integer, Menu>>) obj;
			if (menu != null) {
				for (Iterator it = menu.iterator(); it.hasNext();) {
					Menu m2 = new Menu();
					Map map2 = (Map) it.next();
					int id2 = Integer.valueOf((String) map2.get("id"));
					m2.setId(id2);
					m2.setName(map2.get("name") != null ? (String) map2
							.get("name") : "");
					m2.setPid(Integer.valueOf((String) map2.get("pid")));
					String image2 = map2.get("image") != null ? (String) map2
							.get("image") : null;
					if (null != image2) {
						m2.setImage(app.getResId(image2));
					}
					m2.setActivity(map2.get("activity") != null ? (String) map2
							.get("activity") : "");
					m.getMenu().add(m2);
					menuCache.put(id2, m2);
				}
			}
			menuCache.put(id, m);
			if (menu != null) {
				for (Iterator it3 = menu.iterator(); it3.hasNext();) {
					Map map3 = (Map) it3.next();
					initMenu(map3);// 迭代处理
				}
			}
		} else if (obj instanceof Map) {// 只有一个下级菜单
			// 只有一个子菜单情况
			Map menu = map.get("menu") != null ? (Map) map.get("menu") : null;
			if (menu != null) {
				Menu m2 = new Menu();
				int id2 = Integer.valueOf((String) menu.get("id"));
				m2.setId(id2);
				m2.setName(menu.get("name") != null ? (String) menu.get("name")
						: "");
				m2.setPid(Integer.valueOf((String) menu.get("pid")));

				String image2 = menu.get("image") != null ? (String) menu
						.get("image") : null;
				if (null != image2) {
					m2.setImage(app.getResId(image2));
				}

				m2.setActivity(menu.get("activity") != null ? (String) menu
						.get("activity") : "");
				m.getMenu().add(m2);
				menuCache.put(id2, m2);
			}
			menuCache.put(id, m);
			initMenu(menu);// 迭代处理
		}
	}

	/**
	 * 通过图片名称获取res图片
	 * 
	 * @param name
	 *            图片名称
	 * @return Bitmap
	 */
	public Bitmap getRes(String name) {
		ApplicationInfo appInfo = getApplicationInfo();
		int resID = getResources().getIdentifier(name, "drawable",
				appInfo.packageName);
		return BitmapFactory.decodeResource(getResources(), resID);
	}

	public int getResId(String name) {
		ApplicationInfo appInfo = getApplicationInfo();
		int resID = getResources().getIdentifier(name, "drawable",
				appInfo.packageName);
		return resID;
	}

}

package com.ztesoft.mobile.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 
 * @author Thunder.xu
 * 
 */
public class StringUtil {

	public static boolean isBlank(String str) {
		if (null == str || str.trim().length() == 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 
	 * @param is
	 * @return
	 */
	public static String convertStreamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	public static final String EMPTY_STR = "".intern();
	public static String trimToNull(String str) {
		if (str == null) {
			return null;
		}

		str = str.trim();

		return (str.length() == 0) ? null : str;
	}

}

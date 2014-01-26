package com.lnu.todomorrow.utils;

import android.annotation.SuppressLint;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtil {

	/**
	 * formats the Calendar object to standard date format
	 * 
	 * @param cal
	 * @return empty string if Calendar is null
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getFormattedDate(Calendar cal) {
		if (cal == null) {
			// Log.d("TimeUtil","calendar == null");

			return "";
		}

		Date date = cal.getTime();
		// DateFormat sdf = SimpleDateFormat.getDateTimeInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		String strDead = sdf.format(date);

		// Log.d("TimeUtil", "formated date (" + cal.getTimeInMillis() +
		// ") to: "
		// + strDead);
		return strDead;
	}

}

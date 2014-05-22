
package com.flyhz.avengers.framework.util;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class DateUtil {
	/** 缺省日期格式 */
	public static final String	DEFAULT_DATE_FORMAT				= "yyyy-MM-dd";

	/** 缺省时间格式 */
	public static final String	DEFAULT_TIME_FORMAT				= "HH:mm:ss";

	/** 缺省月格式 */
	public static final String	DEFAULT_MONTH					= "MONTH";

	/** 缺省年格式 */
	public static final String	DEFAULT_YEAR					= "YEAR";

	/** 缺省日格式 */
	public static final String	DEFAULT_DATE					= "DAY";

	/** 缺省小时格式 */
	public static final String	DEFAULT_HOUR					= "HOUR";

	/** 缺省分钟格式 */
	public static final String	DEFAULT_MINUTE					= "MINUTE";

	/** 缺省秒格式 */
	public static final String	DEFAULT_SECOND					= "SECOND";

	/** 缺省长日期格式 */
	public static final String	DEFAULT_DATETIME_FORMAT			= "yyyy-MM-dd HH-mm";

	/** 缺省长日期格式,精确到秒 */
	public static final String	DEFAULT_DATETIME_FORMAT_SEC		= "yyyy-MM-dd HH:mm:ss";

	/** 缺省长日期格式,精确到毫秒 */
	public static final String	DEFAULT_DATETIME_FORMAT_M_SEC	= "yyyyMMddHHmmssSSS";

	/**
	 * 得到yyyy-MM-dd'T'HH:mm:ssZ格式类型的时间用来solr查询
	 * 
	 * @param date
	 * @return
	 */
	public static String dateToStrLong(Date date) {
		if (date != null) {
			SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
			String currentDateTime = fmt.format(date);
			return currentDateTime;
		}
		return null;
	}

	/**
	 * 将长时间格式字符串转换为时间 yyyy-MM-dd HH:mm:ss
	 * 
	 * @param strDate
	 * @return
	 */
	public static Date strToDateLong(String strDate) {
		if (strDate != null && !"".equals(strDate)) {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			ParsePosition pos = new ParsePosition(0);
			Date strtodate = formatter.parse(strDate, pos);
			return strtodate;
		}
		return null;
	}

	/**
	 * 得到yyyy-MM-dd HH:mm:ss格式类型的时间
	 * 
	 * @param date
	 * @return
	 */
	public static String dateToStr(Date date) {
		if (date != null) {
			SimpleDateFormat fmt = new SimpleDateFormat(DEFAULT_DATETIME_FORMAT_SEC);
			String currentDateTime = fmt.format(date);
			return currentDateTime;
		}
		return null;
	}

	/**
	 * 得到yyyy-MM-dd HH:mm:ss sss格式类型的时间
	 * 
	 * @param date
	 * @return
	 */
	public static String dateToStrMSec(Date date) {
		if (date != null) {
			SimpleDateFormat fmt = new SimpleDateFormat(DEFAULT_DATETIME_FORMAT_M_SEC);
			String currentDateTime = fmt.format(date);
			return currentDateTime;
		}
		return null;
	}

	/**
	 * date时区转换，和当前时间相差多少个时区utc
	 * 
	 * @param date
	 * @param utc
	 * @return String yyyy-MM-dd HH:mm:ss
	 */
	public static String dateToStrTimeZone(Date myDate, Integer GMT) {

		String fromFormat = "yyyy-MM-dd HH:mm:ss";
		SimpleDateFormat format = new SimpleDateFormat(fromFormat);
		TimeZone zone = TimeZone.getTimeZone("GMT-8");
		format.setTimeZone(zone);
		String strdate = format.format(myDate);

		return strdate;
	}

	public static java.util.Date getAddDay(String strApdate, int amount) throws ParseException {
		int year = new Integer(strApdate.substring(0, 4)).intValue();
		int month = new Integer(strApdate.substring(5, 7)).intValue();
		int date = new Integer(strApdate.substring(8, 10)).intValue();
		int hour = new Integer(strApdate.substring(11, 13)).intValue();
		int min = new Integer(strApdate.substring(14, 16)).intValue();
		int sed = new Integer(strApdate.substring(17, 19)).intValue();
		GregorianCalendar rightNow = new GregorianCalendar(year, month - 1, date, hour, min, sed);
		SimpleDateFormat formatter = new SimpleDateFormat(DEFAULT_DATETIME_FORMAT_SEC);
		rightNow.add(GregorianCalendar.DATE, amount);
		java.util.Date dayaddafter = formatter.parse(formatter.format(rightNow.getTime()));
		return dayaddafter;
	}

	/**
	 * 转换时间为：N小时/分钟/周/月/年之前
	 * 
	 * @param paramData
	 * @return string
	 */
	public static String convertDataTime(Date paramData) {
		if (paramData != null) {
			Date nowDate = new Date();
			// 时间差转换
			long between = (nowDate.getTime() - paramData.getTime()) / 1000;
			long year = between / (365 * 24 * 3600);
			long month = between / (30 * 24 * 3600);
			long week = between / (7 * 24 * 3600);
			long day = between / (24 * 3600);
			long hour = between / 3600;
			long minute = between / 60;
			if (year > 0) {
				return year + "年以前";
			} else if (month > 0) {
				return month + "月以前";
			} else if (week > 0) {
				return week + "周以前";
			} else if (day > 0) {
				return day + "天以前";
			} else if (hour > 0) {
				return hour + "小时以前";
			} else if (minute > 0) {
				return minute + "分钟以前";
			} else {
				return between + "秒以前";
			}
		}
		return null;
	}

	/***
	 * 在当前日期上增加天数
	 * 
	 * @param day
	 * @return
	 */
	public static Calendar getAddDaysForCalendar(int day) {
		Calendar cal = Calendar.getInstance();
		// 在当前时间上增加day天
		cal.add(Calendar.DATE, day);
		return cal;
	}

	public static void main(String[] args) {
		System.out.println(DateUtil.dateToStrMSec(new Date()));
	}
}

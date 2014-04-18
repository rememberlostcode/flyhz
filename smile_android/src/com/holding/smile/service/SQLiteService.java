
package com.holding.smile.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.holding.smile.activity.MyApplication;
import com.holding.smile.entity.City;
import com.holding.smile.entity.District;
import com.holding.smile.entity.Province;
import com.holding.smile.entity.SUser;
import com.holding.smile.tools.DBHelper;
import com.holding.smile.tools.DateUtil;

/**
 * 本地DB工具 ，先要初始化public SQLiteService(Context context) ，不用后需要调用closeDB()来关闭
 * 
 * @author zhangb 2014年2月25日 上午10:56:54
 * 
 */
public class SQLiteService {

	private DBHelper		helper;
	private SQLiteDatabase	db;

	/**
	 * 初始化DB
	 * 
	 * @param context
	 *            不能为null
	 */
	public SQLiteService(Context context) {
		if (context == null) {
			try {
				throw new Exception("SQLiteService:context is not init!");
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			if (helper == null || db == null) {
				helper = new DBHelper(context);
				db = helper.getWritableDatabase();
			}
		}
	}

	/**
	 * 关闭DB
	 */
	public void closeDB() {
		if (db != null) {
			db.close();
		}
	}

	/**
	 * 添加搜索词
	 * 
	 * @param content
	 * @return
	 */
	public boolean addSearch(String content) {
		try {
			Cursor cursor = db.query("SEARCH", new String[] { "ID", "CONTENT", "TIME", "COUNT" },
					"CONTENT = ?", new String[] { String.valueOf(content) }, null, null, null, "1");

			while (cursor.moveToNext()) {
				db.execSQL("UPDATE SEARCH SET COUNT=COUNT+1 WHERE CONTENT=?",
						new Object[] { content });
				return true;
			}
			db.execSQL("INSERT INTO SEARCH (CONTENT,TIME,COUNT)VALUES(?, ?, 1)", new Object[] {
					content, DateUtil.dateToStr(new Date()) });
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 获取最近的搜索词，搜得越多越在前面，最多10个
	 * 
	 * @param content
	 * @return
	 */
	public List<String> getSearchWords(String content) {
		List<String> list = new ArrayList<String>();
		Cursor cursor = null;
		if (content == null || content.trim().equals("")) {
			cursor = db.rawQuery(
					"SELECT S.CONTENT FROM SEARCH S ORDER BY S.COUNT DESC,S.TIME DESC LIMIT 10",
					null);
		} else {
			cursor = db.rawQuery(
					"SELECT S.CONTENT FROM SEARCH S WHERE S.CONTENT LIKE ? ORDER BY S.COUNT DESC,S.TIME DESC LIMIT 10",
					new String[] { content + "%" });
		}
		while (cursor != null && cursor.moveToNext()) {
			list.add(cursor.getString(0));
		}
		return list;
	}

	/**
	 * 获取指定国家的所有省份
	 * 
	 * @param conturyId
	 *            国家ID
	 * @return
	 */
	public List<Province> getProvinces(Integer conturyId) {
		if (conturyId == null || conturyId == 0) {
			return null;
		}
		List<Province> list = new ArrayList<Province>();
		Cursor cursor = db.rawQuery(
				"select id,name from province where conturyid=? order by sort asc",
				new String[] { conturyId + "" });
		while (cursor != null && cursor.moveToNext()) {
			Province province = new Province();
			province.setId(cursor.getInt(0));
			province.setName(cursor.getString(1));
			list.add(province);
		}
		return list;
	}

	/**
	 * 获取指定省份的所有城市
	 * 
	 * @param proid
	 *            省份ID
	 * @return
	 */
	public List<City> getCitys(Integer proid) {
		if (proid == null || proid == 0) {
			return null;
		}
		List<City> list = new ArrayList<City>();
		Cursor cursor = db.rawQuery("select id,name from city where proid=? order by sort asc",
				new String[] { proid + "" });
		while (cursor != null && cursor.moveToNext()) {
			City city = new City();
			city.setId(cursor.getInt(0));
			city.setName(cursor.getString(1));
			list.add(city);
		}
		return list;
	}

	/**
	 * 获取指定城市的所有区县
	 * 
	 * @param cityid
	 *            城市ID
	 * @return
	 */
	public List<District> getDistricts(Integer cityid) {
		if (cityid == null || cityid == 0) {
			return null;
		}
		List<District> list = new ArrayList<District>();
		Cursor cursor = db.rawQuery(
				"select id,name from district where cityid=? order by sort asc",
				new String[] { cityid + "" });
		while (cursor != null && cursor.moveToNext()) {
			District district = new District();
			district.setId(cursor.getInt(0));
			district.setName(cursor.getString(1));
			list.add(district);
		}
		return list;
	}

	/**
	 * 获取指定的省市区信息，如：浙江省杭州市西湖区
	 * 
	 * @param proid
	 *            省份ID
	 * @param cityid
	 *            城市ID
	 * @param districtid
	 *            区县ID
	 * @return
	 */
	public String getProvinceCityDistrict(Integer proid, Integer cityid, Integer districtid) {
		if (proid == null || proid == 0 || cityid == null || cityid == 0 || districtid == null
				|| districtid == 0) {
			return "";
		}
		String pcd = "";
		Cursor cursor = db.rawQuery(
				"select (p.name||c.name||d.name) name from province p,city c,district d where p.id=? and c.id=? and d.id=?",
				new String[] { proid + "", cityid + "", districtid + "" });
		while (cursor != null && cursor.moveToNext()) {
			pcd = cursor.getString(0);
		}
		return pcd;
	}

	/**
	 * 获取当前登录用户信息(即最后一个登录的人)
	 * 
	 * @return
	 */
	public SUser getScurrentUser() {
		SUser user = null;
		Cursor cursor = db.query("user", new String[] { "cuid", "username", "token", "mobilephone",
				"identitycard", "qq", "email", "weibo", "weixin" }, "flag = ?",
				new String[] { "1" }, null, null, null, "1");

		while (cursor.moveToNext()) {
			user = new SUser();
			user.setId(cursor.getInt(0));
			user.setUsername(cursor.getString(1));
			user.setToken(cursor.getString(2));
			user.setMobilephone(cursor.getString(3));
			user.setIdentitycard(cursor.getString(4));
			user.setQq(cursor.getString(5));
			user.setEmail(cursor.getString(6));
			user.setWeibo(cursor.getString(7));
			user.setWeixin(cursor.getString(8));
		}
		cursor.close();

		return user;
	}

	/**
	 * 添加当前登录用户到本地数据库，添加后会把此用户标识设置成当前登录：先在MyApplication.getInstance().
	 * getCurrentUser()中设置要修改的内容， 再调用此方法
	 * 
	 * @param user
	 *            用户信息
	 * @return 当前登录用户
	 */
	public SUser addUser() {
		SUser user = MyApplication.getInstance().getCurrentUser();
		if (user != null && user.getId() != null && user.getId() != 0 && user.getUsername() != null) {
			SUser bdUser = MyApplication.getInstance().getSqliteService().getUserById(user.getId());
			if (bdUser == null) {// 本地不存在就添加
				db.beginTransaction(); // 开始事务
				try {
					// 先把所有的用户设置成非当前用户
					ContentValues cv = new ContentValues();
					cv.put("flag", "0");
					db.update("user", cv, "1 = 1", new String[] {});

					// 插入新用户
					db.execSQL(
							"INSERT INTO user VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, 1)",
							new Object[] { user.getId(), user.getUsername(), user.getToken(),
									user.getMobilephone(), user.getIdentitycard(), user.getQq(),
									user.getEmail(), user.getWeibo(), user.getWeixin() });
					db.setTransactionSuccessful(); // 设置事务成功完成
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					db.endTransaction(); // 结束事务
				}
			} else {// 已存在修改
				updateUser();
			}
		}
		return MyApplication.getInstance().getCurrentUser();
	}

	/**
	 * 保存当前登录用户的信息：先在MyApplication.getInstance().getCurrentUser()中设置要修改的内容，
	 * 再调用此方法
	 * 
	 */
	public void updateUser() {
		SUser user = MyApplication.getInstance().getCurrentUser();
		if (user != null && user.getId() != null & user.getId() > 0) {
			db.beginTransaction(); // 开始事务
			try {
				ContentValues cv = new ContentValues();

				// 先把所有的用户设置成非当前用户
				cv.put("flag", "0");
				db.update("user", cv, "1 = 1", new String[] {});

				cv.clear();
				cv.put("username", user.getUsername());
				cv.put("token", user.getToken());
				cv.put("mobilephone", user.getMobilephone());
				cv.put("identitycard", user.getIdentitycard());
				cv.put("qq", user.getQq());
				cv.put("email", user.getEmail());
				cv.put("weibo", user.getWeibo());
				cv.put("weixin", user.getWeixin());
				cv.put("flag", "1");
				db.update("user", cv, "cuid = ?", new String[] { String.valueOf(user.getId()) });

				MyApplication.getInstance().getCurrentUser().setFlag("1");
				db.setTransactionSuccessful(); // 设置事务成功完成
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				db.endTransaction(); // 结束事务
			}
		}
	}

	/**
	 * 根据id设置当前登录用户
	 * 
	 * @param cuid
	 * @return 当前登录用户
	 */
	public SUser setCurrentUser(String cuid) {
		db.beginTransaction();
		try {
			ContentValues cv = new ContentValues();

			// 先把所有的用户设置成非当前用户
			cv.put("flag", "0");
			db.update("user", cv, "1 = 1", new String[] {});

			// 把指定用户设置成当前登录用户
			cv.clear();
			cv.put("flag", "1");
			db.update("user", cv, "cuid = ?", new String[] { cuid });

			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
		}
		return getScurrentUser();
	}

	/**
	 * 本地数据库的当前用户标识去掉,去掉后下次进入应用需要重新登录
	 * 
	 * @param cuid
	 *            当前登录用户ID
	 * @return
	 */
	public void setCurrentUserEmpty(int cuid) {
		if (cuid != 0) {
			db.beginTransaction();
			try {
				ContentValues cv = new ContentValues();
				cv.put("flag", "0");
				db.update("user", cv, "cuid = ?", new String[] { String.valueOf(cuid) });
				db.setTransactionSuccessful();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				db.endTransaction();
			}
		}
	}

	/**
	 * 根据ID获取登录用户信息
	 * 
	 * @return
	 */
	private SUser getUserById(int cuid) {
		SUser user = new SUser();
		Cursor cursor = db.query("user", new String[] { "cuid", "username", "token", "mobilephone",
				"identitycard", "qq", "email", "weibo", "weixin", "flag" }, "cuid = ?",
				new String[] { String.valueOf(cuid) }, null, null, null, "1");

		while (cursor.moveToNext()) {
			user.setId(cursor.getInt(0));
			user.setUsername(cursor.getString(1));
			user.setToken(cursor.getString(2));
			user.setMobilephone(cursor.getString(3));
			user.setIdentitycard(cursor.getString(4));
			user.setQq(cursor.getString(5));
			user.setEmail(cursor.getString(6));
			user.setWeibo(cursor.getString(7));
			user.setWeixin(cursor.getString(8));
		}
		cursor.close();

		if (user.getId() != null && user.getId() != 0) {
			return user;
		}
		return null;
	}

	/**
	 * 根据openid获取登录用户信息，有就说明是登录过的用户
	 * 
	 * @return 未找到返回null
	 */
	public SUser getUserByUsername(String username) {
		SUser user = new SUser();
		Cursor cursor = db.query("user", new String[] { "cuid", "username", "token", "mobilephone",
				"identitycard", "qq", "email", "weibo", "weixin", "flag" }, "username = ?",
				new String[] { username }, null, null, null, "1");

		while (cursor.moveToNext()) {
			user.setId(cursor.getInt(0));
			user.setUsername(cursor.getString(1));
			user.setToken(cursor.getString(2));
			user.setMobilephone(cursor.getString(3));
			user.setIdentitycard(cursor.getString(4));
			user.setQq(cursor.getString(5));
			user.setEmail(cursor.getString(6));
			user.setWeibo(cursor.getString(7));
			user.setWeixin(cursor.getString(8));
		}
		cursor.close();

		if (user.getId() != null && user.getId() != 0) {
			return user;
		}
		return null;
	}

	public boolean isDefaultConsignee(Integer consigneeId) {
		if (consigneeId == null) {
			return false;
		}
		boolean is = false;
		Cursor cursor = db.query("consignee", new String[] { "is_default" }, "id = ?",
				new String[] { String.valueOf(consigneeId) }, null, null, null, "1");

		while (cursor.moveToNext()) {
			if (cursor.getString(0) != null && "1".equals(cursor.getString(0))) {
				is = true;
			}
		}
		cursor.close();

		return is;
	}

}

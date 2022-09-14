package com.dummy.code.web.admin.util;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Locale;

import javax.sql.DataSource;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.context.MessageSource;

import com.dummy.code.web.general.dbutil.DStrUtil;

public class AdminMenuUtil {

	public static JSONArray getMainMenuAccess(DataSource dataSource, MessageSource msgSrc, Locale locale,
			BigDecimal userSystemID) {
		JSONArray mainMenuAccessList = new JSONArray();

		Connection connection = null;
		String accessDStr = null;

		try {
			connection = dataSource.getConnection();
			String sqlStmt = "SELECT b.menu_access_dstr FROM admin_user a "
					+ "INNER JOIN role_access b ON a.role_access = b.id WHERE a.id = ?";
			PreparedStatement ps = connection.prepareStatement(sqlStmt);
			ps.setBigDecimal(1, userSystemID);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				accessDStr = rs.getString("menu_access_dstr");
			}
			rs.close();
			ps.close();

			String accessDStrBin = DStrUtil.dStrHexToBin(accessDStr);

			StringBuilder sb = new StringBuilder();
			for (int x = 0; x < accessDStrBin.length(); x++) {
				if (accessDStrBin.charAt(x) == '1') {
					if (sb.length() > 0) {
						sb.append(",");
					}
					sb.append(x + 1);
				}
			}
			String accessIDStr = sb.toString();

			sqlStmt = "SELECT id, locale_name, nav_name, icon_html, redirect_link FROM main_menu "
					+ "WHERE is_enabled AND parent_id IS NULL AND id IN (" + accessIDStr
					+ ") ORDER BY menu_sequence ASC";
			ps = connection.prepareStatement(sqlStmt);
			rs = ps.executeQuery();
			while (rs.next()) {
				JSONObject menuData = new JSONObject();
				menuData.put("menu_name", msgSrc.getMessage(rs.getString("locale_name"), null, locale));
				menuData.put("nav_name", rs.getString("nav_name"));
				menuData.put("icon_html", rs.getString("icon_html"));
				menuData.put("redirect_link",
						rs.getString("redirect_link") == null ? "" : rs.getString("redirect_link"));

				JSONArray subMenuList = new JSONArray();
				String sqlStmt2 = "SELECT id, locale_name, nav_name, icon_html, redirect_link FROM main_menu "
						+ "WHERE is_enabled AND parent_id = ? AND id IN (" + accessIDStr
						+ ") ORDER BY menu_sequence ASC";
				PreparedStatement ps2 = connection.prepareStatement(sqlStmt2);
				ps2.setInt(1, rs.getInt("id"));
				ResultSet rs2 = ps2.executeQuery();
				while (rs2.next()) {
					JSONObject subMenuData = new JSONObject();
					subMenuData.put("menu_name", msgSrc.getMessage(rs2.getString("locale_name"), null, locale));
					subMenuData.put("nav_name", rs2.getString("nav_name"));
					subMenuData.put("icon_html", rs2.getString("icon_html"));
					subMenuData.put("redirect_link",
							rs2.getString("redirect_link") == null ? "" : rs2.getString("redirect_link"));

					subMenuList.put(subMenuData);
				}
				rs2.close();
				ps2.close();

				menuData.put("sub_menu", subMenuList);

				mainMenuAccessList.put(menuData);
			}
			rs.close();
			ps.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (Exception e) {
			}
		}

		return mainMenuAccessList;
	}
}

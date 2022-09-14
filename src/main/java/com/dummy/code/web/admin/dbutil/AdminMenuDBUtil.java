package com.dummy.code.web.admin.dbutil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.json.JSONArray;
import org.json.JSONObject;

public class AdminMenuDBUtil {
	public static JSONArray getMenuList(Connection connection) throws Exception {
		JSONArray mainMenuList = new JSONArray();

		String sqlStmt = "SELECT id, menu_name, locale_name, nav_name, icon_html, redirect_link, is_enabled FROM main_menu "
				+ "WHERE parent_id IS NULL AND NOT is_hidden ORDER BY menu_sequence ASC";
		PreparedStatement ps = connection.prepareStatement(sqlStmt);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			JSONObject menuData = new JSONObject();
			menuData.put("id", rs.getInt("id"));
			menuData.put("menu_name", rs.getString("menu_name"));
			menuData.put("locale_name", rs.getString("locale_name"));
			menuData.put("nav_name", rs.getString("nav_name"));
			menuData.put("icon_html", rs.getString("icon_html"));
			menuData.put("redirect_link", rs.getString("redirect_link"));
			menuData.put("is_enabled", rs.getBoolean("is_enabled"));

			JSONArray subMenuList = new JSONArray();
			String sqlStmt2 = "SELECT id, menu_name, locale_name, nav_name, icon_html, redirect_link, is_enabled FROM main_menu "
					+ "WHERE parent_id = ? AND NOT is_hidden ORDER BY menu_sequence ASC";
			PreparedStatement ps2 = connection.prepareStatement(sqlStmt2);
			ps2.setInt(1, rs.getInt("id"));
			ResultSet rs2 = ps2.executeQuery();
			while (rs2.next()) {
				JSONObject subMenuData = new JSONObject();
				subMenuData.put("id", rs2.getInt("id"));
				subMenuData.put("menu_name", rs2.getString("menu_name"));
				subMenuData.put("locale_name", rs2.getString("locale_name"));
				subMenuData.put("nav_name", rs2.getString("nav_name"));
				subMenuData.put("icon_html", rs2.getString("icon_html"));
				subMenuData.put("redirect_link", rs2.getString("redirect_link"));
				subMenuData.put("is_enabled", rs2.getBoolean("is_enabled"));

				subMenuList.put(subMenuData);
			}
			rs2.close();
			ps2.close();

			menuData.put("sub_menu", subMenuList);

			mainMenuList.put(menuData);
		}
		rs.close();
		ps.close();

		return mainMenuList;
	}

	public static boolean updateMenu(Connection connection, JSONArray menuList) throws Exception {
		boolean isUpdateSuccess = true;

		for (int x = 0; x < menuList.length(); x++) {
			JSONObject menuData = menuList.getJSONObject(x);

			String sqlStmt = null;
			PreparedStatement ps = null;

			if (menuData.has("id")) {
				sqlStmt = "UPDATE main_menu SET menu_name = ?, locale_name = ?, nav_name = ?, icon_html = ?, redirect_link = ?, is_enabled = ?, menu_sequence = ?, update_time = NOW() WHERE id = ?";
				ps = connection.prepareStatement(sqlStmt);
				ps.setString(1, menuData.getString("menu_name"));
				ps.setString(2, menuData.getString("locale_name"));
				ps.setString(3, menuData.getString("nav_name"));
				ps.setString(4,
						menuData.has("icon_html") && !menuData.getString("icon_html").isEmpty()
								? menuData.getString("icon_html")
								: null);
				ps.setString(5,
						menuData.has("redirect_link") && !menuData.getString("redirect_link").isEmpty()
								? menuData.getString("redirect_link")
								: null);
				ps.setBoolean(6, menuData.getBoolean("is_enabled"));
				ps.setInt(7, x);
				ps.setInt(8, menuData.getInt("id"));
				int updatedRow = ps.executeUpdate();
				ps.close();
				isUpdateSuccess = updatedRow >= 1;
			} else {
				sqlStmt = "INSERT INTO main_menu (menu_name,locale_name,nav_name,icon_html,redirect_link,is_enabled,menu_sequence) VALUES (?,?,?,?,?,?,?)";
				ps = connection.prepareStatement(sqlStmt, Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, menuData.getString("menu_name"));
				ps.setString(2, menuData.getString("locale_name"));
				ps.setString(3, menuData.getString("nav_name"));
				ps.setString(4,
						menuData.has("icon_html") && !menuData.getString("icon_html").isEmpty()
								? menuData.getString("icon_html")
								: null);
				ps.setString(5,
						menuData.has("redirect_link") && !menuData.getString("redirect_link").isEmpty()
								? menuData.getString("redirect_link")
								: null);
				ps.setBoolean(6, menuData.getBoolean("is_enabled"));
				ps.setInt(7, x);
				int updatedRow = ps.executeUpdate();
				isUpdateSuccess = updatedRow >= 1;

				if (isUpdateSuccess) {
					ResultSet generatedKeys = ps.getGeneratedKeys();
					if (generatedKeys.next()) {
						menuData.put("id", generatedKeys.getLong(1));
					}
				}
				ps.close();
			}

			if (isUpdateSuccess && menuData.has("sub_menu") && menuData.getJSONArray("sub_menu").length() > 0) {
				JSONArray subMenuList = menuData.getJSONArray("sub_menu");

				for (int y = 0; y < subMenuList.length(); y++) {
					JSONObject subMenuData = subMenuList.getJSONObject(y);

					if (subMenuData.has("id")) {
						sqlStmt = "UPDATE main_menu SET menu_name = ?, locale_name = ?, nav_name = ?, icon_html = ?, redirect_link = ?, is_enabled = ?, menu_sequence = ?, parent_id = ?, update_time = NOW() WHERE id = ?";
						ps = connection.prepareStatement(sqlStmt);
						ps.setString(1, subMenuData.getString("menu_name"));
						ps.setString(2, subMenuData.getString("locale_name"));
						ps.setString(3, subMenuData.getString("nav_name"));
						ps.setString(4,
								subMenuData.has("icon_html") && !subMenuData.getString("icon_html").isEmpty()
										? subMenuData.getString("icon_html")
										: null);
						ps.setString(5,
								subMenuData.has("redirect_link") && !subMenuData.getString("redirect_link").isEmpty()
										? subMenuData.getString("redirect_link")
										: null);
						ps.setBoolean(6, subMenuData.getBoolean("is_enabled"));
						ps.setInt(7, y);
						ps.setInt(8, menuData.getInt("id"));
						ps.setInt(9, subMenuData.getInt("id"));
						int updatedRow = ps.executeUpdate();
						ps.close();
						isUpdateSuccess = updatedRow >= 1;
					} else {
						sqlStmt = "INSERT INTO main_menu (menu_name,locale_name,nav_name,icon_html,redirect_link,is_enabled,menu_sequence,parent_id) VALUES (?,?,?,?,?,?,?,?)";
						ps = connection.prepareStatement(sqlStmt);
						ps.setString(1, subMenuData.getString("menu_name"));
						ps.setString(2, subMenuData.getString("locale_name"));
						ps.setString(3, subMenuData.getString("nav_name"));
						ps.setString(4,
								subMenuData.has("icon_html") && !subMenuData.getString("icon_html").isEmpty()
										? subMenuData.getString("icon_html")
										: null);
						ps.setString(5,
								subMenuData.has("redirect_link") && !subMenuData.getString("redirect_link").isEmpty()
										? subMenuData.getString("redirect_link")
										: null);
						ps.setBoolean(6, subMenuData.getBoolean("is_enabled"));
						ps.setInt(7, y);
						ps.setInt(8, menuData.getInt("id"));
						int updatedRow = ps.executeUpdate();
						ps.close();
						isUpdateSuccess = updatedRow >= 1;
					}
					
					if (!isUpdateSuccess) {
						break;
					}
				}
			}

			if (!isUpdateSuccess) {
				break;
			}
		}

		return isUpdateSuccess;
	}
}

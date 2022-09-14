package com.dummy.code.web.admin.dbutil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONObject;

import com.dummy.code.general.util.DataTableUtil;
import com.dummy.code.web.general.dbutil.DStrUtil;

public class AdminRoleAccessDBUtil {
	private static final String[] DT_CHECK_COLUMNS = { "id", "role_name", "status_name" };
	private static final String[] DT_DB_COLUMNS = { "a.id", "a.role_name", "b.status_name" };

	public static BigDecimal getRoleAccessTotalRecord(Connection connection) throws Exception {
		BigDecimal totalRecords = null;

		String sqlStmt = "SELECT COUNT(*) AS total_records FROM role_access a "
				+ "INNER JOIN slu_role_access_status b ON a.role_status = b.id ";
		PreparedStatement ps = connection.prepareStatement(sqlStmt);
		ResultSet rs = ps.executeQuery();
		if (rs.next()) {
			totalRecords = rs.getBigDecimal("total_records");
		}
		rs.close();
		ps.close();

		return totalRecords;
	}

	public static BigDecimal getRoleAccessTotalFiltered(Connection connection, String searchString,
			JSONArray columnData) throws Exception {
		BigDecimal totalFilteredRecords = null;

		String sqlStmt = "SELECT COUNT(*) AS total_filtered_records FROM role_access a "
				+ "INNER JOIN slu_role_access_status b ON a.role_status = b.id ";
		String extFilterStr = DataTableUtil.generateSQLExtFilterStr(columnData, DT_CHECK_COLUMNS, DT_DB_COLUMNS);
		if (!extFilterStr.isEmpty()) {
			if (sqlStmt.substring(sqlStmt.lastIndexOf("FROM")).contains("WHERE")) {
				sqlStmt += " AND ";
			} else {
				sqlStmt += " WHERE ";
			}
			sqlStmt += extFilterStr;
		}
		if (searchString != null && !searchString.isEmpty()) {
			if (sqlStmt.substring(sqlStmt.lastIndexOf("FROM")).contains("WHERE")) {
				sqlStmt += " AND ";
			} else {
				sqlStmt += " WHERE ";
			}
			sqlStmt += DataTableUtil.generateSQLLikeStr(columnData, DT_CHECK_COLUMNS, DT_DB_COLUMNS);
		}

		PreparedStatement ps = connection.prepareStatement(sqlStmt);
		int psIndex = 1;
		if (!extFilterStr.isEmpty()) {
			psIndex = DataTableUtil.fillSQLExtFilter(columnData, DT_CHECK_COLUMNS, DT_DB_COLUMNS, psIndex, ps);
		}
		if (searchString != null && !searchString.isEmpty()) {
			for (int x = 0; x < DataTableUtil.getSQLLikeCount(columnData, DT_CHECK_COLUMNS, DT_DB_COLUMNS); x++) {
				ps.setString(psIndex++, "%" + searchString + "%");
			}
		}
		ResultSet rs = ps.executeQuery();
		if (rs.next()) {
			totalFilteredRecords = rs.getBigDecimal("total_filtered_records");
		}
		rs.close();
		ps.close();

		return totalFilteredRecords;
	}

	public static JSONArray getRoleAccessList(Connection connection, BigDecimal limit, BigDecimal offset,
			String searchString, JSONArray columnData, JSONArray orderData) throws Exception {
		JSONArray roleAccessList = new JSONArray();

		if (columnData != null && columnData.length() > 0 && orderData != null && orderData.length() > 0) {
			for (int x = 0; x < columnData.length(); x++) {
				if (!columnData.getJSONObject(x).getString("data").isEmpty()
						&& !Arrays.asList(DT_CHECK_COLUMNS).contains(columnData.getJSONObject(x).getString("data"))) {
					throw new Exception("Invalid Column/Order DT Data");
				}
			}
		} else {
			throw new Exception("Missing Column/Order DT Data");
		}

		String sqlStmt = "SELECT a.id, a.role_name, a.page_access_dstr, a.api_access_dstr, a.menu_access_dstr, a.action_access_dstr, "
				+ "a.role_status, b.status_name "
				+ "FROM role_access a INNER JOIN slu_role_access_status b ON a.role_status = b.id ";
		String extFilterStr = DataTableUtil.generateSQLExtFilterStr(columnData, DT_CHECK_COLUMNS, DT_DB_COLUMNS);
		if (!extFilterStr.isEmpty()) {
			if (sqlStmt.substring(sqlStmt.lastIndexOf("FROM")).contains("WHERE")) {
				sqlStmt += " AND ";
			} else {
				sqlStmt += " WHERE ";
			}
			sqlStmt += extFilterStr;
		}
		if (searchString != null && !searchString.isEmpty()) {
			if (sqlStmt.substring(sqlStmt.lastIndexOf("FROM")).contains("WHERE")) {
				sqlStmt += " AND ";
			} else {
				sqlStmt += " WHERE ";
			}
			sqlStmt += DataTableUtil.generateSQLLikeStr(columnData, DT_CHECK_COLUMNS, DT_DB_COLUMNS);
		}
		sqlStmt += " ORDER BY "
				+ DataTableUtil.generateSQLOrderStr(columnData, DT_CHECK_COLUMNS, DT_DB_COLUMNS, orderData)
				+ " LIMIT ? OFFSET ?";
		PreparedStatement ps = connection.prepareStatement(sqlStmt);
		int psIndex = 1;
		if (!extFilterStr.isEmpty()) {
			psIndex = DataTableUtil.fillSQLExtFilter(columnData, DT_CHECK_COLUMNS, DT_DB_COLUMNS, psIndex, ps);
		}
		if (searchString != null && !searchString.isEmpty()) {
			for (int x = 0; x < DataTableUtil.getSQLLikeCount(columnData, DT_CHECK_COLUMNS, DT_DB_COLUMNS); x++) {
				ps.setString(psIndex++, "%" + searchString + "%");
			}
		}
		ps.setBigDecimal(psIndex++, limit);
		ps.setBigDecimal(psIndex++, offset);

		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			JSONObject roleAccessObj = new JSONObject();
			roleAccessObj.put("id", rs.getBigDecimal("id"));
			roleAccessObj.put("role_name", rs.getString("role_name"));
			roleAccessObj.put("page_access",
					getPageAccessByRole(connection, rs.getBigDecimal("id"), rs.getString("page_access_dstr")));
			roleAccessObj.put("api_access",
					getAPIAccessByRole(connection, rs.getBigDecimal("id"), rs.getString("api_access_dstr")));
			roleAccessObj.put("menu_access",
					getMenuAccessByRole(connection, rs.getBigDecimal("id"), rs.getString("menu_access_dstr")));
			roleAccessObj.put("action_access",
					getActionAccessByRole(connection, rs.getBigDecimal("id"), rs.getString("action_access_dstr")));
			roleAccessObj.put("role_status", rs.getBigDecimal("role_status"));
			roleAccessObj.put("status_name", rs.getString("status_name"));

			roleAccessList.put(roleAccessObj);
		}
		rs.close();
		ps.close();

		return roleAccessList;
	}

	public static JSONArray getPageAccessByRole(Connection connection, BigDecimal roleID, String pageAccessDStr)
			throws Exception {
		JSONArray pageAccessByRole = new JSONArray();

		String sqlStmt = "SELECT id, page_name, page_desc, is_hidden FROM page_access ORDER BY id ASC";
		PreparedStatement ps = connection.prepareStatement(sqlStmt);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			JSONObject pageAccessObj = new JSONObject();
			pageAccessObj.put("id", rs.getBigDecimal("id"));
			pageAccessObj.put("page_name", rs.getString("page_name"));
			pageAccessObj.put("page_desc", rs.getString("page_desc"));
			pageAccessObj.put("is_hidden", rs.getBoolean("is_hidden"));
			if (roleID.equals(new BigDecimal(-1))) {
				pageAccessObj.put("flag", false);
			} else {
				pageAccessObj.put("flag", DStrUtil.getDStrValue(pageAccessDStr, rs.getInt("id") - 1));
			}

			pageAccessByRole.put(pageAccessObj);
		}
		rs.close();
		ps.close();

		return pageAccessByRole;
	}

	public static JSONArray getAPIAccessByRole(Connection connection, BigDecimal roleID, String apiAccessDStr)
			throws Exception {
		JSONArray apiAccessByRole = new JSONArray();

		String sqlStmt = "SELECT id, api_name, api_desc, is_hidden FROM api_access ORDER BY id ASC";
		PreparedStatement ps = connection.prepareStatement(sqlStmt);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			JSONObject apiAccessObj = new JSONObject();
			apiAccessObj.put("id", rs.getBigDecimal("id"));
			apiAccessObj.put("api_name", rs.getString("api_name"));
			apiAccessObj.put("api_desc", rs.getString("api_desc"));
			apiAccessObj.put("is_hidden", rs.getBoolean("is_hidden"));
			if (roleID.equals(new BigDecimal(-1))) {
				apiAccessObj.put("flag", false);
			} else {
				apiAccessObj.put("flag", DStrUtil.getDStrValue(apiAccessDStr, rs.getInt("id") - 1));
			}

			apiAccessByRole.put(apiAccessObj);
		}
		rs.close();
		ps.close();

		return apiAccessByRole;
	}

	public static JSONArray getMenuAccessByRole(Connection connection, BigDecimal roleID, String menuAccessDStr)
			throws Exception {
		JSONArray menuAccessByRole = new JSONArray();

		String sqlStmt = "SELECT id, menu_name, icon_html, is_hidden FROM main_menu "
				+ "WHERE parent_id IS NULL ORDER BY menu_sequence ASC";
		PreparedStatement ps = connection.prepareStatement(sqlStmt);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			JSONObject menuObj = new JSONObject();
			menuObj.put("id", rs.getInt("id"));
			menuObj.put("menu_name", rs.getString("menu_name"));
			menuObj.put("icon_html", rs.getString("icon_html"));
			menuObj.put("is_hidden", rs.getBoolean("is_hidden"));
			if (roleID.equals(new BigDecimal(-1))) {
				menuObj.put("flag", false);
			} else {
				menuObj.put("flag", DStrUtil.getDStrValue(menuAccessDStr, rs.getInt("id") - 1));
			}

			JSONArray subMenuList = new JSONArray();
			String sqlStmt2 = "SELECT id, menu_name, icon_html FROM main_menu "
					+ "WHERE parent_id = ? ORDER BY menu_sequence ASC";
			PreparedStatement ps2 = connection.prepareStatement(sqlStmt2);
			ps2.setInt(1, rs.getInt("id"));
			ResultSet rs2 = ps2.executeQuery();
			while (rs2.next()) {
				JSONObject subMenuObj = new JSONObject();
				subMenuObj.put("id", rs2.getInt("id"));
				subMenuObj.put("menu_name", rs2.getString("menu_name"));
				subMenuObj.put("icon_html", rs2.getString("icon_html"));
				subMenuObj.put("is_hidden", rs.getBoolean("is_hidden"));
				if (roleID.equals(new BigDecimal(-1))) {
					subMenuObj.put("flag", false);
				} else {
					subMenuObj.put("flag", DStrUtil.getDStrValue(menuAccessDStr, rs2.getInt("id") - 1));
				}

				subMenuList.put(subMenuObj);
			}
			rs2.close();
			ps2.close();

			menuObj.put("sub_menu", subMenuList);

			menuAccessByRole.put(menuObj);
		}
		rs.close();
		ps.close();

		return menuAccessByRole;
	}

	public static JSONArray getActionAccessByRole(Connection connection, BigDecimal roleID, String actionAccessDStr)
			throws Exception {
		JSONArray actionAccessByRole = new JSONArray();

		String sqlStmt = "SELECT id, action_name, is_hidden FROM action_access ORDER BY id ASC";
		PreparedStatement ps = connection.prepareStatement(sqlStmt);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			JSONObject actionAccessObj = new JSONObject();
			actionAccessObj.put("id", rs.getBigDecimal("id"));
			actionAccessObj.put("action_name", rs.getString("action_name"));
			actionAccessObj.put("is_hidden", rs.getBoolean("is_hidden"));
			if (roleID.equals(new BigDecimal(-1))) {
				actionAccessObj.put("flag", false);
			} else {
				actionAccessObj.put("flag", DStrUtil.getDStrValue(actionAccessDStr, rs.getInt("id") - 1));
			}

			actionAccessByRole.put(actionAccessObj);
		}
		rs.close();
		ps.close();

		return actionAccessByRole;
	}

	public static JSONObject getRoleAccessDT(Connection connection, BigDecimal pageIndex, BigDecimal limit,
			BigDecimal offset, String searchString, JSONArray columnData, JSONArray orderData) throws Exception {
		searchString = searchString.toLowerCase();

		JSONObject roleAccessDT = new JSONObject();
		roleAccessDT.put("draw", pageIndex);
		roleAccessDT.put("recordsTotal", getRoleAccessTotalRecord(connection));
		roleAccessDT.put("recordsFiltered", getRoleAccessTotalFiltered(connection, searchString, columnData));
		roleAccessDT.put("data", getRoleAccessList(connection, limit, offset, searchString, columnData, orderData));

		return roleAccessDT;
	}

	public static JSONArray getRoleStatusList(Connection connection) throws Exception {
		JSONArray roleStatusList = new JSONArray();

		String sqlStmt = "SELECT id, status_name FROM slu_role_access_status ORDER BY id ASC";
		PreparedStatement ps = connection.prepareStatement(sqlStmt);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			JSONObject roleStatusObj = new JSONObject();
			roleStatusObj.put("id", rs.getBigDecimal("id"));
			roleStatusObj.put("status_name", rs.getString("status_name"));

			roleStatusList.put(roleStatusObj);
		}
		rs.close();
		ps.close();

		return roleStatusList;
	}

	public static boolean createRole(Connection connection, String roleName, JSONArray pageAccess, JSONArray apiAccess,
			JSONArray menuAccess, JSONArray actionAccess, BigDecimal roleStatusID) throws Exception {
		String sqlStmt = "SELECT MAX(id) AS max_id FROM page_access";
		PreparedStatement ps = connection.prepareStatement(sqlStmt);
		ResultSet rs = ps.executeQuery();
		rs.next();
		String[] pageAccessDStr = new String[rs.getInt("max_id")];
		ps.close();
		rs.close();
		for (int x = 0; x < pageAccessDStr.length; x++) {
			pageAccessDStr[x] = "0";
		}

		sqlStmt = "SELECT MAX(id) AS max_id FROM api_access";
		ps = connection.prepareStatement(sqlStmt);
		rs = ps.executeQuery();
		rs.next();
		String[] apiAccessDStr = new String[rs.getInt("max_id")];
		ps.close();
		rs.close();
		for (int x = 0; x < apiAccessDStr.length; x++) {
			apiAccessDStr[x] = "0";
		}

		sqlStmt = "SELECT MAX(id) AS max_id FROM main_menu";
		ps = connection.prepareStatement(sqlStmt);
		rs = ps.executeQuery();
		rs.next();
		String[] menuAccessDStr = new String[rs.getInt("max_id")];
		ps.close();
		rs.close();
		for (int x = 0; x < menuAccessDStr.length; x++) {
			menuAccessDStr[x] = "0";
		}

		sqlStmt = "SELECT MAX(id) AS max_id FROM action_access";
		ps = connection.prepareStatement(sqlStmt);
		rs = ps.executeQuery();
		rs.next();
		String[] actionAccessDStr = new String[rs.getInt("max_id")];
		ps.close();
		rs.close();
		for (int x = 0; x < actionAccessDStr.length; x++) {
			actionAccessDStr[x] = "0";
		}

		for (int x = 0; x < pageAccess.length(); x++) {
			JSONObject pageAccessObj = pageAccess.getJSONObject(x);
			pageAccessDStr[pageAccessObj.getInt("id") - 1] = pageAccessObj.getBoolean("flag") ? "1" : "0";
		}

		for (int x = 0; x < apiAccess.length(); x++) {
			JSONObject apiAccessObj = apiAccess.getJSONObject(x);
			apiAccessDStr[apiAccessObj.getInt("id") - 1] = apiAccessObj.getBoolean("flag") ? "1" : "0";
		}

		for (int x = 0; x < menuAccess.length(); x++) {
			JSONObject menuAccessObj = menuAccess.getJSONObject(x);
			menuAccessDStr[menuAccessObj.getInt("id") - 1] = menuAccessObj.getBoolean("flag") ? "1" : "0";
		}

		for (int x = 0; x < actionAccess.length(); x++) {
			JSONObject actionAccessObj = actionAccess.getJSONObject(x);
			actionAccessDStr[actionAccessObj.getInt("id") - 1] = actionAccessObj.getBoolean("flag") ? "1" : "0";
		}

		sqlStmt = "INSERT INTO role_access (role_name,page_access_dstr,api_access_dstr,menu_access_dstr,action_access_dstr,role_status) VALUES (?,?,?,?,?,?)";
		ps = connection.prepareStatement(sqlStmt);
		int psIndex = 1;
		ps.setString(psIndex++, roleName);
		ps.setString(psIndex++, DStrUtil.dStrBinToHex(String.join("", pageAccessDStr)));
		ps.setString(psIndex++, DStrUtil.dStrBinToHex(String.join("", apiAccessDStr)));
		ps.setString(psIndex++, DStrUtil.dStrBinToHex(String.join("", menuAccessDStr)));
		ps.setString(psIndex++, DStrUtil.dStrBinToHex(String.join("", actionAccessDStr)));
		ps.setBigDecimal(psIndex++, roleStatusID);
		int updatedRow = ps.executeUpdate();
		ps.close();

		return updatedRow >= 1;
	}

	public static boolean updateRole(Connection connection, BigDecimal roleID, String roleName, JSONArray pageAccess,
			JSONArray apiAccess, JSONArray menuAccess, JSONArray actionAccess, BigDecimal roleStatusID)
			throws Exception {
		String sqlStmt = "SELECT MAX(id) AS max_id FROM page_access";
		PreparedStatement ps = connection.prepareStatement(sqlStmt);
		ResultSet rs = ps.executeQuery();
		rs.next();
		String[] pageAccessDStr = new String[rs.getInt("max_id")];
		ps.close();
		rs.close();
		for (int x = 0; x < pageAccessDStr.length; x++) {
			pageAccessDStr[x] = "0";
		}

		sqlStmt = "SELECT MAX(id) AS max_id FROM api_access";
		ps = connection.prepareStatement(sqlStmt);
		rs = ps.executeQuery();
		rs.next();
		String[] apiAccessDStr = new String[rs.getInt("max_id")];
		ps.close();
		rs.close();
		for (int x = 0; x < apiAccessDStr.length; x++) {
			apiAccessDStr[x] = "0";
		}

		sqlStmt = "SELECT MAX(id) AS max_id FROM main_menu";
		ps = connection.prepareStatement(sqlStmt);
		rs = ps.executeQuery();
		rs.next();
		String[] menuAccessDStr = new String[rs.getInt("max_id")];
		ps.close();
		rs.close();
		for (int x = 0; x < menuAccessDStr.length; x++) {
			menuAccessDStr[x] = "0";
		}

		sqlStmt = "SELECT MAX(id) AS max_id FROM action_access";
		ps = connection.prepareStatement(sqlStmt);
		rs = ps.executeQuery();
		rs.next();
		String[] actionAccessDStr = new String[rs.getInt("max_id")];
		ps.close();
		rs.close();
		for (int x = 0; x < actionAccessDStr.length; x++) {
			actionAccessDStr[x] = "0";
		}

		for (int x = 0; x < pageAccess.length(); x++) {
			JSONObject pageAccessObj = pageAccess.getJSONObject(x);
			pageAccessDStr[pageAccessObj.getInt("id") - 1] = pageAccessObj.getBoolean("flag") ? "1" : "0";
		}

		for (int x = 0; x < apiAccess.length(); x++) {
			JSONObject apiAccessObj = apiAccess.getJSONObject(x);
			apiAccessDStr[apiAccessObj.getInt("id") - 1] = apiAccessObj.getBoolean("flag") ? "1" : "0";
		}

		for (int x = 0; x < menuAccess.length(); x++) {
			JSONObject mainMenuAccessObj = menuAccess.getJSONObject(x);
			menuAccessDStr[mainMenuAccessObj.getInt("id") - 1] = mainMenuAccessObj.getBoolean("flag") ? "1" : "0";
			if (mainMenuAccessObj.has("sub_menu") && mainMenuAccessObj.getJSONArray("sub_menu").length() > 0) {
				JSONArray subMenuListObj = mainMenuAccessObj.getJSONArray("sub_menu");
				for (int y = 0; y < subMenuListObj.length(); y++) {
					JSONObject subMenuAccessObj = subMenuListObj.getJSONObject(y);
					menuAccessDStr[subMenuAccessObj.getInt("id") - 1] = subMenuAccessObj.getBoolean("flag") ? "1" : "0";
				}
			}
		}

		for (int x = 0; x < actionAccess.length(); x++) {
			JSONObject actionAccessObj = actionAccess.getJSONObject(x);
			actionAccessDStr[actionAccessObj.getInt("id") - 1] = actionAccessObj.getBoolean("flag") ? "1" : "0";
		}

		sqlStmt = "UPDATE role_access SET role_name = ?, page_access_dstr = ?, api_access_dstr = ?, menu_access_dstr = ?, action_access_dstr = ?, role_status = ?, update_time = NOW() WHERE id = ?";
		ps = connection.prepareStatement(sqlStmt);
		int psIndex = 1;
		ps.setString(psIndex++, roleName);
		ps.setString(psIndex++, DStrUtil.dStrBinToHex(String.join("", pageAccessDStr)));
		ps.setString(psIndex++, DStrUtil.dStrBinToHex(String.join("", apiAccessDStr)));
		ps.setString(psIndex++, DStrUtil.dStrBinToHex(String.join("", menuAccessDStr)));
		ps.setString(psIndex++, DStrUtil.dStrBinToHex(String.join("", actionAccessDStr)));
		ps.setBigDecimal(psIndex++, roleStatusID);
		ps.setBigDecimal(psIndex++, roleID);
		int updatedRow = ps.executeUpdate();
		ps.close();

		return updatedRow >= 1;
	}
}

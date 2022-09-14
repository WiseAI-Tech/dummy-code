package com.dummy.code.web.admin.dbutil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.dummy.code.general.util.DataTableUtil;
import com.dummy.code.general.util.ExportDataListUtil;

public class AdminUserDBUtil {
	private static final String[] DT_CHECK_COLUMNS = { "id", "login_id", "full_name", "display_name", "email",
			"failed_login_attempt", "last_login_time", "role_name", "status_name" };
	private static final String[] DT_DB_COLUMNS = { "a.id", "a.login_id", "a.full_name", "a.display_name", "a.email",
			"a.failed_login_attempt", "a.last_login_time", "b.role_name", "c.status_name" };

	public static BigDecimal getUserTotalRecord(Connection connection) throws Exception {
		BigDecimal totalRecords = null;

		String sqlStmt = "SELECT COUNT(*) AS total_records FROM admin_user a "
				+ "INNER JOIN role_access b ON a.role_access = b.id "
				+ "INNER JOIN slu_admin_user_status c ON a.user_status = c.id ";

		PreparedStatement ps = connection.prepareStatement(sqlStmt);
		ResultSet rs = ps.executeQuery();
		if (rs.next()) {
			totalRecords = rs.getBigDecimal("total_records");
		}
		rs.close();
		ps.close();

		return totalRecords;
	}

	public static BigDecimal getUserTotalFiltered(Connection connection, String searchString, JSONArray columnData)
			throws Exception {
		BigDecimal totalFilteredRecords = null;

		String sqlStmt = "SELECT COUNT(*) AS total_filtered_records FROM admin_user a "
				+ "INNER JOIN role_access b ON a.role_access = b.id "
				+ "INNER JOIN slu_admin_user_status c ON a.user_status = c.id ";
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

	public static JSONArray getDataList(Connection connection, BigDecimal limit, BigDecimal offset, String searchString,
			JSONArray columnData, JSONArray orderData) throws Exception {
		JSONArray dataList = new JSONArray();

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

		String sqlStmt = "SELECT a.id, a.login_id, a.full_name, a.display_name, a.email, a.failed_login_attempt, "
				+ "a.last_login_time, a.role_access, a.user_status, b.role_name, c.status_name FROM admin_user a "
				+ "INNER JOIN role_access b ON a.role_access = b.id "
				+ "INNER JOIN slu_admin_user_status c ON a.user_status = c.id ";
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
			JSONObject dataObj = new JSONObject();
			dataObj.put("id", rs.getBigDecimal("id"));
			dataObj.put("login_id", rs.getString("login_id"));
			dataObj.put("full_name", rs.getString("full_name"));
			dataObj.put("display_name", rs.getString("display_name"));
			dataObj.put("email", rs.getString("email"));
			dataObj.put("last_login_time",
					rs.getTimestamp("last_login_time") != null
							? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(rs.getTimestamp("last_login_time"))
							: null);
			dataObj.put("failed_login_attempt", rs.getInt("failed_login_attempt"));
			dataObj.put("role_id", rs.getBigDecimal("role_access"));
			dataObj.put("role_name", rs.getString("role_name"));
			dataObj.put("status_id", rs.getBigDecimal("user_status"));
			dataObj.put("status_name", rs.getString("status_name"));

			dataList.put(dataObj);
		}
		rs.close();
		ps.close();

		return dataList;
	}

	public static JSONObject getUserDT(Connection connection, BigDecimal pageIndex, BigDecimal limit, BigDecimal offset,
			String searchString, JSONArray columnData, JSONArray orderData) throws Exception {
		searchString = searchString.toLowerCase();

		JSONObject userDT = new JSONObject();
		userDT.put("draw", pageIndex);
		userDT.put("recordsTotal", getUserTotalRecord(connection));
		userDT.put("recordsFiltered", getUserTotalFiltered(connection, searchString, columnData));
		userDT.put("data", getDataList(connection, limit, offset, searchString, columnData, orderData));

		return userDT;
	}

	public static byte[] doExportDT(Connection connection, String searchString, JSONArray columnData,
			JSONArray orderData, String exportType, int filterRoleID, BigDecimal filterLoginID) throws Exception {
		JSONArray dataList = getDataList(connection, null, BigDecimal.ZERO, searchString, columnData, orderData);
		int totalColumnData = 0;
		for (int x = 0; x < columnData.length(); x++) {
			JSONObject curColumnData = columnData.getJSONObject(x);
			if (curColumnData.optBoolean("isExportVisible", false)) {
				totalColumnData++;
			}
		}
		String[] headerNameList = new String[totalColumnData];
		String[] headerMDataList = new String[totalColumnData];
		int[] displayColumnLengthDataList = new int[totalColumnData];
		int columnIndex = 0;
		for (int x = 0; x < columnData.length(); x++) {
			JSONObject curColumnData = columnData.getJSONObject(x);
			if (!curColumnData.optBoolean("isExportVisible", false)) {
				continue;
			}
			headerNameList[columnIndex] = curColumnData.getString("name");
			headerMDataList[columnIndex] = curColumnData.getString("data");
			displayColumnLengthDataList[columnIndex] = 30;
			columnIndex++;
		}

		Map<String, String> extraData = new LinkedHashMap<String, String>();
		extraData.put("Report Name", "Corporate User");
		byte[] byteData = ExportDataListUtil.generateDataFile(exportType, headerNameList, headerMDataList,
				displayColumnLengthDataList, dataList, extraData, null);

		return byteData;
	}

	public static BigDecimal getSuperGroupUserTotalRecord(Connection connection, BigDecimal filterSuperGroupID)
			throws Exception {
		BigDecimal totalRecords = null;

		String sqlStmt = "SELECT COUNT(*) AS total_records FROM admin_user a "
				+ "INNER JOIN role_access b ON a.role_access = b.id "
				+ "INNER JOIN slu_admin_user_status c ON a.user_status = c.id "
				+ "LEFT JOIN super_group d ON a.super_group_id = d.id " + "LEFT JOIN store e ON a.store_id = e.id "
				+ "WHERE a.super_group_id = ?";

		PreparedStatement ps = connection.prepareStatement(sqlStmt);
		int psIndex = 1;
		ps.setBigDecimal(psIndex++, filterSuperGroupID);
		ResultSet rs = ps.executeQuery();
		if (rs.next()) {
			totalRecords = rs.getBigDecimal("total_records");
		}
		rs.close();
		ps.close();

		return totalRecords;
	}

	public static BigDecimal getSuperGroupUserTotalFiltered(Connection connection, String searchString,
			JSONArray columnData, BigDecimal filterSuperGroupID) throws Exception {
		BigDecimal totalFilteredRecords = null;

		String sqlStmt = "SELECT COUNT(*) AS total_filtered_records FROM admin_user a "
				+ "INNER JOIN role_access b ON a.role_access = b.id "
				+ "INNER JOIN slu_admin_user_status c ON a.user_status = c.id "
				+ "LEFT JOIN super_group d ON a.super_group_id = d.id " + "LEFT JOIN store e ON a.store_id = e.id "
				+ "WHERE a.super_group_id = ?";
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
		ps.setBigDecimal(psIndex++, filterSuperGroupID);
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

	public static JSONArray getSuperGroupUserList(Connection connection, BigDecimal limit, BigDecimal offset,
			String searchString, JSONArray columnData, JSONArray orderData, BigDecimal filterSuperGroupID)
			throws Exception {
		JSONArray userList = new JSONArray();

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

		String sqlStmt = "SELECT a.id, a.login_id, a.full_name, a.display_name, a.email, a.failed_login_attempt, "
				+ "a.last_login_time, a.role_access, a.user_status, a.super_group_id, a.store_id, "
				+ "b.role_name, c.status_name, e.store_name FROM admin_user a "
				+ "INNER JOIN role_access b ON a.role_access = b.id "
				+ "INNER JOIN slu_admin_user_status c ON a.user_status = c.id "
				+ "LEFT JOIN super_group d ON a.super_group_id = d.id " + "LEFT JOIN store e ON a.store_id = e.id "
				+ "WHERE a.super_group_id = ?";
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
		ps.setBigDecimal(psIndex++, filterSuperGroupID);
		if (searchString != null && !searchString.isEmpty()) {
			for (int x = 0; x < DataTableUtil.getSQLLikeCount(columnData, DT_CHECK_COLUMNS, DT_DB_COLUMNS); x++) {
				ps.setString(psIndex++, "%" + searchString + "%");
			}
		}
		ps.setBigDecimal(psIndex++, limit);
		ps.setBigDecimal(psIndex++, offset);

		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			JSONObject userObj = new JSONObject();
			userObj.put("id", rs.getBigDecimal("id"));
			userObj.put("login_id", rs.getString("login_id"));
			userObj.put("full_name", rs.getString("full_name"));
			userObj.put("display_name", rs.getString("display_name"));
			userObj.put("email", rs.getString("email"));
			userObj.put("last_login_time",
					rs.getTimestamp("last_login_time") != null
							? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(rs.getTimestamp("last_login_time"))
							: null);
			userObj.put("failed_login_attempt", rs.getInt("failed_login_attempt"));
			userObj.put("role_id", rs.getBigDecimal("role_access"));
			userObj.put("role_name", rs.getString("role_name"));
			userObj.put("status_id", rs.getBigDecimal("user_status"));
			userObj.put("status_name", rs.getString("status_name"));
			userObj.put("super_group_id", rs.getBigDecimal("super_group_id"));
			userObj.put("store_id", rs.getBigDecimal("store_id"));
			userObj.put("store_name", rs.getString("store_name"));

			userList.put(userObj);
		}
		rs.close();
		ps.close();

		return userList;
	}

	public static JSONObject getSuperGroupUserDT(Connection connection, BigDecimal pageIndex, BigDecimal limit,
			BigDecimal offset, String searchString, JSONArray columnData, JSONArray orderData, BigDecimal filterStoreID)
			throws Exception {
		searchString = searchString.toLowerCase();

		JSONObject userDT = new JSONObject();
		userDT.put("draw", pageIndex);
		userDT.put("recordsTotal", getSuperGroupUserTotalRecord(connection, filterStoreID));
		userDT.put("recordsFiltered",
				getSuperGroupUserTotalFiltered(connection, searchString, columnData, filterStoreID));
		userDT.put("data",
				getSuperGroupUserList(connection, limit, offset, searchString, columnData, orderData, filterStoreID));

		return userDT;
	}

	public static BigDecimal getStoreUserTotalRecord(Connection connection, BigDecimal filterStoreID) throws Exception {
		BigDecimal totalRecords = null;

		String sqlStmt = "SELECT COUNT(*) AS total_records FROM admin_user a "
				+ "INNER JOIN role_access b ON a.role_access = b.id "
				+ "INNER JOIN slu_admin_user_status c ON a.user_status = c.id "
				+ "LEFT JOIN super_group d ON a.super_group_id = d.id " + "LEFT JOIN store e ON a.store_id = e.id "
				+ "WHERE a.store_id = ?";

		PreparedStatement ps = connection.prepareStatement(sqlStmt);
		int psIndex = 1;
		ps.setBigDecimal(psIndex++, filterStoreID);
		ResultSet rs = ps.executeQuery();
		if (rs.next()) {
			totalRecords = rs.getBigDecimal("total_records");
		}
		rs.close();
		ps.close();

		return totalRecords;
	}

	public static BigDecimal getStoreUserTotalFiltered(Connection connection, String searchString, JSONArray columnData,
			BigDecimal filterStoreID) throws Exception {
		BigDecimal totalFilteredRecords = null;

		String sqlStmt = "SELECT COUNT(*) AS total_filtered_records FROM admin_user a "
				+ "INNER JOIN role_access b ON a.role_access = b.id "
				+ "INNER JOIN slu_admin_user_status c ON a.user_status = c.id "
				+ "LEFT JOIN super_group d ON a.super_group_id = d.id " + "LEFT JOIN store e ON a.store_id = e.id "
				+ "WHERE a.store_id = ?";
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
		ps.setBigDecimal(psIndex++, filterStoreID);
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

	public static JSONArray getStoreUserList(Connection connection, BigDecimal limit, BigDecimal offset,
			String searchString, JSONArray columnData, JSONArray orderData, BigDecimal filterStoreID) throws Exception {
		JSONArray userList = new JSONArray();

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

		String sqlStmt = "SELECT a.id, a.login_id, a.full_name, a.display_name, a.email, a.failed_login_attempt, "
				+ "a.last_login_time, a.role_access, a.user_status, a.super_group_id, a.store_id, "
				+ "b.role_name, c.status_name, e.store_name FROM admin_user a "
				+ "INNER JOIN role_access b ON a.role_access = b.id "
				+ "INNER JOIN slu_admin_user_status c ON a.user_status = c.id "
				+ "LEFT JOIN super_group d ON a.super_group_id = d.id " + "LEFT JOIN store e ON a.store_id = e.id "
				+ "WHERE a.store_id = ?";
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
		ps.setBigDecimal(psIndex++, filterStoreID);
		if (searchString != null && !searchString.isEmpty()) {
			for (int x = 0; x < DataTableUtil.getSQLLikeCount(columnData, DT_CHECK_COLUMNS, DT_DB_COLUMNS); x++) {
				ps.setString(psIndex++, "%" + searchString + "%");
			}
		}
		ps.setBigDecimal(psIndex++, limit);
		ps.setBigDecimal(psIndex++, offset);

		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			JSONObject userObj = new JSONObject();
			userObj.put("id", rs.getBigDecimal("id"));
			userObj.put("login_id", rs.getString("login_id"));
			userObj.put("full_name", rs.getString("full_name"));
			userObj.put("display_name", rs.getString("display_name"));
			userObj.put("email", rs.getString("email"));
			userObj.put("last_login_time",
					rs.getTimestamp("last_login_time") != null
							? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(rs.getTimestamp("last_login_time"))
							: null);
			userObj.put("failed_login_attempt", rs.getInt("failed_login_attempt"));
			userObj.put("role_id", rs.getBigDecimal("role_access"));
			userObj.put("role_name", rs.getString("role_name"));
			userObj.put("status_id", rs.getBigDecimal("user_status"));
			userObj.put("status_name", rs.getString("status_name"));
			userObj.put("super_group_id", rs.getBigDecimal("super_group_id"));
			userObj.put("store_id", rs.getBigDecimal("store_id"));
			userObj.put("store_name", rs.getString("store_name"));

			userList.put(userObj);
		}
		rs.close();
		ps.close();

		return userList;
	}

	public static JSONObject getStoreUserDT(Connection connection, BigDecimal pageIndex, BigDecimal limit,
			BigDecimal offset, String searchString, JSONArray columnData, JSONArray orderData, BigDecimal filterStoreID)
			throws Exception {
		searchString = searchString.toLowerCase();

		JSONObject userDT = new JSONObject();
		userDT.put("draw", pageIndex);
		userDT.put("recordsTotal", getStoreUserTotalRecord(connection, filterStoreID));
		userDT.put("recordsFiltered", getStoreUserTotalFiltered(connection, searchString, columnData, filterStoreID));
		userDT.put("data",
				getStoreUserList(connection, limit, offset, searchString, columnData, orderData, filterStoreID));

		return userDT;
	}

	public static JSONArray getUserRoleList(Connection connection) throws Exception {
		JSONArray userRoleList = new JSONArray();

		String sqlStmt = "SELECT id, role_name FROM role_access ORDER BY id ASC";
		PreparedStatement ps = connection.prepareStatement(sqlStmt);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			JSONObject userRoleObj = new JSONObject();
			userRoleObj.put("id", rs.getBigDecimal("id"));
			userRoleObj.put("role_name", rs.getString("role_name"));

			userRoleList.put(userRoleObj);
		}
		rs.close();
		ps.close();

		return userRoleList;
	}

	public static JSONArray getUserStatusList(Connection connection) throws Exception {
		JSONArray userStatusList = new JSONArray();

		String sqlStmt = "SELECT id, status_name FROM slu_admin_user_status ORDER BY id ASC";
		PreparedStatement ps = connection.prepareStatement(sqlStmt);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			JSONObject userStatusObj = new JSONObject();
			userStatusObj.put("id", rs.getBigDecimal("id"));
			userStatusObj.put("status_name", rs.getString("status_name"));

			userStatusList.put(userStatusObj);
		}
		rs.close();
		ps.close();

		return userStatusList;
	}

	public static boolean isLoginIDExist(Connection connection, String loginID) throws Exception {
		boolean isExist = false;

		String sqlStmt = "SELECT COUNT(*) AS total_count FROM admin_user WHERE login_id = ?";
		PreparedStatement ps = connection.prepareStatement(sqlStmt);
		ps.setString(1, loginID);
		ResultSet rs = ps.executeQuery();
		if (rs.next()) {
			if (rs.getInt("total_count") > 0) {
				isExist = true;
			}
		}
		ps.close();

		return isExist;
	}

	public static boolean isEmailExist(Connection connection, String email, BigDecimal userID) throws Exception {
		boolean isExist = false;

		String sqlStmt = "SELECT COUNT(*) AS total_count FROM admin_user WHERE email = ?";
		if (userID != null) {
			sqlStmt += " AND id <> ?";
		}
		PreparedStatement ps = connection.prepareStatement(sqlStmt);
		ps.setString(1, email);
		if (userID != null) {
			ps.setBigDecimal(2, userID);
		}
		ResultSet rs = ps.executeQuery();
		if (rs.next()) {
			if (rs.getInt("total_count") > 0) {
				isExist = true;
			}
		}
		ps.close();

		return isExist;
	}

	public static boolean createUser(Connection connection, String loginID, String loginPassword, String fullName,
			String displayName, String email, BigDecimal roleAccessID, BigDecimal userStatusID) throws Exception {
		String sqlStmt = "INSERT INTO admin_user (login_id,login_password,full_name,display_name,email,role_access,user_status) VALUES (?,?,?,?,?,?,?)";
		PreparedStatement ps = connection.prepareStatement(sqlStmt);
		ps.setString(1, loginID);
		ps.setString(2, loginPassword);
		ps.setString(3, fullName);
		ps.setString(4, displayName);
		ps.setString(5, email);
		ps.setBigDecimal(6, roleAccessID);
		ps.setBigDecimal(7, userStatusID);
		int updatedRow = ps.executeUpdate();
		ps.close();

		return updatedRow >= 1;
	}

	public static boolean updateUser(Connection connection, BigDecimal userID, String fullName, String displayName,
			String email, BigDecimal roleAccessID, BigDecimal userStatusID) throws Exception {
		String sqlStmt = "UPDATE admin_user SET full_name = ?, display_name = ?, email = ?, role_access = ?, user_status = ?, update_time = NOW() WHERE id = ?";
		PreparedStatement ps = connection.prepareStatement(sqlStmt);
		ps.setString(1, fullName);
		ps.setString(2, displayName);
		ps.setString(3, email);
		ps.setBigDecimal(4, roleAccessID);
		ps.setBigDecimal(5, userStatusID);
		ps.setBigDecimal(6, userID);
		int updatedRow = ps.executeUpdate();
		ps.close();

		return updatedRow >= 1;
	}

	public static boolean resetPassword(Connection connection, BigDecimal userID, String loginPassword)
			throws Exception {
		String sqlStmt = "UPDATE admin_user SET login_password = ?, update_time = NOW() WHERE id = ?";
		PreparedStatement ps = connection.prepareStatement(sqlStmt);
		ps.setString(1, loginPassword);
		ps.setBigDecimal(2, userID);
		int updatedRow = ps.executeUpdate();
		ps.close();

		return updatedRow >= 1;
	}
}

package com.dummy.code.web.admin.dbutil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONObject;

import com.dummy.code.general.util.DataTableUtil;

public class AdminAPIAccessDBUtil {
	private static final String[] DT_CHECK_COLUMNS = { "id", "api_name", "api_desc", "status_name" };
	private static final String[] DT_DB_COLUMNS = { "a.id", "a.api_name", "a.api_desc", "b.status_name" };

	public static BigDecimal getAPIAccessTotalRecord(Connection connection) throws Exception {
		BigDecimal totalRecords = null;

		String sqlStmt = "SELECT COUNT(*) AS total_records FROM api_access a "
				+ "INNER JOIN slu_api_access_status b ON a.api_status = b.id WHERE NOT a.is_hidden";
		PreparedStatement ps = connection.prepareStatement(sqlStmt);
		ResultSet rs = ps.executeQuery();
		if (rs.next()) {
			totalRecords = rs.getBigDecimal("total_records");
		}
		rs.close();
		ps.close();

		return totalRecords;
	}

	public static BigDecimal getAPIAccessTotalFiltered(Connection connection, String searchString, JSONArray columnData)
			throws Exception {
		BigDecimal totalFilteredRecords = null;

		String sqlStmt = "SELECT COUNT(*) AS total_filtered_records FROM api_access a "
				+ "INNER JOIN slu_api_access_status b ON a.api_status = b.id WHERE NOT a.is_hidden";
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

	public static JSONArray getAPIAccessList(Connection connection, BigDecimal limit, BigDecimal offset,
			String searchString, JSONArray columnData, JSONArray orderData) throws Exception {
		JSONArray apiAccessList = new JSONArray();

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

		String sqlStmt = "SELECT a.id, a.api_name, a.api_desc, a.api_status, b.status_name FROM api_access a "
				+ "INNER JOIN slu_api_access_status b ON a.api_status = b.id WHERE NOT a.is_hidden";
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
			JSONObject apiAccessObj = new JSONObject();
			apiAccessObj.put("id", rs.getBigDecimal("id"));
			apiAccessObj.put("api_name", rs.getString("api_name"));
			apiAccessObj.put("api_desc", rs.getString("api_desc"));
			apiAccessObj.put("api_status", rs.getInt("api_status"));
			apiAccessObj.put("status_name", rs.getString("status_name"));

			apiAccessList.put(apiAccessObj);
		}
		rs.close();
		ps.close();

		return apiAccessList;
	}

	public static JSONObject getAPIAccessDT(Connection connection, BigDecimal pageIndex, BigDecimal limit,
			BigDecimal offset, String searchString, JSONArray columnData, JSONArray orderData) throws Exception {
		searchString = searchString.toLowerCase();

		JSONObject apiAccessDT = new JSONObject();
		apiAccessDT.put("draw", pageIndex);
		apiAccessDT.put("recordsTotal", getAPIAccessTotalRecord(connection));
		apiAccessDT.put("recordsFiltered", getAPIAccessTotalFiltered(connection, searchString, columnData));
		apiAccessDT.put("data", getAPIAccessList(connection, limit, offset, searchString, columnData, orderData));

		return apiAccessDT;
	}

	public static JSONArray getAPIStatusList(Connection connection) throws Exception {
		JSONArray apiStatusList = new JSONArray();

		String sqlStmt = "SELECT id, status_name FROM slu_api_access_status ORDER BY id ASC";
		PreparedStatement ps = connection.prepareStatement(sqlStmt);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			JSONObject apiStatusObj = new JSONObject();
			apiStatusObj.put("id", rs.getBigDecimal("id"));
			apiStatusObj.put("status_name", rs.getString("status_name"));

			apiStatusList.put(apiStatusObj);
		}
		rs.close();
		ps.close();

		return apiStatusList;
	}

	public static boolean createAPI(Connection connection, String apiName, String apiDesc, BigDecimal apiStatusID)
			throws Exception {
		String sqlStmt = "INSERT INTO api_access (api_name,api_desc,api_status) VALUES (?,?,?)";
		PreparedStatement ps = connection.prepareStatement(sqlStmt);
		ps.setString(1, apiName);
		ps.setString(2, apiDesc);
		ps.setBigDecimal(3, apiStatusID);
		int updatedRow = ps.executeUpdate();
		ps.close();

		return updatedRow >= 1;
	}

	public static boolean updateAPI(Connection connection, BigDecimal apiID, String apiName, String apiDesc,
			BigDecimal apiStatusID) throws Exception {
		String sqlStmt = "UPDATE api_access SET api_name = ?, api_desc = ?, api_status = ?, update_time = NOW() WHERE id = ?";
		PreparedStatement ps = connection.prepareStatement(sqlStmt);
		ps.setString(1, apiName);
		ps.setString(2, apiDesc);
		ps.setBigDecimal(3, apiStatusID);
		ps.setBigDecimal(4, apiID);
		int updatedRow = ps.executeUpdate();
		ps.close();

		return updatedRow >= 1;
	}
}

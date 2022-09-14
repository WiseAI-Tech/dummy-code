package com.dummy.code.web.admin.dbutil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONObject;

import com.dummy.code.general.util.DataTableUtil;

public class AdminPageAccessDBUtil {
	private static final String[] DT_CHECK_COLUMNS = { "id", "page_name", "page_desc", "status_name" };
	private static final String[] DT_DB_COLUMNS = { "a.id", "a.page_name", "a.page_desc", "b.status_name" };

	public static BigDecimal getPageAccessTotalRecord(Connection connection) throws Exception {
		BigDecimal totalRecords = null;

		String sqlStmt = "SELECT COUNT(*) AS total_records FROM page_access a "
				+ "INNER JOIN slu_page_access_status b ON a.page_status = b.id WHERE NOT a.is_hidden";
		PreparedStatement ps = connection.prepareStatement(sqlStmt);
		ResultSet rs = ps.executeQuery();
		if (rs.next()) {
			totalRecords = rs.getBigDecimal("total_records");
		}
		rs.close();
		ps.close();

		return totalRecords;
	}

	public static BigDecimal getPageAccessTotalFiltered(Connection connection, String searchString,
			JSONArray columnData) throws Exception {
		BigDecimal totalFilteredRecords = null;

		String sqlStmt = "SELECT COUNT(*) AS total_filtered_records FROM page_access a "
				+ "INNER JOIN slu_page_access_status b ON a.page_status = b.id WHERE NOT a.is_hidden";
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

	public static JSONArray getPageAccessList(Connection connection, BigDecimal limit, BigDecimal offset,
			String searchString, JSONArray columnData, JSONArray orderData) throws Exception {
		JSONArray pageAccessList = new JSONArray();

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

		String sqlStmt = "SELECT a.id, a.page_name, a.page_desc, a.page_status, b.status_name FROM page_access a "
				+ "INNER JOIN slu_page_access_status b ON a.page_status = b.id WHERE NOT a.is_hidden";
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
			JSONObject pageAccessObj = new JSONObject();
			pageAccessObj.put("id", rs.getBigDecimal("id"));
			pageAccessObj.put("page_name", rs.getString("page_name"));
			pageAccessObj.put("page_desc", rs.getString("page_desc"));
			pageAccessObj.put("page_status", rs.getInt("page_status"));
			pageAccessObj.put("status_name", rs.getString("status_name"));

			pageAccessList.put(pageAccessObj);
		}
		rs.close();
		ps.close();

		return pageAccessList;
	}

	public static JSONObject getPageAccessDT(Connection connection, BigDecimal pageIndex, BigDecimal limit,
			BigDecimal offset, String searchString, JSONArray columnData, JSONArray orderData) throws Exception {
		searchString = searchString.toLowerCase();

		JSONObject pageAccessDT = new JSONObject();
		pageAccessDT.put("draw", pageIndex);
		pageAccessDT.put("recordsTotal", getPageAccessTotalRecord(connection));
		pageAccessDT.put("recordsFiltered", getPageAccessTotalFiltered(connection, searchString, columnData));
		pageAccessDT.put("data", getPageAccessList(connection, limit, offset, searchString, columnData, orderData));

		return pageAccessDT;
	}

	public static JSONArray getPageStatusList(Connection connection) throws Exception {
		JSONArray pageStatusList = new JSONArray();

		String sqlStmt = "SELECT id, status_name FROM slu_page_access_status ORDER BY id ASC";
		PreparedStatement ps = connection.prepareStatement(sqlStmt);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			JSONObject pageStatusObj = new JSONObject();
			pageStatusObj.put("id", rs.getBigDecimal("id"));
			pageStatusObj.put("status_name", rs.getString("status_name"));

			pageStatusList.put(pageStatusObj);
		}
		rs.close();
		ps.close();

		return pageStatusList;
	}

	public static boolean createPage(Connection connection, String pageName, String pageDesc, BigDecimal pageStatusID)
			throws Exception {
		String sqlStmt = "INSERT INTO page_access (page_name,page_desc,page_status) VALUES (?,?,?)";
		PreparedStatement ps = connection.prepareStatement(sqlStmt);
		ps.setString(1, pageName);
		ps.setString(2, pageDesc);
		ps.setBigDecimal(3, pageStatusID);
		int updatedRow = ps.executeUpdate();
		ps.close();

		return updatedRow >= 1;
	}

	public static boolean updatePage(Connection connection, BigDecimal pageID, String pageName, String pageDesc,
			BigDecimal pageStatusID) throws Exception {
		String sqlStmt = "UPDATE page_access SET page_name = ?, page_desc = ?, page_status = ?, update_time = NOW() WHERE id = ?";
		PreparedStatement ps = connection.prepareStatement(sqlStmt);
		ps.setString(1, pageName);
		ps.setString(2, pageDesc);
		ps.setBigDecimal(3, pageStatusID);
		ps.setBigDecimal(4, pageID);
		int updatedRow = ps.executeUpdate();
		ps.close();

		return updatedRow >= 1;
	}
}

package com.dummy.code.web.admin.dbutil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.dummy.code.general.util.DataTableUtil;
import com.dummy.code.general.util.ExportDataListUtil;

public class AdminAuditTrailDBUtil {

	private static final String[] DT_CHECK_COLUMNS = { "user_id", "user_name", "action_desc", "action_date" };
	private static final String[] DT_DB_COLUMNS = { "a.user_id", "b.full_name", "a.action_desc", "a.created_time" };

	public static BigDecimal getDataTotalRecord(Connection connection, BigDecimal auditTypeID, Date startDate,
			Date endDate) throws Exception {
		BigDecimal totalRecords = null;

		String sqlStmt = "SELECT COUNT(*) AS total_records FROM admin_audit_trail a "
				+ "INNER JOIN admin_user b ON a.user_id = b.id WHERE a.audit_type = ?";
		if (sqlStmt.substring(sqlStmt.lastIndexOf("FROM")).contains("WHERE")) {
			sqlStmt += " AND ";
		} else {
			sqlStmt += " WHERE ";
		}
		sqlStmt += "a.created_time BETWEEN ? AND ?";

		PreparedStatement ps = connection.prepareStatement(sqlStmt);
		int psIndex = 1;
		ps.setBigDecimal(psIndex++, auditTypeID);
		ps.setTimestamp(psIndex++, new java.sql.Timestamp(startDate.getTime()));
		ps.setTimestamp(psIndex++, new java.sql.Timestamp(endDate.getTime()));
		ResultSet rs = ps.executeQuery();
		if (rs.next()) {
			totalRecords = rs.getBigDecimal("total_records");
		}
		rs.close();
		ps.close();

		return totalRecords;
	}

	public static BigDecimal getDataTotalFiltered(Connection connection, String searchString, JSONArray columnData,
			BigDecimal auditTypeID, Date startDate, Date endDate) throws Exception {
		BigDecimal totalFilteredRecords = null;

		String sqlStmt = "SELECT COUNT(*) AS total_filtered_records FROM admin_audit_trail a "
				+ "INNER JOIN admin_user b ON a.user_id = b.id WHERE a.audit_type = ?";
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
		if (sqlStmt.substring(sqlStmt.lastIndexOf("FROM")).contains("WHERE")) {
			sqlStmt += " AND ";
		} else {
			sqlStmt += " WHERE ";
		}
		sqlStmt += "a.created_time BETWEEN ? AND ?";

		PreparedStatement ps = connection.prepareStatement(sqlStmt);
		int psIndex = 1;
		ps.setBigDecimal(psIndex++, auditTypeID);
		if (!extFilterStr.isEmpty()) {
			psIndex = DataTableUtil.fillSQLExtFilter(columnData, DT_CHECK_COLUMNS, DT_DB_COLUMNS, psIndex, ps);
		}
		if (searchString != null && !searchString.isEmpty()) {
			for (int x = 0; x < DataTableUtil.getSQLLikeCount(columnData, DT_CHECK_COLUMNS, DT_DB_COLUMNS); x++) {
				ps.setString(psIndex++, "%" + searchString + "%");
			}
		}
		ps.setTimestamp(psIndex++, new java.sql.Timestamp(startDate.getTime()));
		ps.setTimestamp(psIndex++, new java.sql.Timestamp(endDate.getTime()));
		ResultSet rs = ps.executeQuery();
		if (rs.next()) {
			totalFilteredRecords = rs.getBigDecimal("total_filtered_records");
		}
		rs.close();
		ps.close();

		return totalFilteredRecords;
	}

	public static JSONArray getDataList(Connection connection, BigDecimal limit, BigDecimal offset, String searchString,
			JSONArray columnData, JSONArray orderData, BigDecimal auditTypeID, Date startDate, Date endDate)
			throws Exception {
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

		String sqlStmt = "SELECT a.user_id, a.action_desc, a.created_time, b.full_name FROM admin_audit_trail a "
				+ "INNER JOIN admin_user b ON a.user_id = b.id WHERE a.audit_type = ?";
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
		if (sqlStmt.substring(sqlStmt.lastIndexOf("FROM")).contains("WHERE")) {
			sqlStmt += " AND ";
		} else {
			sqlStmt += " WHERE ";
		}
		sqlStmt += "a.created_time BETWEEN ? AND ?";
		sqlStmt += " ORDER BY "
				+ DataTableUtil.generateSQLOrderStr(columnData, DT_CHECK_COLUMNS, DT_DB_COLUMNS, orderData)
				+ " LIMIT ? OFFSET ?";

		PreparedStatement ps = connection.prepareStatement(sqlStmt);
		int psIndex = 1;
		ps.setBigDecimal(psIndex++, auditTypeID);
		if (!extFilterStr.isEmpty()) {
			psIndex = DataTableUtil.fillSQLExtFilter(columnData, DT_CHECK_COLUMNS, DT_DB_COLUMNS, psIndex, ps);
		}
		if (searchString != null && !searchString.isEmpty()) {
			for (int x = 0; x < DataTableUtil.getSQLLikeCount(columnData, DT_CHECK_COLUMNS, DT_DB_COLUMNS); x++) {
				ps.setString(psIndex++, "%" + searchString + "%");
			}
		}
		ps.setTimestamp(psIndex++, new java.sql.Timestamp(startDate.getTime()));
		ps.setTimestamp(psIndex++, new java.sql.Timestamp(endDate.getTime()));
		ps.setBigDecimal(psIndex++, limit);
		ps.setBigDecimal(psIndex++, offset);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			JSONObject dataObj = new JSONObject();
			dataObj.put("user_id", rs.getBigDecimal("user_id"));
			dataObj.put("user_name", rs.getString("full_name"));
			dataObj.put("action_desc", rs.getString("action_desc"));
			dataObj.put("action_date",
					rs.getTimestamp("created_time") != null
							? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(rs.getTimestamp("created_time"))
							: null);

			dataList.put(dataObj);
		}
		rs.close();
		ps.close();

		return dataList;
	}

	public static JSONObject getAuditTrailDT(Connection connection, BigDecimal pageIndex, BigDecimal limit,
			BigDecimal offset, String searchString, JSONArray columnData, JSONArray orderData, BigDecimal auditTypeID,
			String startDate, String endDate) throws Exception {
		searchString = searchString.toLowerCase();

		Calendar startCal = Calendar.getInstance();
		startCal.setTime(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX").parse(startDate));
		startCal.set(Calendar.HOUR_OF_DAY, 0);
		startCal.set(Calendar.MINUTE, 0);
		startCal.set(Calendar.SECOND, 0);
		startCal.set(Calendar.MILLISECOND, 0);
		Calendar endCal = Calendar.getInstance();
		endCal.setTime(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX").parse(endDate));
		endCal.set(Calendar.HOUR_OF_DAY, 23);
		endCal.set(Calendar.MINUTE, 59);
		endCal.set(Calendar.SECOND, 59);
		endCal.set(Calendar.MILLISECOND, 999);

		JSONObject itemDT = new JSONObject();
		itemDT.put("draw", pageIndex);
		itemDT.put("recordsTotal", getDataTotalRecord(connection, auditTypeID, startCal.getTime(), endCal.getTime()));
		itemDT.put("recordsFiltered", getDataTotalFiltered(connection, searchString, columnData, auditTypeID,
				startCal.getTime(), endCal.getTime()));
		itemDT.put("data", getDataList(connection, limit, offset, searchString, columnData, orderData, auditTypeID,
				startCal.getTime(), endCal.getTime()));

		return itemDT;
	}

	public static byte[] doExportDT(Connection connection, String searchString, JSONArray columnData,
			JSONArray orderData, String exportType, BigDecimal auditTypeID, String startDate, String endDate)
			throws Exception {
		Calendar startCal = Calendar.getInstance();
		startCal.setTime(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX").parse(startDate));
		startCal.set(Calendar.HOUR_OF_DAY, 0);
		startCal.set(Calendar.MINUTE, 0);
		startCal.set(Calendar.SECOND, 0);
		startCal.set(Calendar.MILLISECOND, 0);
		Calendar endCal = Calendar.getInstance();
		endCal.setTime(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX").parse(endDate));
		endCal.set(Calendar.HOUR_OF_DAY, 23);
		endCal.set(Calendar.MINUTE, 59);
		endCal.set(Calendar.SECOND, 59);
		endCal.set(Calendar.MILLISECOND, 999);

		JSONArray dataList = getDataList(connection, null, BigDecimal.ZERO, searchString, columnData, orderData,
				auditTypeID, startCal.getTime(), endCal.getTime());
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
		extraData.put("Report Name", "Audit Trail");
		extraData.put("Start Date", new SimpleDateFormat("yyyy-MM-dd").format(startCal.getTime()));
		extraData.put("End Date", new SimpleDateFormat("yyyy-MM-dd").format(endCal.getTime()));
		byte[] byteData = ExportDataListUtil.generateDataFile(exportType, headerNameList, headerMDataList,
				displayColumnLengthDataList, dataList, extraData, null);

		return byteData;
	}

	public static JSONArray getAuditTypeList(Connection connection) throws Exception {
		JSONArray auditTypeList = new JSONArray();

		String sqlStmt = "SELECT id, type_name FROM slu_admin_audit_trail_type ORDER BY id ASC";
		PreparedStatement ps = connection.prepareStatement(sqlStmt);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			JSONObject auditTypeObj = new JSONObject();
			auditTypeObj.put("id", rs.getBigDecimal("id"));
			auditTypeObj.put("name", rs.getString("type_name"));

			auditTypeList.put(auditTypeObj);
		}
		rs.close();
		ps.close();

		return auditTypeList;
	}
}

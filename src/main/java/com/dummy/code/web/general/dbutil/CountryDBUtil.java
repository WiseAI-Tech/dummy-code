package com.dummy.code.web.general.dbutil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.json.JSONArray;
import org.json.JSONObject;

public class CountryDBUtil {
	public static JSONArray getCallingCodeData(Connection connection, String contextPath) throws Exception {
		JSONArray storeList = new JSONArray();

		String sqlStmt = "SELECT id, country_name, country_calling_code FROM slu_country_data "
				+ "WHERE is_enabled ORDER BY special_sort ASC, country_name ASC";
		PreparedStatement ps = connection.prepareStatement(sqlStmt);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			JSONObject storeObj = new JSONObject();
			storeObj.put("id", rs.getBigDecimal("id"));
			storeObj.put("countryName", rs.getString("country_name"));
			storeObj.put("countryCallingCode", rs.getString("country_calling_code"));
			storeObj.put("countryImage", contextPath + "/corp_media/country_image/" + rs.getBigDecimal("id"));

			storeList.put(storeObj);
		}
		rs.close();
		ps.close();

		return storeList;
	}

	public static String getCallingCodeByID(Connection connection, int countryID) throws Exception {
		String callingCode = null;

		String sqlStmt = "SELECT country_calling_code FROM slu_country_data WHERE id = ?";
		PreparedStatement ps = connection.prepareStatement(sqlStmt);
		int psIndex = 1;
		ps.setInt(psIndex++, countryID);
		ResultSet rs = ps.executeQuery();
		if (rs.next()) {
			callingCode = rs.getString("country_calling_code");
		}
		rs.close();
		ps.close();

		return callingCode;
	}
}

package com.dummy.code.web.admin.util;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.sql.DataSource;

import com.dummy.code.web.general.dbutil.DStrUtil;

public class AdminAPIAccessUtil {

	public enum APIAccessType {
		ALLOW, DENIED, DISABLED, NOT_FOUND
	}

	public enum APIStatus {
		ACTIVE(1), DISABLED(2);

		private final int value;

		APIStatus(final int newValue) {
			value = newValue;
		}

		public int getValue() {
			return value;
		}
	}

	public static APIAccessType getAccessType(DataSource dataSource, String apiName, BigDecimal userSystemID) {
		APIAccessType accessType = null;

		Connection connection = null;
		int apiID = 0;
		int apiStatus = 0;
		String accessDStr = null;

		try {
			connection = dataSource.getConnection();
			String sqlStmt = "SELECT id, api_status FROM api_access WHERE api_name = ?";
			PreparedStatement ps = connection.prepareStatement(sqlStmt);
			ps.setString(1, apiName);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				apiID = rs.getInt("id");
				apiStatus = rs.getInt("api_status");
			}
			rs.close();
			ps.close();

			sqlStmt = "SELECT b.api_access_dstr FROM admin_user a "
					+ "INNER JOIN role_access b ON a.role_access = b.id WHERE a.id = ?";
			ps = connection.prepareStatement(sqlStmt);
			ps.setBigDecimal(1, userSystemID);
			rs = ps.executeQuery();
			if (rs.next()) {
				accessDStr = rs.getString("api_access_dstr");
			}
			rs.close();
			ps.close();
		} catch (Exception e) {
			e.printStackTrace();

			accessType = APIAccessType.DENIED;
		} finally {
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (Exception e) {
			}
		}

		if (accessDStr != null && apiID != 0 && apiStatus != 0) {
			if (DStrUtil.getDStrValue(accessDStr, apiID - 1)) {
				if (apiStatus == APIStatus.ACTIVE.getValue()) {
					accessType = APIAccessType.ALLOW;
				} else if (apiStatus == APIStatus.DISABLED.getValue()) {
					accessType = APIAccessType.DISABLED;
				} else {
					accessType = APIAccessType.NOT_FOUND;
				}
			} else {
				accessType = APIAccessType.DENIED;
			}
		} else {
			accessType = APIAccessType.NOT_FOUND;
		}

		return accessType;
	}
}

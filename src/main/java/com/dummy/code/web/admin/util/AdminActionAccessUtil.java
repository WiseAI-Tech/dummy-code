package com.dummy.code.web.admin.util;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.sql.DataSource;

import com.dummy.code.web.general.dbutil.DStrUtil;

public class AdminActionAccessUtil {
	public static String getActionAccess(DataSource dataSource, BigDecimal userSystemID) {
		Connection connection = null;
		String accessDStr = null;

		try {
			connection = dataSource.getConnection();
			String sqlStmt = "SELECT b.action_access_dstr FROM admin_user a "
					+ "INNER JOIN role_access b ON a.role_access = b.id WHERE a.id = ?";
			PreparedStatement ps = connection.prepareStatement(sqlStmt);
			ps.setBigDecimal(1, userSystemID);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				accessDStr = DStrUtil.dStrHexToBin(rs.getString("action_access_dstr"));
			}
			rs.close();
			ps.close();

			StringBuilder newAccessDStr = new StringBuilder(accessDStr);

			sqlStmt = "SELECT id, is_enabled FROM action_access order by id ASC";
			ps = connection.prepareStatement(sqlStmt);
			rs = ps.executeQuery();
			while (rs.next()) {
				if (!rs.getBoolean("is_enabled")) {
					newAccessDStr.setCharAt(rs.getInt("id") - 1, '0');
				}
			}
			rs.close();
			ps.close();

			accessDStr = newAccessDStr.toString();
			newAccessDStr.setLength(0);
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

		return accessDStr;
	}
}

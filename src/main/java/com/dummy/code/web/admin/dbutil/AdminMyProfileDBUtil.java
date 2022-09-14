package com.dummy.code.web.admin.dbutil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class AdminMyProfileDBUtil {
	public static boolean isCurrentPasswordMatch(Connection connection, BigDecimal userID, String currentPassword)
			throws Exception {
		String currentDBPassword = null;
		String sqlStmt = "SELECT login_password FROM admin_user WHERE id = ?";
		PreparedStatement ps = connection.prepareStatement(sqlStmt);
		ps.setBigDecimal(1, userID);
		ResultSet rs = ps.executeQuery();
		if (rs.next()) {
			currentDBPassword = rs.getString("login_password");
		}
		rs.close();
		ps.close();

		return new BCryptPasswordEncoder().matches(currentPassword, currentDBPassword);
	}

	public static boolean changePassword(Connection connection, BigDecimal userID, String newPassword)
			throws Exception {
		boolean isSuccess = false;

		String sqlStmt = "UPDATE admin_user SET login_password = ? WHERE id = ?";
		PreparedStatement ps = connection.prepareStatement(sqlStmt);
		ps.setString(1, newPassword);
		ps.setBigDecimal(2, userID);
		isSuccess = ps.executeUpdate() > 0;
		ps.close();

		return isSuccess;
	}
}

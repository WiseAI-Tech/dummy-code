package com.dummy.code.web.admin.util;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.sql.DataSource;

public class AdminAuditTrailUtil {
	public enum TrailType {
		ROLE_ACCESS(1), USER(2), SUPER_GROUP(3), STORE(4), STORE_MENU(5), CORP_USER(6), CORP_ORDER(7),
		PUBLIC_HOLIDAY(8), COUPON(9), MARKETING_MEDIA(10), CRM_VOUCHER_REDEMPTION(11), POS_CRM_TRANSACTION(12), OUTLET(13),
		PROMOTION(14);

		private final int value;

		TrailType(final int newValue) {
			value = newValue;
		}

		public int getValue() {
			return value;
		}
	}

	public static void saveAuditTrail(DataSource dataSource, BigDecimal userID, TrailType auditTrailType,
			String actionDesc) {
		Connection connection = null;

		try {
			connection = dataSource.getConnection();
			String sqlStmt = "INSERT INTO admin_audit_trail (user_id,audit_type,action_desc,created_time) "
					+ "VALUES (?,?,?,NOW())";
			PreparedStatement ps = connection.prepareStatement(sqlStmt);
			int psIndex = 1;
			ps.setBigDecimal(psIndex++, userID);
			ps.setInt(psIndex++, auditTrailType.getValue());
			ps.setString(psIndex++, actionDesc);
			ps.executeUpdate();
			ps.close();
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
	}
}

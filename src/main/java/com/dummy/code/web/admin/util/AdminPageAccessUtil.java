package com.dummy.code.web.admin.util;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;

import javax.sql.DataSource;

import com.dummy.code.web.general.dbutil.DStrUtil;

public class AdminPageAccessUtil {

	public enum PageAccessType {
		ALLOW, DENIED, UNDER_DEVELOPMENT, UNDER_MAINTENANCE, NOT_FOUND
	}

	public enum PageStatus {
		UNDER_DEVELOPMENT(1), ACTIVE(2), UNDER_MAINTENANCE(3);

		private final int value;

		PageStatus(final int newValue) {
			value = newValue;
		}

		public int getValue() {
			return value;
		}
	}

	public static final String PAGE_ACCESS_DENIED = "access_denied";
	public static final String PAGE_404_NOT_FOUND = "404_not_found";
	public static final String PAGE_ERROR = "error";
	public static final String PAGE_UNDER_DEVELOPMENT = "under_development";
	public static final String PAGE_UNDER_MAINTENANCE = "under_maintenance";
	public static final String PAGE_SESSION_EXPIRED = "session_expired";

	private static final String[] PUBLIC_ACCESS_PAGE = { PAGE_ACCESS_DENIED, PAGE_404_NOT_FOUND, PAGE_ERROR,
			PAGE_UNDER_DEVELOPMENT, PAGE_UNDER_MAINTENANCE, PAGE_SESSION_EXPIRED };

	public static PageAccessType getAccessType(DataSource dataSource, String pageName, BigDecimal userSystemID) {
		PageAccessType accessType = null;

		if (Arrays.asList(PUBLIC_ACCESS_PAGE).contains(pageName)) {
			accessType = PageAccessType.ALLOW;
		} else {
			Connection connection = null;
			int pageID = 0;
			int pageStatus = 0;
			String accessDStr = null;

			try {
				connection = dataSource.getConnection();
				String sqlStmt = "SELECT id, page_status FROM page_access WHERE page_name = ?";
				PreparedStatement ps = connection.prepareStatement(sqlStmt);
				ps.setString(1, pageName);
				ResultSet rs = ps.executeQuery();
				if (rs.next()) {
					pageID = rs.getInt("id");
					pageStatus = rs.getInt("page_status");
				}
				rs.close();
				ps.close();

				sqlStmt = "SELECT b.page_access_dstr FROM admin_user a "
						+ "INNER JOIN role_access b ON a.role_access = b.id WHERE a.id = ?";
				ps = connection.prepareStatement(sqlStmt);
				ps.setBigDecimal(1, userSystemID);
				rs = ps.executeQuery();
				if (rs.next()) {
					accessDStr = rs.getString("page_access_dstr");
				}
				rs.close();
				ps.close();
			} catch (Exception e) {
				e.printStackTrace();

				accessType = PageAccessType.DENIED;
			} finally {
				try {
					if (connection != null) {
						connection.close();
					}
				} catch (Exception e) {
				}
			}

			if (accessDStr != null && pageID != 0 && pageStatus != 0) {
				if (DStrUtil.getDStrValue(accessDStr, pageID - 1)) {
					if (pageStatus == PageStatus.ACTIVE.getValue()) {
						accessType = PageAccessType.ALLOW;
					} else if (pageStatus == PageStatus.UNDER_DEVELOPMENT.getValue()) {
						accessType = PageAccessType.UNDER_DEVELOPMENT;
					} else if (pageStatus == PageStatus.UNDER_MAINTENANCE.getValue()) {
						accessType = PageAccessType.UNDER_MAINTENANCE;
					} else {
						accessType = PageAccessType.NOT_FOUND;
					}
				} else {
					accessType = PageAccessType.DENIED;
				}
			} else {
				accessType = PageAccessType.NOT_FOUND;
			}
		}

		return accessType;
	}
}

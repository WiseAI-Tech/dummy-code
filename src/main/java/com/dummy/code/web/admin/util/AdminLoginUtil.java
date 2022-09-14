package com.dummy.code.web.admin.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.dummy.code.general.util.UniversalCaptcha;
import com.dummy.code.web.admin.modal.AdminLoginSessionModal;

public class AdminLoginUtil {

	public enum LoginStatus {
		PENDING(1), ACTIVE(2), SUSPENDED(3);

		private final int value;

		LoginStatus(final int newValue) {
			value = newValue;
		}

		public int getValue() {
			return value;
		}
	}

	public static final int MAX_LOGIN_FAIL_ATTEMPT = 3;

	public static final String USER_SESSION_NAME = "admin_session";
	public static final String USER_SESSION_LOGIN_ERR_NAME = "admin_session_login_err";

	public static final String USER_SESSION_FAILED_ATTEMPT = "admin_session_failed_attempt";
	public static final String USER_SESSION_SECURITY_CODE = "admin_session_security_code";
	public static final String USER_SESSION_SECURITY_CODE_IMAGE = "admin_session_security_code_image";

	public static final String SC_PASSWORD = "123qweasd!@#QWEASD";
	public static final int SC_CODE_LENGTH = 6;
	public static final int SC_FONT_SIZE = 50;
	public static final int SC_NOISE = 5;

	public static AdminLoginSessionModal getUserSession(HttpServletRequest request) {
		return (AdminLoginSessionModal) request.getSession().getAttribute(USER_SESSION_NAME);
	}

	public static AdminLoginSessionModal authenticateSession(DataSource dataSource, String loginID,
			String loginPassword) {
		AdminLoginSessionModal loginModal = null;
		Connection connection = null;

		try {
			connection = dataSource.getConnection();
			String sqlStmt = "SELECT id, role_access, login_id, login_password, user_status, display_name FROM admin_user WHERE login_id = ?";
			PreparedStatement ps = connection.prepareStatement(sqlStmt);
			ps.setString(1, loginID);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				if (new BCryptPasswordEncoder().matches(loginPassword, rs.getString("login_password"))) {
					loginModal = new AdminLoginSessionModal();
					loginModal.setSystemID(rs.getBigDecimal("id"));
					loginModal.setLoginID(rs.getString("login_id"));
					loginModal.setRoleID(rs.getInt("role_access"));
					loginModal.setLoginStatus(rs.getInt("user_status"));
					loginModal.setLoginName(rs.getString("display_name"));
				}
			}
			rs.close();
			ps.close();
		} catch (Exception e) {
			e.printStackTrace();

			loginModal = null;
		} finally {
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (Exception e) {
			}
		}

		return loginModal;
	}

	public static int getLoginFailedAttempt(DataSource dataSource, String loginID) {
		int loginFailedAttempt = -1;
		Connection connection = null;

		try {
			connection = dataSource.getConnection();
			String sqlStmt = "SELECT failed_login_attempt FROM admin_user WHERE login_id = ?";
			PreparedStatement ps = connection.prepareStatement(sqlStmt);
			ps.setString(1, loginID);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				loginFailedAttempt = rs.getInt("failed_login_attempt");
			}
			rs.close();
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

		return loginFailedAttempt;
	}

	public static void setLoginFailedAttempt(DataSource dataSource, String loginID, int loginAttempt) {
		Connection connection = null;

		try {
			connection = dataSource.getConnection();
			String sqlStmt = "UPDATE admin_user SET failed_login_attempt = ? WHERE login_id = ?";
			PreparedStatement ps = connection.prepareStatement(sqlStmt);
			ps.setInt(1, loginAttempt);
			ps.setString(2, loginID);
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

		if (loginAttempt > MAX_LOGIN_FAIL_ATTEMPT) {
			setUserStatus(dataSource, loginID, LoginStatus.SUSPENDED);
		} else if (loginAttempt <= 0) {
			setUserStatus(dataSource, loginID, LoginStatus.ACTIVE);
		}
	}

	public static void setUserStatus(DataSource dataSource, String loginID, LoginStatus loginStatus) {
		Connection connection = null;

		try {
			connection = dataSource.getConnection();
			String sqlStmt = "UPDATE admin_user SET user_status = ? WHERE login_id = ?";
			PreparedStatement ps = connection.prepareStatement(sqlStmt);
			ps.setInt(1, loginStatus.getValue());
			ps.setString(2, loginID);
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

	public static void setUpdateLoginTime(DataSource dataSource, String loginID) {
		Connection connection = null;

		try {
			connection = dataSource.getConnection();
			String sqlStmt = "UPDATE admin_user SET last_login_time = NOW() WHERE login_id = ?";
			PreparedStatement ps = connection.prepareStatement(sqlStmt);
			ps.setString(1, loginID);
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

	public static String generateRandomSecurityString(int codeLength) {
		String randomPhrase = "1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ";

		int codeLengthIndex = codeLength;
		StringBuilder sb = new StringBuilder();
		while (codeLengthIndex-- > 0) {
			int character = (int) (Math.random() * randomPhrase.length());
			sb.append(randomPhrase.charAt(character));
		}

		return sb.toString();
	}

	public static boolean isSecurityCodeMatch(String securityString, String securityCheckString) {
		boolean isMatch = false;
		try {
			UniversalCaptcha uc = new UniversalCaptcha();
			uc.setCaptchaData(securityString, SC_PASSWORD, SC_CODE_LENGTH, SC_FONT_SIZE, SC_NOISE);
			uc.processCaptcha();
			isMatch = uc.getCaptchaCode().equalsIgnoreCase(securityCheckString);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return isMatch;
	}

	public static String getSecurityCodeBase64Image(String securityString) {
		String base64String = null;
		try {
			UniversalCaptcha uc = new UniversalCaptcha();
			uc.setCaptchaData(securityString, SC_PASSWORD, SC_CODE_LENGTH, SC_FONT_SIZE, SC_NOISE);
			uc.processCaptcha();
			base64String = uc.getCaptchaBase64PNG();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return base64String;
	}

	public static void deauthenticateSession(HttpServletRequest request) {
		request.getSession().removeAttribute(USER_SESSION_NAME);
	}
}

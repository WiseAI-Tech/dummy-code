package com.dummy.code.web.admin.restcontroller;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.Base64;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.dummy.code.general.util.DataTableUtil;
import com.dummy.code.general.util.StatusCodeUtil;
import com.dummy.code.properties.GeneralProperties;
import com.dummy.code.web.admin.dbutil.AdminUserDBUtil;
import com.dummy.code.web.admin.modal.AdminLoginSessionModal;
import com.dummy.code.web.admin.util.AdminAPIAccessUtil;
import com.dummy.code.web.admin.util.AdminAuditTrailUtil;
import com.dummy.code.web.admin.util.AdminLoginUtil;
import com.dummy.code.web.admin.util.AdminAPIAccessUtil.APIAccessType;

@RestController
@RequestMapping("/admin_api/user_management/")
public class AdminUserManagementRestController {

	@Autowired
	GeneralProperties generalProperties;

	@Autowired
	DataSource dataSource;

	@Autowired
	MessageSource msgSrc;

	@RequestMapping(value = "get_user_init_data", method = { RequestMethod.POST })
	public ResponseEntity<?> AdminAPI_GetUserInitData(HttpServletRequest request, HttpServletResponse response,
			Locale locale) {
		AdminLoginSessionModal loginSession = AdminLoginUtil.getUserSession(request);

		ResponseEntity<?> responseEntity = null;
		HttpHeaders header = new HttpHeaders();
		JSONObject resultObj = new JSONObject();

		Connection connection = null;
		try {
			if (loginSession != null) {
				APIAccessType accessType = AdminAPIAccessUtil.getAccessType(dataSource,
						this.getClass().getAnnotation(RequestMapping.class).value()[0] + new Object() {
						}.getClass().getEnclosingMethod().getAnnotation(RequestMapping.class).value()[0],
						loginSession.getSystemID());
				if (accessType == APIAccessType.ALLOW) {
					connection = dataSource.getConnection();
					connection.setAutoCommit(false);

					JSONArray userRoleList = AdminUserDBUtil.getUserRoleList(connection);
					resultObj.put("user_role_list", userRoleList);

					JSONArray userStatusList = AdminUserDBUtil.getUserStatusList(connection);
					resultObj.put("user_status_list", userStatusList);

					header.setContentType(MediaType.APPLICATION_JSON);
					responseEntity = new ResponseEntity<>(resultObj.toString(), header, HttpStatus.OK);

					connection.commit();
				} else if (accessType == APIAccessType.DENIED) {
					header.setContentType(MediaType.TEXT_PLAIN);
					responseEntity = new ResponseEntity<>(
							msgSrc.getMessage("gt.error.general.access_denied", null, locale), header,
							HttpStatus.FORBIDDEN);
				} else if (accessType == APIAccessType.DISABLED) {
					header.setContentType(MediaType.TEXT_PLAIN);
					responseEntity = new ResponseEntity<>(
							msgSrc.getMessage("gt.error.general.method_disabled", null, locale), header,
							HttpStatus.METHOD_NOT_ALLOWED);
				} else if (accessType == APIAccessType.NOT_FOUND) {
					header.setContentType(MediaType.TEXT_PLAIN);
					responseEntity = new ResponseEntity<>(
							msgSrc.getMessage("gt.error.general.method_not_found", null, locale), header,
							HttpStatus.NOT_FOUND);
				}
			} else {
				header.setContentType(MediaType.TEXT_PLAIN);
				responseEntity = new ResponseEntity<>(
						msgSrc.getMessage("gt.error.general.session_timeout", null, locale), header,
						HttpStatus.UNAUTHORIZED);
			}
		} catch (Exception ex) {
			ex.printStackTrace();

			try {
				if (connection != null) {
					connection.rollback();
				}
			} catch (Exception ex2) {
			}

			header.setContentType(MediaType.TEXT_PLAIN);
			responseEntity = new ResponseEntity<>(msgSrc.getMessage("gt.error.general.system_error", null, locale),
					header, HttpStatus.INTERNAL_SERVER_ERROR);
		} finally {
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (Exception ex) {
			}
		}

		return responseEntity;
	}

	@RequestMapping(value = "get_user_list", method = { RequestMethod.POST })
	public ResponseEntity<?> AdminAPI_GetUserList(@RequestBody String requestBody, HttpServletRequest request,
			HttpServletResponse response, Locale locale) {
		AdminLoginSessionModal loginSession = AdminLoginUtil.getUserSession(request);

		ResponseEntity<?> responseEntity = null;
		HttpHeaders header = new HttpHeaders();
		JSONObject resultObj = new JSONObject();

		Connection connection = null;
		try {
			if (loginSession != null) {
				APIAccessType accessType = AdminAPIAccessUtil.getAccessType(dataSource,
						this.getClass().getAnnotation(RequestMapping.class).value()[0] + new Object() {
						}.getClass().getEnclosingMethod().getAnnotation(RequestMapping.class).value()[0],
						loginSession.getSystemID());
				if (accessType == APIAccessType.ALLOW) {
					connection = dataSource.getConnection();
					connection.setAutoCommit(false);

					JSONObject dtObj = DataTableUtil.convertDTDataToJSON(requestBody);
					boolean isExport = dtObj.optBoolean("isExport", false);
					String exportType = dtObj.optString("exportType");
					String exportGlobalSearch = dtObj.optString("exportGlobalSearch");

					if (isExport) {
						byte[] byteData = AdminUserDBUtil.doExportDT(connection, exportGlobalSearch,
								dtObj.getJSONArray("columns"), dtObj.getJSONArray("order"), exportType,
								loginSession.getRoleID(), loginSession.getSystemID());

						header.setContentType(MediaType.APPLICATION_OCTET_STREAM);
						responseEntity = new ResponseEntity<>(Base64.getEncoder().encodeToString(byteData), header,
								HttpStatus.OK);
					} else {
						JSONObject userDT = AdminUserDBUtil.getUserDT(connection,
								new BigDecimal(dtObj.getString("draw")), new BigDecimal(dtObj.getString("length")),
								new BigDecimal(dtObj.getString("start")),
								dtObj.getJSONObject("search").getString("value"), dtObj.getJSONArray("columns"),
								dtObj.getJSONArray("order"));

						header.setContentType(MediaType.APPLICATION_JSON);
						responseEntity = new ResponseEntity<>(userDT.toString(), header, HttpStatus.OK);
					}

					connection.commit();
				} else if (accessType == APIAccessType.DENIED) {
					header.setContentType(MediaType.TEXT_PLAIN);
					responseEntity = new ResponseEntity<>(
							msgSrc.getMessage("gt.error.general.access_denied", null, locale), header,
							HttpStatus.FORBIDDEN);
				} else if (accessType == APIAccessType.DISABLED) {
					header.setContentType(MediaType.TEXT_PLAIN);
					responseEntity = new ResponseEntity<>(
							msgSrc.getMessage("gt.error.general.method_disabled", null, locale), header,
							HttpStatus.METHOD_NOT_ALLOWED);
				} else if (accessType == APIAccessType.NOT_FOUND) {
					header.setContentType(MediaType.TEXT_PLAIN);
					responseEntity = new ResponseEntity<>(
							msgSrc.getMessage("gt.error.general.method_not_found", null, locale), header,
							HttpStatus.NOT_FOUND);
				}
			} else {
				header.setContentType(MediaType.TEXT_PLAIN);
				responseEntity = new ResponseEntity<>(
						msgSrc.getMessage("gt.error.general.session_timeout", null, locale), header,
						HttpStatus.UNAUTHORIZED);
			}
		} catch (Exception ex) {
			ex.printStackTrace();

			try {
				if (connection != null) {
					connection.rollback();
				}
			} catch (Exception ex2) {
			}

			header.setContentType(MediaType.TEXT_PLAIN);
			responseEntity = new ResponseEntity<>(msgSrc.getMessage("gt.error.general.system_error", null, locale),
					header, HttpStatus.INTERNAL_SERVER_ERROR);
		} finally {
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (Exception ex) {
			}
		}

		return responseEntity;
	}

	@RequestMapping(value = "create_user", method = { RequestMethod.POST })
	public ResponseEntity<?> AdminAPI_CreateUser(@RequestBody String requestBody, HttpServletRequest request,
			HttpServletResponse response, Locale locale) {
		AdminLoginSessionModal loginSession = AdminLoginUtil.getUserSession(request);

		ResponseEntity<?> responseEntity = null;
		HttpHeaders header = new HttpHeaders();
		JSONObject resultObj = new JSONObject();

		Connection connection = null;
		try {
			if (loginSession != null) {
				APIAccessType accessType = AdminAPIAccessUtil.getAccessType(dataSource,
						this.getClass().getAnnotation(RequestMapping.class).value()[0] + new Object() {
						}.getClass().getEnclosingMethod().getAnnotation(RequestMapping.class).value()[0],
						loginSession.getSystemID());
				if (accessType == APIAccessType.ALLOW) {
					connection = dataSource.getConnection();
					connection.setAutoCommit(false);

					JSONObject requestObj = new JSONObject(requestBody);

					if (AdminUserDBUtil.isLoginIDExist(connection, requestObj.getString("loginID"))) {
						resultObj.put("response_code", StatusCodeUtil.ResponseStatus.CREATE_FAILED.getValue());
						resultObj.put("response_message", msgSrc.getMessage("gt.error.user_existed", null, locale));
					} else if (AdminUserDBUtil.isEmailExist(connection, requestObj.getString("email"), null)) {
						resultObj.put("response_code", StatusCodeUtil.ResponseStatus.CREATE_FAILED.getValue());
						resultObj.put("response_message", msgSrc.getMessage("gt.error.email_existed", null, locale));
					} else {
						boolean isUpdateSuccess = AdminUserDBUtil.createUser(connection,
								requestObj.getString("loginID"),
								new BCryptPasswordEncoder().encode(requestObj.getString("loginPassword")),
								requestObj.getString("fullName"), requestObj.getString("displayName"),
								requestObj.getString("email"), requestObj.getBigDecimal("userRoleID"),
								requestObj.getBigDecimal("userStatusID"));
						if (isUpdateSuccess) {
							AdminAuditTrailUtil.saveAuditTrail(dataSource, loginSession.getSystemID(),
									AdminAuditTrailUtil.TrailType.USER, "Created User : " + requestObj.toString());

							resultObj.put("response_code", StatusCodeUtil.ResponseStatus.SUCCESS.getValue());
							resultObj.put("response_message",
									msgSrc.getMessage("gt.success.create_success", null, locale));
						} else {
							resultObj.put("response_code", StatusCodeUtil.ResponseStatus.CREATE_FAILED.getValue());
							resultObj.put("response_message",
									msgSrc.getMessage("gt.error.create_failed", null, locale));
						}
					}

					header.setContentType(MediaType.APPLICATION_JSON);
					responseEntity = new ResponseEntity<>(resultObj.toString(), header, HttpStatus.OK);

					connection.commit();
				} else if (accessType == APIAccessType.DENIED) {
					header.setContentType(MediaType.TEXT_PLAIN);
					responseEntity = new ResponseEntity<>(
							msgSrc.getMessage("gt.error.general.access_denied", null, locale), header,
							HttpStatus.FORBIDDEN);
				} else if (accessType == APIAccessType.DISABLED) {
					header.setContentType(MediaType.TEXT_PLAIN);
					responseEntity = new ResponseEntity<>(
							msgSrc.getMessage("gt.error.general.method_disabled", null, locale), header,
							HttpStatus.METHOD_NOT_ALLOWED);
				} else if (accessType == APIAccessType.NOT_FOUND) {
					header.setContentType(MediaType.TEXT_PLAIN);
					responseEntity = new ResponseEntity<>(
							msgSrc.getMessage("gt.error.general.method_not_found", null, locale), header,
							HttpStatus.NOT_FOUND);
				}
			} else {
				header.setContentType(MediaType.TEXT_PLAIN);
				responseEntity = new ResponseEntity<>(
						msgSrc.getMessage("gt.error.general.session_timeout", null, locale), header,
						HttpStatus.UNAUTHORIZED);
			}
		} catch (Exception ex) {
			ex.printStackTrace();

			try {
				if (connection != null) {
					connection.rollback();
				}
			} catch (Exception ex2) {
			}

			header.setContentType(MediaType.TEXT_PLAIN);
			responseEntity = new ResponseEntity<>(msgSrc.getMessage("gt.error.general.system_error", null, locale),
					header, HttpStatus.INTERNAL_SERVER_ERROR);
		} finally {
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (Exception ex) {
			}
		}

		return responseEntity;
	}

	@RequestMapping(value = "update_user", method = { RequestMethod.POST })
	public ResponseEntity<?> AdminAPI_UpdateUser(@RequestBody String requestBody, HttpServletRequest request,
			HttpServletResponse response, Locale locale) {
		AdminLoginSessionModal loginSession = AdminLoginUtil.getUserSession(request);

		ResponseEntity<?> responseEntity = null;
		HttpHeaders header = new HttpHeaders();
		JSONObject resultObj = new JSONObject();

		Connection connection = null;
		try {
			if (loginSession != null) {
				APIAccessType accessType = AdminAPIAccessUtil.getAccessType(dataSource,
						this.getClass().getAnnotation(RequestMapping.class).value()[0] + new Object() {
						}.getClass().getEnclosingMethod().getAnnotation(RequestMapping.class).value()[0],
						loginSession.getSystemID());
				if (accessType == APIAccessType.ALLOW) {
					connection = dataSource.getConnection();
					connection.setAutoCommit(false);

					JSONObject requestObj = new JSONObject(requestBody);

					if (AdminUserDBUtil.isEmailExist(connection, requestObj.getString("email"),
							requestObj.getBigDecimal("userID"))) {
						resultObj.put("response_code", StatusCodeUtil.ResponseStatus.UPDATE_FAILED.getValue());
						resultObj.put("response_message", msgSrc.getMessage("gt.error.email_existed", null, locale));
					} else {
						boolean isUpdateSuccess = AdminUserDBUtil.updateUser(connection,
								requestObj.getBigDecimal("userID"), requestObj.getString("fullName"),
								requestObj.getString("displayName"), requestObj.getString("email"),
								requestObj.getBigDecimal("userRoleID"), requestObj.getBigDecimal("userStatusID"));
						if (isUpdateSuccess) {
							AdminAuditTrailUtil.saveAuditTrail(dataSource, loginSession.getSystemID(),
									AdminAuditTrailUtil.TrailType.USER, "Updated User ID: "
											+ requestObj.getBigDecimal("userID") + ", Data: " + requestObj.toString());

							resultObj.put("response_code", StatusCodeUtil.ResponseStatus.SUCCESS.getValue());
							resultObj.put("response_message",
									msgSrc.getMessage("gt.success.update_success", null, locale));
						} else {
							resultObj.put("response_code", StatusCodeUtil.ResponseStatus.UPDATE_FAILED.getValue());
							resultObj.put("response_message",
									msgSrc.getMessage("gt.error.update_failed", null, locale));
						}
					}

					header.setContentType(MediaType.APPLICATION_JSON);
					responseEntity = new ResponseEntity<>(resultObj.toString(), header, HttpStatus.OK);

					connection.commit();
				} else if (accessType == APIAccessType.DENIED) {
					header.setContentType(MediaType.TEXT_PLAIN);
					responseEntity = new ResponseEntity<>(
							msgSrc.getMessage("gt.error.general.access_denied", null, locale), header,
							HttpStatus.FORBIDDEN);
				} else if (accessType == APIAccessType.DISABLED) {
					header.setContentType(MediaType.TEXT_PLAIN);
					responseEntity = new ResponseEntity<>(
							msgSrc.getMessage("gt.error.general.method_disabled", null, locale), header,
							HttpStatus.METHOD_NOT_ALLOWED);
				} else if (accessType == APIAccessType.NOT_FOUND) {
					header.setContentType(MediaType.TEXT_PLAIN);
					responseEntity = new ResponseEntity<>(
							msgSrc.getMessage("gt.error.general.method_not_found", null, locale), header,
							HttpStatus.NOT_FOUND);
				}
			} else {
				header.setContentType(MediaType.TEXT_PLAIN);
				responseEntity = new ResponseEntity<>(
						msgSrc.getMessage("gt.error.general.session_timeout", null, locale), header,
						HttpStatus.UNAUTHORIZED);
			}
		} catch (Exception ex) {
			ex.printStackTrace();

			try {
				if (connection != null) {
					connection.rollback();
				}
			} catch (Exception ex2) {
			}

			header.setContentType(MediaType.TEXT_PLAIN);
			responseEntity = new ResponseEntity<>(msgSrc.getMessage("gt.error.general.system_error", null, locale),
					header, HttpStatus.INTERNAL_SERVER_ERROR);
		} finally {
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (Exception ex) {
			}
		}

		return responseEntity;
	}

	@RequestMapping(value = "reset_password", method = { RequestMethod.POST })
	public ResponseEntity<?> AdminAPI_ResetPassword(@RequestBody String requestBody, HttpServletRequest request,
			HttpServletResponse response, Locale locale) {
		AdminLoginSessionModal loginSession = AdminLoginUtil.getUserSession(request);

		ResponseEntity<?> responseEntity = null;
		HttpHeaders header = new HttpHeaders();
		JSONObject resultObj = new JSONObject();

		Connection connection = null;
		try {
			if (loginSession != null) {
				APIAccessType accessType = AdminAPIAccessUtil.getAccessType(dataSource,
						this.getClass().getAnnotation(RequestMapping.class).value()[0] + new Object() {
						}.getClass().getEnclosingMethod().getAnnotation(RequestMapping.class).value()[0],
						loginSession.getSystemID());
				if (accessType == APIAccessType.ALLOW) {
					connection = dataSource.getConnection();
					connection.setAutoCommit(false);

					JSONObject requestObj = new JSONObject(requestBody);

					boolean isUpdateSuccess = AdminUserDBUtil.resetPassword(connection,
							requestObj.getBigDecimal("userID"),
							new BCryptPasswordEncoder().encode(requestObj.getString("loginPassword")));
					if (isUpdateSuccess) {
						AdminAuditTrailUtil.saveAuditTrail(dataSource, loginSession.getSystemID(),
								AdminAuditTrailUtil.TrailType.USER,
								"Reset User Password For User ID: " + requestObj.getBigDecimal("userID"));

						resultObj.put("response_code", StatusCodeUtil.ResponseStatus.SUCCESS.getValue());
						resultObj.put("response_message", msgSrc.getMessage("gt.success.reset_success", null, locale));
					} else {
						resultObj.put("response_code", StatusCodeUtil.ResponseStatus.RESET_FAILED);
						resultObj.put("response_message", msgSrc.getMessage("gt.error.reset_failed", null, locale));
					}

					header.setContentType(MediaType.APPLICATION_JSON);
					responseEntity = new ResponseEntity<>(resultObj.toString(), header, HttpStatus.OK);

					connection.commit();
				} else if (accessType == APIAccessType.DENIED) {
					header.setContentType(MediaType.TEXT_PLAIN);
					responseEntity = new ResponseEntity<>(
							msgSrc.getMessage("gt.error.general.access_denied", null, locale), header,
							HttpStatus.FORBIDDEN);
				} else if (accessType == APIAccessType.DISABLED) {
					header.setContentType(MediaType.TEXT_PLAIN);
					responseEntity = new ResponseEntity<>(
							msgSrc.getMessage("gt.error.general.method_disabled", null, locale), header,
							HttpStatus.METHOD_NOT_ALLOWED);
				} else if (accessType == APIAccessType.NOT_FOUND) {
					header.setContentType(MediaType.TEXT_PLAIN);
					responseEntity = new ResponseEntity<>(
							msgSrc.getMessage("gt.error.general.method_not_found", null, locale), header,
							HttpStatus.NOT_FOUND);
				}
			} else {
				header.setContentType(MediaType.TEXT_PLAIN);
				responseEntity = new ResponseEntity<>(
						msgSrc.getMessage("gt.error.general.session_timeout", null, locale), header,
						HttpStatus.UNAUTHORIZED);
			}
		} catch (Exception ex) {
			ex.printStackTrace();

			try {
				if (connection != null) {
					connection.rollback();
				}
			} catch (Exception ex2) {
			}

			header.setContentType(MediaType.TEXT_PLAIN);
			responseEntity = new ResponseEntity<>(msgSrc.getMessage("gt.error.general.system_error", null, locale),
					header, HttpStatus.INTERNAL_SERVER_ERROR);
		} finally {
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (Exception ex) {
			}
		}

		return responseEntity;
	}
}

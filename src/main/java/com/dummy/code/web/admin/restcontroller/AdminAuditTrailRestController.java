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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.dummy.code.general.util.DataTableUtil;
import com.dummy.code.properties.GeneralProperties;
import com.dummy.code.web.admin.dbutil.AdminAuditTrailDBUtil;
import com.dummy.code.web.admin.modal.AdminLoginSessionModal;
import com.dummy.code.web.admin.util.AdminAPIAccessUtil;
import com.dummy.code.web.admin.util.AdminLoginUtil;
import com.dummy.code.web.admin.util.AdminAPIAccessUtil.APIAccessType;

@RestController
@RequestMapping("/admin_api/audit_trail/")
public class AdminAuditTrailRestController {

	@Autowired
	GeneralProperties generalProperties;

	@Autowired
	DataSource dataSource;

	@Autowired
	MessageSource msgSrc;

	@RequestMapping(value = "get_audit_trail_init_data", method = { RequestMethod.POST })
	public ResponseEntity<?> AdminAPI_GetAuditTrailInitData(HttpServletRequest request, HttpServletResponse response,
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

					JSONArray omniuOrderTypeList = AdminAuditTrailDBUtil.getAuditTypeList(connection);
					resultObj.put("audit_type_list", omniuOrderTypeList);

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

	@RequestMapping(value = "get_audit_trail_list", method = { RequestMethod.POST })
	public ResponseEntity<?> AdminAPI_GetAuditTrailList(@RequestBody String requestBody, HttpServletRequest request,
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
						byte[] byteData = AdminAuditTrailDBUtil.doExportDT(connection, exportGlobalSearch,
								dtObj.getJSONArray("columns"), dtObj.getJSONArray("order"), exportType,
								dtObj.getBigDecimal("auditTypeID"), dtObj.getString("startDate"),
								dtObj.getString("endDate"));

						header.setContentType(MediaType.APPLICATION_OCTET_STREAM);
						responseEntity = new ResponseEntity<>(Base64.getEncoder().encodeToString(byteData), header,
								HttpStatus.OK);
					} else {
						JSONObject omniuOrderDT = AdminAuditTrailDBUtil.getAuditTrailDT(connection,
								new BigDecimal(dtObj.getString("draw")), new BigDecimal(dtObj.getString("length")),
								new BigDecimal(dtObj.getString("start")),
								dtObj.getJSONObject("search").getString("value"), dtObj.getJSONArray("columns"),
								dtObj.getJSONArray("order"), dtObj.getBigDecimal("auditTypeID"),
								dtObj.getString("startDate"), dtObj.getString("endDate"));

						header.setContentType(MediaType.APPLICATION_JSON);
						responseEntity = new ResponseEntity<>(omniuOrderDT.toString(), header, HttpStatus.OK);
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
}
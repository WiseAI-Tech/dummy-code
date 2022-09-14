package com.dummy.code.web.admin.restcontroller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.dummy.code.properties.GeneralProperties;
import com.dummy.code.web.admin.util.AdminLoginUtil;

@RestController
@RequestMapping("/admin_api/")
public class AdminMainRestController {

	@Autowired
	GeneralProperties generalProperties;

	@Autowired
	DataSource dataSource;

	@Autowired
	MessageSource msgSrc;

	@RequestMapping(value = "get_pwa_status", method = { RequestMethod.POST })
	public ResponseEntity<?> AdminAPI_GetPWAStatus(HttpServletRequest request, HttpServletResponse response) {
		JSONObject resultObj = new JSONObject();
		resultObj.put("isPWA", generalProperties.isAllowPwa());
		resultObj.put("pwaVersion", generalProperties.getPwaVersion());
		return new ResponseEntity<>(resultObj.toString(), HttpStatus.OK);
	}

	@RequestMapping(value = { "refresh_security_code" }, method = { RequestMethod.POST })
	public ResponseEntity<?> AdminAPI_RefreshSecurityCode(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();

		String sc = AdminLoginUtil.generateRandomSecurityString(10);
		String base64Image = AdminLoginUtil.getSecurityCodeBase64Image(sc);
		session.setAttribute(AdminLoginUtil.USER_SESSION_SECURITY_CODE, sc);
		session.setAttribute(AdminLoginUtil.USER_SESSION_SECURITY_CODE_IMAGE, base64Image);

		return new ResponseEntity<>("data:image/png;base64," + base64Image, HttpStatus.OK);
	}
}

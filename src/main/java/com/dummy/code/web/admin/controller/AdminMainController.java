package com.dummy.code.web.admin.controller;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.dummy.code.properties.GeneralProperties;
import com.dummy.code.web.admin.dbutil.AdminUserDBUtil;
import com.dummy.code.web.admin.modal.AdminLoginSessionModal;
import com.dummy.code.web.admin.util.AdminActionAccessUtil;
import com.dummy.code.web.admin.util.AdminLoginUtil;
import com.dummy.code.web.admin.util.AdminMenuUtil;
import com.dummy.code.web.admin.util.AdminPageAccessUtil;
import com.dummy.code.web.admin.util.AdminLoginUtil.LoginStatus;
import com.dummy.code.web.admin.util.AdminPageAccessUtil.PageAccessType;

@Controller
@RequestMapping("/admin/")
public class AdminMainController {

	@Autowired
	GeneralProperties generalProperties;

	@Autowired
	DataSource dataSource;

	@Autowired
	MessageSource msgSrc;

	@RequestMapping(value = { "" }, method = { RequestMethod.GET })
	public ModelAndView Admin_DefaultPage(HttpServletRequest request) {
		ModelAndView model = new ModelAndView();
		AdminLoginSessionModal loginSession = AdminLoginUtil.getUserSession(request);

		if (loginSession != null) {
			model.setViewName("redirect:/admin/home");
		} else {
			model.setViewName("redirect:/admin/login");
		}
		return model;
	}

	@RequestMapping(value = { "home" }, method = { RequestMethod.GET })
	public ModelAndView Admin_HomePage(HttpServletRequest request, Locale locale) {
		ModelAndView model = new ModelAndView();
		AdminLoginSessionModal loginSession = AdminLoginUtil.getUserSession(request);

		if (loginSession != null) {
			setupReturnModel(model, request, locale);
			model.setViewName("admin/home");
		} else {
			model.setViewName("redirect:/admin/login");
		}
		return model;
	}

	@RequestMapping(value = { "login" }, method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView Admin_LoginPage(HttpServletRequest request, Locale locale,
			@RequestParam(value = "loginID", required = false) String loginID,
			@RequestParam(value = "loginPassword", required = false) String loginPassword,
			@RequestParam(value = "securityCode", required = false) String securityCode) {
		ModelAndView model = new ModelAndView();
		AdminLoginSessionModal loginSession = AdminLoginUtil.getUserSession(request);

		HttpSession session = request.getSession(false);
		session.removeAttribute(AdminLoginUtil.USER_SESSION_LOGIN_ERR_NAME);

		if (session.getAttribute(AdminLoginUtil.USER_SESSION_FAILED_ATTEMPT) == null) {
			session.setAttribute(AdminLoginUtil.USER_SESSION_FAILED_ATTEMPT, 0);
		}

		if (loginSession != null) {
			model.setViewName("redirect:/admin/home");
		} else {
			if (loginID != null && loginPassword != null) {
				boolean isPassSecurityCode = false;
				if (session.getAttribute(AdminLoginUtil.USER_SESSION_SECURITY_CODE) != null) {
					if (securityCode == null || securityCode.isEmpty()) {
						session.setAttribute(AdminLoginUtil.USER_SESSION_LOGIN_ERR_NAME,
								msgSrc.getMessage("gt.error.login.security_code_required", null, locale));
						setupReturnModel(model, request, locale);
						model.setViewName("admin/login");
					} else {
						if (AdminLoginUtil.isSecurityCodeMatch(
								session.getAttribute(AdminLoginUtil.USER_SESSION_SECURITY_CODE).toString(),
								securityCode)) {
							isPassSecurityCode = true;
						} else {
							session.setAttribute(AdminLoginUtil.USER_SESSION_LOGIN_ERR_NAME,
									msgSrc.getMessage("gt.error.login.invalid_security_code", null, locale));
							setupReturnModel(model, request, locale);
							model.setViewName("admin/login");
						}
					}
				} else {
					isPassSecurityCode = true;
				}

				if (isPassSecurityCode) {
					AdminLoginSessionModal authLoginSession = AdminLoginUtil.authenticateSession(dataSource, loginID,
							loginPassword);
					if (authLoginSession != null) {
						if (authLoginSession.getLoginStatus() == LoginStatus.PENDING.getValue()) {
							model.addObject("generalData", generalProperties);
							session.setAttribute(AdminLoginUtil.USER_SESSION_LOGIN_ERR_NAME,
									msgSrc.getMessage("gt.error.login.activation_required", null, locale));
							setupReturnModel(model, request, locale);
							model.setViewName("admin/login");
						} else if (authLoginSession.getLoginStatus() == LoginStatus.ACTIVE.getValue()) {
							AdminLoginUtil.setLoginFailedAttempt(dataSource, loginID, 0);
							AdminLoginUtil.setUpdateLoginTime(dataSource, loginID);

							session.setAttribute(AdminLoginUtil.USER_SESSION_NAME, authLoginSession);
							session.setAttribute(AdminLoginUtil.USER_SESSION_FAILED_ATTEMPT, 0);
							model.setViewName("redirect:/admin/home");
						} else if (authLoginSession.getLoginStatus() == LoginStatus.SUSPENDED.getValue()) {
							model.addObject("generalData", generalProperties);
							session.setAttribute(AdminLoginUtil.USER_SESSION_LOGIN_ERR_NAME,
									msgSrc.getMessage("gt.error.login.account_suspended", null, locale));
							setupReturnModel(model, request, locale);
							model.setViewName("admin/login");
						}
					} else {
						session.setAttribute(AdminLoginUtil.USER_SESSION_FAILED_ATTEMPT,
								(int) session.getAttribute(AdminLoginUtil.USER_SESSION_FAILED_ATTEMPT) + 1);

						int loginFailedAttempt = AdminLoginUtil.getLoginFailedAttempt(dataSource, loginID);
						if (loginFailedAttempt >= 0) {
							AdminLoginUtil.setLoginFailedAttempt(dataSource, loginID, loginFailedAttempt + 1);
						}

						model.addObject("generalData", generalProperties);
						session.setAttribute(AdminLoginUtil.USER_SESSION_LOGIN_ERR_NAME,
								msgSrc.getMessage("gt.error.login.invalid_username_password", null, locale));
						setupReturnModel(model, request, locale);
						model.setViewName("admin/login");
					}
				}
			} else {
				setupReturnModel(model, request, locale);
				model.setViewName("admin/login");
			}
		}

		if ((int) session.getAttribute(AdminLoginUtil.USER_SESSION_FAILED_ATTEMPT) >= 3) {
			String sc = AdminLoginUtil.generateRandomSecurityString(10);
			session.setAttribute(AdminLoginUtil.USER_SESSION_SECURITY_CODE, sc);
			session.setAttribute(AdminLoginUtil.USER_SESSION_SECURITY_CODE_IMAGE,
					AdminLoginUtil.getSecurityCodeBase64Image(sc));
		} else {
			session.removeAttribute(AdminLoginUtil.USER_SESSION_SECURITY_CODE);
			session.removeAttribute(AdminLoginUtil.USER_SESSION_SECURITY_CODE_IMAGE);
		}

		return model;
	}

	@RequestMapping(value = { "logout" }, method = { RequestMethod.GET })
	public ModelAndView Admin_Logout(HttpServletRequest request) {
		ModelAndView model = new ModelAndView();

		AdminLoginUtil.deauthenticateSession(request);
		model.setViewName("redirect:/admin/login");

		return model;
	}

	@RequestMapping(value = { "views/{viewPage}" }, method = { RequestMethod.GET })
	public ModelAndView Admin_ViewControl(HttpServletRequest request, @PathVariable("viewPage") String viewPage,
			Locale locale) {
		ModelAndView model = new ModelAndView();
		AdminLoginSessionModal loginSession = AdminLoginUtil.getUserSession(request);

		if (loginSession != null) {
			PageAccessType pageAccessType = AdminPageAccessUtil.getAccessType(dataSource, viewPage,
					loginSession.getSystemID());
			if (pageAccessType == PageAccessType.ALLOW) {
				model.setViewName("admin/views/" + viewPage);
			} else if (pageAccessType == PageAccessType.DENIED) {
				model.setViewName("admin/views/" + AdminPageAccessUtil.PAGE_ACCESS_DENIED);
			} else if (pageAccessType == PageAccessType.NOT_FOUND) {
				model.setViewName("admin/views/" + AdminPageAccessUtil.PAGE_404_NOT_FOUND);
			} else if (pageAccessType == PageAccessType.UNDER_DEVELOPMENT) {
				model.setViewName("admin/views/" + AdminPageAccessUtil.PAGE_UNDER_DEVELOPMENT);
			} else if (pageAccessType == PageAccessType.UNDER_MAINTENANCE) {
				model.setViewName("admin/views/" + AdminPageAccessUtil.PAGE_UNDER_MAINTENANCE);
			} else {
				model.setViewName("admin/views/" + AdminPageAccessUtil.PAGE_ERROR);
			}

			setupReturnModel(model, request, locale);
		} else {
			model.setViewName("admin/views/" + AdminPageAccessUtil.PAGE_SESSION_EXPIRED);
		}

		return model;
	}

	public void setupReturnModel(ModelAndView model, HttpServletRequest request, Locale locale) {
		// Never append this to setViewName redirect: or it will append on URL
		AdminLoginSessionModal loginSession = AdminLoginUtil.getUserSession(request);

		model.addObject("isAllowPWA", generalProperties.isAllowPwa());
		if (loginSession != null) {
			model.addObject("mainMenuData",
					AdminMenuUtil.getMainMenuAccess(dataSource, msgSrc, locale, loginSession.getSystemID()));
			model.addObject("actionAccessData",
					AdminActionAccessUtil.getActionAccess(dataSource, loginSession.getSystemID()));
			model.addObject("displayName", loginSession.getLoginName());
		}
	}
}

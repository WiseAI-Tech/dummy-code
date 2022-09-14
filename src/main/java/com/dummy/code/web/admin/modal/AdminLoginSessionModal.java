package com.dummy.code.web.admin.modal;

import java.math.BigDecimal;

public class AdminLoginSessionModal {

	private BigDecimal systemID;
	private String loginID;
	private String loginName;
	private int roleID;
	private int loginStatus;

	public BigDecimal getSystemID() {
		return systemID;
	}

	public void setSystemID(BigDecimal systemID) {
		this.systemID = systemID;
	}

	public String getLoginID() {
		return loginID;
	}

	public void setLoginID(String loginID) {
		this.loginID = loginID;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public int getRoleID() {
		return roleID;
	}

	public void setRoleID(int roleID) {
		this.roleID = roleID;
	}

	public int getLoginStatus() {
		return loginStatus;
	}

	public void setLoginStatus(int loginStatus) {
		this.loginStatus = loginStatus;
	}
}

package com.dummy.code.web.admin.util;

public class AdminUserUtil {

	public enum UserStatus {
		PENDING(1), ACTIVE(2), SUSPENDED(3), TERMINATED(4);

		private final int value;

		UserStatus(final int newValue) {
			value = newValue;
		}

		public int getValue() {
			return value;
		}
	}
}

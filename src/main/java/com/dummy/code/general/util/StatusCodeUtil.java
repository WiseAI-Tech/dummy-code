package com.dummy.code.general.util;

public class StatusCodeUtil {
	public enum ResponseStatus {
		SUCCESS("00"), RETRIEVE_FAILED("E01"), CREATE_FAILED("E02"), UPDATE_FAILED("E03"), DELETE_FAILED("E04"),
		RESET_FAILED("E05");

		private final String value;

		ResponseStatus(final String newValue) {
			value = newValue;
		}

		public String getValue() {
			return value;
		}
	}
}

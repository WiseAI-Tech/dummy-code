package com.dummy.code.general.util;

import java.util.regex.Pattern;

public class GeneralUtil {
	public static boolean isValidEmail(String email) {
		String regex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
		Pattern pattern = Pattern.compile(regex);
		return pattern.matcher(email).matches();
	}

	public static boolean isNumberOnly(String numberCheck) {
		String regex = "\\d+";
		Pattern pattern = Pattern.compile(regex);
		return pattern.matcher(numberCheck).matches();
	}
}
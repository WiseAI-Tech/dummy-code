package com.dummy.code.general.util;

public class GeneratorUtil {
	public static String generateRandomAlphaNumeric(boolean isLowerCase, boolean isUpperCase, boolean isNumeric,
			int stringLength) throws Exception {
		String lowerCaseString = "abcdefghijklmnopqrstuvwxyz";
		String upperCaseString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String numericString = "0123456789";

		String generationString = "";
		String generatedString = "";

		if (!isLowerCase && !isUpperCase && !isNumeric) {
			throw new Exception("Must allow at least one type of generation type");
		} else if (stringLength <= 0) {
			throw new Exception("Length cannot be less than 0");
		}

		if (isLowerCase) {
			generationString += lowerCaseString;
		}
		if (isUpperCase) {
			generationString += upperCaseString;
		}
		if (isNumeric) {
			generationString += numericString;
		}

		for (int x = 0; x < stringLength; x++) {
			generatedString += generationString.charAt((int) (Math.random() * generationString.length()));
		}

		return generatedString;
	}

	public static String generatAuthCode(int stringLength) throws Exception {
		String numericString = "0123456789";
		String generatedString = "AU-";

		for (int x = 0; x < stringLength; x++) {
			generatedString += numericString.charAt((int) (Math.random() * numericString.length()));
		}

		return generatedString;
	}

	public static String generateOTP(int stringLength) throws Exception {
		String numericString = "0123456789";
		String generatedString = "";

		for (int x = 0; x < stringLength; x++) {
			generatedString += numericString.charAt((int) (Math.random() * numericString.length()));
		}

		return generatedString;
	}

	public static String generateForgetPasswordToken(long uniqueID) throws Exception {
		String tokenString = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String generatedString = Long.toHexString(uniqueID);

		for (int x = 0; x < 4; x++) {
			generatedString += "-";
			for (int y = 0; y < 8; y++) {
				generatedString += tokenString.charAt((int) (Math.random() * tokenString.length()));
			}
		}

		return generatedString;
	}
}

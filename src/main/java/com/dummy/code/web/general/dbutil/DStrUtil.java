package com.dummy.code.web.general.dbutil;

import java.math.BigInteger;

public class DStrUtil {
	public static boolean getDStrValue(String accessDStr, int dstrPos) {
		String decodedAccessDStr = dStrHexToBin(accessDStr);

		if (dstrPos >= decodedAccessDStr.length()) {
			return false;
		} else {
			return decodedAccessDStr.charAt(dstrPos) == '1';
		}
	}

	public static String dStrHexToBin(String accessDStr) {
		String fullStr = "";
		for (int x = 0; x < accessDStr.length(); x++) {
			fullStr += String.format("%04d",
					Integer.parseInt(new BigInteger(String.valueOf(accessDStr.charAt(x)), 16).toString(2)));
		}
		return fullStr;
	}

	public static String dStrBinToHex(String accessDStr) {
		while (accessDStr.length() % 4 != 0) {
			accessDStr += "0";
		}

		String fullStr = "";
		for (int x = 0; x < accessDStr.length(); x += 4) {
			fullStr += new BigInteger(accessDStr.substring(x, x + 4), 2).toString(16).toUpperCase();
		}
		return fullStr;
	}
}

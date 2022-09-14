package com.dummy.code.general.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.bind.DatatypeConverter;

public class SecureHashUtil {

	private static final char[] HEX_TABLE = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B',
			'C', 'D', 'E', 'F' };

	public static String generateSecureHash(String mdInstance, String originalString) {
		MessageDigest md = null;
		byte[] ba = null;
		try {
			md = MessageDigest.getInstance(mdInstance);
			ba = md.digest(originalString.getBytes("ISO-8859-1"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return hex(ba);
	}

	private static String hex(byte[] input) {
		StringBuffer sb = new StringBuffer(input.length * 2);
		for (int i = 0; i < input.length; i++) {
			sb.append(HEX_TABLE[(input[i] >> 4) & 0xf]);
			sb.append(HEX_TABLE[input[i] & 0xf]);
		}
		return sb.toString();
	}

	public static String toSHA256(String... fields) {
		StringBuffer sb = null;
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			sb = new StringBuffer();
			for (String field : fields) {
				sb.append(field.trim());
			}
			md.update(sb.toString().getBytes("utf-8"));
			byte[] mdbytes = md.digest();
			return DatatypeConverter.printHexBinary(mdbytes).toLowerCase();
		} catch (NoSuchAlgorithmException e) {
			sb = null;
		} catch (UnsupportedEncodingException e) {
			sb = null;
		}
		return sb == null ? null : sb.toString();
	}

	public static String toSHA512(String... fields) {
		StringBuffer sb = null;
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-512");
			sb = new StringBuffer();
			for (String field : fields) {
				sb.append(field.trim());
			}
			md.update(sb.toString().getBytes("utf-8"));
			byte[] mdbytes = md.digest();
			return DatatypeConverter.printHexBinary(mdbytes).toLowerCase();
		} catch (NoSuchAlgorithmException e) {
			sb = null;
		} catch (UnsupportedEncodingException e) {
			sb = null;
		}
		return sb == null ? null : sb.toString();
	}

	public static String toMD5(String... fields) {
		StringBuffer sb = null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			sb = new StringBuffer();
			for (String field : fields) {
				sb.append(field.trim());
			}
			md.update(sb.toString().getBytes("utf-8"));
			byte[] mdbytes = md.digest();
			return DatatypeConverter.printHexBinary(mdbytes).toLowerCase();
		} catch (NoSuchAlgorithmException e) {
			sb = null;
		} catch (UnsupportedEncodingException e) {
			sb = null;
		}
		return sb == null ? null : sb.toString();
	}
}

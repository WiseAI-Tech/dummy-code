package tech.usdtech.omniu;

import com.dummy.code.general.util.EncryptionUtil;

public class EncryptDecrypt {
	public static void main(String[] args) {
		System.out.println("Encrypted String: " + EncryptionUtil.encrypt("test"));
		System.out.println("Decrypted String: " + EncryptionUtil.decrypt("RxeLlBGFbN4oA1AhqLsPVQ=="));
	}
}

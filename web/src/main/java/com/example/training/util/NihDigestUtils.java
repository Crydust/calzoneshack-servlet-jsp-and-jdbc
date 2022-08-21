package com.example.training.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/**
 * This class should be replaced by "org.apache.commons.codec.digest.DigestUtils".
 */
public final class NihDigestUtils {
	private NihDigestUtils() {
		throw new UnsupportedOperationException();
	}

	public static String sha256Hex(byte[] data) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			return HexFormat.of().formatHex(digest.digest(data));
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}
}

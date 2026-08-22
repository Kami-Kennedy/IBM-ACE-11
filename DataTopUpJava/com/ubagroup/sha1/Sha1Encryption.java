package com.ubagroup.sha1;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

public class Sha1Encryption {
	
	public static void main(String[] args) {
		// Test with your credentials
		String result = getSecurePassword("e?TMV@*9ybs}e'?");
		System.out.println("Password Hash: " + result.substring(0, result.indexOf(".")));
		System.out.println("Salt: " + result.substring(result.indexOf(".") + 1));
	}

	/**
	 * Encrypt password using SHA-1
	 * @param password - the clear text password
	 * @return SHA-1 hash of the password
	 */
	public static String encryptPassword(String password) {
		String sha1 = "";
		try {
			MessageDigest crypt = MessageDigest.getInstance("SHA-1");
			crypt.reset();
			crypt.update(password.getBytes("UTF-8"));
			sha1 = byteToHex(crypt.digest());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return sha1;
	}

	/**
	 * Convert byte array to hexadecimal string
	 */
	private static String byteToHex(final byte[] hash) {
		Formatter formatter = new Formatter();
		for (byte b : hash) {
			formatter.format("%02x", b);
		}
		String result = formatter.toString();
		formatter.close();
		return result;
	}

	/**
	 * Generate secure password hash following the new algorithm:
	 * 1. Generate SHA-1 hash of clear text password
	 * 2. Concatenate salt + SHA-1 hash
	 * 3. Generate SHA-512 hash of the concatenated string
	 * 
	 * @param password - the clear text password
	 * @return finalPasswordHash.salt
	 */
	public static String getSecurePassword(String password) {
		String saltString = getSalt();
		saltString = padString(saltString, 40, '0');
		
		String generatedPassword = null;
		try {
			// Step 1: Generate SHA-1 hash of the clear text password
			String passwordSha1 = generateHash("SHA-1", password);
			
			// Step 2: Concatenate salt + SHA-1 hash
			String saltAndPassword = saltString + passwordSha1;
			
			// Step 3: Generate SHA-512 hash of the concatenated string
			generatedPassword = generateHash("SHA-512", saltAndPassword);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// Return format: passwordHash.salt
		return generatedPassword + "." + saltString;
	}

	/**
	 * Generate the hash of the string using the specified digest type
	 * 
	 * @param digestType - the digest type (algorithm) to use (SHA-1 or SHA-512)
	 * @param stringToHash - the string to be hashed
	 * @return a hash of the string formatted as 40 character hex string
	 * @throws Exception
	 */
	private static String generateHash(String digestType, String stringToHash) throws Exception {
		MessageDigest digest = MessageDigest.getInstance(digestType);
		digest.reset();
		digest.update(stringToHash.getBytes(StandardCharsets.UTF_8));
		return String.format("%040x", new BigInteger(1, digest.digest()));
	}

	/**
	 * Pad string to specified length with given character
	 */
	public static String padString(String input, int length, char padChar) {
		if (input.length() >= length) {
			return input.substring(0, length); // Truncate if input is too long
		}
		
		StringBuilder sb = new StringBuilder(input);
		while (sb.length() < length) {
			sb.append(padChar);
		}
		return sb.toString();
	}

	/**
	 * Generate a random salt (40 hex characters)
	 */
	public static String getSalt() {
		SecureRandom random = new SecureRandom();
		byte[] saltBytes = new byte[20];
		random.nextBytes(saltBytes);
		StringBuilder saltBuilder = new StringBuilder();
		for (byte b : saltBytes) {
			saltBuilder.append(String.format("%02x", b));
		}
		return saltBuilder.toString();
	}
}

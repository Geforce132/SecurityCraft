package net.geforcemods.securitycraft.util;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import net.minecraft.nbt.CompoundTag;

public class PasscodeUtils {
	public static CompoundTag filterPasscodeAndSaltFromTag(CompoundTag tag) {
		tag.remove("passcode");
		tag.remove("saltKey");
		return tag;
	}

	public static String hashPasscodeWithoutSalt(String original) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-512");

			return bytesToString(md.digest(original.getBytes(StandardCharsets.UTF_8)));
		}
        catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static byte[] hashPasscode(String passcode, byte[] salt) {
		try {
			KeySpec spec = new PBEKeySpec(passcode.toCharArray(), salt, 65536, 128);
			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

			return factory.generateSecret(spec).getEncoded();
		}
        catch (GeneralSecurityException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static String bytesToString(byte[] bytes) {
		if (bytes == null)
			return "";

		StringBuilder sb = new StringBuilder();

		for (byte currentByte : bytes) {
			sb.append(Integer.toString((currentByte & 0xFF) + 0x100, 16).substring(1));
		}

		return sb.toString();
	}

	public static byte[] stringToBytes(String string) {
		if (string == null || string.isEmpty())
			return null;

		byte[] bytes = new byte[string.length() / 2];

		for (int i = 0; i < string.length() / 2; i++) {
			int index = i * 2;

			int parsedInt = Integer.parseInt(string.substring(index, index + 2), 16);
			bytes[i] = (byte) parsedInt;
		}

		return bytes;
	}

	public static byte[] generateSalt() {
		SecureRandom random = new SecureRandom();
		byte[] salt = new byte[16];

		random.nextBytes(salt);
		return salt;
	}
}

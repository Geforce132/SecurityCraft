package net.geforcemods.securitycraft.util;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import net.minecraft.nbt.CompoundTag;

public class PasscodeUtils {
	private static HashingThread hashingThread;

	public static void startHashingThread(Executor executor) {
		hashingThread = new HashingThread(executor);
		hashingThread.start();
	}

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

	public static void hashPasscode(String passcode, byte[] salt, Consumer<byte[]> afterHashing) {
		hashingThread.workList.addLast(new HashingWork(passcode, salt, afterHashing));
	}

	private static byte[] hashPasscode(String passcode, byte[] salt) {
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

	private static class HashingThread extends Thread {
		private double sleepOverhead = 0.0D;
		private final ConcurrentLinkedDeque<HashingWork> workList = new ConcurrentLinkedDeque<>();
		private final Executor mainExecutor;

		private HashingThread(Executor mainExecutor) {
			this.mainExecutor = mainExecutor;
		}

		@Override
		public void run() {
			while (!isInterrupted()) {
				try {
					long start = System.nanoTime();

					if (!workList.isEmpty()) {
						HashingWork work = workList.pop();
						byte[] hash = hashPasscode(work.passcode, work.salt);

						mainExecutor.execute(() -> work.afterHashing.accept(hash));
					}

					double d = (System.nanoTime() - start) / 1_000_000.0D + sleepOverhead;
					long sleepTime = 10 - (long) d;

					sleepOverhead = d % 1.0D;

					if (sleepTime > 0)
						Thread.sleep(sleepTime);
				}
				catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		}
	}

	private record HashingWork(String passcode, byte[] salt, Consumer<byte[]> afterHashing) {}
}

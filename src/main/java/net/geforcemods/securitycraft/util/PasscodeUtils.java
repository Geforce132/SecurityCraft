package net.geforcemods.securitycraft.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import net.geforcemods.securitycraft.ConfigHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;

public class PasscodeUtils {
	private static final SecureRandom SECURE_RANDOM = new SecureRandom();
	private static HashingThread hashingThread;
	private static final Map<PlayerEntity, Long> LAST_PASSCODE_CHECKS = new HashMap<>();

	private PasscodeUtils() {}

	public static void startHashingThread(Executor executor) {
		if (hashingThread == null) {
			hashingThread = new HashingThread(executor);
			hashingThread.start();
		}
	}

	public static void stopHashingThread() {
		if (hashingThread != null) {
			hashingThread.interrupt();
			hashingThread = null;
		}
	}

	public static CompoundNBT filterPasscodeAndSaltFromTag(CompoundNBT tag) {
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
		if (passcode != null && salt != null)
			hashingThread.workList.addLast(new HashingWork(passcode, salt, afterHashing));
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
		byte[] salt = new byte[16];

		SECURE_RANDOM.nextBytes(salt);
		return salt;
	}

	public static void setOnCooldown(PlayerEntity player) {
		LAST_PASSCODE_CHECKS.put(player, System.currentTimeMillis());
	}

	public static boolean isOnCooldown(PlayerEntity player) {
		if (!LAST_PASSCODE_CHECKS.containsKey(player) || System.currentTimeMillis() > LAST_PASSCODE_CHECKS.get(player) + ConfigHandler.SERVER.passcodeCheckCooldown.get()) {
			LAST_PASSCODE_CHECKS.remove(player);
			return false;
		}

		return true;
	}

	private static class HashingThread extends Thread {
		private double sleepOverhead = 0.0D;
		private final ConcurrentLinkedDeque<HashingWork> workList = new ConcurrentLinkedDeque<>();
		private final Executor mainExecutor;

		private HashingThread(Executor mainExecutor) {
			this.mainExecutor = mainExecutor;
			setDaemon(true);
			setName("SecurityCraft Passcode Hashing");
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
					interrupt();
				}
			}
		}

		private static byte[] hashPasscode(String passcode, byte[] salt) {
			try {
				KeySpec spec = new PBEKeySpec(passcode.toCharArray(), salt, 65536, 128);
				SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

				return factory.generateSecret(spec).getEncoded();
			}
			catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}
	}

	private static class HashingWork {
		private final String passcode;
		private final byte[] salt;
		private final Consumer<byte[]> afterHashing;

		private HashingWork(String passcode, byte[] salt, Consumer<byte[]> afterHashing) {
			this.passcode = passcode;
			this.salt = salt;
			this.afterHashing = afterHashing;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;

			if (obj == null || getClass() != obj.getClass())
				return false;

			HashingWork hashingWork = (HashingWork) obj;

			return passcode != null && passcode.equals(hashingWork.passcode) && Arrays.equals(salt, hashingWork.salt);
		}

		@Override
		public int hashCode() {
			return 31 * passcode.hashCode() + Arrays.hashCode(salt);
		}

		@Override
		public String toString() {
			return "HashingWork{" + "passcode=" + passcode + ", salt=" + Arrays.toString(salt) + "}";
		}
	}
}

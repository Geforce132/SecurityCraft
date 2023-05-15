package net.geforcemods.securitycraft.util;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

public class Utils {
	public static final Style GRAY_STYLE = Style.EMPTY.withColor(ChatFormatting.GRAY);
	public static final Component INVENTORY_TEXT = Utils.localize("container.inventory");

	/**
	 * Removes the last character in the given String. <p>
	 */
	public static String removeLastChar(String line) {
		if (line == null || line.isEmpty())
			return "";

		return line.substring(0, line.length() - 1);
	}

	public static Component getFormattedCoordinates(BlockPos pos) {
		return Component.translatable("messages.securitycraft:formattedCoordinates", pos.getX(), pos.getY(), pos.getZ());
	}

	/**
	 * Localizes a String with the given format
	 *
	 * @param key The string to localize (aka the identifier in the .lang file)
	 * @param params The parameters to insert into the String ala String.format
	 * @return The localized String
	 */
	public static MutableComponent localize(String key, Object... params) {
		for (int i = 0; i < params.length; i++) {
			if (params[i] instanceof Component component && component.getContents() instanceof TranslatableContents translatableContents)
				params[i] = localize(translatableContents.getKey(), translatableContents.getArgs());
			else if (params[i] instanceof BlockPos pos)
				params[i] = getFormattedCoordinates(pos);
		}

		return Component.translatable(key, params);
	}

	public static CompoundTag filterPasscodeAndSaltFromTag(CompoundTag tag) {
		tag.remove("passcode");
		tag.remove("salt");
		return tag;
	}

	public static String hashPasscodeWithoutSalt(String original) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-512");

			return Utils.bytesToString(md.digest(original.getBytes(StandardCharsets.UTF_8)));
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

	public static ResourceLocation getRegistryName(Block block) {
		return ForgeRegistries.BLOCKS.getKey(block);
	}

	public static ResourceLocation getRegistryName(EntityType<?> entityType) {
		return ForgeRegistries.ENTITY_TYPES.getKey(entityType);
	}

	public static ResourceLocation getRegistryName(Item item) {
		return ForgeRegistries.ITEMS.getKey(item);
	}

	public static ResourceLocation getRegistryName(Potion potion) {
		return ForgeRegistries.POTIONS.getKey(potion);
	}
}

package net.geforcemods.securitycraft.components;

import java.util.Arrays;
import java.util.UUID;
import java.util.function.Consumer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.misc.SaltData;
import net.geforcemods.securitycraft.util.PasscodeUtils;
import net.minecraft.core.UUIDUtil;
import net.minecraft.world.item.ItemStack;

public record PasscodeData(String passcode, UUID saltKey) {
	//@formatter:off
	public static final Codec<PasscodeData> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
					Codec.STRING.fieldOf("passcode").forGetter(PasscodeData::passcode),
					UUIDUtil.CODEC.fieldOf("salt_key").forGetter(PasscodeData::saltKey))
			.apply(instance, PasscodeData::new));
	//@formatter:on

	public void checkPasscode(ItemStack stack, String codeToCheck, Runnable ifTrue) {
		byte[] salt = SaltData.getSalt(saltKey);

		if (salt == null) { //If no salt associated with the given key can be found, a new passcode needs to be set
			stack.remove(SCContent.PASSCODE_DATA);
			return;
		}

		PasscodeUtils.hashPasscode(codeToCheck, salt, hashedCode -> {
			if (Arrays.equals(PasscodeUtils.stringToBytes(passcode), hashedCode))
				ifTrue.run();
		});
	}

	public static void hashAndSetPasscode(ItemStack stack, String passcode, Consumer<PasscodeData> afterSet) {
		byte[] salt = PasscodeUtils.generateSalt();
		UUID saltKey = SaltData.putSalt(salt);

		PasscodeUtils.hashPasscode(passcode, salt, hashedCode -> {
			PasscodeData passcodeData = new PasscodeData(PasscodeUtils.bytesToString(hashedCode), saltKey);

			stack.set(SCContent.PASSCODE_DATA, passcodeData);
			afterSet.accept(passcodeData);
		});
	}
}

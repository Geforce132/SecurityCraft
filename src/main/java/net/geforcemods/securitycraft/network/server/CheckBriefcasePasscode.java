package net.geforcemods.securitycraft.network.server;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.components.PasscodeData;
import net.geforcemods.securitycraft.items.BriefcaseItem;
import net.geforcemods.securitycraft.util.PasscodeUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record CheckBriefcasePasscode(String passcode) implements CustomPacketPayload {
	public static final Type<CheckBriefcasePasscode> TYPE = new Type<>(SecurityCraft.resLoc("check_briefcase_passcode"));
	//@formatter:off
	public static final StreamCodec<RegistryFriendlyByteBuf, CheckBriefcasePasscode> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.STRING_UTF8, packet -> PasscodeUtils.hashPasscodeWithoutSalt(packet.passcode),
			CheckBriefcasePasscode::new);
	//@formatter:on

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public void handle(IPayloadContext ctx) {
		Player player = ctx.player();
		ItemStack briefcase = PlayerUtils.getItemStackFromAnyHand(player, SCContent.BRIEFCASE.get());
		PasscodeData passcodeData = briefcase.get(SCContent.PASSCODE_DATA);

		if (passcodeData != null) {
			if (PasscodeUtils.isOnCooldown(player)) {
				PlayerUtils.sendMessageToPlayer(player, Component.literal("SecurityCraft"), Component.translatable("messages.securitycraft:passcodeProtected.onCooldown"), ChatFormatting.RED);

				if (ConfigHandler.SERVER.passcodeSpamLogWarningEnabled.get())
					SecurityCraft.LOGGER.warn(String.format(ConfigHandler.SERVER.passcodeSpamLogWarning.get(), player.getGameProfile().name(), SCContent.BRIEFCASE.get().getName().getString(), new GlobalPos(player.level().dimension(), player.blockPosition())));

				return;
			}

			String dataCode = passcodeData.passcode();

			if (dataCode.length() == 4) //If an old plaintext passcode is encountered, generate and check with the hashed variant
				PasscodeData.hashAndSetPasscode(briefcase, PasscodeUtils.hashPasscodeWithoutSalt(dataCode), newPasscodeData -> BriefcaseItem.checkPasscode(player, briefcase, passcode, newPasscodeData));
			else
				BriefcaseItem.checkPasscode(player, briefcase, passcode, passcodeData);
		}
	}
}

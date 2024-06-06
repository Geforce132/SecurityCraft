package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.items.BriefcaseItem;
import net.geforcemods.securitycraft.util.PasscodeUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

public class CheckBriefcasePasscode {
	private String passcode;

	public CheckBriefcasePasscode() {}

	public CheckBriefcasePasscode(String passcode) {
		this.passcode = PasscodeUtils.hashPasscodeWithoutSalt(passcode);
	}

	public CheckBriefcasePasscode(FriendlyByteBuf buf) {
		passcode = buf.readUtf();
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeUtf(passcode);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		ServerPlayer player = ctx.get().getSender();
		ItemStack briefcase = PlayerUtils.getItemStackFromAnyHand(player, SCContent.BRIEFCASE.get());

		if (!briefcase.isEmpty()) {
			if (PasscodeUtils.isOnCooldown(player)) {
				PlayerUtils.sendMessageToPlayer(player, Component.literal("SecurityCraft"), Component.translatable("messages.securitycraft:passcodeProtected.onCooldown"), ChatFormatting.RED);
				SecurityCraft.LOGGER.warn(String.format(ConfigHandler.SERVER.passcodeSpamLogWarning.get(), player.getGameProfile().getName(), SCContent.BRIEFCASE.get().getDescription().getString(), GlobalPos.of(player.level.dimension(), player.blockPosition())));
				return;
			}

			CompoundTag tag = briefcase.getOrCreateTag();
			String tagCode = tag.getString("passcode");

			if (tagCode.length() == 4) //If an old plaintext passcode is encountered, generate and check with the hashed variant
				BriefcaseItem.hashAndSetPasscode(tag, PasscodeUtils.hashPasscodeWithoutSalt(tagCode), p -> BriefcaseItem.checkPasscode(player, briefcase, passcode, PasscodeUtils.bytesToString(p), tag));
			else
				BriefcaseItem.checkPasscode(player, briefcase, passcode, tagCode, tag);
		}
	}
}

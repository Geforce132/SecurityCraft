package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.items.BriefcaseItem;
import net.geforcemods.securitycraft.util.PasscodeUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

public class CheckBriefcasePasscode {
	private String passcode;

	public CheckBriefcasePasscode() {}

	public CheckBriefcasePasscode(String passcode) {
		this.passcode = PasscodeUtils.hashPasscodeWithoutSalt(passcode);
	}

	public static void encode(CheckBriefcasePasscode message, FriendlyByteBuf buf) {
		buf.writeUtf(message.passcode);
	}

	public static CheckBriefcasePasscode decode(FriendlyByteBuf buf) {
		CheckBriefcasePasscode message = new CheckBriefcasePasscode();

		message.passcode = buf.readUtf();
		return message;
	}

	public static void onMessage(CheckBriefcasePasscode message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayer player = ctx.get().getSender();
			ItemStack briefcase = PlayerUtils.getSelectedItemStack(player, SCContent.BRIEFCASE.get());
			String passcode = message.passcode;

			if (!briefcase.isEmpty()) {
				CompoundTag tag = briefcase.getOrCreateTag();
				String tagCode = tag.getString("passcode");

				if (tagCode.length() == 4) //If an old plaintext passcode is encountered, generate and check with the hashed variant
					BriefcaseItem.hashAndSetPasscode(tag, PasscodeUtils.hashPasscodeWithoutSalt(tagCode), p -> BriefcaseItem.checkPasscode(player, briefcase, passcode, PasscodeUtils.bytesToString(p), tag));
				else
					BriefcaseItem.checkPasscode(player, briefcase, passcode, tagCode, tag);
			}
		});

		ctx.get().setPacketHandled(true);
	}
}

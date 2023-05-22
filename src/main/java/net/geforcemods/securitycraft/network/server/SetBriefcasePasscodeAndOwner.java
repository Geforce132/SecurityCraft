package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.items.BriefcaseItem;
import net.geforcemods.securitycraft.util.PasscodeUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

public class SetBriefcasePasscodeAndOwner {
	private String passcode;

	public SetBriefcasePasscodeAndOwner() {}

	public SetBriefcasePasscodeAndOwner(String passcode) {
		this.passcode = passcode.isEmpty() ? passcode : PasscodeUtils.hashPasscodeWithoutSalt(passcode);
	}

	public static void encode(SetBriefcasePasscodeAndOwner message, FriendlyByteBuf buf) {
		buf.writeUtf(message.passcode);
	}

	public static SetBriefcasePasscodeAndOwner decode(FriendlyByteBuf buf) {
		SetBriefcasePasscodeAndOwner message = new SetBriefcasePasscodeAndOwner();

		message.passcode = buf.readUtf();
		return message;
	}

	public static void onMessage(SetBriefcasePasscodeAndOwner message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			Player player = ctx.get().getSender();
			ItemStack stack = PlayerUtils.getSelectedItemStack(player, SCContent.BRIEFCASE.get());

			if (!stack.isEmpty()) {
				CompoundTag tag = stack.getOrCreateTag();

				if (!tag.contains("owner")) {
					tag.putString("owner", player.getName().getString());
					tag.putString("ownerUUID", player.getUUID().toString());
				}

				if (!message.passcode.isEmpty() && !tag.contains("passcode"))
					BriefcaseItem.hashAndSetPasscode(tag, message.passcode);
			}
		});

		ctx.get().setPacketHandled(true);
	}
}

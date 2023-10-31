package net.geforcemods.securitycraft.network.server;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.items.BriefcaseItem;
import net.geforcemods.securitycraft.util.PasscodeUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.NetworkEvent;

public class SetBriefcasePasscodeAndOwner {
	private String passcode;

	public SetBriefcasePasscodeAndOwner() {}

	public SetBriefcasePasscodeAndOwner(String passcode) {
		this.passcode = passcode.isEmpty() ? passcode : PasscodeUtils.hashPasscodeWithoutSalt(passcode);
	}

	public SetBriefcasePasscodeAndOwner(FriendlyByteBuf buf) {
		passcode = buf.readUtf();
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeUtf(passcode);
	}

	public void handle(NetworkEvent.Context ctx) {
		Player player = ctx.getSender();
		ItemStack stack = PlayerUtils.getItemStackFromAnyHand(player, SCContent.BRIEFCASE.get());

		if (!stack.isEmpty()) {
			CompoundTag tag = stack.getOrCreateTag();

			if (!tag.contains("owner")) {
				tag.putString("owner", player.getName().getString());
				tag.putString("ownerUUID", player.getUUID().toString());
			}

			if (!passcode.isEmpty() && !tag.contains("passcode"))
				BriefcaseItem.hashAndSetPasscode(tag, passcode, p -> {});
		}
	}
}

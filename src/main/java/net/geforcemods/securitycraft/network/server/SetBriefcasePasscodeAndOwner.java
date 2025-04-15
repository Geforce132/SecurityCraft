package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.items.BriefcaseItem;
import net.geforcemods.securitycraft.network.client.OpenScreen;
import net.geforcemods.securitycraft.util.PasscodeUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

public class SetBriefcasePasscodeAndOwner {
	private String passcode;

	public SetBriefcasePasscodeAndOwner() {}

	public SetBriefcasePasscodeAndOwner(String passcode) {
		this.passcode = passcode.isEmpty() ? passcode : PasscodeUtils.hashPasscodeWithoutSalt(passcode);
	}

	public SetBriefcasePasscodeAndOwner(PacketBuffer buf) {
		passcode = buf.readUtf();
	}

	public void encode(PacketBuffer buf) {
		buf.writeUtf(passcode);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		PlayerEntity player = ctx.get().getSender();
		ItemStack stack = PlayerUtils.getItemStackFromAnyHand(player, SCContent.BRIEFCASE.get());

		if (!player.isSpectator() && !stack.isEmpty()) {
			CompoundNBT tag = stack.getOrCreateTag();

			if (!tag.contains("owner")) {
				tag.putString("owner", player.getName().getString());
				tag.putString("ownerUUID", player.getUUID().toString());
			}

			if (!passcode.isEmpty() && !tag.contains("passcode")) {
				BriefcaseItem.hashAndSetPasscode(tag, passcode, p -> {
					SecurityCraft.channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new OpenScreen(OpenScreen.DataType.CHECK_BRIEFCASE_PASSCODE));
				});
			}
		}
	}
}

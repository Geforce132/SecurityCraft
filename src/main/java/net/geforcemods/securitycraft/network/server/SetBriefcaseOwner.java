package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class SetBriefcaseOwner {
	private String passcode;

	public SetBriefcaseOwner() {}

	public SetBriefcaseOwner(String passcode) {
		this.passcode = passcode;
	}

	public static void encode(SetBriefcaseOwner message, PacketBuffer buf) {
		buf.writeUtf(message.passcode);
	}

	public static SetBriefcaseOwner decode(PacketBuffer buf) {
		SetBriefcaseOwner message = new SetBriefcaseOwner();

		message.passcode = buf.readUtf();
		return message;
	}

	public static void onMessage(SetBriefcaseOwner message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			PlayerEntity player = ctx.get().getSender();
			ItemStack stack = PlayerUtils.getSelectedItemStack(player, SCContent.BRIEFCASE.get());

			if (!stack.isEmpty()) {
				CompoundNBT tag = stack.getOrCreateTag();

				if (!tag.contains("owner")) {
					tag.putString("owner", player.getName().getString());
					tag.putString("ownerUUID", player.getUUID().toString());
				}

				if (!tag.contains("passcode") && message.passcode.matches("[0-9]{4}"))
					tag.putString("passcode", message.passcode);
			}
		});

		ctx.get().setPacketHandled(true);
	}
}

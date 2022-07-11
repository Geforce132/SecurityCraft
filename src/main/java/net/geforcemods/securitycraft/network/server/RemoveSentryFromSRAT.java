package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class RemoveSentryFromSRAT {
	private int sentryIndex;

	public RemoveSentryFromSRAT() {}

	public RemoveSentryFromSRAT(int mineIndex) {
		this.sentryIndex = mineIndex;
	}

	public static void encode(RemoveSentryFromSRAT message, PacketBuffer buf) {
		buf.writeVarInt(message.sentryIndex);
	}

	public static RemoveSentryFromSRAT decode(PacketBuffer buf) {
		RemoveSentryFromSRAT message = new RemoveSentryFromSRAT();

		message.sentryIndex = buf.readVarInt();
		return message;
	}

	public static void onMessage(RemoveSentryFromSRAT message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			PlayerEntity player = ctx.get().getSender();
			ItemStack stack = PlayerUtils.getSelectedItemStack(player, SCContent.REMOTE_ACCESS_SENTRY.get());

			if (!stack.isEmpty()) {
				CompoundNBT tag = stack.getOrCreateTag();
				String key = "sentry" + message.sentryIndex;

				if (tag.contains(key)) {
					tag.putIntArray(key, new int[] {
							0, 0, 0
					});
				}
			}
		});

		ctx.get().setPacketHandled(true);
	}
}

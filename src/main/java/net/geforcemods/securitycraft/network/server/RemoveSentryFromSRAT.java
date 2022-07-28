package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

public class RemoveSentryFromSRAT {
	private int sentryIndex;

	public RemoveSentryFromSRAT() {}

	public RemoveSentryFromSRAT(int mineIndex) {
		this.sentryIndex = mineIndex;
	}

	public static void encode(RemoveSentryFromSRAT message, FriendlyByteBuf buf) {
		buf.writeVarInt(message.sentryIndex);
	}

	public static RemoveSentryFromSRAT decode(FriendlyByteBuf buf) {
		RemoveSentryFromSRAT message = new RemoveSentryFromSRAT();

		message.sentryIndex = buf.readVarInt();
		return message;
	}

	public static void onMessage(RemoveSentryFromSRAT message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			Player player = ctx.get().getSender();
			ItemStack stack = PlayerUtils.getSelectedItemStack(player, SCContent.REMOTE_ACCESS_SENTRY.get());

			if (!stack.isEmpty()) {
				CompoundTag tag = stack.getOrCreateTag();

				if (tag.contains("sentry" + message.sentryIndex))
					tag.remove("sentry" + message.sentryIndex);
			}
		});

		ctx.get().setPacketHandled(true);
	}
}

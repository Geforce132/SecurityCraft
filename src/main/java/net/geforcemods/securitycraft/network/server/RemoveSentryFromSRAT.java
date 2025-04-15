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

	public RemoveSentryFromSRAT(FriendlyByteBuf buf) {
		sentryIndex = buf.readVarInt();
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeVarInt(sentryIndex);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		Player player = ctx.get().getSender();
		ItemStack stack = PlayerUtils.getItemStackFromAnyHand(player, SCContent.SENTRY_REMOTE_ACCESS_TOOL.get());

		if (!player.isSpectator() && !stack.isEmpty()) {
			CompoundTag tag = stack.getOrCreateTag();

			if (tag.contains("sentry" + sentryIndex))
				tag.remove("sentry" + sentryIndex);
		}
	}
}

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

	public RemoveSentryFromSRAT(PacketBuffer buf) {
		sentryIndex = buf.readVarInt();
	}

	public void encode(PacketBuffer buf) {
		buf.writeVarInt(sentryIndex);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		PlayerEntity player = ctx.get().getSender();
		ItemStack stack = PlayerUtils.getItemStackFromAnyHand(player, SCContent.SENTRY_REMOTE_ACCESS_TOOL.get());

		if (!player.isSpectator() && !stack.isEmpty()) {
			CompoundNBT tag = stack.getOrCreateTag();
			String key = "sentry" + sentryIndex;

			if (tag.contains(key)) {
				tag.putIntArray(key, new int[] {
						0, 0, 0
				});
			}
		}
	}
}

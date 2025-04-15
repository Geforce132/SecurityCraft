package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class RemoveMineFromMRAT {
	private int mineIndex;

	public RemoveMineFromMRAT() {}

	public RemoveMineFromMRAT(int mineIndex) {
		this.mineIndex = mineIndex;
	}

	public RemoveMineFromMRAT(PacketBuffer buf) {
		mineIndex = buf.readVarInt();
	}

	public void encode(PacketBuffer buf) {
		buf.writeVarInt(mineIndex);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		PlayerEntity player = ctx.get().getSender();
		ItemStack stack = PlayerUtils.getItemStackFromAnyHand(player, SCContent.MINE_REMOTE_ACCESS_TOOL.get());

		if (!player.isSpectator() && !stack.isEmpty()) {
			CompoundNBT tag = stack.getOrCreateTag();
			String key = "mine" + mineIndex;

			if (tag.contains(key)) {
				stack.getTag().putIntArray(key, new int[] {
						0, 0, 0
				});
			}
		}
	}
}

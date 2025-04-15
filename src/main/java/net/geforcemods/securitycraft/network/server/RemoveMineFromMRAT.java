package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

public class RemoveMineFromMRAT {
	private int mineIndex;

	public RemoveMineFromMRAT() {}

	public RemoveMineFromMRAT(int mineIndex) {
		this.mineIndex = mineIndex;
	}

	public RemoveMineFromMRAT(FriendlyByteBuf buf) {
		mineIndex = buf.readVarInt();
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeVarInt(mineIndex);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		Player player = ctx.get().getSender();
		ItemStack stack = PlayerUtils.getItemStackFromAnyHand(player, SCContent.MINE_REMOTE_ACCESS_TOOL.get());

		if (!player.isSpectator() && !stack.isEmpty()) {
			CompoundTag tag = stack.getOrCreateTag();

			if (tag.contains("mine" + mineIndex))
				tag.remove("mine" + mineIndex);
		}
	}
}

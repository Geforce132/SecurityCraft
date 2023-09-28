package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.blockentities.BlockPocketManagerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

public class ToggleBlockPocketManager {
	private BlockPos pos;
	private int size;
	private boolean enabling;

	public ToggleBlockPocketManager() {}

	public ToggleBlockPocketManager(BlockPocketManagerBlockEntity be, boolean enabling, int size) {
		pos = be.getBlockPos();
		this.enabling = enabling;
		this.size = size;
	}

	public ToggleBlockPocketManager(FriendlyByteBuf buf) {
		pos = BlockPos.of(buf.readLong());
		enabling = buf.readBoolean();
		size = buf.readInt();
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeLong(pos.asLong());
		buf.writeBoolean(enabling);
		buf.writeInt(size);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		Player player = ctx.get().getSender();

		if (player.level().getBlockEntity(pos) instanceof BlockPocketManagerBlockEntity be && be.isOwnedBy(player)) {
			be.setSize(size);

			if (enabling)
				be.enableMultiblock();
			else
				be.disableMultiblock();

			be.setChanged();
		}
	}
}

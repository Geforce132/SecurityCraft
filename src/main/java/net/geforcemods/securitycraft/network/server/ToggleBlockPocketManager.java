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

	public ToggleBlockPocketManager(BlockPocketManagerBlockEntity be, boolean enabling) {
		pos = be.getBlockPos();
		size = be.getSize();
		this.enabling = enabling;
	}

	public ToggleBlockPocketManager(FriendlyByteBuf buf) {
		pos = BlockPos.of(buf.readLong());
		size = buf.readInt();
		enabling = buf.readBoolean();
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeLong(pos.asLong());
		buf.writeInt(size);
		buf.writeBoolean(enabling);
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

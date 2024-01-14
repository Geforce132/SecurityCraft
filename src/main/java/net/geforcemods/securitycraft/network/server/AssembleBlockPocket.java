package net.geforcemods.securitycraft.network.server;

import net.geforcemods.securitycraft.blockentities.BlockPocketManagerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.neoforge.network.NetworkEvent;

public class AssembleBlockPocket {
	private BlockPos pos;
	private int size;

	public AssembleBlockPocket() {}

	public AssembleBlockPocket(BlockPocketManagerBlockEntity be) {
		pos = be.getBlockPos();
		size = be.getSize();
	}

	public AssembleBlockPocket(FriendlyByteBuf buf) {
		pos = buf.readBlockPos();
		size = buf.readInt();
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeBlockPos(pos);
		buf.writeInt(size);
	}

	public void handle(NetworkEvent.Context ctx) {
		if (ctx.getSender().level().getBlockEntity(pos) instanceof BlockPocketManagerBlockEntity be && be.isOwnedBy(ctx.getSender())) {
			be.setSize(size);
			be.autoAssembleMultiblock();
			be.setChanged();
		}
	}
}

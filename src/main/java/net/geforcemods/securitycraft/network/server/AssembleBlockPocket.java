package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.blockentities.BlockPocketManagerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class AssembleBlockPocket {
	private BlockPos pos;
	private int size;

	public AssembleBlockPocket() {}

	public AssembleBlockPocket(BlockPocketManagerBlockEntity te, int size) {
		pos = te.getBlockPos();
		this.size = size;
	}

	public AssembleBlockPocket(FriendlyByteBuf buf) {
		pos = BlockPos.of(buf.readLong());
		size = buf.readInt();
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeLong(pos.asLong());
		buf.writeInt(size);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		if (ctx.get().getSender().level.getBlockEntity(pos) instanceof BlockPocketManagerBlockEntity be && be.isOwnedBy(ctx.get().getSender())) {
			be.setSize(size);
			be.autoAssembleMultiblock();
			be.setChanged();
		}
	}
}

package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.blockentities.BlockPocketManagerBlockEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

public class AssembleBlockPocket {
	private BlockPos pos;
	private int size;

	public AssembleBlockPocket() {}

	public AssembleBlockPocket(BlockPocketManagerBlockEntity te, int size) {
		pos = te.getBlockPos();
		this.size = size;
	}

	public AssembleBlockPocket(PacketBuffer buf) {
		pos = BlockPos.of(buf.readLong());
		size = buf.readInt();
	}

	public void encode(PacketBuffer buf) {
		buf.writeLong(pos.asLong());
		buf.writeInt(size);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		TileEntity te = ctx.get().getSender().level.getBlockEntity(pos);

		if (te instanceof BlockPocketManagerBlockEntity && ((BlockPocketManagerBlockEntity) te).isOwnedBy(ctx.get().getSender())) {
			((BlockPocketManagerBlockEntity) te).setSize(size);
			((BlockPocketManagerBlockEntity) te).autoAssembleMultiblock();
		}
	}
}

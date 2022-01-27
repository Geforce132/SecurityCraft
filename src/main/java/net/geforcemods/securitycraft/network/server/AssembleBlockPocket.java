package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.blockentity.BlockPocketManagerBlockEntity;
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

	public static void encode(AssembleBlockPocket message, PacketBuffer buf) {
		buf.writeLong(message.pos.asLong());
		buf.writeInt(message.size);
	}

	public static AssembleBlockPocket decode(PacketBuffer buf) {
		AssembleBlockPocket message = new AssembleBlockPocket();

		message.pos = BlockPos.of(buf.readLong());
		message.size = buf.readInt();
		return message;
	}

	public static void onMessage(AssembleBlockPocket message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			TileEntity te = ctx.get().getSender().level.getBlockEntity(message.pos);

			if (te instanceof BlockPocketManagerBlockEntity && ((BlockPocketManagerBlockEntity) te).getOwner().isOwner(ctx.get().getSender())) {
				((BlockPocketManagerBlockEntity) te).size = message.size;
				((BlockPocketManagerBlockEntity) te).autoAssembleMultiblock();
			}
		});
		ctx.get().setPacketHandled(true);
	}
}

package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.blockentities.BlockPocketManagerBlockEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class AssembleBlockPocket {
	private BlockPos pos;
	private int dimension, size;

	public AssembleBlockPocket() {}

	public AssembleBlockPocket(BlockPocketManagerBlockEntity te, int size) {
		pos = te.getBlockPos();
		dimension = te.getLevel().getDimension().getType().getId();
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
			TileEntity te = ServerLifecycleHooks.getCurrentServer().getLevel(DimensionType.getById(message.dimension)).getBlockEntity(message.pos);

			if (te instanceof BlockPocketManagerBlockEntity && ((BlockPocketManagerBlockEntity) te).getOwner().isOwner(ctx.get().getSender())) {
				((BlockPocketManagerBlockEntity) te).size = message.size;
				((BlockPocketManagerBlockEntity) te).autoAssembleMultiblock();
			}
		});
		ctx.get().setPacketHandled(true);
	}
}

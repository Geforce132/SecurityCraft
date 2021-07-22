package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.tileentity.BlockPocketManagerTileEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

public class AssembleBlockPocket
{
	private BlockPos pos;
	private int size;

	public AssembleBlockPocket() {}

	public AssembleBlockPocket(BlockPocketManagerTileEntity te, int size)
	{
		pos = te.getBlockPos();
		this.size = size;
	}

	public static void encode(AssembleBlockPocket message, FriendlyByteBuf buf)
	{
		buf.writeLong(message.pos.asLong());
		buf.writeInt(message.size);
	}

	public static AssembleBlockPocket decode(FriendlyByteBuf buf)
	{
		AssembleBlockPocket message = new AssembleBlockPocket();

		message.pos = BlockPos.of(buf.readLong());
		message.size = buf.readInt();
		return message;
	}

	public static void onMessage(AssembleBlockPocket message, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() -> {
			BlockEntity te = ctx.get().getSender().level.getBlockEntity(message.pos);

			if(te instanceof BlockPocketManagerTileEntity && ((BlockPocketManagerTileEntity)te).getOwner().isOwner(ctx.get().getSender()))
			{
				((BlockPocketManagerTileEntity)te).size = message.size;
				((BlockPocketManagerTileEntity)te).autoAssembleMultiblock();
			}
		});
		ctx.get().setPacketHandled(true);
	}
}

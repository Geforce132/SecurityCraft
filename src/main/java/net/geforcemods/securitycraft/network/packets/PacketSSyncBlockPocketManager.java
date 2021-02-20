package net.geforcemods.securitycraft.network.packets;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.tileentity.TileEntityBlockPocketManager;
import net.geforcemods.securitycraft.util.WorldUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSSyncBlockPocketManager implements IMessage
{
	private BlockPos pos;
	private int size;
	private boolean showOutline;
	private int autoBuildOffset;

	public PacketSSyncBlockPocketManager(){}

	public PacketSSyncBlockPocketManager(BlockPos pos, int size, boolean showOutline, int autoBuildOffset)
	{
		this.pos = pos;
		this.size = size;
		this.showOutline = showOutline;
		this.autoBuildOffset = autoBuildOffset;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		pos = BlockPos.fromLong(buf.readLong());
		size = ByteBufUtils.readVarInt(buf, 5);
		showOutline = buf.readBoolean();
		autoBuildOffset = ByteBufUtils.readVarInt(buf, 5);
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeLong(pos.toLong());
		ByteBufUtils.writeVarInt(buf, size, 5);
		buf.writeBoolean(showOutline);
		ByteBufUtils.writeVarInt(buf, autoBuildOffset, 5);
	}

	public static class Handler extends PacketHelper implements IMessageHandler<PacketSSyncBlockPocketManager, IMessage>
	{
		@Override
		public IMessage onMessage(PacketSSyncBlockPocketManager message, MessageContext ctx)
		{
			WorldUtils.addScheduledTask(getWorld(ctx.getServerHandler().player), () -> {
				BlockPos pos = message.pos;
				World world = ctx.getServerHandler().player.world;
				TileEntity te = world.getTileEntity(pos);

				if(world.isBlockLoaded(pos) && te instanceof TileEntityBlockPocketManager)
				{
					TileEntityBlockPocketManager bpm = (TileEntityBlockPocketManager)te;
					IBlockState state = world.getBlockState(pos);

					bpm.size = message.size;
					bpm.showOutline = message.showOutline;
					bpm.autoBuildOffset = message.autoBuildOffset;
					world.notifyBlockUpdate(pos, state, state, 2);
				}
			});

			return null;
		}
	}
}

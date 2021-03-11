package net.geforcemods.securitycraft.network.server;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.blocks.BlockSecurityCamera;
import net.geforcemods.securitycraft.util.WorldUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SetCameraPowered implements IMessage
{
	private BlockPos pos;
	private boolean powered;

	public SetCameraPowered() {}

	public SetCameraPowered(BlockPos pos, boolean powered)
	{
		this.pos = pos;
		this.powered = powered;
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeLong(pos.toLong());
		buf.writeBoolean(powered);
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		pos = BlockPos.fromLong(buf.readLong());
		powered = buf.readBoolean();
	}

	public static class Handler implements IMessageHandler<SetCameraPowered, IMessage>
	{
		@Override
		public IMessage onMessage(SetCameraPowered message, MessageContext context)
		{
			WorldUtils.addScheduledTask(context.getServerHandler().player.world, () -> {
				BlockPos pos = message.pos;
				EntityPlayer player = context.getServerHandler().player;
				World world = player.world;
				TileEntity te = world.getTileEntity(pos);

				if(te instanceof IOwnable && ((IOwnable)te).getOwner().isOwner(player))
				{
					world.setBlockState(pos, world.getBlockState(pos).withProperty(BlockSecurityCamera.POWERED, message.powered));
					world.notifyNeighborsOfStateChange(pos.offset(world.getBlockState(pos).getValue(BlockSecurityCamera.FACING), -1), world.getBlockState(pos).getBlock(), false);
				}
			});
			return null;
		}
	}
}

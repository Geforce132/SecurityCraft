package net.geforcemods.securitycraft.network.server;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blocks.BlockSecurityCamera;
import net.geforcemods.securitycraft.misc.EnumModuleType;
import net.geforcemods.securitycraft.tileentity.TileEntitySecurityCamera;
import net.geforcemods.securitycraft.util.WorldUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MountCamera implements IMessage
{
	private BlockPos pos;
	private int id;

	public MountCamera() {}

	public MountCamera(BlockPos pos, int id)
	{
		this.pos = pos;
		this.id = id;
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeLong(pos.toLong());
		buf.writeInt(id);
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		pos = BlockPos.fromLong(buf.readLong());
		id = buf.readInt();
	}

	public static class Handler implements IMessageHandler<MountCamera, IMessage>
	{
		@Override
		public IMessage onMessage(MountCamera message, MessageContext context) {
			WorldUtils.addScheduledTask(context.getServerHandler().player.world, (() -> {
				BlockPos pos = message.pos;
				int id = message.id;
				EntityPlayerMP player = context.getServerHandler().player;
				World world = player.world;
				IBlockState state = world.getBlockState(pos);

				if(state.getBlock() == SCContent.securityCamera)
				{
					TileEntity te = world.getTileEntity(pos);

					if(te instanceof TileEntitySecurityCamera && (((TileEntitySecurityCamera)te).getOwner().isOwner(player) || ((TileEntitySecurityCamera)te).hasModule(EnumModuleType.SMART)))
						((BlockSecurityCamera)state.getBlock()).mountCamera(world, pos.getX(), pos.getY(), pos.getZ(), id, player);
				}
			}));

			return null;
		}
	}
}
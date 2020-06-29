package net.geforcemods.securitycraft.network.packets;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.tileentity.TileEntityProjector;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.WorldUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSSyncProjector implements IMessage
{
	private int x, y, z;
	private int width, range, offset;

	public PacketSSyncProjector(){}

	public PacketSSyncProjector(int x, int y, int z, int width, int range, int offset){
		this.x = x;
		this.y = y;
		this.z = z;
		this.width = width;
		this.range = range;
		this.offset = offset;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		width = buf.readInt();
		range = buf.readInt();
		offset = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeInt(width);
		buf.writeInt(range);
		buf.writeInt(offset);
	}

	public static class Handler extends PacketHelper implements IMessageHandler<PacketSSyncProjector, IMessage>
	{
		@Override
		public IMessage onMessage(PacketSSyncProjector message, MessageContext ctx)
		{
			WorldUtils.addScheduledTask(getWorld(ctx.getServerHandler().player), () -> {
				BlockPos pos = BlockUtils.toPos(message.x, message.y, message.z);
				World world = ctx.getServerHandler().player.world;

				if(world.isBlockLoaded(pos) && world.getTileEntity(pos) instanceof TileEntityProjector)
				{
					((TileEntityProjector) world.getTileEntity(pos)).setProjectionWidth(message.width);
					((TileEntityProjector) world.getTileEntity(pos)).setProjectionRange(message.range);
					((TileEntityProjector) world.getTileEntity(pos)).setProjectionOffset(message.offset);
				}
			});

			return null;
		}
	}
}

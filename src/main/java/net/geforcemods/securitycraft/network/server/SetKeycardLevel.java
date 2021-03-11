package net.geforcemods.securitycraft.network.server;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.tileentity.TileEntityKeycardReader;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.WorldUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SetKeycardLevel implements IMessage{

	private int x, y, z, level;
	private boolean exactCard;

	public SetKeycardLevel(){

	}

	public SetKeycardLevel(int x, int y, int z, int level, boolean exactCard){
		this.x = x;
		this.y = y;
		this.z = z;
		this.level = level;
		this.exactCard  = exactCard;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeInt(level);
		buf.writeBoolean(exactCard);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		level = buf.readInt();
		exactCard = buf.readBoolean();
	}

	public static class Handler implements IMessageHandler<SetKeycardLevel, IMessage> {

		@Override
		public IMessage onMessage(SetKeycardLevel message, MessageContext context) {
			WorldUtils.addScheduledTask(context.getServerHandler().player.world, () -> {
				BlockPos pos = BlockUtils.toPos(message.x, message.y, message.z);
				EntityPlayer player = context.getServerHandler().player;
				TileEntity te = player.world.getTileEntity(pos);

				if(te instanceof TileEntityKeycardReader && ((TileEntityKeycardReader)te).getOwner().isOwner(player))
				{
					((TileEntityKeycardReader)te).setPassword(String.valueOf(message.level));
					((TileEntityKeycardReader)te).setRequiresExactKeycard(message.exactCard);
				}
			});

			return null;
		}
	}


}

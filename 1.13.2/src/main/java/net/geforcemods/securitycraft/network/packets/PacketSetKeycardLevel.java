package net.geforcemods.securitycraft.network.packets;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.tileentity.TileEntityKeycardReader;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.WorldUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;

public class PacketSetKeycardLevel implements IMessage{

	private int x, y, z, level;
	private boolean exactCard;

	public PacketSetKeycardLevel(){

	}

	public PacketSetKeycardLevel(int x, int y, int z, int level, boolean exactCard){
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

	public static class Handler extends PacketHelper implements IMessageHandler<PacketSetKeycardLevel, IMessage> {

		@Override
		public IMessage onMessage(PacketSetKeycardLevel message, MessageContext context) {
			WorldUtils.addScheduledTask(getWorld(context.getServerHandler().player), () -> {
				BlockPos pos = BlockUtils.toPos(message.x, message.y, message.z);
				int level = message.level;
				boolean exactCard = message.exactCard;
				EntityPlayer player = context.getServerHandler().player;

				((TileEntityKeycardReader) getWorld(player).getTileEntity(pos)).setPassword(String.valueOf(level));
				((TileEntityKeycardReader) getWorld(player).getTileEntity(pos)).setRequiresExactKeycard(exactCard);
			});

			return null;
		}
	}


}

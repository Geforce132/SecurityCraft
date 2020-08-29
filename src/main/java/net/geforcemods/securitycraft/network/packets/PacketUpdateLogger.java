package net.geforcemods.securitycraft.network.packets;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.tileentity.TileEntityLogger;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketUpdateLogger implements IMessage{

	private int x, y, z, i;
	private String username;
	private String uuid;
	private long timestamp;

	public PacketUpdateLogger(){

	}

	public PacketUpdateLogger(int x, int y, int z, int i, String username, String uuid, long timestamp){
		this.x = x;
		this.y = y;
		this.z = z;
		this.i = i;
		this.username = username;
		this.uuid = uuid;
		this.timestamp = timestamp;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeInt(i);
		ByteBufUtils.writeUTF8String(buf, username);
		ByteBufUtils.writeUTF8String(buf, uuid);
		buf.writeLong(timestamp);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		i = buf.readInt();
		username = ByteBufUtils.readUTF8String(buf);
		uuid = ByteBufUtils.readUTF8String(buf);
		timestamp = buf.readLong();
	}

	public static class Handler extends PacketHelper implements IMessageHandler<PacketUpdateLogger, IMessage> {

		@Override
		@SideOnly(Side.CLIENT)
		public IMessage onMessage(PacketUpdateLogger message, MessageContext context) {
			Minecraft.getMinecraft().addScheduledTask(() -> {
				BlockPos pos = BlockUtils.toPos(message.x, message.y, message.z);
				int i = message.i;
				TileEntityLogger te = (TileEntityLogger) getClientWorld(Minecraft.getMinecraft().player).getTileEntity(pos);

				if(te != null)
				{
					te.players[i] = message.username;
					te.uuids[i] = message.uuid;
					te.timestamps[i] = message.timestamp;
				}
			});

			return null;
		}
	}

}

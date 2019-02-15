package net.geforcemods.securitycraft.network.packets;

import io.netty.buffer.ByteBuf;
import javafx.geometry.Side;
import net.geforcemods.securitycraft.tileentity.TileEntityLogger;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketUpdateLogger implements IMessage{

	private int x, y, z, i;
	private String username;

	public PacketUpdateLogger(){

	}

	public PacketUpdateLogger(int x, int y, int z, int i, String username){
		this.x = x;
		this.y = y;
		this.z = z;
		this.i = i;
		this.username = username;
	}

	@Override
	public void toBytes(ByteBuf par2ByteBuf) {
		par2ByteBuf.writeInt(x);
		par2ByteBuf.writeInt(y);
		par2ByteBuf.writeInt(z);
		par2ByteBuf.writeInt(i);
		ByteBufUtils.writeUTF8String(par2ByteBuf, username);
	}

	@Override
	public void fromBytes(ByteBuf par2ByteBuf) {
		x = par2ByteBuf.readInt();
		y = par2ByteBuf.readInt();
		z = par2ByteBuf.readInt();
		i = par2ByteBuf.readInt();
		username = ByteBufUtils.readUTF8String(par2ByteBuf);
	}

	public static class Handler extends PacketHelper implements IMessageHandler<PacketUpdateLogger, IMessage> {

		@Override
		@SideOnly(Side.CLIENT)
		public IMessage onMessage(PacketUpdateLogger message, MessageContext context) {
			Minecraft.getMinecraft().addScheduledTask(() -> {
				BlockPos pos = BlockUtils.toPos(message.x, message.y, message.z);
				int i = message.i;
				String username = message.username;
				EntityPlayer player = Minecraft.getMinecraft().player;

				TileEntityLogger te = (TileEntityLogger) getClientWorld(player).getTileEntity(pos);

				if(te != null)
					te.players[i] = username;
			});

			return null;
		}
	}

}

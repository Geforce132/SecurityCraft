package net.geforcemods.securitycraft.network.packets;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.tileentity.TileEntityKeypadChest;
import net.minecraft.entity.player.EntityPlayer;

public class PacketSSetPassword implements IMessage{

	private String password;
	private int x, y, z;

	public PacketSSetPassword(){

	}

	public PacketSSetPassword(int x, int y, int z, String code){
		this.x = x;
		this.y = y;
		this.z = z;
		password = code;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		ByteBufUtils.writeUTF8String(buf, password);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		password = ByteBufUtils.readUTF8String(buf);
	}

	public static class Handler extends PacketHelper implements IMessageHandler<PacketSSetPassword, IMessage> {

		@Override
		public IMessage onMessage(PacketSSetPassword packet, MessageContext ctx) {
			int x = packet.x;
			int y = packet.y;
			int z = packet.z;
			String password = packet.password;
			EntityPlayer player = ctx.getServerHandler().playerEntity;

			if(getWorld(player).getTileEntity(x, y, z) != null && getWorld(player).getTileEntity(x, y, z) instanceof IPasswordProtected){
				((IPasswordProtected) getWorld(player).getTileEntity(x, y, z)).setPassword(password);
				checkForAdjecentChest(x, y, z, password, player);
			}

			return null;
		}

		private void checkForAdjecentChest(int x, int y, int z, String codeToSet, EntityPlayer player) {
			if(getWorld(player).getTileEntity(x, y, z) != null && getWorld(player).getTileEntity(x, y, z) instanceof TileEntityKeypadChest)
				if(getWorld(player).getTileEntity(x + 1, y, z) != null && getWorld(player).getTileEntity(x + 1, y, z) instanceof TileEntityKeypadChest)
					((IPasswordProtected) getWorld(player).getTileEntity(x + 1, y, z)).setPassword(codeToSet);
				else if(getWorld(player).getTileEntity(x - 1, y, z) != null && getWorld(player).getTileEntity(x - 1, y, z) instanceof TileEntityKeypadChest)
					((IPasswordProtected) getWorld(player).getTileEntity(x - 1, y, z)).setPassword(codeToSet);
				else if(getWorld(player).getTileEntity(x, y, z + 1) != null && getWorld(player).getTileEntity(x, y, z + 1) instanceof TileEntityKeypadChest)
					((IPasswordProtected) getWorld(player).getTileEntity(x, y, z + 1)).setPassword(codeToSet);
				else if(getWorld(player).getTileEntity(x, y, z - 1) != null && getWorld(player).getTileEntity(x, y, z - 1) instanceof TileEntityKeypadChest)
					((IPasswordProtected) getWorld(player).getTileEntity(x, y, z - 1)).setPassword(codeToSet);
		}
	}

}

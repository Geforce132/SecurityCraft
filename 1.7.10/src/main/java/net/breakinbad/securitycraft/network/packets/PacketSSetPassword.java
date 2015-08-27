package net.breakinbad.securitycraft.network.packets;

import io.netty.buffer.ByteBuf;
import net.breakinbad.securitycraft.api.IPasswordProtected;
import net.breakinbad.securitycraft.tileentity.TileEntityKeypadChest;
import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketSSetPassword implements IMessage{
	
	private String password;
	private int x, y, z;
	
	public PacketSSetPassword(){
		
	}
	
	public PacketSSetPassword(int x, int y, int z, String code){
		this.x = x;
		this.y = y;
		this.z = z;
		this.password = code;
	}

	public void toBytes(ByteBuf par1ByteBuf) {
		par1ByteBuf.writeInt(x);
		par1ByteBuf.writeInt(y);
		par1ByteBuf.writeInt(z);
		ByteBufUtils.writeUTF8String(par1ByteBuf, password);
	}

	public void fromBytes(ByteBuf par1ByteBuf) {
		this.x = par1ByteBuf.readInt();
		this.y = par1ByteBuf.readInt();
		this.z = par1ByteBuf.readInt();
		this.password = ByteBufUtils.readUTF8String(par1ByteBuf);
	}
	
public static class Handler extends PacketHelper implements IMessageHandler<PacketSSetPassword, IMessage> {

	public IMessage onMessage(PacketSSetPassword packet, MessageContext ctx) {
		int x = packet.x;
		int y = packet.y;
		int z = packet.z;
		String password = packet.password;
		EntityPlayer player = ctx.getServerHandler().playerEntity;

		if(getWorld(player).getTileEntity(x, y, z) != null && getWorld(player).getTileEntity(x, y, z) instanceof IPasswordProtected){
			((IPasswordProtected) getWorld(player).getTileEntity(x, y, z)).setPassword(password);
			this.checkForAdjecentChest(x, y, z, password, player);
		}
		
		return null;
	}

	private void checkForAdjecentChest(int x, int y, int z, String codeToSet, EntityPlayer player) {
		if(getWorld(player).getTileEntity(x, y, z) != null && getWorld(player).getTileEntity(x, y, z) instanceof TileEntityKeypadChest){
			if(getWorld(player).getTileEntity(x + 1, y, z) != null && getWorld(player).getTileEntity(x + 1, y, z) instanceof TileEntityKeypadChest){
				((IPasswordProtected) getWorld(player).getTileEntity(x + 1, y, z)).setPassword(codeToSet);
			}else if(getWorld(player).getTileEntity(x - 1, y, z) != null && getWorld(player).getTileEntity(x - 1, y, z) instanceof TileEntityKeypadChest){
				((IPasswordProtected) getWorld(player).getTileEntity(x - 1, y, z)).setPassword(codeToSet);
			}else if(getWorld(player).getTileEntity(x, y, z + 1) != null && getWorld(player).getTileEntity(x, y, z + 1) instanceof TileEntityKeypadChest){
				((IPasswordProtected) getWorld(player).getTileEntity(x, y, z + 1)).setPassword(codeToSet);
			}else if(getWorld(player).getTileEntity(x, y, z - 1) != null && getWorld(player).getTileEntity(x, y, z - 1) instanceof TileEntityKeypadChest){
				((IPasswordProtected) getWorld(player).getTileEntity(x, y, z - 1)).setPassword(codeToSet);
			}
		}
	}
}

}

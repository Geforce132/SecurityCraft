package org.freeforums.geforce.securitycraft.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

import org.freeforums.geforce.securitycraft.tileentity.TileEntityKeypad;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityKeypadChest;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketSetKeypadCode implements IMessage{
	
	private int codeToSend;
	private int codeToSet;
	private int x, y, z;
	
	public PacketSetKeypadCode(){
		
	}
	
	public PacketSetKeypadCode(int x, int y, int z, int code){
		this.codeToSend = code;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public void toBytes(ByteBuf par1ByteBuf) {
		//Make packet here
		par1ByteBuf.writeInt(x);
		par1ByteBuf.writeInt(y);
		par1ByteBuf.writeInt(z);
		par1ByteBuf.writeInt(this.codeToSend);
	}

	public void fromBytes(ByteBuf par1ByteBuf) {
		this.x = par1ByteBuf.readInt();
		this.y = par1ByteBuf.readInt();
		this.z = par1ByteBuf.readInt();
		this.codeToSet = par1ByteBuf.readInt();
	}
	
public static class Handler extends PacketHelper implements IMessageHandler<PacketSetKeypadCode, IMessage> {

	public IMessage onMessage(PacketSetKeypadCode packet, MessageContext ctx) {
		int x = packet.x;
		int y = packet.y;
		int z = packet.z;
		int codeToSet = packet.codeToSet;
		EntityPlayer player = ctx.getServerHandler().playerEntity;

		if(getWorld(player).getTileEntity(x, y, z) != null && getWorld(player).getTileEntity(x, y, z) instanceof TileEntityKeypad){
			((TileEntityKeypad) getWorld(player).getTileEntity(x, y, z)).setKeypadCode(codeToSet);

		}else if(getWorld(player).getTileEntity(x, y, z) != null && getWorld(player).getTileEntity(x, y, z) instanceof TileEntityKeypadChest){
			((TileEntityKeypadChest) getWorld(player).getTileEntity(x, y, z)).setKeypadCode(codeToSet);
			this.checkForAdjecentChest(x, y, z, codeToSet, player);

		}
		
		return null;
	}

	private void checkForAdjecentChest(int x, int y, int z, int codeToSet, EntityPlayer player) {
		if(getWorld(player).getTileEntity(x + 1, y, z) != null && getWorld(player).getTileEntity(x + 1, y, z) instanceof TileEntityKeypadChest){
			((TileEntityKeypadChest)getWorld(player).getTileEntity(x + 1, y, z)).setKeypadCode(codeToSet);
		}else if(getWorld(player).getTileEntity(x - 1, y, z) != null && getWorld(player).getTileEntity(x - 1, y, z) instanceof TileEntityKeypadChest){
			((TileEntityKeypadChest)getWorld(player).getTileEntity(x - 1, y, z)).setKeypadCode(codeToSet);
		}else if(getWorld(player).getTileEntity(x, y, z + 1) != null && getWorld(player).getTileEntity(x, y, z + 1) instanceof TileEntityKeypadChest){
			((TileEntityKeypadChest)getWorld(player).getTileEntity(x, y, z + 1)).setKeypadCode(codeToSet);
		}else if(getWorld(player).getTileEntity(x, y, z - 1) != null && getWorld(player).getTileEntity(x, y, z - 1) instanceof TileEntityKeypadChest){
			((TileEntityKeypadChest)getWorld(player).getTileEntity(x, y, z - 1)).setKeypadCode(codeToSet);
		}
	}
}

}

package org.freeforums.geforce.securitycraft.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import org.freeforums.geforce.securitycraft.tileentity.TileEntityKeypad;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityKeypadChest;

public class PacketSetKeypadCode implements IMessage{
	
	private String codeToSend;
	private int x, y, z;
	
	public PacketSetKeypadCode(){
		
	}
	
	public PacketSetKeypadCode(int x, int y, int z, String code){
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
		ByteBufUtils.writeUTF8String(par1ByteBuf, codeToSend);
	}

	public void fromBytes(ByteBuf par1ByteBuf) {
		this.x = par1ByteBuf.readInt();
		this.y = par1ByteBuf.readInt();
		this.z = par1ByteBuf.readInt();
		this.codeToSend = ByteBufUtils.readUTF8String(par1ByteBuf);
	}
	
public static class Handler extends PacketHelper implements IMessageHandler<PacketSetKeypadCode, IMessage> {

	public IMessage onMessage(PacketSetKeypadCode packet, MessageContext ctx) {
		int x = packet.x;
		int y = packet.y;
		int z = packet.z;
		BlockPos pos = new BlockPos(x, y, z);
		String codeToSet = packet.codeToSend;
		EntityPlayer player = ctx.getServerHandler().playerEntity;

		if(getWorld(player).getTileEntity(pos) != null && getWorld(player).getTileEntity(pos) instanceof TileEntityKeypad){
			((TileEntityKeypad) getWorld(player).getTileEntity(pos)).setKeypadCode(codeToSet);
			System.out.println("Setting code to: " + codeToSet);
		}else if(getWorld(player).getTileEntity(pos) != null && getWorld(player).getTileEntity(pos) instanceof TileEntityKeypadChest){
			((TileEntityKeypadChest) getWorld(player).getTileEntity(pos)).setKeypadCode(codeToSet);
			this.checkForAdjecentChest(pos, codeToSet, player);

		}
		
		return null;
	}

	private void checkForAdjecentChest(BlockPos pos, String codeToSet, EntityPlayer player) {
		if(getWorld(player).getTileEntity(pos.east()) != null && getWorld(player).getTileEntity(pos.east()) instanceof TileEntityKeypadChest){
			((TileEntityKeypadChest)getWorld(player).getTileEntity(pos.east())).setKeypadCode(codeToSet);
		}else if(getWorld(player).getTileEntity(pos.west()) != null && getWorld(player).getTileEntity(pos.west()) instanceof TileEntityKeypadChest){
			((TileEntityKeypadChest)getWorld(player).getTileEntity(pos.west())).setKeypadCode(codeToSet);
		}else if(getWorld(player).getTileEntity(pos.south()) != null && getWorld(player).getTileEntity(pos.south()) instanceof TileEntityKeypadChest){
			((TileEntityKeypadChest)getWorld(player).getTileEntity(pos.south())).setKeypadCode(codeToSet);
		}else if(getWorld(player).getTileEntity(pos.north()) != null && getWorld(player).getTileEntity(pos.north()) instanceof TileEntityKeypadChest){
			((TileEntityKeypadChest)getWorld(player).getTileEntity(pos.north())).setKeypadCode(codeToSet);
		}
	}
}

}

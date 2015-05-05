package org.freeforums.geforce.securitycraft.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import org.freeforums.geforce.securitycraft.main.Utils;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityInventoryScanner;

public class PacketSetISType implements IMessage{
	
	private int x, y, z;
	private String type;
	
	public PacketSetISType(){
		
	}
	
	public PacketSetISType(int x, int y, int z, String type){
		this.x = x;
		this.y = y;
		this.z = z;
		this.type = type;
	}

	public void fromBytes(ByteBuf par1ByteBuf) {
		this.x = par1ByteBuf.readInt();
		this.y = par1ByteBuf.readInt();
		this.z = par1ByteBuf.readInt();
		this.type = ByteBufUtils.readUTF8String(par1ByteBuf);

	}
	
	public void toBytes(ByteBuf par1ByteBuf) {
		par1ByteBuf.writeInt(x);
		par1ByteBuf.writeInt(y);
		par1ByteBuf.writeInt(z);
		ByteBufUtils.writeUTF8String(par1ByteBuf, type);

	}
	
public static class Handler extends PacketHelper implements IMessageHandler<PacketSetISType, IMessage> {
	
	public IMessage onMessage(PacketSetISType packet, MessageContext context) {
		int x = packet.x;
		int y = packet.y;
		int z = packet.z;
		
		((TileEntityInventoryScanner) getWorld(context.getServerHandler().playerEntity).getTileEntity(new BlockPos(x, y, z))).setType(packet.type);
		
		mod_SecurityCraft.log("Setting type to " + packet.type);
		getWorld(context.getServerHandler().playerEntity).scheduleUpdate(new BlockPos(x, y, z), getWorld(context.getServerHandler().playerEntity).getBlockState(new BlockPos(x, y, z)).getBlock(), 1);
		
		Utils.setISinTEAppropriately(getWorld(context.getServerHandler().playerEntity), new BlockPos(x, y, z), ((TileEntityInventoryScanner) getWorld(context.getServerHandler().playerEntity).getTileEntity(new BlockPos(x, y, z))).getContents(), ((TileEntityInventoryScanner) getWorld(context.getServerHandler().playerEntity).getTileEntity(new BlockPos(x, y, z))).getType());			
		
		return null;
	}

}

}

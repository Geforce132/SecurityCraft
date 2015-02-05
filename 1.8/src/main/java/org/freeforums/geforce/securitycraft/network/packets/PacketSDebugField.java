package org.freeforums.geforce.securitycraft.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import org.freeforums.geforce.securitycraft.tileentity.TileEntityInventoryScanner;

public class PacketSDebugField implements IMessage {
	
	private int x, y, z;
	
	public PacketSDebugField(){
		
	}
	
	public PacketSDebugField(int par1, int par2, int par3){
		
	}

	public void fromBytes(ByteBuf buf) {
		this.x = buf.readInt();
		this.y = buf.readInt();
		this.z = buf.readInt();
	}

	public void toBytes(ByteBuf buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
	}
	
public static class Handler extends PacketHelper implements IMessageHandler<PacketSDebugField, IMessage> {

	public IMessage onMessage(PacketSDebugField message, MessageContext ctx) {
		String ownerName = ((TileEntityInventoryScanner)getWorld(ctx.getServerHandler().playerEntity).getTileEntity(new BlockPos(message.x, message.y, message.z))).getOwnerName();
		System.out.println(ownerName);
		return null;
	}
	
}

}

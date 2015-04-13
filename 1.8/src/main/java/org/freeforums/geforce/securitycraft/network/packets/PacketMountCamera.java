package org.freeforums.geforce.securitycraft.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import org.freeforums.geforce.securitycraft.blocks.BlockSecurityCamera;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;

public class PacketMountCamera implements IMessage{
	
	private int x, y, z;
	
	public PacketMountCamera(){
		
	}
	
	public PacketMountCamera(int x, int y, int z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public void toBytes(ByteBuf par1ByteBuf) {
		par1ByteBuf.writeInt(x);
		par1ByteBuf.writeInt(y);
		par1ByteBuf.writeInt(z);
	}

	public void fromBytes(ByteBuf par1ByteBuf) {
		this.x = par1ByteBuf.readInt();
		this.y = par1ByteBuf.readInt();
		this.z = par1ByteBuf.readInt();
	}
	
public static class Handler extends PacketHelper implements IMessageHandler<PacketMountCamera, IMessage> {

	public IMessage onMessage(PacketMountCamera packet, MessageContext context) {
		int x = packet.x;
		int y = packet.y;
		int z = packet.z;
		BlockPos pos = new BlockPos(x, y, z);
		EntityPlayer player = context.getServerHandler().playerEntity;
	
		if(getWorld(player).getBlockState(pos).getBlock() == mod_SecurityCraft.securityCamera){
			((BlockSecurityCamera) getWorld(player).getBlockState(pos).getBlock()).mountCamera(getWorld(player), pos, player);
		}
		
		return null;
	}
}
	
}

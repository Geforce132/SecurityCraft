package org.freeforums.geforce.securitycraft.network.packets;

import org.freeforums.geforce.securitycraft.interfaces.IExplosive;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSetExplosiveState implements IMessage{
	
	private int x, y, z;
	private String state;
	
	public PacketSetExplosiveState(){
		
	}
	
	public PacketSetExplosiveState(int x, int y, int z, String state){
		this.x = x;
		this.y = y;
		this.z = z;
		this.state = state;
	}

	public void fromBytes(ByteBuf par1ByteBuf) {
		this.x = par1ByteBuf.readInt();
		this.y = par1ByteBuf.readInt();
		this.z = par1ByteBuf.readInt();
		this.state = ByteBufUtils.readUTF8String(par1ByteBuf);
	}
	
	public void toBytes(ByteBuf par1ByteBuf) {
		par1ByteBuf.writeInt(x);
		par1ByteBuf.writeInt(y);
		par1ByteBuf.writeInt(z);
		ByteBufUtils.writeUTF8String(par1ByteBuf, state);
	}
	
public static class Handler extends PacketHelper implements IMessageHandler<PacketSetExplosiveState, IMessage> {
	
	public IMessage onMessage(PacketSetExplosiveState packet, MessageContext context) {
		BlockPos pos = new BlockPos(packet.x, packet.y, packet.z);
		EntityPlayer player = context.getServerHandler().playerEntity;
		
		if(getWorld(player).getBlockState(pos) != null && getWorld(player).getBlockState(pos).getBlock() instanceof IExplosive){
			if(packet.state.equalsIgnoreCase("activate")){
				((IExplosive) getWorld(player).getBlockState(pos).getBlock()).activateMine(getWorld(player), pos);
			}else if(packet.state.equalsIgnoreCase("defuse")){
				((IExplosive) getWorld(player).getBlockState(pos).getBlock()).defuseMine(getWorld(player), pos);
			}else if(packet.state.equalsIgnoreCase("detonate")){
				((IExplosive) getWorld(player).getBlockState(pos).getBlock()).explode(getWorld(player), pos);
			}
		}
		
		return null;
	}

}

}

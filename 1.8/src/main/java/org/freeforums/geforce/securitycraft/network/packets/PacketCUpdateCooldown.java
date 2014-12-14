package org.freeforums.geforce.securitycraft.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;

public class PacketCUpdateCooldown implements IMessage{
	
	private int cooldown;
	
	public PacketCUpdateCooldown(){
		
	}
	
	public PacketCUpdateCooldown(int cooldown){
		this.cooldown = cooldown;
	}

	public void fromBytes(ByteBuf buf) {
		this.cooldown = buf.readInt();
	}

	public void toBytes(ByteBuf buf) {
		buf.writeInt(cooldown);
	}
	
public static class Handler extends PacketHelper implements IMessageHandler<PacketCUpdateCooldown, IMessage>{

	@SideOnly(Side.CLIENT)
	public IMessage onMessage(PacketCUpdateCooldown message, MessageContext ctx) {
		mod_SecurityCraft.eventHandler.setCooldown(message.cooldown);
		return null;
	}
	
}

}

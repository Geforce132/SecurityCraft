package net.breakinbad.securitycraft.network.packets;

import io.netty.buffer.ByteBuf;
import net.breakinbad.securitycraft.main.mod_SecurityCraft;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

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

package net.geforcemods.securitycraft.network.packets;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.entity.EntitySecurityCamera;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSSetCameraRotation implements IMessage {
	
	private float rotationYaw, rotationPitch;
	
	public PacketSSetCameraRotation(){
		
	}
	
	public PacketSSetCameraRotation(float yaw, float pitch){
		this.rotationYaw = yaw;
		this.rotationPitch = pitch;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.rotationYaw = buf.readFloat();
		this.rotationPitch = buf.readFloat();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeFloat(rotationYaw);
		buf.writeFloat(rotationPitch);
	}
	
public static class Handler extends PacketHelper implements IMessageHandler<PacketSSetCameraRotation, IMessage>{

	@Override
	public IMessage onMessage(PacketSSetCameraRotation packet, MessageContext ctx) {
		EntityPlayer player = ctx.getServerHandler().player;
		
		if(player.getRidingEntity() != null && player.getRidingEntity() instanceof EntitySecurityCamera){
			player.getRidingEntity().rotationYaw = packet.rotationYaw;
			player.getRidingEntity().rotationPitch = packet.rotationPitch;
		}
		
		return null;
	}
	
}

}

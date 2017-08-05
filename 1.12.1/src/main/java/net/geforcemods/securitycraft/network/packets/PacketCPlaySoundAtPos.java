package net.geforcemods.securitycraft.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketCPlaySoundAtPos implements IMessage{
	
	private int x, y, z;
	private String sound;
	private double volume;
	
	public PacketCPlaySoundAtPos(){
		
	}
	
	public PacketCPlaySoundAtPos(int par1, int par2, int par3, String par4String, double par5){
		this.x = par1;
		this.y = par2;
		this.z = par3;
		this.sound = par4String;
		this.volume = par5;
	}
	
	public PacketCPlaySoundAtPos(double par1, double par2, double par3, String par4String, double par5){
		this.x = (int) par1;
		this.y = (int) par2;
		this.z = (int) par3;
		this.sound = par4String;
		this.volume = par5;
	}
	
	public PacketCPlaySoundAtPos(int par1, int par2, int par3, ResourceLocation par4ResourceLocation, double par5){
		this.x = par1;
		this.y = par2;
		this.z = par3;
		this.sound = par4ResourceLocation.getResourcePath();
		this.volume = par5;
	}
	
	public PacketCPlaySoundAtPos(double par1, double par2, double par3, ResourceLocation par4ResourceLocation, double par5){
		this.x = (int) par1;
		this.y = (int) par2;
		this.z = (int) par3;
		this.sound = par4ResourceLocation.getResourcePath();
		this.volume = par5;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.x = buf.readInt();
		this.y = buf.readInt();
		this.z = buf.readInt();
		this.sound = ByteBufUtils.readUTF8String(buf);
		this.volume = buf.readDouble();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.x);
		buf.writeInt(this.y);
		buf.writeInt(this.z);
		ByteBufUtils.writeUTF8String(buf, this.sound);
		buf.writeDouble(this.volume);
	}
	
public static class Handler extends PacketHelper implements IMessageHandler<PacketCPlaySoundAtPos, IMessage> {

	@Override
	@SideOnly(Side.CLIENT)
	public IMessage onMessage(PacketCPlaySoundAtPos message, MessageContext ctx) {
		Minecraft.getMinecraft().world.playSound(Minecraft.getMinecraft().player, new BlockPos(message.x, message.y, message.z), new SoundEvent(new ResourceLocation(message.sound)), SoundCategory.BLOCKS, (float) message.volume, 1.0F);
		return null;
	}
	
}

}

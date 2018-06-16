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
	private String category;

	public PacketCPlaySoundAtPos(){

	}

	public PacketCPlaySoundAtPos(int par1, int par2, int par3, String par4String, double par5, String cat){
		x = par1;
		y = par2;
		z = par3;
		sound = par4String;
		volume = par5;
		category = cat;
	}

	public PacketCPlaySoundAtPos(double par1, double par2, double par3, String par4String, double par5, String cat){
		x = (int) par1;
		y = (int) par2;
		z = (int) par3;
		sound = par4String;
		volume = par5;
		category = cat;
	}

	public PacketCPlaySoundAtPos(int par1, int par2, int par3, ResourceLocation par4ResourceLocation, double par5, String cat){
		x = par1;
		y = par2;
		z = par3;
		sound = par4ResourceLocation.getResourcePath();
		volume = par5;
		category = cat;
	}

	public PacketCPlaySoundAtPos(double par1, double par2, double par3, ResourceLocation par4ResourceLocation, double par5, String cat){
		x = (int) par1;
		y = (int) par2;
		z = (int) par3;
		sound = par4ResourceLocation.getResourcePath();
		volume = par5;
		category = cat;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		sound = ByteBufUtils.readUTF8String(buf);
		volume = buf.readDouble();
		category = ByteBufUtils.readUTF8String(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		ByteBufUtils.writeUTF8String(buf, sound);
		buf.writeDouble(volume);
		ByteBufUtils.writeUTF8String(buf, category);
	}

	public static class Handler extends PacketHelper implements IMessageHandler<PacketCPlaySoundAtPos, IMessage> {

		@Override
		@SideOnly(Side.CLIENT)
		public IMessage onMessage(PacketCPlaySoundAtPos message, MessageContext ctx) {
			Minecraft.getMinecraft().theWorld.playSound(Minecraft.getMinecraft().thePlayer, new BlockPos(message.x, message.y, message.z), new SoundEvent(new ResourceLocation(message.sound)), SoundCategory.getByName(message.category), (float) message.volume, 1.0F);
			return null;
		}

	}

}

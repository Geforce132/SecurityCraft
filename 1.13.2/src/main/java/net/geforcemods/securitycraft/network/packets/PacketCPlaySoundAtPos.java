package net.geforcemods.securitycraft.network.packets;

import io.netty.buffer.ByteBuf;
import javafx.geometry.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketCPlaySoundAtPos implements IMessage{

	private int x, y, z;
	private String sound;
	private double volume;
	private String category;

	public PacketCPlaySoundAtPos(){

	}

	public PacketCPlaySoundAtPos(int x, int y, int z, String sound, double volume, String cat){
		this.x = x;
		this.y = y;
		this.z = z;
		this.sound = sound;
		this.volume = volume;
		category = cat;
	}

	public PacketCPlaySoundAtPos(double x, double y, double z, String sound, double volume, String cat){
		this((int)x, (int)y, (int)z, sound, volume, cat);
	}

	public PacketCPlaySoundAtPos(int x, int y, int z, ResourceLocation resourceLocation, double volume, String cat){
		this(x, y, z, resourceLocation.getPath(), volume, cat);
	}

	public PacketCPlaySoundAtPos(double x, double y, double z, ResourceLocation resourceLocation, double volume, String cat){
		this((int)x, (int)y, (int)z, resourceLocation.getPath(), volume, cat);
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
			Minecraft.getMinecraft().world.playSound(Minecraft.getMinecraft().player, new BlockPos(message.x, message.y, message.z), new SoundEvent(new ResourceLocation(message.sound)), SoundCategory.getByName(message.category), (float) message.volume, 1.0F);
			return null;
		}

	}

}

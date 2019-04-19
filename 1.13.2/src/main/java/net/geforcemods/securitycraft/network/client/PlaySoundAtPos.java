package net.geforcemods.securitycraft.network.client;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

public class PlaySoundAtPos{

	private int x, y, z;
	private String sound;
	private double volume;
	private String category;

	public PlaySoundAtPos(){

	}

	public PlaySoundAtPos(int x, int y, int z, String sound, double volume, String cat){
		this.x = x;
		this.y = y;
		this.z = z;
		this.sound = sound;
		this.volume = volume;
		category = cat;
	}

	public PlaySoundAtPos(double x, double y, double z, String sound, double volume, String cat){
		this((int)x, (int)y, (int)z, sound, volume, cat);
	}

	public PlaySoundAtPos(int x, int y, int z, ResourceLocation resourceLocation, double volume, String cat){
		this(x, y, z, resourceLocation.getPath(), volume, cat);
	}

	public PlaySoundAtPos(double x, double y, double z, ResourceLocation resourceLocation, double volume, String cat){
		this((int)x, (int)y, (int)z, resourceLocation.getPath(), volume, cat);
	}

	public void fromBytes(PacketBuffer buf) {
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		sound = buf.readString(Integer.MAX_VALUE / 4);
		volume = buf.readDouble();
		category = buf.readString(Integer.MAX_VALUE / 4);
	}

	public void toBytes(PacketBuffer buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeString(sound);
		buf.writeDouble(volume);
		buf.writeString(category);
	}

	public static void encode(PlaySoundAtPos message, PacketBuffer packet)
	{
		message.toBytes(packet);
	}

	public static PlaySoundAtPos decode(PacketBuffer packet)
	{
		PlaySoundAtPos message = new PlaySoundAtPos();

		message.fromBytes(packet);
		return message;
	}

	public static void onMessage(PlaySoundAtPos message, Supplier<NetworkEvent.Context> ctx)
	{
		DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> handleMessage(message, ctx));
	}

	@OnlyIn(Dist.CLIENT)
	public static void handleMessage(PlaySoundAtPos message, Supplier<NetworkEvent.Context> ctx) {
		Minecraft.getInstance().world.playSound(Minecraft.getInstance().player, new BlockPos(message.x, message.y, message.z), new SoundEvent(new ResourceLocation(message.sound)), SoundCategory.valueOf(message.category.toUpperCase()), (float) message.volume, 1.0F);
		ctx.get().setPacketHandled(true);
	}
}

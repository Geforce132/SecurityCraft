package net.geforcemods.securitycraft.network.client;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.SecurityCraft;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.core.BlockPos;
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

	public static void encode(PlaySoundAtPos message, FriendlyByteBuf buf)
	{
		buf.writeInt(message.x);
		buf.writeInt(message.y);
		buf.writeInt(message.z);
		buf.writeUtf(message.sound);
		buf.writeDouble(message.volume);
		buf.writeUtf(message.category);
	}

	public static PlaySoundAtPos decode(FriendlyByteBuf buf)
	{
		PlaySoundAtPos message = new PlaySoundAtPos();

		message.x = buf.readInt();
		message.y = buf.readInt();
		message.z = buf.readInt();
		message.sound = buf.readUtf(Integer.MAX_VALUE / 4);
		message.volume = buf.readDouble();
		message.category = buf.readUtf(Integer.MAX_VALUE / 4);
		return message;
	}

	public static void onMessage(PlaySoundAtPos message, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() -> {
			Player player = SecurityCraft.proxy.getClientPlayer();
			BlockPos pos = player.blockPosition();
			BlockPos origin = new BlockPos(message.x, message.y, message.z);
			int dist = Math.max(0, Math.min(pos.distManhattan(origin), 20)); //clamp between 0 and 20
			float volume = (float)(message.volume * (1 - ((float)dist / 20))); //the further away the quieter

			Minecraft.getInstance().level.playSound(player, origin, new SoundEvent(new ResourceLocation(message.sound)), SoundSource.valueOf(message.category.toUpperCase()), volume, 1.0F);
		});

		ctx.get().setPacketHandled(true);
	}
}

package net.geforcemods.securitycraft.network.client;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.blockentities.AlarmBlockEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

public class PlayAlarmSound {
	private BlockPos bePos;
	private SoundEvent sound;
	private int soundX, soundY, soundZ;
	private float volume;

	public PlayAlarmSound() {}

	public PlayAlarmSound(BlockPos bePos, SoundEvent sound, float volume) {
		this.bePos = bePos;
		this.sound = sound;
		this.soundX = (int) (bePos.getX() * 8.0F);
		this.soundY = (int) (bePos.getY() * 8.0F);
		this.soundZ = (int) (bePos.getZ() * 8.0F);
		this.volume = volume;
	}

	public static void encode(PlayAlarmSound message, PacketBuffer buf) {
		buf.writeBlockPos(message.bePos);
		buf.writeResourceLocation(ForgeRegistries.SOUND_EVENTS.getKey(message.sound));
		buf.writeInt(message.soundX);
		buf.writeInt(message.soundY);
		buf.writeInt(message.soundZ);
		buf.writeFloat(message.volume);
	}

	public static PlayAlarmSound decode(PacketBuffer buf) {
		PlayAlarmSound message = new PlayAlarmSound();

		message.bePos = buf.readBlockPos();
		message.sound = ForgeRegistries.SOUND_EVENTS.getValue(buf.readResourceLocation());
		message.soundX = buf.readInt();
		message.soundY = buf.readInt();
		message.soundZ = buf.readInt();
		message.volume = buf.readFloat();
		return message;
	}

	public double getX() {
		return soundX / 8.0F;
	}

	public double getY() {
		return soundY / 8.0F;
	}

	public double getZ() {
		return soundZ / 8.0F;
	}

	public static void onMessage(PlayAlarmSound message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			World level = ClientHandler.getClientLevel();
			TileEntity te = level.getBlockEntity(message.bePos);

			if (te instanceof AlarmBlockEntity) {
				((AlarmBlockEntity) te).setPowered(true);
				((AlarmBlockEntity) te).playSound(level, message.getX(), message.getY(), message.getZ(), message.sound, message.volume);
			}
		});

		ctx.get().setPacketHandled(true);
	}
}

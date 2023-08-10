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
	private float volume, pitch;

	public PlayAlarmSound() {}

	public PlayAlarmSound(BlockPos bePos, SoundEvent sound, float volume, float pitch) {
		this.bePos = bePos;
		this.sound = sound;
		this.soundX = (int) (bePos.getX() * 8.0F);
		this.soundY = (int) (bePos.getY() * 8.0F);
		this.soundZ = (int) (bePos.getZ() * 8.0F);
		this.volume = volume;
		this.pitch = pitch;
	}

	public PlayAlarmSound(PacketBuffer buf) {
		bePos = buf.readBlockPos();
		sound = ForgeRegistries.SOUND_EVENTS.getValue(buf.readResourceLocation());
		soundX = buf.readInt();
		soundY = buf.readInt();
		soundZ = buf.readInt();
		volume = buf.readFloat();
		pitch = buf.readFloat();
	}

	public void encode(PacketBuffer buf) {
		buf.writeBlockPos(bePos);
		buf.writeResourceLocation(ForgeRegistries.SOUND_EVENTS.getKey(sound));
		buf.writeInt(soundX);
		buf.writeInt(soundY);
		buf.writeInt(soundZ);
		buf.writeFloat(volume);
		buf.writeFloat(pitch);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		World level = ClientHandler.getClientLevel();
		TileEntity te = level.getBlockEntity(bePos);

		if (te instanceof AlarmBlockEntity) {
			((AlarmBlockEntity) te).setPowered(true);
			((AlarmBlockEntity) te).playSound(getX(), getY(), getZ(), sound, volume, pitch);
		}
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
}

package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.blockentities.AlarmBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

public class SyncAlarmSettings {
	private BlockPos pos;
	private ResourceLocation soundEvent;
	private float pitch;
	private int soundLength;

	public SyncAlarmSettings() {}

	public SyncAlarmSettings(BlockPos pos, ResourceLocation soundEvent, float pitch, int soundLength) {
		this.pos = pos;
		this.soundEvent = soundEvent;
		this.pitch = pitch;
		this.soundLength = soundLength;
	}

	public SyncAlarmSettings(PacketBuffer buf) {
		pos = BlockPos.of(buf.readLong());
		soundEvent = buf.readResourceLocation();
		pitch = buf.readFloat();
		soundLength = buf.readVarInt();
	}

	public void encode(PacketBuffer buf) {
		buf.writeLong(pos.asLong());
		buf.writeResourceLocation(soundEvent);
		buf.writeFloat(pitch);
		buf.writeVarInt(soundLength);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		PlayerEntity player = ctx.get().getSender();
		TileEntity te = player.level.getBlockEntity(pos);

		if (!player.isSpectator() && te instanceof AlarmBlockEntity) {
			AlarmBlockEntity be = (AlarmBlockEntity) te;

			if (be.isOwnedBy(player)) {
				if (!soundEvent.equals(be.getSound().location))
					be.setSound(soundEvent);

				if (pitch != be.getPitch())
					be.setPitch(pitch);

				if (soundLength != be.getSoundLength())
					be.setSoundLength(soundLength);
			}
		}
	}
}

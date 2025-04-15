package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.blockentities.AlarmBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

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

	public SyncAlarmSettings(FriendlyByteBuf buf) {
		pos = BlockPos.of(buf.readLong());
		soundEvent = buf.readResourceLocation();
		pitch = buf.readFloat();
		soundLength = buf.readVarInt();
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeLong(pos.asLong());
		buf.writeResourceLocation(soundEvent);
		buf.writeFloat(pitch);
		buf.writeVarInt(soundLength);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		Player player = ctx.get().getSender();

		if (!player.isSpectator() && player.level().getBlockEntity(pos) instanceof AlarmBlockEntity be && be.isOwnedBy(player)) {
			if (!soundEvent.equals(be.getSound().getLocation()))
				be.setSound(soundEvent);

			if (pitch != be.getPitch())
				be.setPitch(pitch);

			if (soundLength != be.getSoundLength())
				be.setSoundLength(soundLength);
		}
	}
}

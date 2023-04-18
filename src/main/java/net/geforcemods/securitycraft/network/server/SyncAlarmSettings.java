package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.blockentities.AlarmBlockEntity;
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

	public static void encode(SyncAlarmSettings message, PacketBuffer buf) {
		buf.writeLong(message.pos.asLong());
		buf.writeResourceLocation(message.soundEvent);
		buf.writeFloat(message.pitch);
		buf.writeVarInt(message.soundLength);
	}

	public static SyncAlarmSettings decode(PacketBuffer buf) {
		SyncAlarmSettings message = new SyncAlarmSettings();

		message.pos = BlockPos.of(buf.readLong());
		message.soundEvent = buf.readResourceLocation();
		message.pitch = buf.readFloat();
		message.soundLength = buf.readVarInt();
		return message;
	}

	public static void onMessage(SyncAlarmSettings message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			TileEntity tile = ctx.get().getSender().level.getBlockEntity(message.pos);

			if (tile instanceof AlarmBlockEntity) {
				AlarmBlockEntity be = (AlarmBlockEntity) tile;

				if (be.isOwnedBy(ctx.get().getSender())) {
					if (!message.soundEvent.equals(be.getSound().location))
						be.setSound(message.soundEvent);

				if (message.pitch != be.getPitch())
					be.setPitch(message.pitch);

					if (message.soundLength != be.getSoundLength())
						be.setSoundLength(message.soundLength);
				}
			}
		});
		ctx.get().setPacketHandled(true);
	}
}

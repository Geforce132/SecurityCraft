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
	private int soundLength;

	public SyncAlarmSettings() {}

	public SyncAlarmSettings(BlockPos pos, ResourceLocation soundEvent, int soundLength) {
		this.pos = pos;
		this.soundEvent = soundEvent;
		this.soundLength = soundLength;
	}

	public static void encode(SyncAlarmSettings message, PacketBuffer buf) {
		buf.writeLong(message.pos.asLong());
		buf.writeResourceLocation(message.soundEvent);
		buf.writeVarInt(message.soundLength);
	}

	public static SyncAlarmSettings decode(PacketBuffer buf) {
		SyncAlarmSettings message = new SyncAlarmSettings();

		message.pos = BlockPos.of(buf.readLong());
		message.soundEvent = buf.readResourceLocation();
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

					if (message.soundLength != be.getSoundLength())
						be.setSoundLength(message.soundLength);
				}
			}
		});
		ctx.get().setPacketHandled(true);
	}
}

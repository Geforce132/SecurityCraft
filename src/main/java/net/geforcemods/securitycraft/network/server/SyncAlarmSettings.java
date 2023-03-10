package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.blockentities.AlarmBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

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

	public static void encode(SyncAlarmSettings message, FriendlyByteBuf buf) {
		buf.writeLong(message.pos.asLong());
		buf.writeResourceLocation(message.soundEvent);
		buf.writeVarInt(message.soundLength);
	}

	public static SyncAlarmSettings decode(FriendlyByteBuf buf) {
		SyncAlarmSettings message = new SyncAlarmSettings();

		message.pos = BlockPos.of(buf.readLong());
		message.soundEvent = buf.readResourceLocation();
		message.soundLength = buf.readVarInt();
		return message;
	}

	public static void onMessage(SyncAlarmSettings message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			if (ctx.get().getSender().level.getBlockEntity(message.pos) instanceof AlarmBlockEntity be && be.isOwnedBy(ctx.get().getSender())) {
				if (!message.soundEvent.equals(be.getSound().getLocation()))
					be.setSound(message.soundEvent);

				if (message.soundLength != be.getSoundLength())
					be.setSoundLength(message.soundLength);
			}
		});
		ctx.get().setPacketHandled(true);
	}
}

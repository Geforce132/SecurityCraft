package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.blockentities.AlarmBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

public class SetAlarmSound {
	private BlockPos pos;
	private ResourceLocation soundEvent;

	public SetAlarmSound() {}

	public SetAlarmSound(BlockPos pos, ResourceLocation soundEvent) {
		this.pos = pos;
		this.soundEvent = soundEvent;
	}

	public static void encode(SetAlarmSound message, FriendlyByteBuf buf) {
		buf.writeLong(message.pos.asLong());
		buf.writeResourceLocation(message.soundEvent);
	}

	public static SetAlarmSound decode(FriendlyByteBuf buf) {
		SetAlarmSound message = new SetAlarmSound();

		message.pos = BlockPos.of(buf.readLong());
		message.soundEvent = buf.readResourceLocation();
		return message;
	}

	public static void onMessage(SetAlarmSound message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			if (ctx.get().getSender().level.getBlockEntity(message.pos) instanceof AlarmBlockEntity be && be.isOwnedBy(ctx.get().getSender()))
				be.setSound(message.soundEvent);
		});
		ctx.get().setPacketHandled(true);
	}
}

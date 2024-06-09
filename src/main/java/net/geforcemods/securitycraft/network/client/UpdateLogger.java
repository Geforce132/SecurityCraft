package net.geforcemods.securitycraft.network.client;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.UsernameLoggerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record UpdateLogger(BlockPos pos, int index, String username, String uuid, long timestamp) implements CustomPacketPayload {

	public static final Type<UpdateLogger> TYPE = new Type<>(SecurityCraft.resLoc("update_logger"));
	//@formatter:off
	public static final StreamCodec<RegistryFriendlyByteBuf, UpdateLogger> STREAM_CODEC = StreamCodec.composite(
			BlockPos.STREAM_CODEC, UpdateLogger::pos,
			ByteBufCodecs.VAR_INT, UpdateLogger::index,
			ByteBufCodecs.STRING_UTF8, UpdateLogger::username,
			ByteBufCodecs.STRING_UTF8, UpdateLogger::uuid,
			ByteBufCodecs.VAR_LONG, UpdateLogger::timestamp,
			UpdateLogger::new);
	//@formatter:on
	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public void handle(IPayloadContext ctx) {
		UsernameLoggerBlockEntity be = (UsernameLoggerBlockEntity) ctx.player().level().getBlockEntity(pos);

		if (be != null) {
			be.getPlayers()[index] = username;
			be.getUuids()[index] = uuid;
			be.getTimestamps()[index] = timestamp;
		}
	}
}

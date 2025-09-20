package net.geforcemods.securitycraft.network.server;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.SecureTradingStationBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ToggleSecureTradingStation(BlockPos pos, int requestedTransactions) implements CustomPacketPayload {
	public static final Type<ToggleSecureTradingStation> TYPE = new Type<>(SecurityCraft.resLoc("toggle_secure_trading_station"));
	//@formatter:off
	public static final StreamCodec<RegistryFriendlyByteBuf, ToggleSecureTradingStation> STREAM_CODEC = StreamCodec.composite(
			BlockPos.STREAM_CODEC, ToggleSecureTradingStation::pos,
			ByteBufCodecs.VAR_INT, ToggleSecureTradingStation::requestedTransactions,
			ToggleSecureTradingStation::new);
	//@formatter:on

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public void handle(IPayloadContext ctx) {
		Player player = ctx.player();

		if (player.level().getBlockEntity(pos) instanceof SecureTradingStationBlockEntity be)
			be.doTransaction(player, requestedTransactions);
	}
}

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

public record RequestSecureTradingStationTransactions(BlockPos pos, int requestedTransactions) implements CustomPacketPayload {
	public static final Type<RequestSecureTradingStationTransactions> TYPE = new Type<>(SecurityCraft.resLoc("request_secure_trading_station_transactions"));
	//@formatter:off
	public static final StreamCodec<RegistryFriendlyByteBuf, RequestSecureTradingStationTransactions> STREAM_CODEC = StreamCodec.composite(
			BlockPos.STREAM_CODEC, RequestSecureTradingStationTransactions::pos,
			ByteBufCodecs.VAR_INT, RequestSecureTradingStationTransactions::requestedTransactions,
			RequestSecureTradingStationTransactions::new);
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

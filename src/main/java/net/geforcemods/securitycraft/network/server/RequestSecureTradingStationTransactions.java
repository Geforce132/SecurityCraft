package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.blockentities.SecureTradingStationBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent.Context;

public class RequestSecureTradingStationTransactions {
	private final BlockPos pos;
	private final int requestedTransactions;

	public RequestSecureTradingStationTransactions(BlockPos pos, int requestedTransactions) {
		this.pos = pos;
		this.requestedTransactions = requestedTransactions;
	}

	public RequestSecureTradingStationTransactions(FriendlyByteBuf buf) {
		pos = buf.readBlockPos();
		requestedTransactions = buf.readVarInt();
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeBlockPos(pos);
		buf.writeVarInt(requestedTransactions);
	}

	public void handle(Supplier<Context> ctx) {
		Player player = ctx.get().getSender();

		if (player.level().getBlockEntity(pos) instanceof SecureTradingStationBlockEntity be)
			be.doTransaction(player, requestedTransactions);
	}
}


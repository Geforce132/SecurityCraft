package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.blockentities.UsernameLoggerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

public class ClearLoggerServer {
	private BlockPos pos;

	public ClearLoggerServer() {}

	public ClearLoggerServer(BlockPos pos) {
		this.pos = pos;
	}

	public ClearLoggerServer(FriendlyByteBuf buf) {
		pos = buf.readBlockPos();
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeBlockPos(pos);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		ServerPlayer player = ctx.get().getSender();

		if (!player.isSpectator() && player.level().getBlockEntity(pos) instanceof UsernameLoggerBlockEntity be && be.isOwnedBy(player)) {
			be.setPlayers(new String[100]);
			be.getLevel().sendBlockUpdated(be.getBlockPos(), be.getBlockState(), be.getBlockState(), 2);
		}
	}
}

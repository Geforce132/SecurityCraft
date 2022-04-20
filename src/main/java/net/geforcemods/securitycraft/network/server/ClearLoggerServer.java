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

	public static void encode(ClearLoggerServer message, FriendlyByteBuf buf) {
		buf.writeBlockPos(message.pos);
	}

	public static ClearLoggerServer decode(FriendlyByteBuf buf) {
		ClearLoggerServer message = new ClearLoggerServer();

		message.pos = buf.readBlockPos();
		return message;
	}

	public static void onMessage(ClearLoggerServer message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayer player = ctx.get().getSender();

			if (player.level.getBlockEntity(message.pos) instanceof UsernameLoggerBlockEntity be && be.getOwner().isOwner(player)) {
				be.players = new String[100];
				be.getLevel().sendBlockUpdated(be.getBlockPos(), be.getBlockState(), be.getBlockState(), 2);
			}
		});

		ctx.get().setPacketHandled(true);
	}
}

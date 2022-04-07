package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.blockentities.BlockChangeDetectorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

public class ClearChangeDetectorServer {
	private BlockPos pos;

	public ClearChangeDetectorServer() {}

	public ClearChangeDetectorServer(BlockPos pos) {
		this.pos = pos;
	}

	public static void encode(ClearChangeDetectorServer message, FriendlyByteBuf buf) {
		buf.writeBlockPos(message.pos);
	}

	public static ClearChangeDetectorServer decode(FriendlyByteBuf buf) {
		ClearChangeDetectorServer message = new ClearChangeDetectorServer();

		message.pos = buf.readBlockPos();
		return message;
	}

	public static void onMessage(ClearChangeDetectorServer message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayer player = ctx.get().getSender();

			if (player.level.getBlockEntity(message.pos) instanceof BlockChangeDetectorBlockEntity be && be.getOwner().isOwner(player)) {
				be.getEntries().clear();
				be.setChanged();
			}
		});

		ctx.get().setPacketHandled(true);
	}
}

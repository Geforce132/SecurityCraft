package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.blockentities.RiftStabilizerBlockEntity;
import net.geforcemods.securitycraft.blockentities.RiftStabilizerBlockEntity.TeleportationType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fmllegacy.network.NetworkEvent.Context;

public class SyncRiftStabilizer {
	private BlockPos pos;
	private TeleportationType teleportationType;
	private boolean allowed;

	public SyncRiftStabilizer() {}

	public SyncRiftStabilizer(BlockPos pos, TeleportationType teleportationType, boolean allowed) {
		this.pos = pos;
		this.teleportationType = teleportationType;
		this.allowed = allowed;
	}

	public static void encode(SyncRiftStabilizer message, FriendlyByteBuf buf) {
		buf.writeBlockPos(message.pos);
		buf.writeEnum(message.teleportationType);
		buf.writeBoolean(message.allowed);
	}

	public static SyncRiftStabilizer decode(FriendlyByteBuf buf) {
		SyncRiftStabilizer message = new SyncRiftStabilizer();

		message.pos = buf.readBlockPos();
		message.teleportationType = buf.readEnum(TeleportationType.class);
		message.allowed = buf.readBoolean();
		return message;
	}

	public static void onMessage(SyncRiftStabilizer message, Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> {
			if (message.teleportationType != null) {
				Level level = ctx.get().getSender().level;
				BlockPos pos = message.pos;
				boolean allowed = message.allowed;

				if (level.getBlockEntity(pos) instanceof RiftStabilizerBlockEntity be && be.isOwnedBy(ctx.get().getSender())) {
					BlockState state = level.getBlockState(pos);

					be.setFilter(message.teleportationType, allowed);
					level.sendBlockUpdated(pos, state, state, 2);
				}
			}
		});

		ctx.get().setPacketHandled(true);
	}
}

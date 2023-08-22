package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.blockentities.RiftStabilizerBlockEntity;
import net.geforcemods.securitycraft.blockentities.RiftStabilizerBlockEntity.TeleportationType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkEvent.Context;

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

	public SyncRiftStabilizer(FriendlyByteBuf buf) {
		pos = buf.readBlockPos();
		teleportationType = buf.readEnum(TeleportationType.class);
		allowed = buf.readBoolean();
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeBlockPos(pos);
		buf.writeEnum(teleportationType);
		buf.writeBoolean(allowed);
	}

	public void handle(Supplier<Context> ctx) {
		if (teleportationType != null) {
			Level level = ctx.get().getSender().level();

			if (level.getBlockEntity(pos) instanceof RiftStabilizerBlockEntity be && be.isOwnedBy(ctx.get().getSender())) {
				BlockState state = level.getBlockState(pos);

				be.setFilter(teleportationType, allowed);
				level.sendBlockUpdated(pos, state, state, 2);
			}
		}
	}
}

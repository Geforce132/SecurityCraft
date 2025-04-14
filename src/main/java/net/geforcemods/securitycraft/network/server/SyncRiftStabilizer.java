package net.geforcemods.securitycraft.network.server;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.RiftStabilizerBlockEntity;
import net.geforcemods.securitycraft.blockentities.RiftStabilizerBlockEntity.TeleportationType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class SyncRiftStabilizer implements CustomPacketPayload {
	public static final ResourceLocation ID = new ResourceLocation(SecurityCraft.MODID, "sync_rift_stabilizer");
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

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeBlockPos(pos);
		buf.writeEnum(teleportationType);
		buf.writeBoolean(allowed);
	}

	@Override
	public ResourceLocation id() {
		return ID;
	}

	public void handle(PlayPayloadContext ctx) {
		if (teleportationType != null) {
			Player player = ctx.player().orElseThrow();
			Level level = player.level();

			if (!player.isSpectator() && level.getBlockEntity(pos) instanceof RiftStabilizerBlockEntity be && be.isOwnedBy(player)) {
				BlockState state = level.getBlockState(pos);

				be.setFilter(teleportationType, allowed);
				level.sendBlockUpdated(pos, state, state, 2);
			}
		}
	}
}

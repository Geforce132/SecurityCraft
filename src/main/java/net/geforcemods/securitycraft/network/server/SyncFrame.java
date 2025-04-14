package net.geforcemods.securitycraft.network.server;

import java.util.Optional;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.FrameBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class SyncFrame implements CustomPacketPayload {
	public static final ResourceLocation ID = new ResourceLocation(SecurityCraft.MODID, "sync_frame");
	private BlockPos pos;
	private int requestedRenderDistance;
	private Optional<GlobalPos> removedCamera;
	private Optional<GlobalPos> currentCamera;
	boolean disableCurrentCamera;

	public SyncFrame() {}

	public SyncFrame(BlockPos pos, int requestedRenderDistance, Optional<GlobalPos> removedCamera, Optional<GlobalPos> currentCamera, boolean disableCurrentCamera) {
		this.pos = pos;
		this.requestedRenderDistance = requestedRenderDistance;
		this.removedCamera = removedCamera;
		this.currentCamera = currentCamera;
		this.disableCurrentCamera = disableCurrentCamera;
	}

	public SyncFrame(FriendlyByteBuf buf) {
		pos = buf.readBlockPos();
		requestedRenderDistance = buf.readVarInt();
		removedCamera = buf.readOptional(FriendlyByteBuf::readGlobalPos);
		currentCamera = buf.readOptional(FriendlyByteBuf::readGlobalPos);
		disableCurrentCamera = buf.readBoolean();
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeBlockPos(pos);
		buf.writeVarInt(requestedRenderDistance);
		buf.writeOptional(removedCamera, FriendlyByteBuf::writeGlobalPos);
		buf.writeOptional(currentCamera, FriendlyByteBuf::writeGlobalPos);
		buf.writeBoolean(disableCurrentCamera);
	}

	@Override
	public ResourceLocation id() {
		return ID;
	}

	public void handle(IPayloadContext ctx) {
		Player player = ctx.player().orElseThrow();
		Level level = player.level();
		GlobalPos currentCamera = this.currentCamera.orElse(null);

		if (!player.isSpectator() && level.getBlockEntity(pos) instanceof FrameBlockEntity be) {
			if (be.isDisabled())
				return;

			boolean isOwner = be.isOwnedBy(player);

			if (isOwner)
				removedCamera.ifPresent(be::removeCamera);

			if (isOwner || be.isAllowed(player))
				be.switchCameras(currentCamera, player, requestedRenderDistance, disableCurrentCamera);
		}
	}
}

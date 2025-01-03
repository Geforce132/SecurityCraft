package net.geforcemods.securitycraft.network.server;

import java.util.Optional;
import java.util.function.Supplier;

import net.geforcemods.securitycraft.blockentities.FrameBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent.Context;

public class SyncFrame {
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
		requestedRenderDistance = buf.readInt();
		removedCamera = buf.readOptional(FriendlyByteBuf::readGlobalPos);
		currentCamera = buf.readOptional(FriendlyByteBuf::readGlobalPos);
		disableCurrentCamera = buf.readBoolean();
	}
	
	public void encode(FriendlyByteBuf buf) {
		buf.writeBlockPos(pos);
		buf.writeInt(requestedRenderDistance);
		buf.writeOptional(removedCamera, FriendlyByteBuf::writeGlobalPos);
		buf.writeOptional(currentCamera, FriendlyByteBuf::writeGlobalPos);
		buf.writeBoolean(disableCurrentCamera);
	}

	public void handle(Supplier<Context> ctx) {
		Player player = ctx.get().getSender();
		Level level = player.level();
		GlobalPos currentCamera = this.currentCamera.orElse(null);

		if (level.getBlockEntity(pos) instanceof FrameBlockEntity be) {
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

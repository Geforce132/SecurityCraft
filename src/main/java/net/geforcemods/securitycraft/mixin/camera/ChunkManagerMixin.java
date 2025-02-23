package net.geforcemods.securitycraft.mixin.camera;

import java.util.HashSet;
import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity.ChunkTrackingView;
import net.geforcemods.securitycraft.entity.camera.SecurityCamera;
import net.geforcemods.securitycraft.misc.BlockEntityTracker;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.IPacket;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ChunkManager;

/**
 * This mixin makes sure that chunks near cameras are properly sent to the player viewing it, as well as fixing block updates
 * not getting sent to chunks loaded by cameras
 */
@Mixin(value = ChunkManager.class, priority = 1100)
public abstract class ChunkManagerMixin {
	@Shadow
	private int viewDistance;

	@Shadow
	protected abstract void updateChunkTracking(ServerPlayerEntity player, ChunkPos chunkPos, IPacket<?>[] packetCache, boolean wasLoaded, boolean load);

	/**
	 * Fixes block updates not being sent to chunks loaded by cameras. For mounted cameras, this is being done by returning
	 * the camera's SectionPos to the distance check. For frame cameras, it is checked whether any frame camera is close enough
	 * to the given chunk pos, and if so, the chunk pos itself is returned to make the distance check always pass.
	 */
	@Redirect(method = "checkerboardDistance(Lnet/minecraft/util/math/ChunkPos;Lnet/minecraft/entity/player/ServerPlayerEntity;Z)I", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/ServerPlayerEntity;getLastSectionPos()Lnet/minecraft/util/math/SectionPos;"))
	private static SectionPos securitycraft$getCameraSectionPos(ServerPlayerEntity player, ChunkPos chunkPos) {
		if (PlayerUtils.isPlayerMountedOnCamera(player))
			return SectionPos.of(player.getCamera());
		else if (!BlockEntityTracker.FRAME_VIEWED_SECURITY_CAMERAS.getBlockEntitiesWithCondition(player.level, camera -> camera.shouldKeepChunkTracked(player, chunkPos.x, chunkPos.z)).isEmpty())
			return SectionPos.of(chunkPos, 0);

		return player.getLastSectionPos();
	}

	/**
	 * Fixes entities not being sent to chunks loaded by cameras. For mounted cameras, this is being done by returning
	 * the camera's SectionPos to the distance check. For frame cameras, it is checked whether any frame camera is close enough
	 * to the given chunk pos, and if so, the chunk pos itself is returned to make the distance check always pass.
	 */
	@ModifyArgs(method = "checkerboardDistance(Lnet/minecraft/util/math/ChunkPos;Lnet/minecraft/entity/player/ServerPlayerEntity;Z)I", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;floor(D)I", ordinal = 0))
	private static void securitycraft$modifyPlayerX(Args args, ChunkPos pos, ServerPlayerEntity player, boolean useSectionPos) {
		if (PlayerUtils.isPlayerMountedOnCamera(player))
			args.set(0, player.getCamera().getX() / 16.0D);
		else if (!BlockEntityTracker.FRAME_VIEWED_SECURITY_CAMERAS.getBlockEntitiesWithCondition(player.level, camera -> camera.shouldKeepChunkTracked(player, pos.x, pos.z)).isEmpty())
			args.set(0, (double) pos.x);
	}

	/**
	 * Fixes entities not being sent to chunks loaded by cameras. For mounted cameras, this is being done by returning
	 * the camera's SectionPos to the distance check. For frame cameras, it is checked whether any frame camera is close enough
	 * to the given chunk pos, and if so, the chunk pos itself is returned to make the distance check always pass.
	 */
	@ModifyArgs(method = "checkerboardDistance(Lnet/minecraft/util/math/ChunkPos;Lnet/minecraft/entity/player/ServerPlayerEntity;Z)I", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;floor(D)I", ordinal = 1))
	private static void securitycraft$modifyPlayerZ(Args args, ChunkPos pos, ServerPlayerEntity player, boolean useSectionPos) {
		if (PlayerUtils.isPlayerMountedOnCamera(player))
			args.set(0, player.getCamera().getZ() / 16.0D);
		else if (!BlockEntityTracker.FRAME_VIEWED_SECURITY_CAMERAS.getBlockEntitiesWithCondition(player.level, camera -> camera.shouldKeepChunkTracked(player, pos.x, pos.z)).isEmpty())
			args.set(0, (double) pos.z);
	}

	/**
	 * Sends chunks loaded by mounted cameras or frame cameras to the client. Also drops chunks that were near a dismounted
	 * camera or a stopped frame camera feed.
	 */
	@Inject(method = "move", at = @At(value = "TAIL"))
	private void securitycraft$trackCameraLoadedChunks(ServerPlayerEntity player, CallbackInfo ci) {
		World level = player.level;
		ChunkPos playerPos = new ChunkPos(player.xChunk, player.zChunk);

		if (player.getCamera() instanceof SecurityCamera) {
			SecurityCamera camera = ((SecurityCamera) player.getCamera());

			if (!camera.hasSentChunks()) {
				SectionPos pos = SectionPos.of(camera);

				for (int i = pos.x() - viewDistance; i <= pos.x() + viewDistance; ++i) {
					for (int j = pos.z() - viewDistance; j <= pos.z() + viewDistance; ++j) {
						if (!Utils.isInViewDistance(playerPos.x, playerPos.z, viewDistance, i, j))
							updateChunkTracking(player, new ChunkPos(i, j), new IPacket[2], false, true);
					}
				}

				camera.setHasSentChunks(true);
			}
		}

		for (SecurityCameraBlockEntity viewedCamera : BlockEntityTracker.FRAME_VIEWED_SECURITY_CAMERAS.getBlockEntitiesWithCondition(level, be -> be.shouldSendChunksToPlayer(player))) {
			SectionPos pos = SectionPos.of(viewedCamera.myPos());
			int cameraViewDistance = viewedCamera.getCameraFeedChunks(player).viewDistance();

			for (int i = pos.x() - cameraViewDistance; i <= pos.x() + cameraViewDistance; ++i) {
				for (int j = pos.z() - cameraViewDistance; j <= pos.z() + cameraViewDistance; ++j) {
					if (!Utils.isInViewDistance(playerPos.x, playerPos.z, viewDistance, i, j))
						updateChunkTracking(player, new ChunkPos(i, j), new IPacket[2], false, true);
				}
			}
		}

		Set<ChunkTrackingView> unviewedChunkViews = new HashSet<>();

		if (SecurityCameraBlockEntity.hasRecentlyUnviewedCameras(player)) {
			for (SecurityCameraBlockEntity viewedCamera : SecurityCameraBlockEntity.fetchRecentlyUnviewedCameras(player)) {
				ChunkTrackingView unviewedChunks = viewedCamera.getCameraFeedChunks(player);

				if (unviewedChunks != null) {
					unviewedChunkViews.add(unviewedChunks);
					viewedCamera.clearCameraFeedChunks(player);
				}
			}
		}

		if (SecurityCamera.hasRecentlyDismounted(player))
			unviewedChunkViews.add(new ChunkTrackingView(new ChunkPos(SecurityCamera.fetchRecentDismountLocation(player)), viewDistance));

		for (ChunkTrackingView unviewedChunkView : unviewedChunkViews) {
			ChunkPos centerPos = unviewedChunkView.center();
			int unViewDistance = unviewedChunkView.viewDistance();

			for (int i = centerPos.x - unViewDistance; i <= centerPos.x + unViewDistance; ++i) {
				for (int j = centerPos.z - unViewDistance; j <= centerPos.z + unViewDistance; ++j) {
					if (!Utils.isInViewDistance(playerPos.x, playerPos.z, viewDistance, i, j) && BlockEntityTracker.FRAME_VIEWED_SECURITY_CAMERAS.getBlockEntitiesWithCondition(level, be -> be.shouldKeepChunkTracked(player, centerPos.x, centerPos.z)).isEmpty())
						updateChunkTracking(player, new ChunkPos(i, j), new IPacket[2], true, false);
				}
			}
		}
	}

	/**
	 * Makes sure that chunks in the view area of a frame do not get dropped when the player moves out of them
	 */
	@Inject(method = "updateChunkTracking", at = @At("HEAD"), cancellable = true)
	private void securitycraft$onDropChunk(ServerPlayerEntity player, ChunkPos pos, IPacket<?>[] packetCache, boolean wasLoaded, boolean load, CallbackInfo ci) {
		if (wasLoaded && !load && !BlockEntityTracker.FRAME_VIEWED_SECURITY_CAMERAS.getBlockEntitiesWithCondition(player.level, be -> be.shouldKeepChunkTracked(player, pos.x, pos.z)).isEmpty())
			ci.cancel();
	}
}

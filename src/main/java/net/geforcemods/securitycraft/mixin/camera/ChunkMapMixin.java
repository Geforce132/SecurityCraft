package net.geforcemods.securitycraft.mixin.camera;

import java.util.HashSet;
import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.sugar.Local;

import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.entity.camera.SecurityCamera;
import net.geforcemods.securitycraft.misc.BlockEntityTracker;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ChunkTrackingView;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;

/**
 * This mixin makes sure that chunks near cameras are properly sent to the player viewing it, as well as fixing block updates
 * not getting sent to chunks loaded by cameras
 */
@Mixin(value = ChunkMap.class, priority = 1100)
public abstract class ChunkMapMixin {
	@Shadow
	protected abstract void markChunkPendingToSend(ServerPlayer player, ChunkPos pos);

	@Shadow
	private static void markChunkPendingToSend(ServerPlayer player, LevelChunk chunk) {}

	@Shadow
	private static void dropChunk(ServerPlayer player, ChunkPos chunkPos) {}

	@Shadow
	abstract int getPlayerViewDistance(ServerPlayer player);

	/**
	 * Sends chunks loaded by mounted cameras or frame cameras to the client. Also drops chunks that were near a dismounted
	 * camera or a stopped frame camera feed.
	 */
	@Inject(method = "updateChunkTracking", at = @At("HEAD"))
	private void securitycraft$onUpdateChunkTracking(ServerPlayer player, CallbackInfo ci) {
		Level level = player.level();
		int viewDistance = getPlayerViewDistance(player);

		if (player.getCamera() instanceof SecurityCamera camera && !camera.hasSentChunks()) {
			ChunkTrackingView.difference(player.getChunkTrackingView(), camera.getCameraChunks(), chunkPos -> markChunkPendingToSend(player, chunkPos), chunkPos -> {});
			camera.setHasSentChunks(true);
		}

		for (SecurityCameraBlockEntity viewedCamera : BlockEntityTracker.FRAME_VIEWED_SECURITY_CAMERAS.getBlockEntitiesWithCondition(level, be -> be.shouldSendChunksToPlayer(player))) {
			ChunkTrackingView.difference(player.getChunkTrackingView(), viewedCamera.getCameraFeedChunks(player), chunkPos -> markChunkPendingToSend(player, chunkPos), chunkPos -> {});
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
			unviewedChunkViews.add(ChunkTrackingView.of(new ChunkPos(SecurityCamera.fetchRecentDismountLocation(player)), viewDistance));

		for (ChunkTrackingView unviewedChunkView : unviewedChunkViews) {
			Set<ChunkPos> droppingChunks = new HashSet<>();

			ChunkTrackingView.difference(unviewedChunkView, player.getChunkTrackingView(), chunkPos -> {}, droppingChunks::add);

			for (ChunkPos pos : droppingChunks) {
				if (BlockEntityTracker.FRAME_VIEWED_SECURITY_CAMERAS.getBlockEntitiesWithCondition(level, be -> be.shouldKeepChunkTracked(player, pos.x, pos.z)).isEmpty())
					dropChunk(player, pos);
			}
		}
	}

	/**
	 * Allows chunks that are forceloaded near a currently active camera to be sent to the player mounting the camera or viewing
	 * the camera feed in a frame.
	 */
	@Inject(method = "onChunkReadyToSend", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;getChunkTrackingView()Lnet/minecraft/server/level/ChunkTrackingView;"))
	private void securitycraft$sendChunksToCameras(ChunkHolder holder, LevelChunk chunk, CallbackInfo ci, @Local ServerPlayer player) {
		ChunkPos pos = chunk.getPos();

		if ((player.getCamera() instanceof SecurityCamera camera && camera.getCameraChunks().contains(pos)) || !BlockEntityTracker.FRAME_VIEWED_SECURITY_CAMERAS.getBlockEntitiesWithCondition(player.level(), camera -> camera.shouldKeepChunkTracked(player, pos.x, pos.z)).isEmpty())
			markChunkPendingToSend(player, chunk);
	}

	/**
	 * Fixes block updates not getting sent to chunks around mounted or frame cameras by marking all nearby chunks as tracked
	 */
	@Inject(method = "isChunkTracked", at = @At("HEAD"), cancellable = true)
	private void securitycraft$onIsChunkTracked(ServerPlayer player, int x, int z, CallbackInfoReturnable<Boolean> cir) {
		if (((player.getCamera() instanceof SecurityCamera camera && camera.getCameraChunks().contains(x, z)) || !BlockEntityTracker.FRAME_VIEWED_SECURITY_CAMERAS.getBlockEntitiesWithCondition(player.level(), camera -> camera.shouldKeepChunkTracked(player, x, z)).isEmpty()) && !player.connection.chunkSender.isPending(ChunkPos.asLong(x, z)))
			cir.setReturnValue(true);
	}

	/**
	 * Makes sure that chunks in the view area of a frame do not get dropped when the player moves out of them
	 */
	@Inject(method = "dropChunk", at = @At("HEAD"), cancellable = true)
	private static void securitycraft$onDropChunk(ServerPlayer player, ChunkPos pos, CallbackInfo ci) {
		if (!BlockEntityTracker.FRAME_VIEWED_SECURITY_CAMERAS.getBlockEntitiesWithCondition(player.level(), be -> be.shouldKeepChunkTracked(player, pos.x, pos.z)).isEmpty())
			ci.cancel();
	}
}

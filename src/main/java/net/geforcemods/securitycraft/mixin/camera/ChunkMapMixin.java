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
	 * Sends chunks loaded by cameras to the client, and re-sends chunks around the player when they stop viewing a camera to
	 * preemptively prevent missing chunks when exiting the camera
	 */
	@Inject(method = "updateChunkTracking", at = @At(value = "HEAD"))
	private void securitycraft$onUpdateChunkTracking(ServerPlayer player, CallbackInfo callback) {
		Level level = player.level();

		if (player.getCamera() instanceof SecurityCamera camera) {
			if (!camera.hasSentChunks()) {
				ChunkTrackingView.difference(player.getChunkTrackingView(), camera.getCameraChunks(), chunkPos -> markChunkPendingToSend(player, chunkPos), chunkPos -> {});
				camera.setHasSentChunks(true);
			}
		}

		for (SecurityCameraBlockEntity viewedCamera : BlockEntityTracker.FRAME_VIEWED_SECURITY_CAMERAS.getBlockEntitiesWithCondition(level, be -> be.shouldSendChunksToPlayer(player))) {
			ChunkTrackingView.difference(player.getChunkTrackingView(), viewedCamera.getCameraFeedChunks(getPlayerViewDistance(player)), chunkPos -> markChunkPendingToSend(player, chunkPos), chunkPos -> {});
		}

		for (SecurityCameraBlockEntity viewedCamera : SecurityCameraBlockEntity.fetchRecentlyUnviewedCameras()) {
			Set<ChunkPos> droppingChunks = new HashSet<>();

			ChunkTrackingView.difference(viewedCamera.getCameraFeedChunks(getPlayerViewDistance(player)), player.getChunkTrackingView(), chunkPos -> {}, droppingChunks::add);

			for (ChunkPos pos : droppingChunks) {
				if (BlockEntityTracker.FRAME_VIEWED_SECURITY_CAMERAS.getBlockEntitiesWithCondition(level, be -> be.shouldKeepChunkLoaded(pos.x, pos.z)).isEmpty())
					dropChunk(player, pos);
			}
		}
	}

	/**
	 * Allows chunks that are loaded near a currently active camera (for example by ForcedChunkManager) to be sent to the player
	 * viewing the camera
	 */
	@Inject(method = "onChunkReadyToSend", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;getChunkTrackingView()Lnet/minecraft/server/level/ChunkTrackingView;"))
	private void securitycraft$sendChunksToCameras(LevelChunk chunk, CallbackInfo callback, @Local ServerPlayer player) {
		ChunkPos pos = chunk.getPos();

		if ((player.getCamera() instanceof SecurityCamera camera && camera.getCameraChunks().contains(pos)) || !BlockEntityTracker.FRAME_VIEWED_SECURITY_CAMERAS.getBlockEntitiesWithCondition(player.level(), camera -> camera.shouldKeepChunkLoaded(pos.x, pos.z)).isEmpty())
			markChunkPendingToSend(player, chunk);
	}

	/**
	 * Fixes block updates not getting sent to chunks around cameras by marking all nearby chunks as tracked
	 */
	@Inject(method = "isChunkTracked", at = @At("HEAD"), cancellable = true)
	private void securitycraft$onIsChunkTracked(ServerPlayer player, int x, int z, CallbackInfoReturnable<Boolean> callback) {
		if (((player.getCamera() instanceof SecurityCamera camera && camera.getCameraChunks().contains(x, z)) || !BlockEntityTracker.FRAME_VIEWED_SECURITY_CAMERAS.getBlockEntitiesWithCondition(player.level(), camera -> camera.shouldKeepChunkLoaded(x, z)).isEmpty()) && !player.connection.chunkSender.isPending(ChunkPos.asLong(x, z)))
			callback.setReturnValue(true);
	}

	/**
	 * Makes sure that chunks in the view area of a frame do not get dropped when the player moves out of them
	 */
	@Inject(method = "dropChunk", at = @At("HEAD"), cancellable = true)
	private static void securitycraft$onDropChunk(ServerPlayer player, ChunkPos pos, CallbackInfo callback) {
		if (!BlockEntityTracker.FRAME_VIEWED_SECURITY_CAMERAS.getBlockEntitiesWithCondition(player.level(), be -> be.shouldKeepChunkLoaded(pos.x, pos.z)).isEmpty())
			callback.cancel();
	}
}

package net.geforcemods.securitycraft.mixin.camera;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.geforcemods.securitycraft.entity.camera.SecurityCamera;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.IPacket;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.SectionPos;
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

	@Shadow
	private static int checkerboardDistance(ChunkPos chunkPos, int x, int y) {
		throw new IllegalStateException("Shadowing checkerboardDistance failed!");
	}

	/**
	 * Fixes block updates and entities not getting sent to chunks loaded by cameras by returning the camera's chunk distance
	 * instead of the player's
	 */
	@Redirect(method = "checkerboardDistance(Lnet/minecraft/util/math/ChunkPos;Lnet/minecraft/entity/player/ServerPlayerEntity;Z)I", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/server/ChunkManager;checkerboardDistance(Lnet/minecraft/util/math/ChunkPos;II)I"))
	private static int getCameraChunkDistance(ChunkPos chunkPos, int x, int y, ChunkPos pos, ServerPlayerEntity player, boolean flag) {
		int playerChunkDistance = checkerboardDistance(chunkPos, x, y);

		if (PlayerUtils.isPlayerMountedOnCamera(player)) {
			x = MathHelper.floor(player.getCamera().getX() / 16.0D);
			y = MathHelper.floor(player.getCamera().getZ() / 16.0D);

			int cameraChunkDistance = checkerboardDistance(chunkPos, x, y);

			if (cameraChunkDistance < playerChunkDistance)
				playerChunkDistance = cameraChunkDistance; //only return the camera chunk distance if the camera is closer to the chunk than the player
		}

		return playerChunkDistance;
	}

	/**
	 * Tracks chunks loaded by cameras to make sure they're being sent to the client
	 */
	@Inject(method = "move", at = @At(value = "TAIL"))
	private void trackCameraLoadedChunks(ServerPlayerEntity player, CallbackInfo callback) {
		if (PlayerUtils.isPlayerMountedOnCamera(player)) {
			SectionPos pos = SectionPos.of(player.getCamera());
			SecurityCamera camera = ((SecurityCamera) player.getCamera());

			for (int i = pos.x() - viewDistance; i <= pos.x() + viewDistance; ++i) {
				for (int j = pos.z() - viewDistance; j <= pos.z() + viewDistance; ++j) {
					ChunkPos chunkPos = new ChunkPos(i, j);

					updateChunkTracking(player, chunkPos, new IPacket[2], camera.hasLoadedChunks(), true);
				}
			}

			camera.setHasLoadedChunks(viewDistance);
		}
	}
}

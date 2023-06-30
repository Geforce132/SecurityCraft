package net.geforcemods.securitycraft.mixin.camera;

import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.geforcemods.securitycraft.entity.camera.SecurityCamera;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.core.SectionPos;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;

/**
 * This mixin makes sure that chunks near cameras are properly sent to the player viewing it, as well as fixing block updates
 * not getting sent to chunks loaded by cameras
 */
@Mixin(value = ChunkMap.class, priority = 1100)
public abstract class ChunkMapMixin {
	@Shadow
	int viewDistance;

	@Shadow
	protected abstract void updateChunkTracking(ServerPlayer player, ChunkPos chunkPos, MutableObject<ClientboundLevelChunkWithLightPacket> packetCache, boolean wasLoaded, boolean load);

	/**
	 * Fixes block updates not getting sent to chunks loaded by cameras by returning the camera's SectionPos to the distance
	 * checking methods
	 */
	@Redirect(method = {"getPlayers", "lambda$setViewDistance$51"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;getLastSectionPos()Lnet/minecraft/core/SectionPos;"))
	private SectionPos securitycraft$getCameraSectionPos(ServerPlayer player) {
		if (PlayerUtils.isPlayerMountedOnCamera(player))
			return SectionPos.of(player.getCamera());

		return player.getLastSectionPos();
	}

	/**
	 * Tracks chunks loaded by cameras to send them to the client, and tracks chunks around the player to properly update them
	 * when they stop viewing a camera
	 */
	@Inject(method = "move", at = @At(value = "TAIL"))
	private void securitycraft$trackCameraLoadedChunks(ServerPlayer player, CallbackInfo callback) {
		if (player.getCamera() instanceof SecurityCamera camera) {
			if (!camera.hasSentChunks()) {
				SectionPos pos = SectionPos.of(camera);

				for (int i = pos.x() - viewDistance; i <= pos.x() + viewDistance; ++i) {
					for (int j = pos.z() - viewDistance; j <= pos.z() + viewDistance; ++j) {
						updateChunkTracking(player, new ChunkPos(i, j), new MutableObject<>(), false, true);
					}
				}

				camera.setHasSentChunks(true);
			}
		}
		else if (SecurityCamera.hasRecentlyDismounted(player)) {
			SectionPos pos = player.getLastSectionPos();

			for (int i = pos.x() - viewDistance; i <= pos.x() + viewDistance; ++i) {
				for (int j = pos.z() - viewDistance; j <= pos.z() + viewDistance; ++j) {
					updateChunkTracking(player, new ChunkPos(i, j), new MutableObject<>(), false, true);
				}
			}
		}
	}
}

package net.geforcemods.securitycraft.mixin.camera;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.geforcemods.securitycraft.entity.camera.SecurityCameraEntity;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.IPacket;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.world.server.ChunkManager;

/**
 * This mixin makes sure that chunks near cameras are properly sent to the player viewing it, as well as fixing block updates not getting sent to chunks loaded by cameras
 */
@Mixin(ChunkManager.class)
public abstract class ChunkManagerMixin {
	@Shadow
	private int viewDistance;

	@Shadow
	protected abstract void setChunkLoadedAtClient(ServerPlayerEntity player, ChunkPos chunkPos, IPacket<?>[] packetCache, boolean wasLoaded, boolean load);

	/**
	 * Fixes block updates not getting sent to chunks loaded by cameras by returning the camera's SectionPos to the distance checking method
	 */
	@Redirect(method = "func_219215_b(Lnet/minecraft/util/math/ChunkPos;Lnet/minecraft/entity/player/ServerPlayerEntity;Z)I", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/ServerPlayerEntity;getManagedSectionPos()Lnet/minecraft/util/math/SectionPos;"))
	private static SectionPos getCameraSectionPos(ServerPlayerEntity player) {
		if (PlayerUtils.isPlayerMountedOnCamera(player))
			return SectionPos.from(player.getSpectatingEntity());

		return player.getManagedSectionPos();
	}

	/**
	 * Tracks chunks loaded by cameras to make sure they're being sent to the client
	 */
	@Inject(method = "updatePlayerPosition", at = @At(value = "TAIL"))
	private void trackCameraLoadedChunks(ServerPlayerEntity player, CallbackInfo callback) {
		if (PlayerUtils.isPlayerMountedOnCamera(player)) {
			SectionPos pos = SectionPos.from(player.getSpectatingEntity());
			SecurityCameraEntity camera = ((SecurityCameraEntity)player.getSpectatingEntity());

			for(int i = pos.getSectionX() - viewDistance; i <= pos.getSectionX() + viewDistance; ++i) {
				for(int j = pos.getSectionZ() - viewDistance; j <= pos.getSectionZ() + viewDistance; ++j) {
					ChunkPos chunkPos = new ChunkPos(i, j);

					setChunkLoadedAtClient(player, chunkPos, new IPacket[2], camera.hasLoadedChunks(), true);
				}
			}

			camera.setHasLoadedChunks(viewDistance);
		}
	}
}

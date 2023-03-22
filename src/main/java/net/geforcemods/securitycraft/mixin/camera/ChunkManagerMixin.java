package net.geforcemods.securitycraft.mixin.camera;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import net.geforcemods.securitycraft.entity.camera.SecurityCamera;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.IPacket;
import net.minecraft.util.math.ChunkPos;
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

	/**
	 * Fixes block updates not getting sent to chunks loaded by cameras by returning the camera's SectionPos to the distance
	 * checking method
	 */
	@Redirect(method = "checkerboardDistance(Lnet/minecraft/util/math/ChunkPos;Lnet/minecraft/entity/player/ServerPlayerEntity;Z)I", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/ServerPlayerEntity;getLastSectionPos()Lnet/minecraft/util/math/SectionPos;"))
	private static SectionPos securitycraft$getCameraSectionPos(ServerPlayerEntity player) {
		if (PlayerUtils.isPlayerMountedOnCamera(player))
			return SectionPos.of(player.getCamera());

		return player.getLastSectionPos();
	}

	/**
	 * Fixes entities not getting sent to chunks loaded by cameras by returning the camera's position to the distance checking
	 * method
	 */
	@ModifyArgs(method = "checkerboardDistance(Lnet/minecraft/util/math/ChunkPos;Lnet/minecraft/entity/player/ServerPlayerEntity;Z)I", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;floor(D)I", ordinal = 0))
	private static void securitycraft$modifyPlayerX(Args args, ChunkPos pos, ServerPlayerEntity player, boolean useSectionPos) {
		if (PlayerUtils.isPlayerMountedOnCamera(player))
			args.set(0, player.getCamera().getX() / 16.0D);
	}

	/**
	 * Fixes entities not getting sent to chunks loaded by cameras by returning the camera's position to the distance checking
	 * method
	 */
	@ModifyArgs(method = "checkerboardDistance(Lnet/minecraft/util/math/ChunkPos;Lnet/minecraft/entity/player/ServerPlayerEntity;Z)I", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;floor(D)I", ordinal = 1))
	private static void securitycraft$modifyPlayerZ(Args args, ChunkPos pos, ServerPlayerEntity player, boolean useSectionPos) {
		if (PlayerUtils.isPlayerMountedOnCamera(player))
			args.set(0, player.getCamera().getZ() / 16.0D);
	}

	/**
	 * Tracks chunks loaded by cameras to make sure they're being sent to the client
	 */
	@Inject(method = "move", at = @At(value = "TAIL"))
	private void securitycraft$trackCameraLoadedChunks(ServerPlayerEntity player, CallbackInfo callback) {
		if (player.getCamera() instanceof SecurityCamera) {
			SecurityCamera camera = ((SecurityCamera) player.getCamera());

			if (!camera.hasSentChunks()) {
				SectionPos pos = SectionPos.of(camera);

				for (int i = pos.x() - viewDistance; i <= pos.x() + viewDistance; ++i) {
					for (int j = pos.z() - viewDistance; j <= pos.z() + viewDistance; ++j) {
						updateChunkTracking(player, new ChunkPos(i, j), new IPacket[2], false, true);
					}
				}

				camera.setHasSentChunks(true);
			}
		}
		else if (SecurityCamera.hasRecentlyDismounted(player)) {
			SectionPos pos = player.getLastSectionPos();

			for (int i = pos.x() - viewDistance; i <= pos.x() + viewDistance; ++i) {
				for (int j = pos.z() - viewDistance; j <= pos.z() + viewDistance; ++j) {
					updateChunkTracking(player, new ChunkPos(i, j), new IPacket[2], false, true);
				}
			}
		}
	}
}

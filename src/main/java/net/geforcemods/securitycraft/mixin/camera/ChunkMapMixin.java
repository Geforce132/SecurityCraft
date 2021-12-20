package net.geforcemods.securitycraft.mixin.camera;

import java.util.List;

import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.geforcemods.securitycraft.entity.camera.SecurityCamera;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.core.SectionPos;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;

/**
 * This mixin makes sure that chunks near cameras are properly sent to the player viewing it, as well as fixing block updates
 * not getting sent to chunks loaded by cameras
 */
@Mixin(ChunkMap.class)
public abstract class ChunkMapMixin {
	@Shadow
	int viewDistance;

	@Shadow
	protected abstract void updateChunkTracking(ServerPlayer player, ChunkPos chunkPos, MutableObject<ClientboundLevelChunkWithLightPacket> packetCache, boolean wasLoaded, boolean load);

	@Shadow
	public abstract List<ServerPlayer> getPlayers(ChunkPos pos, boolean boundaryOnly);

	/**
	 * Fixes block updates not getting sent to chunks loaded by cameras by returning the camera's SectionPos to the distance
	 * checking method
	 */
	@Inject(method = "setViewDistance", at = @At(value = "NEW", target = "org/apache/commons/lang3/mutable/MutableObject", shift = Shift.AFTER), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
	private void updateAccordingToCamera(int viewDistance, CallbackInfo callback, int i, int j, ObjectIterator<?> objectIterator, ChunkHolder chunkHolder, ChunkPos chunkPos) {
		MutableObject<ClientboundLevelChunkWithLightPacket> mutableObject = new MutableObject<>();

		getPlayers(chunkPos, false).forEach(player -> {
			SectionPos sectionPos;

			if (PlayerUtils.isPlayerMountedOnCamera(player))
				sectionPos = SectionPos.of(player.getCamera());
			else
				sectionPos = player.getLastSectionPos();

			boolean flag = ChunkMap.isChunkInRange(chunkPos.x, chunkPos.z, sectionPos.x(), sectionPos.z(), j);
			boolean flag1 = ChunkMap.isChunkInRange(chunkPos.x, chunkPos.z, sectionPos.x(), sectionPos.z(), viewDistance);
			updateChunkTracking(player, chunkPos, mutableObject, flag, flag1);
		});
		callback.cancel();
	}

	/**
	 * Tracks chunks loaded by cameras to make sure they're being sent to the client
	 */
	@Inject(method = "move", at = @At(value = "TAIL"))
	private void trackCameraLoadedChunks(ServerPlayer player, CallbackInfo callback) {
		if (PlayerUtils.isPlayerMountedOnCamera(player)) {
			SectionPos pos = SectionPos.of(player.getCamera());
			SecurityCamera camera = ((SecurityCamera) player.getCamera());

			for (int i = pos.x() - viewDistance; i <= pos.x() + viewDistance; ++i) {
				for (int j = pos.z() - viewDistance; j <= pos.z() + viewDistance; ++j) {
					updateChunkTracking(player, new ChunkPos(i, j), new MutableObject<>(), camera.hasLoadedChunks(), true);
				}
			}

			camera.setHasLoadedChunks(viewDistance);
		}
	}
}

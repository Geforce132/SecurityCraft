package net.geforcemods.securitycraft.mixin.camera;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.google.common.collect.ImmutableList;

import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity.ChunkTrackingView;
import net.geforcemods.securitycraft.entity.camera.SecurityCamera;
import net.geforcemods.securitycraft.misc.BlockEntityTracker;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.core.SectionPos;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.PlayerMap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

/**
 * This mixin makes sure that chunks near cameras are properly sent to the player viewing it, as well as fixing block updates
 * not getting sent to chunks loaded by cameras
 */
@Mixin(value = ChunkMap.class, priority = 1100)
public abstract class ChunkMapMixin {
	@Unique
	private static final Map<ServerPlayer, SectionPos> OLD_SECTION_POSITIONS = new HashMap<>();
	@Shadow
	@Final
	private PlayerMap playerMap;
	@Shadow
	int viewDistance;

	@Shadow
	protected abstract void updateChunkTracking(ServerPlayer player, ChunkPos chunkPos, MutableObject<ClientboundLevelChunkWithLightPacket> packetCache, boolean wasLoaded, boolean load);

	/**
	 * Fixes block updates not getting sent to chunks loaded by cameras by returning the camera's SectionPos to the distance
	 * checking methods
	 */
	@Inject(method = "setViewDistance", at = @At("HEAD"))
	private void securitycraft$setCameraSectionPos(int viewDistance, CallbackInfo ci) {
		for (ServerPlayer player : playerMap.getPlayers(0)) { //the parameter is ignored by the game
			if (PlayerUtils.isPlayerMountedOnCamera(player)) {
				OLD_SECTION_POSITIONS.put(player, player.getLastSectionPos());
				player.setLastSectionPos(SectionPos.of(player.getCamera()));
			}
		}
	}

	@Inject(method = "setViewDistance", at = @At("TAIL"))
	private void securitycraft$restorePreviousSectionPos(int viewDistance, CallbackInfo ci) {
		for (Entry<ServerPlayer, SectionPos> entry : OLD_SECTION_POSITIONS.entrySet()) {
			entry.getKey().setLastSectionPos(entry.getValue());
		}

		OLD_SECTION_POSITIONS.clear();
	}

	@Redirect(method = "getPlayers", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;getLastSectionPos()Lnet/minecraft/core/SectionPos;"))
	private SectionPos securitycraft$getCameraSectionPos(ServerPlayer player) {
		if (PlayerUtils.isPlayerMountedOnCamera(player))
			return SectionPos.of(player.getCamera());

		return player.getLastSectionPos();
	}

	/**
	 * Sends chunks loaded by mounted cameras or frame cameras to the client. Also drops chunks that were near a dismounted
	 * camera or a stopped frame camera feed.
	 */
	@Inject(method = "move", at = @At(value = "TAIL"))
	private void securitycraft$trackCameraLoadedChunks(ServerPlayer player, CallbackInfo callback) {
		Level level = player.level;
		ChunkPos playerPos = player.chunkPosition();

		if (player.getCamera() instanceof SecurityCamera camera && !camera.hasSentChunks()) {
			SectionPos pos = SectionPos.of(camera);

			for (int i = pos.x() - viewDistance; i <= pos.x() + viewDistance; ++i) {
				for (int j = pos.z() - viewDistance; j <= pos.z() + viewDistance; ++j) {
					if (!Utils.isInViewDistance(playerPos.x, playerPos.z, viewDistance, i, j))
						updateChunkTracking(player, new ChunkPos(i, j), new MutableObject<>(), false, true);
				}
			}

			camera.setHasSentChunks(true);
		}

		for (SecurityCameraBlockEntity viewedCamera : BlockEntityTracker.FRAME_VIEWED_SECURITY_CAMERAS.getBlockEntitiesWithCondition(level, be -> be.shouldSendChunksToPlayer(player))) {
			SectionPos pos = SectionPos.of(viewedCamera.myPos());
			int cameraViewDistance = viewedCamera.getCameraFeedChunks(player).viewDistance();

			for (int i = pos.x() - cameraViewDistance; i <= pos.x() + cameraViewDistance; ++i) {
				for (int j = pos.z() - cameraViewDistance; j <= pos.z() + cameraViewDistance; ++j) {
					if (!Utils.isInViewDistance(playerPos.x, playerPos.z, viewDistance, i, j))
						updateChunkTracking(player, new ChunkPos(i, j), new MutableObject<>(), false, true);
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
						updateChunkTracking(player, new ChunkPos(i, j), new MutableObject<>(), true, false);
				}
			}
		}
	}

	/**
	 * Allows chunks that are forceloaded near a currently active camera to be sent to the player mounting the camera or viewing
	 * the camera feed in a frame.
	 */
	@Inject(method = "getPlayers", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;getLastSectionPos()Lnet/minecraft/core/SectionPos;"), locals = LocalCapture.CAPTURE_FAILSOFT)
	private void securitycraft$sendChunksToCameras(ChunkPos pos, boolean boundaryOnly, CallbackInfoReturnable<List<ServerPlayer>> callbackInfo, Set<ServerPlayer> allPlayers, ImmutableList.Builder<ServerPlayer> playerList, Iterator<Player> playerIterator, ServerPlayer player) {
		SectionPos playerPos = player.getLastSectionPos();

		if (!ChunkMap.isChunkInRange(pos.x, pos.z, playerPos.x(), playerPos.z(), viewDistance)) {
			if (player.getCamera() instanceof SecurityCamera camera) {
				SectionPos cameraPos = SectionPos.of(camera.blockPosition());

				if (Utils.isInViewDistance(cameraPos.x(), cameraPos.z(), camera.getChunkLoadingDistance(), pos.x, pos.z))
					playerList.add(player);
			}
			else if (!BlockEntityTracker.FRAME_VIEWED_SECURITY_CAMERAS.getBlockEntitiesWithCondition(player.level, camera -> camera.shouldKeepChunkTracked(player, pos.x, pos.z)).isEmpty())
				playerList.add(player);
		}
	}

	/**
	 * Makes sure that chunks in the view area of a frame do not get dropped when the player moves out of them
	 */
	@Inject(method = "updateChunkTracking", at = @At("HEAD"), cancellable = true)
	private void securitycraft$onDropChunk(ServerPlayer player, ChunkPos pos, MutableObject<ClientboundLevelChunkWithLightPacket> packetCache, boolean wasLoaded, boolean load, CallbackInfo callbackInfo) {
		if (wasLoaded && !load && !BlockEntityTracker.FRAME_VIEWED_SECURITY_CAMERAS.getBlockEntitiesWithCondition(player.level, be -> be.shouldKeepChunkTracked(player, pos.x, pos.z)).isEmpty())
			callbackInfo.cancel();
	}
}

package net.geforcemods.securitycraft.mixin.camera;

import java.util.BitSet;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.geforcemods.securitycraft.entity.camera.CameraController;
import net.geforcemods.securitycraft.misc.IChunkStorageProvider;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientChunkCache;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkBiomeContainer;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.ChunkEvent;

/**
 * These mixins aim at implementing the camera chunk storage from CameraController into all the places ClientChunkCache#storage is used
 */
@Mixin(ClientChunkCache.class)
public abstract class ClientChunkCacheMixin implements IChunkStorageProvider {
	@Shadow
	volatile ClientChunkCache.Storage storage;

	@Shadow
	@Final
	ClientLevel level;

	@Shadow
	public abstract LevelLightEngine getLightEngine();

	/**
	 * Initializes the camera storage
	 */
	@Inject(method = "<init>", at = @At(value = "TAIL"))
	public void onInit(ClientLevel level, int viewDistance, CallbackInfo ci) {
		CameraController.setCameraStorage(newStorage(Math.max(2, viewDistance) + 3));
	}

	/**
	 * Updates the camera storage with the new view radius
	 */
	@Inject(method = "updateViewRadius", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/multiplayer/ClientChunkCache$Storage;<init>(Lnet/minecraft/client/multiplayer/ClientChunkCache;I)V"))
	public void onUpdateViewRadius(int viewDistance, CallbackInfo ci) {
		CameraController.setCameraStorage(newStorage(Math.max(2, viewDistance) + 3));
	}

	/**
	 * Handles chunks that are dropped in range of the camera storage
	 */
	@Inject(method = "drop", at = @At(value = "HEAD"))
	public void onDrop(int x, int z, CallbackInfo ci) {
		ClientChunkCache.Storage cameraStorage = CameraController.getCameraStorage();

		if (cameraStorage.inRange(x, z)) {
			int i = cameraStorage.getIndex(x, z);
			LevelChunk chunk = cameraStorage.getChunk(i);

			if (chunk != null && chunk.getPos().x == x && chunk.getPos().z == z) {
				MinecraftForge.EVENT_BUS.post(new ChunkEvent.Unload(chunk));
				cameraStorage.replace(i, chunk, null);
			}
		}
	}

	/**
	 * Handles chunks that get sent to the client which are in range of the camera storage, i.e. place them into the storage for them to be acquired afterwards
	 */
	@Inject(method = "replaceWithPacketData", at = @At(value = "HEAD"), cancellable = true)
	private void onReplace(int x, int z, ChunkBiomeContainer biomeContainer, FriendlyByteBuf buffer, CompoundTag chunkTag, BitSet chunkSection, CallbackInfoReturnable<LevelChunk> callback) {
		ClientChunkCache.Storage cameraStorage = CameraController.getCameraStorage();

		if (PlayerUtils.isPlayerMountedOnCamera(Minecraft.getInstance().player) && !storage.inRange(x, z) && cameraStorage.inRange(x, z)) {
			int index = cameraStorage.getIndex(x, z);
			LevelChunk chunk = cameraStorage.getChunk(index);
			ChunkPos chunkPos = new ChunkPos(x, z);

			if (chunk == null || chunkPos.x != x || chunkPos.z != z) {
				chunk = new LevelChunk(level, chunkPos, biomeContainer);
				chunk.replaceWithPacketData(biomeContainer, buffer, chunkTag, chunkSection);
				cameraStorage.replace(index, chunk);
			}
			else
				chunk.replaceWithPacketData(biomeContainer, buffer, chunkTag, chunkSection);

			LevelChunkSection[] chunkSections = chunk.getSections();
			LevelLightEngine lightEngine = getLightEngine();

			lightEngine.enableLightSources(chunkPos, true);

			for(int j = 0; j < chunkSections.length; ++j) {
				LevelChunkSection levelChunkSection = chunkSections[j];
				int sectionY = level.getSectionYFromSectionIndex(j);

				lightEngine.updateSectionStatus(SectionPos.of(x, sectionY, z), LevelChunkSection.isEmpty(levelChunkSection));
			}

			level.onChunkLoaded(chunkPos);
			MinecraftForge.EVENT_BUS.post(new ChunkEvent.Load(chunk));
			callback.setReturnValue(chunk);
		}
	}

	/**
	 * If chunks in range of a camera storage need to be acquired, ask the camera storage about these chunks
	 */
	@Inject(method = "getChunk(IILnet/minecraft/world/level/chunk/ChunkStatus;Z)Lnet/minecraft/world/level/chunk/LevelChunk;", at = @At("TAIL"), cancellable = true)
	private void onGetChunk(int x, int z, ChunkStatus requiredStatus, boolean load, CallbackInfoReturnable<LevelChunk> callback) {
		if (PlayerUtils.isPlayerMountedOnCamera(Minecraft.getInstance().player) && !storage.inRange(x, z) && CameraController.getCameraStorage().inRange(x, z)) {
			LevelChunk chunk = CameraController.getCameraStorage().getChunk(CameraController.getCameraStorage().getIndex(x, z));

			if (chunk != null && chunk.getPos().x == x && chunk.getPos().z == z) {
				callback.setReturnValue(chunk);
			}
		}
	}
}

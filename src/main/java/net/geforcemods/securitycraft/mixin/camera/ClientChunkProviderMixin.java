package net.geforcemods.securitycraft.mixin.camera;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.compat.sodium.SodiumCompat;
import net.geforcemods.securitycraft.entity.camera.CameraController;
import net.geforcemods.securitycraft.misc.IChunkStorageProvider;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientChunkProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.world.biome.BiomeContainer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.lighting.WorldLightManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.ChunkEvent;

/**
 * These mixins aim at implementing the camera chunk storage from CameraController into all the places
 * ClientChunkProvider#array is used
 */
@Mixin(value = ClientChunkProvider.class, priority = 1100)
public abstract class ClientChunkProviderMixin implements IChunkStorageProvider {
	@Shadow
	@Final
	private ClientWorld level;

	@Shadow
	public abstract WorldLightManager getLightEngine();

	/**
	 * Initializes the camera storage
	 */
	@Inject(method = "<init>", at = @At(value = "TAIL"))
	private void securitycraft$onInit(ClientWorld level, int viewDistance, CallbackInfo ci) {
		CameraController.setCameraStorage(newStorage(Math.max(2, viewDistance) + 3));
	}

	/**
	 * Updates the camera storage's view radius by creating a new Storage instance with the same view center and chunks as the
	 * previous one
	 */
	@Inject(method = "updateViewRadius", at = @At(value = "FIELD", target = "Lnet/minecraft/client/multiplayer/ClientChunkProvider;storage:Lnet/minecraft/client/multiplayer/ClientChunkProvider$ChunkArray;", ordinal = 1))
	private void securitycraft$onUpdateViewRadius(int viewDistance, CallbackInfo ci) {
		ClientChunkProvider.ChunkArray oldStorage = CameraController.getCameraStorage();
		ClientChunkProvider.ChunkArray newStorage = newStorage(Math.max(2, viewDistance) + 3);

		newStorage.viewCenterX = oldStorage.viewCenterX;
		newStorage.viewCenterZ = oldStorage.viewCenterZ;

		for (int i = 0; i < oldStorage.chunks.length(); ++i) {
			Chunk chunk = oldStorage.chunks.get(i);

			if (chunk != null) {
				ChunkPos pos = chunk.getPos();

				if (newStorage.inRange(pos.x, pos.z))
					newStorage.replace(newStorage.getIndex(pos.x, pos.z), chunk);
			}
		}

		CameraController.setCameraStorage(newStorage);
	}

	/**
	 * Handles chunks that are unloaded in range of the camera storage
	 */
	@Inject(method = "drop", at = @At(value = "HEAD"))
	private void securitycraft$onDrop(int x, int z, CallbackInfo ci) {
		ClientChunkProvider.ChunkArray cameraStorage = CameraController.getCameraStorage();

		if (cameraStorage.inRange(x, z)) {
			int i = cameraStorage.getIndex(x, z);
			Chunk chunk = cameraStorage.getChunk(i);

			if (chunk != null && chunk.getPos().x == x && chunk.getPos().z == z) {
				MinecraftForge.EVENT_BUS.post(new ChunkEvent.Unload(chunk));
				cameraStorage.replace(i, chunk, null);

				if (SecurityCraft.isASodiumModInstalled)
					SodiumCompat.onChunkStatusRemoved(x, z);
			}
		}
	}

	/**
	 * Handles chunks that get sent to the client which are in range of the camera storage, i.e. place them into the storage for
	 * them to be acquired afterwards
	 */
	@Inject(method = "replaceWithPacketData", at = @At(value = "HEAD"), cancellable = true)
	private void securitycraft$onReplace(int x, int z, BiomeContainer biomeContainer, PacketBuffer buffer, CompoundNBT chunkTag, int size, boolean fullChunk, CallbackInfoReturnable<Chunk> callback) {
		ClientChunkProvider.ChunkArray cameraStorage = CameraController.getCameraStorage();

		if (PlayerUtils.isPlayerMountedOnCamera(Minecraft.getInstance().player) && cameraStorage.inRange(x, z)) {
			int index = cameraStorage.getIndex(x, z);
			Chunk chunk = cameraStorage.getChunk(index);
			ChunkPos chunkPos = new ChunkPos(x, z);

			if (chunk == null || chunk.getPos().x != x || chunk.getPos().z != z) {
				chunk = new Chunk(level, chunkPos, biomeContainer);
				chunk.replaceWithPacketData(biomeContainer, buffer, chunkTag, size);
				cameraStorage.replace(index, chunk);
			}
			else
				chunk.replaceWithPacketData(biomeContainer, buffer, chunkTag, size);

			ChunkSection[] chunkSections = chunk.getSections();
			WorldLightManager lightEngine = getLightEngine();

			lightEngine.enableLightSources(chunkPos, true);

			for (int y = 0; y < chunkSections.length; ++y) {
				lightEngine.updateSectionStatus(SectionPos.of(x, y, z), ChunkSection.isEmpty(chunkSections[y]));
			}

			level.onChunkLoaded(x, z);

			if (SecurityCraft.isASodiumModInstalled)
				SodiumCompat.onChunkStatusAdded(x, z);

			MinecraftForge.EVENT_BUS.post(new ChunkEvent.Load(chunk));
			callback.setReturnValue(chunk);
		}
	}

	/**
	 * If chunks in range of a camera storage need to be acquired, ask the camera storage about these chunks
	 */
	@Inject(method = "getChunk(IILnet/minecraft/world/chunk/ChunkStatus;Z)Lnet/minecraft/world/chunk/Chunk;", at = @At("TAIL"), cancellable = true)
	private void securitycraft$onGetChunk(int x, int z, ChunkStatus requiredStatus, boolean load, CallbackInfoReturnable<Chunk> callback) {
		if (PlayerUtils.isPlayerMountedOnCamera(Minecraft.getInstance().player) && CameraController.getCameraStorage().inRange(x, z)) {
			Chunk chunk = CameraController.getCameraStorage().getChunk(CameraController.getCameraStorage().getIndex(x, z));

			if (chunk != null && chunk.getPos().x == x && chunk.getPos().z == z)
				callback.setReturnValue(chunk);
		}
	}
}

package net.geforcemods.securitycraft.mixin.camera;

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
 * These mixins aim at implementing the camera chunk storage from CameraController into all the places ClientChunkProvider#array is used
 */
@Mixin(ClientChunkProvider.class)
public abstract class ClientChunkProviderMixin implements IChunkStorageProvider {
	@Shadow
	volatile ClientChunkProvider.ChunkArray array;

	@Shadow
	@Final
	ClientWorld world;

	@Shadow
	public abstract WorldLightManager getLightManager();

	/**
	 * Initializes the camera storage
	 */
	@Inject(method = "<init>", at = @At(value = "TAIL"))
	public void onInit(ClientWorld world, int viewDistance, CallbackInfo ci) {
		CameraController.setCameraStorage(newStorage(Math.max(2, viewDistance) + 3));
	}

	/**
	 * Updates the camera storage with the new view radius
	 */
	@Inject(method = "setViewDistance", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/multiplayer/ClientChunkProvider$ChunkArray;<init>(Lnet/minecraft/client/multiplayer/ClientChunkProvider;I)V"))
	public void onSetViewDistance(int viewDistance, CallbackInfo ci) {
		CameraController.setCameraStorage(newStorage(Math.max(2, viewDistance) + 3));
	}

	/**
	 * Handles chunks that are unloaded in range of the camera storage
	 */
	@Inject(method = "unloadChunk", at = @At(value = "HEAD"))
	public void onUnloadChunk(int x, int z, CallbackInfo ci) {
		ClientChunkProvider.ChunkArray cameraStorage = CameraController.getCameraStorage();

		if (cameraStorage.inView(x, z)) {
			int i = cameraStorage.getIndex(x, z);
			Chunk chunk = cameraStorage.get(i);

			if (chunk != null && chunk.getPos().x == x && chunk.getPos().z == z) {
				MinecraftForge.EVENT_BUS.post(new ChunkEvent.Unload(chunk));
				cameraStorage.unload(i, chunk, null);
			}
		}
	}

	/**
	 * Handles chunks that get sent to the client which are in range of the camera storage, i.e. place them into the storage for them to be acquired afterwards
	 */
	@Inject(method = "loadChunk", at = @At(value = "HEAD"), cancellable = true)
	private void onLoad(int x, int z, BiomeContainer biomeContainer, PacketBuffer buffer, CompoundNBT chunkTag, int size, boolean fullChunk, CallbackInfoReturnable<Chunk> callback) {
		ClientChunkProvider.ChunkArray cameraStorage = CameraController.getCameraStorage();

		if (PlayerUtils.isPlayerMountedOnCamera(Minecraft.getInstance().player) && !array.inView(x, z) && cameraStorage.inView(x, z)) {
			int index = cameraStorage.getIndex(x, z);
			Chunk chunk = cameraStorage.get(index);
			ChunkPos chunkPos = new ChunkPos(x, z);

			if (chunk == null || chunk.getPos().x != x || chunk.getPos().z != z) {
				chunk = new Chunk(world, chunkPos, biomeContainer);
				chunk.read(biomeContainer, buffer, chunkTag, size);
				cameraStorage.replace(index, chunk);
			}
			else
				chunk.read(biomeContainer, buffer, chunkTag, size);

			ChunkSection[] chunkSections = chunk.getSections();
			WorldLightManager lightEngine = getLightManager();

			lightEngine.enableLightSources(chunkPos, true);

			for(int y = 0; y < chunkSections.length; ++y) {
				lightEngine.updateSectionStatus(SectionPos.of(x, y, z), ChunkSection.isEmpty(chunkSections[y]));
			}

			world.onChunkLoaded(x, z);
			MinecraftForge.EVENT_BUS.post(new ChunkEvent.Load(chunk));
			callback.setReturnValue(chunk);
		}
	}

	/**
	 * If chunks in range of a camera storage need to be acquired, ask the camera storage about these chunks
	 */
	@Inject(method = "getChunk(IILnet/minecraft/world/chunk/ChunkStatus;Z)Lnet/minecraft/world/chunk/Chunk;", at = @At("TAIL"), cancellable = true)
	private void onGetChunk(int x, int z, ChunkStatus requiredStatus, boolean load, CallbackInfoReturnable<Chunk> callback) {
		if (PlayerUtils.isPlayerMountedOnCamera(Minecraft.getInstance().player) && !array.inView(x, z) && CameraController.getCameraStorage().inView(x, z)) {
			Chunk chunk = CameraController.getCameraStorage().get(CameraController.getCameraStorage().getIndex(x, z));

			if (chunk != null && chunk.getPos().x == x && chunk.getPos().z == z) {
				callback.setReturnValue(chunk);
			}
		}
	}
}

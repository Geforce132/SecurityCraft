package net.geforcemods.securitycraft.mixin.camera;

import java.util.function.Consumer;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.geforcemods.securitycraft.SecurityCraftClient;
import net.geforcemods.securitycraft.entity.camera.CameraController;
import net.geforcemods.securitycraft.misc.IChunkStorageProvider;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientChunkCache;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.ChunkEvent;

/**
 * These mixins aim at implementing the camera chunk storage from CameraController into all the places
 * ClientChunkCache#storage is used
 */
@Mixin(value = ClientChunkCache.class, priority = 1100)
public abstract class ClientChunkCacheMixin implements IChunkStorageProvider {
	@Shadow
	volatile ClientChunkCache.Storage storage;
	@Shadow
	@Final
	ClientLevel level;

	@Shadow
	private static boolean isValidChunk(LevelChunk chunk, int x, int z) {
		throw new IllegalStateException("Shadowing isValidChunk did not work!");
	}

	/**
	 * Initializes the camera storage
	 */
	@Inject(method = "<init>", at = @At(value = "TAIL"))
	public void securitycraft$onInit(ClientLevel level, int viewDistance, CallbackInfo ci) {
		CameraController.setCameraStorage(newStorage(Math.max(2, viewDistance) + 3));
	}

	/**
	 * Updates the camera storage's view radius by creating a new Storage instance with the same view center and chunks as the
	 * previous one
	 */
	@Inject(method = "updateViewRadius", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientChunkCache$Storage;<init>(Lnet/minecraft/client/multiplayer/ClientChunkCache;I)V"))
	public void securitycraft$onUpdateViewRadius(int viewDistance, CallbackInfo ci) {
		ClientChunkCache.Storage oldStorage = CameraController.getCameraStorage();
		ClientChunkCache.Storage newStorage = newStorage(Math.max(2, viewDistance) + 3);

		newStorage.viewCenterX = oldStorage.viewCenterX;
		newStorage.viewCenterZ = oldStorage.viewCenterZ;

		for (int i = 0; i < oldStorage.chunks.length(); ++i) {
			LevelChunk chunk = oldStorage.chunks.get(i);

			if (chunk != null) {
				ChunkPos pos = chunk.getPos();

				if (newStorage.inRange(pos.x, pos.z))
					newStorage.replace(newStorage.getIndex(pos.x, pos.z), chunk);
			}
		}

		CameraController.setCameraStorage(newStorage);
	}

	/**
	 * Handles chunks that are dropped in range of the camera storage
	 */
	@Inject(method = "drop", at = @At(value = "HEAD"))
	public void securitycraft$onDrop(ChunkPos pos, CallbackInfo ci) {
		int x = pos.x;
		int z = pos.z;
		ClientChunkCache.Storage cameraStorage = CameraController.getCameraStorage();

		if (cameraStorage.inRange(x, z)) {
			int i = cameraStorage.getIndex(x, z);
			LevelChunk chunk = cameraStorage.getChunk(i);

			if (chunk != null && chunk.getPos().x == x && chunk.getPos().z == z) {
				NeoForge.EVENT_BUS.post(new ChunkEvent.Unload(chunk));
				cameraStorage.replace(i, chunk, null);
				SecurityCraftClient.INSTALLED_IUM_MOD.onChunkStatusRemoved(level, x, z);
			}
		}
	}

	/**
	 * Handles chunks that get sent to the client which are in range of the camera storage, i.e. place them into the storage for
	 * them to be acquired afterwards
	 */
	@Inject(method = "replaceWithPacketData", at = @At(value = "HEAD"), cancellable = true)
	private void securitycraft$onReplace(int x, int z, FriendlyByteBuf buffer, CompoundTag chunkTag, Consumer<ClientboundLevelChunkPacketData.BlockEntityTagOutput> tagOutputConsumer, CallbackInfoReturnable<LevelChunk> callback) {
		ClientChunkCache.Storage cameraStorage = CameraController.getCameraStorage();

		if (PlayerUtils.isPlayerMountedOnCamera(Minecraft.getInstance().player) && cameraStorage.inRange(x, z)) {
			int index = cameraStorage.getIndex(x, z);
			LevelChunk chunk = cameraStorage.getChunk(index);
			ChunkPos chunkPos = new ChunkPos(x, z);

			if (!isValidChunk(chunk, x, z)) {
				chunk = new LevelChunk(level, chunkPos);
				chunk.replaceWithPacketData(buffer, chunkTag, tagOutputConsumer);
				cameraStorage.replace(index, chunk);
			}
			else
				chunk.replaceWithPacketData(buffer, chunkTag, tagOutputConsumer);

			level.onChunkLoaded(chunkPos);
			SecurityCraftClient.INSTALLED_IUM_MOD.onChunkStatusAdded(level, x, z);
			NeoForge.EVENT_BUS.post(new ChunkEvent.Load(chunk, false));
			callback.setReturnValue(chunk);
		}
	}

	/**
	 * If chunks in range of a camera storage need to be acquired, ask the camera storage about these chunks
	 */
	@Inject(method = "getChunk(IILnet/minecraft/world/level/chunk/status/ChunkStatus;Z)Lnet/minecraft/world/level/chunk/LevelChunk;", at = @At("TAIL"), cancellable = true)
	private void securitycraft$onGetChunk(int x, int z, ChunkStatus requiredStatus, boolean load, CallbackInfoReturnable<LevelChunk> callback) {
		if (PlayerUtils.isPlayerMountedOnCamera(Minecraft.getInstance().player) && CameraController.getCameraStorage().inRange(x, z)) {
			LevelChunk chunk = CameraController.getCameraStorage().getChunk(CameraController.getCameraStorage().getIndex(x, z));

			if (chunk != null && chunk.getPos().x == x && chunk.getPos().z == z)
				callback.setReturnValue(chunk);
		}
	}
}

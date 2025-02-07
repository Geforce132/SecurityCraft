package net.geforcemods.securitycraft.mixin.camera;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.geforcemods.securitycraft.entity.camera.CameraClientChunkCacheExtension;
import net.geforcemods.securitycraft.entity.camera.CameraController;
import net.geforcemods.securitycraft.entity.camera.SecurityCamera;
import net.geforcemods.securitycraft.misc.IChunkStorageProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientChunkProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.biome.BiomeContainer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;

/**
 * These mixins aim at implementing the camera chunk storage from CameraController into all the places
 * ClientChunkProvider#array is used
 */
@Mixin(value = ClientChunkProvider.class, priority = 1100)
public abstract class ClientChunkProviderMixin implements IChunkStorageProvider {
	@Shadow
	private volatile ClientChunkProvider.ChunkArray storage;
	@Shadow
	@Final
	private ClientWorld level;

	/**
	 * Removes dropped chunks from the camera client chunk cache, unless the chunk is in range of a currently mounted camera or a
	 * frame camera
	 */
	@Inject(method = "drop", at = @At(value = "HEAD"))
	private void securitycraft$onDrop(int x, int z, CallbackInfo ci) {
		ChunkPos pos = new ChunkPos(x, z);
		int renderDistance = Minecraft.getInstance().options.renderDistance;
		Entity cameraEntity = Minecraft.getInstance().cameraEntity;

		if (cameraEntity instanceof SecurityCamera && pos.getChessboardDistance(new ChunkPos(cameraEntity.xChunk, cameraEntity.zChunk)) <= (renderDistance + 1))
			return;

		for (GlobalPos cameraPos : CameraController.FRAME_LINKS.keySet()) {
			if (pos.getChessboardDistance(new ChunkPos(cameraPos.pos())) <= (renderDistance + 1))
				return;
		}

		CameraClientChunkCacheExtension.drop(level, pos);
	}

	/**
	 * Places clientside received chunks which are in range of a mounted camera or a frame camera into the camera client chunk
	 * cache
	 */
	@Inject(method = "replaceWithPacketData", at = @At(value = "HEAD"), cancellable = true)
	private void securitycraft$onReplaceChunk(int x, int z, BiomeContainer biomeContainer, PacketBuffer buffer, CompoundNBT chunkTag, int size, boolean fullChunk, CallbackInfoReturnable<Chunk> ci) {
		int renderDistance = Minecraft.getInstance().options.renderDistance;
		Entity cameraEntity = Minecraft.getInstance().cameraEntity;
		ChunkPos pos = new ChunkPos(x, z);
		boolean isInPlayerRange = storage.inRange(x, z);
		boolean shouldAddChunk = false;

		if (cameraEntity instanceof SecurityCamera && pos.getChessboardDistance(new ChunkPos(cameraEntity.blockPosition())) <= (renderDistance + 1))
			shouldAddChunk = true;
		else {
			for (GlobalPos cameraPos : CameraController.FRAME_LINKS.keySet()) {
				if (pos.getChessboardDistance(new ChunkPos(cameraPos.pos())) <= (renderDistance + 1)) {
					shouldAddChunk = true;
					break;
				}
			}
		}

		if (shouldAddChunk) {
			Chunk newChunk = CameraClientChunkCacheExtension.replaceWithPacketData(level, x, z, biomeContainer, new PacketBuffer(buffer.copy()), chunkTag, size, fullChunk);

			if (!isInPlayerRange)
				ci.setReturnValue(newChunk);
		}
	}

	/**
	 * If chunks in range of a camera storage need to be acquired, ask the camera storage about these chunks
	 */
	@Inject(method = "getChunk(IILnet/minecraft/world/chunk/ChunkStatus;Z)Lnet/minecraft/world/chunk/Chunk;", at = @At("TAIL"), cancellable = true)
	private void securitycraft$onGetChunk(int x, int z, ChunkStatus requiredStatus, boolean requireChunk, CallbackInfoReturnable<Chunk> callback) {
		if (!storage.inRange(x, z)) {
			Chunk chunk = CameraClientChunkCacheExtension.getChunk(x, z);

			if (chunk != null)
				callback.setReturnValue(chunk);
		}
	}
}

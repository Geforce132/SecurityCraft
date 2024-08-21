package net.geforcemods.securitycraft.entity.camera;

import java.util.function.Consumer;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.geforcemods.securitycraft.SecurityCraftClient;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.ChunkEvent;

// TODO: As per Immersive Portals' license, changes made to the class need to be stated in the source code
// TODO: As per Immersive Portals' license, it needs to be included in the derivative work
public class CameraClientChunkCacheExtension { //taken from Immersive Portals
	// the most chunk accesses are from the main thread,
	// so we use two maps to reduce synchronization.
	// the main thread accesses this map, without synchronization
	private static final Long2ObjectOpenHashMap<LevelChunk> CHUNK_MAP = new Long2ObjectOpenHashMap<>();
	// other threads read this map, with synchronization
	//private static final Long2ObjectOpenHashMap<LevelChunk> chunkMapForOtherThreads =
	//		new Long2ObjectOpenHashMap<>();
	//TODO: make threadsafe

	public static void drop(ClientLevel level, ChunkPos chunkPos) {
		LevelChunk chunk = CHUNK_MAP.get(chunkPos.toLong());

		if (chunk != null) {
			CHUNK_MAP.remove(chunkPos.toLong());
			NeoForge.EVENT_BUS.post(new ChunkEvent.Unload(chunk));
			level.unload(chunk);
			SecurityCraftClient.INSTALLED_IUM_MOD.onChunkStatusRemoved(level, chunkPos.x, chunkPos.z);
		}
	}

	public static LevelChunk getChunk(int x, int z) {
		return CHUNK_MAP.get(ChunkPos.asLong(x, z));
	}

	public static LevelChunk replaceWithPacketData(ClientLevel level, int x, int z, FriendlyByteBuf packetData, CompoundTag chunkTag, Consumer<ClientboundLevelChunkPacketData.BlockEntityTagOutput> tagOutput) {
		long chunkPos = ChunkPos.asLong(x, z);
		LevelChunk chunk = CHUNK_MAP.get(chunkPos);

		if (chunk == null) {
			chunk = new LevelChunk(level, new ChunkPos(x, z));
			chunk.replaceWithPacketData(packetData, chunkTag, tagOutput);
			CHUNK_MAP.put(chunkPos, chunk);
		}
		else
			chunk.replaceWithPacketData(packetData, chunkTag, tagOutput);

		level.onChunkLoaded(new ChunkPos(x, z));
		SecurityCraftClient.INSTALLED_IUM_MOD.onChunkStatusAdded(level, x, z);
		NeoForge.EVENT_BUS.post(new ChunkEvent.Load(chunk, false));
		return chunk;
	}

	public static void clear() {
		CHUNK_MAP.clear();
	}
}

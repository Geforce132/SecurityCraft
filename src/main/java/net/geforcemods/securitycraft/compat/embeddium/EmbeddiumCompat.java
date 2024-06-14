package net.geforcemods.securitycraft.compat.embeddium;

import org.embeddedt.embeddium.impl.render.chunk.map.ChunkStatus;
import org.embeddedt.embeddium.impl.render.chunk.map.ChunkTrackerHolder;

import net.minecraft.client.multiplayer.ClientLevel;

public class EmbeddiumCompat {
	private EmbeddiumCompat() {}

	public static void onChunkStatusAdded(ClientLevel level, int x, int z) {
		ChunkTrackerHolder.get(level).onChunkStatusAdded(x, z, ChunkStatus.FLAG_HAS_BLOCK_DATA);
	}

	public static void onChunkStatusRemoved(ClientLevel level, int x, int z) {
		ChunkTrackerHolder.get(level).onChunkStatusRemoved(x, z, ChunkStatus.FLAG_HAS_BLOCK_DATA);
	}
}

package net.geforcemods.securitycraft.compat.sodium;

import net.minecraft.client.multiplayer.ClientLevel;

public class SodiumCompat {
	private SodiumCompat() {}

	public static void onChunkStatusAdded(ClientLevel level, int x, int z) {
		//		ChunkTrackerHolder.get(level).onChunkStatusAdded(x, z, ChunkStatus.FLAG_HAS_BLOCK_DATA);
	}

	public static void onChunkStatusRemoved(ClientLevel level, int x, int z) {
		//		ChunkTrackerHolder.get(level).onChunkStatusRemoved(x, z, ChunkStatus.FLAG_HAS_BLOCK_DATA);
	}
}

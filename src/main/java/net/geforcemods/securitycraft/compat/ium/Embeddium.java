package net.geforcemods.securitycraft.compat.ium;

import org.embeddedt.embeddium.impl.render.chunk.map.ChunkTrackerHolder;

import net.geforcemods.securitycraft.compat.ium.IumCompat.IumMod;
import net.minecraft.client.multiplayer.ClientLevel;

public class Embeddium implements IumMod {
	@Override
	public void onChunkStatusAdded(ClientLevel level, int x, int z) {
		ChunkTrackerHolder.get(level).onChunkStatusAdded(x, z, FLAG_HAS_BLOCK_DATA);
	}

	@Override
	public void onChunkStatusRemoved(ClientLevel level, int x, int z) {
		ChunkTrackerHolder.get(level).onChunkStatusRemoved(x, z, FLAG_HAS_BLOCK_DATA);
	}
}

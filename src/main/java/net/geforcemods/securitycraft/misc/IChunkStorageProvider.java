package net.geforcemods.securitycraft.misc;

import net.minecraft.client.multiplayer.ClientChunkProvider;

/**
 * Helper interface for creating new ChunkArrays, as these are inner classes and have to be created from ClientChunkProviders
 */
public interface IChunkStorageProvider {
	default ClientChunkProvider.ChunkArray newStorage(int viewDistance) {
		if (this instanceof ClientChunkProvider)
			return ((ClientChunkProvider)this).new ChunkArray(viewDistance);

		return null;
	}
}

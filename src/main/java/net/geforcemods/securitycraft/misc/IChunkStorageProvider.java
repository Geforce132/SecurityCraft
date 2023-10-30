package net.geforcemods.securitycraft.misc;

import net.minecraft.client.multiplayer.ClientChunkCache;
import net.minecraft.client.multiplayer.ClientChunkCache.Storage;

/**
 * Helper interface for creating new Storages, as these are inner classes and have to be created from ClientChunkCaches
 */
public interface IChunkStorageProvider {
	default ClientChunkCache.Storage newStorage(int viewDistance) {
		if (this instanceof ClientChunkCache cache)
			return cache.new Storage(viewDistance);

		return null;
	}
}

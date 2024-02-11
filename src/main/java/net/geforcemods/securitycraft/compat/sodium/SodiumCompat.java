package net.geforcemods.securitycraft.compat.sodium;

import me.jellysquid.mods.sodium.client.render.SodiumWorldRenderer;

public class SodiumCompat {
	private SodiumCompat() {}

	public static void onChunkStatusAdded(int x, int z) {
		try {
			SodiumWorldRenderer renderer = SodiumWorldRenderer.getInstance();

			if (renderer != null)
				renderer.onChunkAdded(x, z);
		}
		catch (IllegalStateException e) {}
	}

	public static void onChunkStatusRemoved(int x, int z) {
		try {
			SodiumWorldRenderer renderer = SodiumWorldRenderer.getInstance();

			if (renderer != null)
				renderer.onChunkRemoved(x, z);
		}
		catch (IllegalStateException e) {}
	}
}

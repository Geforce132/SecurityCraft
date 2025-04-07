package net.geforcemods.securitycraft.compat.ium;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Supplier;

import net.geforcemods.securitycraft.SecurityCraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.neoforged.fml.ModList;

/**
 * Sodium and Embeddium are incompatible with each other, so to have them both in our development environment without the
 * compiler complaining, the methods are invoked via reflection.
 */
public enum IumCompat {
	//@formatter:off
	EMBEDDIUM(
			() -> getClass("org.embeddedt.embeddium.impl.render.chunk.map.ChunkTrackerHolder"),
			() -> getClass("org.embeddedt.embeddium.impl.render.chunk.map.ChunkTracker")),
	SODIUM(
			() -> getClass("net.caffeinemc.mods.sodium.client.render.chunk.map.ChunkTrackerHolder"),
			() -> getClass("net.caffeinemc.mods.sodium.client.render.chunk.map.ChunkTracker")),
	//@formatter:on
	NONE(null, null);

	private static final int FLAG_HAS_BLOCK_DATA = 1; //from the ChunkStatus class
	private final Supplier<Class<?>> chunkTrackerHolderClass;
	private final Supplier<Class<?>> chunkTrackerClass;
	private Method chunkTrackerHolder$get;
	private Method chunkTracker$onChunkStatusAdded;
	private Method chunkTracker$onChunkStatusRemoved;

	private IumCompat(Supplier<Class<?>> chunkTrackerHolder, Supplier<Class<?>> chunkTracker) {
		this.chunkTrackerHolderClass = chunkTrackerHolder;
		this.chunkTrackerClass = chunkTracker;
	}

	public void onChunkStatusAdded(ClientLevel level, int x, int z) {
		if (this == NONE)
			return;

		try {
			chunkTracker$onChunkStatusAdded.invoke(chunkTrackerHolder$get.invoke(null, level), x, z, FLAG_HAS_BLOCK_DATA);
		}
		catch (IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	public void onChunkStatusRemoved(ClientLevel level, int x, int z) {
		if (this == NONE)
			return;

		try {
			chunkTracker$onChunkStatusRemoved.invoke(chunkTrackerHolder$get.invoke(null, level), x, z, FLAG_HAS_BLOCK_DATA);
		}
		catch (IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	public void resolve() {
		if (this == NONE)
			return;

		try {
			Class<?> chunkTracker = chunkTrackerClass.get();
			chunkTrackerHolder$get = chunkTrackerHolderClass.get().getDeclaredMethod("get", ClientLevel.class);
			chunkTracker$onChunkStatusAdded = chunkTracker.getDeclaredMethod("onChunkStatusAdded", int.class, int.class, int.class);
			chunkTracker$onChunkStatusRemoved = chunkTracker.getDeclaredMethod("onChunkStatusRemoved", int.class, int.class, int.class);
		}
		catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
	}

	public static IumCompat getInstalledIumMod() {
		if (ModList.get().isLoaded("embeddium"))
			return EMBEDDIUM;
		else if (ModList.get().isLoaded("sodium"))
			return SODIUM;
		else
			return NONE;
	}

	private static Class<?> getClass(String name) {
		try {
			return Class.forName(name, false, SecurityCraft.class.getClassLoader());
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return null;
	}
}

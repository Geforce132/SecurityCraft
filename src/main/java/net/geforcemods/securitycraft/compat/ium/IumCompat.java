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
	private final Supplier<Class<?>> chunkTrackerHolder;
	private final Supplier<Class<?>> chunkTracker;
	private Method getChunkTracker;
	private Method onChunkStatusAdded;
	private Method onChunkStatusRemoved;

	private IumCompat(Supplier<Class<?>> chunkTrackerHolder, Supplier<Class<?>> chunkTracker) {
		this.chunkTrackerHolder = chunkTrackerHolder;
		this.chunkTracker = chunkTracker;
	}

	public void onChunkStatusAdded(ClientLevel level, int x, int z) {
		if (this == NONE)
			return;

		try {
			onChunkStatusAdded.invoke(getChunkTracker.invoke(null, level), x, z, FLAG_HAS_BLOCK_DATA);
		}
		catch (IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	public void onChunkStatusRemoved(ClientLevel level, int x, int z) {
		if (this == NONE)
			return;

		try {
			onChunkStatusRemoved.invoke(getChunkTracker.invoke(null, level), x, z, FLAG_HAS_BLOCK_DATA);
		}
		catch (IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	public void resolve() {
		if (this == NONE)
			return;

		try {
			Class<?> chunkTrackerClass = chunkTracker.get();

			getChunkTracker = chunkTrackerHolder.get().getDeclaredMethod("get", ClientLevel.class);
			onChunkStatusAdded = chunkTrackerClass.getDeclaredMethod("onChunkStatusAdded", int.class, int.class, int.class);
			onChunkStatusRemoved = chunkTrackerClass.getDeclaredMethod("onChunkStatusRemoved", int.class, int.class, int.class);
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

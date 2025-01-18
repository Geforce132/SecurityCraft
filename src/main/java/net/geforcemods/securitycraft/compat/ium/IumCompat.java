package net.geforcemods.securitycraft.compat.ium;

import java.lang.reflect.Field;
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
			() -> getClass("org.embeddedt.embeddium.impl.render.chunk.map.ChunkTracker"),
			() -> getClass("org.embeddedt.embeddium.impl.render.chunk.RenderSectionManager"),
			() -> getClass("org.embeddedt.embeddium.impl.render.chunk.lists.SortedRenderLists"),
			() -> getClass("org.embeddedt.embeddium.impl.render.EmbeddiumWorldRenderer")),
	SODIUM(
			() -> getClass("net.caffeinemc.mods.sodium.client.render.chunk.map.ChunkTrackerHolder"),
			() -> getClass("net.caffeinemc.mods.sodium.client.render.chunk.map.ChunkTracker"),
			() -> getClass("net.caffeinemc.mods.sodium.client.render.chunk.RenderSectionManager"),
			() -> getClass("net.caffeinemc.mods.sodium.client.render.chunk.lists.SortedRenderLists"),
			() -> getClass("net.caffeinemc.mods.sodium.client.render.SodiumWorldRenderer")),
	//@formatter:on
	NONE(null, null, null, null, null);

	private static final int FLAG_HAS_BLOCK_DATA = 1; //from the ChunkStatus class
	private final Supplier<Class<?>> chunkTrackerHolderClass;
	private final Supplier<Class<?>> chunkTrackerClass;
	private final Supplier<Class<?>> renderSectionManagerClass;
	private final Supplier<Class<?>> sortedRenderListsClass;
	private final Supplier<Class<?>> levelRendererClass;
	private Field renderSectionManager$renderLists;
	private Field levelRenderer$renderSectionManager;
	private Method chunkTrackerHolder$get;
	private Method chunkTracker$onChunkStatusAdded;
	private Method chunkTracker$onChunkStatusRemoved;
	private Method levelRenderer$instance;
	private Method levelRenderer$scheduleTerrainUpdate;
	private Object sortedRenderLists$EMPTY;
	private Object previousRenderLists;

	private IumCompat(Supplier<Class<?>> chunkTrackerHolder, Supplier<Class<?>> chunkTracker, Supplier<Class<?>> renderSectionManager, Supplier<Class<?>> sortedRenderLists, Supplier<Class<?>> levelRenderer) {
		this.chunkTrackerHolderClass = chunkTrackerHolder;
		this.chunkTrackerClass = chunkTracker;
		this.renderSectionManagerClass = renderSectionManager;
		this.sortedRenderListsClass = sortedRenderLists;
		this.levelRendererClass = levelRenderer;
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

	public Object getOldRenderLists(Object instance) {
		if (this == NONE)
			return null;

		try {
			return renderSectionManager$renderLists.get(instance);
		}
		catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}

		return sortedRenderLists$EMPTY;
	}

	public void setRenderLists(Object instance, Object newRenderLists) {
		if (this == NONE)
			return;

		try {
			renderSectionManager$renderLists.set(instance, newRenderLists);
		}
		catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	public void switchToEmptyRenderLists() {
		if (this == NONE)
			return;

		switchRenderLists(sortedRenderLists$EMPTY);
	}

	public void switchToPreviousRenderLists() {
		if (this == NONE)
			return;

		switchRenderLists(previousRenderLists);
	}

	private void switchRenderLists(Object newRenderLists) {
		try {
			Object levelRenderer = levelRenderer$instance.invoke(null);
			Object renderSectionManager = levelRenderer$renderSectionManager.get(levelRenderer);

			levelRenderer$scheduleTerrainUpdate.invoke(levelRenderer);
			previousRenderLists = getOldRenderLists(renderSectionManager);
			setRenderLists(renderSectionManager, newRenderLists);
			levelRenderer$scheduleTerrainUpdate.invoke(levelRenderer);
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
			Class<?> levelRenderer = levelRendererClass.get();

			renderSectionManager$renderLists = renderSectionManagerClass.get().getDeclaredField("renderLists");
			renderSectionManager$renderLists.setAccessible(true);
			levelRenderer$renderSectionManager = levelRenderer.getDeclaredField("renderSectionManager");
			levelRenderer$renderSectionManager.setAccessible(true);
			chunkTrackerHolder$get = chunkTrackerHolderClass.get().getDeclaredMethod("get", ClientLevel.class);
			chunkTracker$onChunkStatusAdded = chunkTracker.getDeclaredMethod("onChunkStatusAdded", int.class, int.class, int.class);
			chunkTracker$onChunkStatusRemoved = chunkTracker.getDeclaredMethod("onChunkStatusRemoved", int.class, int.class, int.class);
			levelRenderer$instance = levelRenderer.getDeclaredMethod("instance");
			levelRenderer$scheduleTerrainUpdate = levelRenderer.getDeclaredMethod("scheduleTerrainUpdate");
			sortedRenderLists$EMPTY = sortedRenderListsClass.get().getMethod("empty").invoke(null);
		}
		catch (NoSuchMethodException | SecurityException | NoSuchFieldException | IllegalAccessException | InvocationTargetException e) {
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

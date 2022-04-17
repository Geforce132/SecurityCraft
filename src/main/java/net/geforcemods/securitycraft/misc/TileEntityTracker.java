package net.geforcemods.securitycraft.misc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import net.geforcemods.securitycraft.tileentity.TileEntityBlockChangeDetector;
import net.geforcemods.securitycraft.tileentity.TileEntitySonicSecuritySystem;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Tracks all of the tile entities in the world. Modified from
 * https://github.com/bl4ckscor3/The-Plopper/blob/1.16/src/main/java/bl4ckscor3/mod/theplopper/tracking/PlopperTracker.java
 */
public class TileEntityTracker<TE extends TileEntity> {
	public static final TileEntityTracker<TileEntitySonicSecuritySystem> SONIC_SECURITY_SYSTEM = new TileEntityTracker<>(te -> TileEntitySonicSecuritySystem.MAX_RANGE);
	public static final TileEntityTracker<TileEntityBlockChangeDetector> BLOCK_CHANGE_DETECTOR = new TileEntityTracker<>(te -> te.getRange());
	private final Map<Integer, Collection<BlockPos>> trackedTileEntities = new HashMap<>();
	private final Function<TE, Integer> range;

	private TileEntityTracker(Function<TE, Integer> range) {
		this.range = range;
	}

	/**
	 * Starts tracking a tile entity
	 *
	 * @param te The tile entity to track
	 */
	public void track(TE te) {
		getTrackedTileEntities(te.getWorld()).add(te.getPos().toImmutable());
	}

	/**
	 * Stops tracking the given tile entity. Use when e.g. removing the tile entity from the world
	 *
	 * @param te The tile entity to stop tracking
	 */
	public void stopTracking(TE te) {
		getTrackedTileEntities(te.getWorld()).remove(te.getPos());
	}

	/**
	 * Gets all tile entities that have the given block position in the given range in the world
	 *
	 * @param world The world
	 * @param pos The block position
	 * @return A list of all tile entities that have the given block position in their range
	 */
	public List<TE> getTileEntitiesInRange(World world, BlockPos pos) {
		final Collection<BlockPos> tileEntities = getTrackedTileEntities(world);
		List<TE> returnValue = new ArrayList<>();

		for (Iterator<BlockPos> it = tileEntities.iterator(); it.hasNext();) {
			BlockPos tePos = it.next();

			if (tePos != null) {
				TE te = (TE) world.getTileEntity(tePos);

				if (te != null && canReach(te, pos))
					returnValue.add(te);

				continue;
			}

			it.remove();
		}

		return returnValue;
	}

	/**
	 * Gets the positions of all tracked tile entities in the given world
	 *
	 * @param world The world to get the tracked tile entities of
	 */
	private Collection<BlockPos> getTrackedTileEntities(World world) {
		Collection<BlockPos> tileEntities = trackedTileEntities.get(world.provider.getDimension());

		if (tileEntities == null) {
			tileEntities = new HashSet<>();
			trackedTileEntities.put(world.provider.getDimension(), tileEntities);
		}

		return tileEntities;
	}

	/**
	 * Checks whether the given block position is contained in the given tile entity's range
	 *
	 * @param te The tile entity
	 * @param pos The block position to check
	 */
	public boolean canReach(TE te, BlockPos pos) {
		AxisAlignedBB range = new AxisAlignedBB(te.getPos()).grow(this.range.apply(te));

		return range.minX <= pos.getX() && range.minY <= pos.getY() && range.minZ <= pos.getZ() && range.maxX >= pos.getX() && range.maxY >= pos.getY() && range.maxZ >= pos.getZ();
	}
}

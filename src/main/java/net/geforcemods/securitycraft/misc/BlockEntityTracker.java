package net.geforcemods.securitycraft.misc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

import net.geforcemods.securitycraft.blockentities.BlockChangeDetectorBlockEntity;
import net.geforcemods.securitycraft.blockentities.RiftStabilizerBlockEntity;
import net.geforcemods.securitycraft.blockentities.SecureRedstoneInterfaceBlockEntity;
import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.blockentities.SonicSecuritySystemBlockEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * Tracks all of the tile entities in the world. Modified from
 * https://github.com/bl4ckscor3/The-Plopper/blob/1.16/src/main/java/bl4ckscor3/mod/theplopper/tracking/PlopperTracker.java
 */
public class BlockEntityTracker<TE extends TileEntity> {
	public static final BlockEntityTracker<SonicSecuritySystemBlockEntity> SONIC_SECURITY_SYSTEM = new BlockEntityTracker<>(te -> SonicSecuritySystemBlockEntity.MAX_RANGE);
	public static final BlockEntityTracker<BlockChangeDetectorBlockEntity> BLOCK_CHANGE_DETECTOR = new BlockEntityTracker<>(te -> te.getRange());
	public static final BlockEntityTracker<RiftStabilizerBlockEntity> RIFT_STABILIZER = new BlockEntityTracker<>(RiftStabilizerBlockEntity::getRange);
	public static final BlockEntityTracker<SecureRedstoneInterfaceBlockEntity> SECURE_REDSTONE_INTERFACE = new BlockEntityTracker<>(SecureRedstoneInterfaceBlockEntity::getSenderRange);
	public static final BlockEntityTracker<SecurityCameraBlockEntity> FRAME_VIEWED_SECURITY_CAMERAS = new BlockEntityTracker<>(be -> 0);
	private final Map<Integer, Collection<BlockPos>> trackedTileEntities = new ConcurrentHashMap<>();
	private final Function<TE, Integer> range;

	private BlockEntityTracker(Function<TE, Integer> range) {
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
	 * Removes all block entities from this tracker
	 */
	public void clear() {
		trackedTileEntities.clear();
	}

	/**
	 * Gets all tile entities that have the given block position in the given range in the world
	 *
	 * @param world The world
	 * @param pos The block position
	 * @return A list of all tile entities that have the given block position in their range
	 */
	public List<TE> getTileEntitiesInRange(World world, BlockPos pos) {
		return getTileEntitiesInRange(world, new Vec3d(pos.getX(), pos.getY(), pos.getZ()));
	}

	/**
	 * @see {@link #getTileEntitiesInRange(World, BlockPos)}
	 */
	public List<TE> getTileEntitiesInRange(World level, Vec3d pos) {
		return getTileEntitiesWithCondition(level, be -> canReach(be, pos));
	}

	/**
	 * Gets all block entities that are in the given range of the given block position
	 *
	 * @param level The level
	 * @param pos The block position
	 * @param range The range around the block position for getting the block entity in
	 * @return A list of all block entities that are in the given range of the given block position
	 */
	public List<TE> getTileEntitiesAround(World level, BlockPos pos, int range) {
		return iterate(level, (list, bePos) -> {
			if (isInRange(pos, range, new Vec3d(bePos.getX(), bePos.getY(), bePos.getZ())))
				list.add((TE) level.getTileEntity(bePos));
		});
	}

	/**
	 * Gets all block entities that are in the given level and satisfy the given condition
	 *
	 * @param level The level
	 * @param condition The condition predicate block entities are checked against
	 * @return A list of all block entities that are in given level and satisfy the given condition
	 */
	public List<TE> getTileEntitiesWithCondition(World level, Predicate<TE> condition) {
		return iterate(level, (list, bePos) -> {
			TE be = (TE) level.getTileEntity(bePos);

			if (be != null && condition.test(be))
				list.add(be);
		});
	}

	private List<TE> iterate(World level, BiConsumer<List<TE>, BlockPos> listAdder) {
		final Collection<BlockPos> blockEntities = getTrackedTileEntities(level);
		List<TE> returnValue = new ArrayList<>();
		Iterator<BlockPos> it = blockEntities.iterator();

		while (it.hasNext()) {
			BlockPos tePos = it.next();

			if (tePos != null) {
				try {
					listAdder.accept(returnValue, tePos);
					continue;
				}
				catch (Exception e) {}
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
	public Collection<BlockPos> getTrackedTileEntities(World world) {
		Collection<BlockPos> tileEntities = trackedTileEntities.get(world.provider.getDimension());

		if (tileEntities == null) {
			tileEntities = ConcurrentHashMap.newKeySet();
			trackedTileEntities.put(world.provider.getDimension(), tileEntities);
		}

		return tileEntities;
	}

	/**
	 * Checks whether the given position is contained in the given tile entity's range
	 *
	 * @param te The tile entity
	 * @param pos The position to check
	 * @return true if the position is in range of the block entity, false otherwise
	 */
	public boolean canReach(TE be, Vec3d pos) {
		return isInRange(be.getPos(), range.apply(be), pos);
	}

	/**
	 * Checks whether a position is contained in the range around the given block position
	 *
	 * @param around The block position around which to check
	 * @param range The range to check within
	 * @param pos The position to check
	 * @return true if the position is in range of the first given position, false otherwise
	 */
	public boolean isInRange(BlockPos around, int range, Vec3d pos) {
		AxisAlignedBB testRange = new AxisAlignedBB(around).grow(range);

		return testRange.minX <= pos.x && testRange.minY <= pos.y && testRange.minZ <= pos.z && testRange.maxX >= pos.x && testRange.maxY >= pos.y && testRange.maxZ >= pos.z;
	}
}

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
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * Tracks all block entities of the given class in the world. Modified from
 * https://github.com/bl4ckscor3/The-Plopper/blob/1.16/src/main/java/bl4ckscor3/mod/theplopper/tracking/PlopperTracker.java
 */
public final class BlockEntityTracker<BE extends BlockEntity> {
	public static final BlockEntityTracker<SonicSecuritySystemBlockEntity> SONIC_SECURITY_SYSTEM = new BlockEntityTracker<>(be -> SonicSecuritySystemBlockEntity.MAX_RANGE);
	public static final BlockEntityTracker<BlockChangeDetectorBlockEntity> BLOCK_CHANGE_DETECTOR = new BlockEntityTracker<>(be -> be.getRange());
	public static final BlockEntityTracker<RiftStabilizerBlockEntity> RIFT_STABILIZER = new BlockEntityTracker<>(RiftStabilizerBlockEntity::getRange);
	public static final BlockEntityTracker<SecureRedstoneInterfaceBlockEntity> SECURE_REDSTONE_INTERFACE = new BlockEntityTracker<>(SecureRedstoneInterfaceBlockEntity::getSenderRange);
	public static final BlockEntityTracker<SecurityCameraBlockEntity> FRAME_VIEWED_SECURITY_CAMERAS = new BlockEntityTracker<>(be -> 0);
	private final Map<ResourceKey<Level>, Collection<BlockPos>> trackedBlockEntities = new ConcurrentHashMap<>();
	private final Function<BE, Integer> range;

	private BlockEntityTracker(Function<BE, Integer> range) {
		this.range = range;
	}

	/**
	 * Starts tracking a block entity
	 *
	 * @param be The block entity to track
	 */
	public void track(BE be) {
		getTrackedBlockEntities(be.getLevel()).add(be.getBlockPos().immutable());
	}

	/**
	 * Stops tracking the given block entity
	 *
	 * @param be The block entity to stop tracking
	 */
	public void stopTracking(BE be) {
		getTrackedBlockEntities(be.getLevel()).remove(be.getBlockPos());
	}

	/**
	 * Removes all block entities from this tracker
	 */
	public void clear() {
		trackedBlockEntities.clear();
	}

	/**
	 * Gets all block entities that have the given block position in their range in the level
	 *
	 * @param level The level
	 * @param pos The block position
	 * @return A list of all block entities that have the given block position in their range
	 */
	public List<BE> getBlockEntitiesInRange(Level level, BlockPos pos) {
		return getBlockEntitiesInRange(level, new Vec3(pos.getX(), pos.getY(), pos.getZ()));
	}

	/**
	 * @see {@link #getBlockEntitiesInRange(Level, BlockPos)}
	 */
	public List<BE> getBlockEntitiesInRange(Level level, Vec3 pos) {
		return getBlockEntitiesWithCondition(level, be -> canReach(be, pos));
	}

	/**
	 * Gets all block entities that are in the given range of the given block position
	 *
	 * @param level The level
	 * @param pos The block position
	 * @param range The range around the block position for getting the block entity in
	 * @return A list of all block entities that are in the given range of the given block position
	 */
	public List<BE> getBlockEntitiesAround(Level level, BlockPos pos, int range) {
		return iterate(level, (list, bePos) -> {
			if (isInRange(pos, range, new Vec3(bePos.getX(), bePos.getY(), bePos.getZ())))
				list.add((BE) level.getBlockEntity(bePos));
		});
	}

	/**
	 * Gets all block entities that are in the given level and satisfy the given condition
	 *
	 * @param level The level
	 * @param condition The condition predicate block entities are checked against
	 * @return A list of all block entities that are in given level and satisfy the given condition
	 */
	public List<BE> getBlockEntitiesWithCondition(Level level, Predicate<BE> condition) {
		return iterate(level, (list, bePos) -> {
			BE be = (BE) level.getBlockEntity(bePos);

			if (be != null && condition.test(be))
				list.add(be);
		});
	}

	private List<BE> iterate(Level level, BiConsumer<List<BE>, BlockPos> listAdder) {
		final Collection<BlockPos> blockEntities = getTrackedBlockEntities(level);
		List<BE> returnValue = new ArrayList<>();
		Iterator<BlockPos> it = blockEntities.iterator();

		while (it.hasNext()) {
			BlockPos bePos = it.next();

			if (bePos != null) {
				try {
					listAdder.accept(returnValue, bePos);
					continue;
				}
				catch (Exception e) {}
			}

			it.remove();
		}

		return returnValue;
	}

	/**
	 * Gets the positions of all tracked block entities in the given level
	 *
	 * @param level The level to get the tracked block entities of
	 */
	public Collection<BlockPos> getTrackedBlockEntities(Level level) {
		Collection<BlockPos> blockEntities = trackedBlockEntities.get(level.dimension());

		if (blockEntities == null) {
			blockEntities = ConcurrentHashMap.newKeySet();
			trackedBlockEntities.put(level.dimension(), blockEntities);
		}

		return blockEntities;
	}

	/**
	 * Checks whether the given position is contained in the given block entity's range
	 *
	 * @param be The block entitiy
	 * @param pos The position to check
	 * @return true if the position is in range of the block entity, false otherwise
	 */
	public boolean canReach(BE be, Vec3 pos) {
		return isInRange(be.getBlockPos(), range.apply(be), pos);
	}

	/**
	 * Checks whether a position is contained in the range around the given block position
	 *
	 * @param around The block position around which to check
	 * @param range The range to check within
	 * @param pos The position to check
	 * @return true if the position is in range of the first given position, false otherwise
	 */
	public boolean isInRange(BlockPos around, int range, Vec3 pos) {
		AABB testRange = new AABB(around).inflate(range);

		return testRange.minX <= pos.x && testRange.minY <= pos.y && testRange.minZ <= pos.z && testRange.maxX >= pos.x && testRange.maxY >= pos.y && testRange.maxZ >= pos.z;
	}
}

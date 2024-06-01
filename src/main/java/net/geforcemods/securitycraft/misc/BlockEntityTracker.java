package net.geforcemods.securitycraft.misc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import net.geforcemods.securitycraft.blockentities.BlockChangeDetectorBlockEntity;
import net.geforcemods.securitycraft.blockentities.RiftStabilizerBlockEntity;
import net.geforcemods.securitycraft.blockentities.SecureRedstoneInterfaceBlockEntity;
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
	public static final BlockEntityTracker<SecureRedstoneInterfaceBlockEntity> SECURE_REDSTONE_INTERFACE = new BlockEntityTracker<>(be -> 16);
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
	 * Gets all block entities that have the given block position in their range in the level
	 *
	 * @param level The level
	 * @param pos The block position
	 * @return A list of all block entities that have the given block position in their range
	 */
	public List<BE> getBlockEntitiesInRange(Level level, BlockPos pos) {
		return getBlockEntitiesInRange(level, new Vec3(pos.getX(), pos.getY(), pos.getZ()));
	}

	public List<BE> getBlockEntitiesInRange(Level level, Vec3 pos) {
		final Collection<BlockPos> blockEntities = getTrackedBlockEntities(level);
		List<BE> returnValue = new ArrayList<>();
		Iterator<BlockPos> it = blockEntities.iterator();

		while (it.hasNext()) {
			BlockPos bePos = it.next();

			if (bePos != null) {
				try {
					BE be = (BE) level.getBlockEntity(bePos);

					if (be != null && canReach(be, pos))
						returnValue.add(be);

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
	 */
	public boolean canReach(BE be, Vec3 pos) {
		AABB testRange = new AABB(be.getBlockPos()).inflate(this.range.apply(be));

		return testRange.minX <= pos.x && testRange.minY <= pos.y && testRange.minZ <= pos.z && testRange.maxX >= pos.x && testRange.maxY >= pos.y && testRange.maxZ >= pos.z;
	}
}

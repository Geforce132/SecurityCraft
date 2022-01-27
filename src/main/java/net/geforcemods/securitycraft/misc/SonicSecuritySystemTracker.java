package net.geforcemods.securitycraft.misc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.geforcemods.securitycraft.blockentities.SonicSecuritySystemBlockEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Tracks all of the Sonic Security Systems in the world. Used when notes are played or lockable blocks are interacted with.
 * Modified from
 * https://github.com/bl4ckscor3/The-Plopper/blob/1.16/src/main/java/bl4ckscor3/mod/theplopper/tracking/PlopperTracker.java
 */
public class SonicSecuritySystemTracker {
	private static final Map<RegistryKey<World>, Collection<BlockPos>> trackedSonicSecuritySystems = new HashMap<>();

	/**
	 * Starts tracking a Sonic Security System
	 *
	 * @param te The Sonic Security System to track
	 */
	public static void track(SonicSecuritySystemBlockEntity te) {
		getTrackedSonicSecuritySystems(te.getLevel()).add(te.getBlockPos().immutable());
	}

	/**
	 * Stops tracking the given Sonic Security System. Use when e.g. removing the tile entity from the world
	 *
	 * @param te The Sonic Security System to stop tracking
	 */
	public static void stopTracking(SonicSecuritySystemBlockEntity te) {
		getTrackedSonicSecuritySystems(te.getLevel()).remove(te.getBlockPos());
	}

	/**
	 * Gets all Sonic Security Systems that have the given block position in their range in the given world
	 *
	 * @param world The world
	 * @param pos The block position
	 * @return A list of all sonic security systems that have the given block position in their range
	 */
	public static List<SonicSecuritySystemBlockEntity> getSonicSecuritySystemsInRange(World world, BlockPos pos) {
		return getSonicSecuritySystemsInRange(world, pos, SonicSecuritySystemBlockEntity.MAX_RANGE);
	}

	/**
	 * Gets all Sonic Security Systems that have the given block position in the given range in the world
	 *
	 * @param world The world
	 * @param pos The block position
	 * @param range The range to search within (in number of blocks)
	 * @return A list of all sonic security systems that have the given block position in their range
	 */
	public static List<SonicSecuritySystemBlockEntity> getSonicSecuritySystemsInRange(World world, BlockPos pos, int range) {
		final Collection<BlockPos> sonicSecuritySystems = getTrackedSonicSecuritySystems(world);
		List<SonicSecuritySystemBlockEntity> returnValue = new ArrayList<>();

		for (Iterator<BlockPos> it = sonicSecuritySystems.iterator(); it.hasNext();) {
			BlockPos sonicSecuritySystemPos = it.next();

			if (sonicSecuritySystemPos != null) {
				TileEntity potentialSonicSecuritySystem = world.getBlockEntity(sonicSecuritySystemPos);

				if (potentialSonicSecuritySystem instanceof SonicSecuritySystemBlockEntity) {
					if (canSonicSecuritySystemReach((SonicSecuritySystemBlockEntity) potentialSonicSecuritySystem, pos))
						returnValue.add((SonicSecuritySystemBlockEntity) potentialSonicSecuritySystem);

					continue;
				}
			}

			it.remove();
		}

		return returnValue;
	}

	/**
	 * Gets the positions of all tracked Sonic Security Systems in the given world
	 *
	 * @param world The world to get the tracked Sonic Security Systems of
	 */
	private static Collection<BlockPos> getTrackedSonicSecuritySystems(World world) {
		Collection<BlockPos> sonicSecuritySystems = trackedSonicSecuritySystems.get(world.dimension());

		if (sonicSecuritySystems == null) {
			sonicSecuritySystems = new HashSet<>();
			trackedSonicSecuritySystems.put(world.dimension(), sonicSecuritySystems);
		}

		return sonicSecuritySystems;
	}

	/**
	 * Checks whether the given block position is contained in the given Sonic Security System's range
	 *
	 * @param te The Sonic Security System
	 * @param pos The block position to check
	 */
	public static boolean canSonicSecuritySystemReach(SonicSecuritySystemBlockEntity te, BlockPos pos) {
		AxisAlignedBB sssRange = new AxisAlignedBB(te.getBlockPos()).inflate(SonicSecuritySystemBlockEntity.MAX_RANGE);

		return sssRange.minX <= pos.getX() && sssRange.minY <= pos.getY() && sssRange.minZ <= pos.getZ() && sssRange.maxX >= pos.getX() && sssRange.maxY >= pos.getY() && sssRange.maxZ >= pos.getZ();
	}
}

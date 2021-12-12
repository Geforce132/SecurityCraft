package net.geforcemods.securitycraft.misc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.geforcemods.securitycraft.blockentities.SonicSecuritySystemBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

/**
 * Tracks all of the Sonic Security Systems in the world. Used when
 * notes are played or lockable blocks are interacted with. Modified from
 * https://github.com/bl4ckscor3/The-Plopper/blob/1.16/src/main/java/bl4ckscor3/mod/theplopper/tracking/PlopperTracker.java
 */
public class SonicSecuritySystemTracker {

	private static final Map<ResourceKey<Level>, Collection<BlockPos>> trackedSonicSecuritySystems = new HashMap<>();

	/**
	 * Starts tracking a Sonic Security System
	 * @param be The Sonic Security System to track
	 */
	public static void track(SonicSecuritySystemBlockEntity be)
	{
		getTrackedSonicSecuritySystems(be.getLevel()).add(be.getBlockPos().immutable());
	}

	/**
	 * Stops tracking the given Sonic Security System. Use when e.g. removing the tile entity from the world
	 * @param be The Sonic Security System to stop tracking
	 */
	public static void stopTracking(SonicSecuritySystemBlockEntity be)
	{
		getTrackedSonicSecuritySystems(be.getLevel()).remove(be.getBlockPos());
	}

	/**
	 * Gets all Sonic Security Systems that have the given block position in their range in the given world
	 * @param level The level
	 * @param pos The block position
	 * @return A list of all sonic security systems that have the given block position in their range
	 */
	public static List<SonicSecuritySystemBlockEntity> getSonicSecuritySystemsInRange(Level level, BlockPos pos) {
		return getSonicSecuritySystemsInRange(level, pos, SonicSecuritySystemBlockEntity.MAX_RANGE);
	}

	/**
	 * Gets all Sonic Security Systems that have the given block position in the given range in the level
	 * @param level The level
	 * @param pos The block position
	 * @param range The range to search within (in number of blocks)
	 * @return A list of all sonic security systems that have the given block position in their range
	 */
	public static List<SonicSecuritySystemBlockEntity> getSonicSecuritySystemsInRange(Level level, BlockPos pos, int range)
	{
		final Collection<BlockPos> sonicSecuritySystems = getTrackedSonicSecuritySystems(level);
		List<SonicSecuritySystemBlockEntity> returnValue = new ArrayList<>();

		for(Iterator<BlockPos> it = sonicSecuritySystems.iterator(); it.hasNext(); )
		{
			BlockPos sonicSecuritySystemPos = it.next();

			if(sonicSecuritySystemPos != null)
			{
				if(level.getBlockEntity(sonicSecuritySystemPos) instanceof SonicSecuritySystemBlockEntity sss)
				{
					if(canSonicSecuritySystemReach(sss, pos))
						returnValue.add(sss);

					continue;
				}
			}

			it.remove();
		}

		return returnValue;
	}

	/**
	 * Gets the positions of all tracked Sonic Security Systems in the given level
	 * @param level The level to get the tracked Sonic Security Systems of
	 */
	private static Collection<BlockPos> getTrackedSonicSecuritySystems(Level level)
	{
		Collection<BlockPos> sonicSecuritySystems = trackedSonicSecuritySystems.get(level.dimension());

		if(sonicSecuritySystems == null)
		{
			sonicSecuritySystems = new HashSet<>();
			trackedSonicSecuritySystems.put(level.dimension(), sonicSecuritySystems);
		}

		return sonicSecuritySystems;
	}

	/**
	 * Checks whether the given block position is contained in the given Sonic Security System's range
	 * @param be The Sonic Security System
	 * @param pos The block position to check
	 */
	public static boolean canSonicSecuritySystemReach(SonicSecuritySystemBlockEntity be, BlockPos pos)
	{
		AABB sssRange = new AABB(be.getBlockPos()).inflate(SonicSecuritySystemBlockEntity.MAX_RANGE);

		return sssRange.minX <= pos.getX() && sssRange.minY <= pos.getY() && sssRange.minZ <= pos.getZ() && sssRange.maxX >= pos.getX() && sssRange.maxY >= pos.getY() && sssRange.maxZ >= pos.getZ();
	}

}

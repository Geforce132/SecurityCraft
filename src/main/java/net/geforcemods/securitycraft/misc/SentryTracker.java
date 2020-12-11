package net.geforcemods.securitycraft.misc;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;

import net.geforcemods.securitycraft.entity.EntitySentry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Tracks all exisiting sentries so searching for them all the time is obsolete.
 * Also manages range checks
 */
public class SentryTracker
{
	private static final Map<Integer,Collection<EntitySentry>> TRACKED_SENTRIES = new HashMap<>();

	/**
	 * Starts tracking a sentry
	 * @param te The sentry to track
	 */
	public static void track(EntitySentry entity)
	{
		getTrackedSentries(entity.world).add(entity);
	}

	/**
	 * Stops tracking the given sentry. Use when e.g. removing the tile entity from the world
	 * @param te The sentry to stop tracking
	 */
	public static void stopTracking(EntitySentry entity)
	{
		getTrackedSentries(entity.world).remove(entity);
	}

	/**
	 * Gets the sentry that is at the given block position in the given world
	 * @param world The world
	 * @param pos The block position
	 * @return The sentry at the given block position.
	 */
	public static Optional<EntitySentry> getSentryAtPosition(World world, BlockPos pos)
	{
		for(EntitySentry sentry : getTrackedSentries(world))
		{
			if(sentry.getPosition().equals(pos))
				return Optional.of(sentry);
		}

		return Optional.empty();
	}

	/**
	 * Gets all block positions at which a sentry is being tracked for the given world
	 * @param world The world to get the tracked sentries of
	 */
	private static Collection<EntitySentry> getTrackedSentries(World world)
	{
		Collection<EntitySentry> sentries = TRACKED_SENTRIES.get(world.provider.getDimension());

		if(sentries == null)
		{
			sentries = new HashSet<>();
			TRACKED_SENTRIES.put(world.provider.getDimension(), sentries);
		}

		return sentries;
	}
}
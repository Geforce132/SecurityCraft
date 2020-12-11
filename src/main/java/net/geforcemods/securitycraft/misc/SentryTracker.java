package net.geforcemods.securitycraft.misc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import net.geforcemods.securitycraft.entity.EntitySentry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Tracks all exisiting sentries server side, so searching for them all the time is obsolete.
 * Also manages range checks
 */
public class SentryTracker
{
	private static final Map<Integer,List<EntitySentry>> TRACKED_SENTRIES = new HashMap<>();

	/**
	 * Starts tracking a sentry
	 * @param entity The sentry to track
	 */
	public static void track(EntitySentry entity)
	{
		if(entity.world.isRemote)
			return;

		List<EntitySentry> sentries = getTrackedSentries(entity.world);

		if(!sentries.contains(entity))
			sentries.add(entity);
	}

	/**
	 * Stops tracking the given sentry. Use when e.g. removing the tile entity from the world
	 * @param entity The sentry to stop tracking
	 */
	public static void stopTracking(EntitySentry entity)
	{
		if(entity.world.isRemote)
			return;

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
		if(!world.isRemote)
		{
			List<EntitySentry> sentries = getTrackedSentries(world);

			for(int i = 0; i < sentries.size(); i++)
			{
				EntitySentry sentry = sentries.get(i);

				if(sentry.getPosition().equals(pos))
					return Optional.of(sentry);
			}
		}

		return Optional.empty();
	}

	/**
	 * Gets all block positions at which a sentry is being tracked for the given world
	 * @param world The world to get the tracked sentries of
	 */
	private static List<EntitySentry> getTrackedSentries(World world)
	{
		List<EntitySentry> sentries = TRACKED_SENTRIES.get(world.provider.getDimension());

		if(sentries == null)
		{
			sentries = new ArrayList<>();
			TRACKED_SENTRIES.put(world.provider.getDimension(), sentries);
		}

		return sentries;
	}
}
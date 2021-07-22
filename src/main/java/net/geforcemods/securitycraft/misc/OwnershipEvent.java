package net.geforcemods.securitycraft.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.Event;

/**
 * Fired when a SecurityCraft block needs to have ownership information attached
 */
public class OwnershipEvent extends Event
{
	private Level world;
	private BlockPos pos;
	private Player player;

	public OwnershipEvent(Level world, BlockPos pos, Player player)
	{
		this.world = world;
		this.pos = pos;
		this.player = player;
	}

	public Level getWorld()
	{
		return world;
	}

	public BlockPos getPos()
	{
		return pos;
	}

	public Player getPlayer()
	{
		return player;
	}
}

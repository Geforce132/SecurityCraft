package net.geforcemods.securitycraft.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.Event;

/**
 * Fired when a SecurityCraft block needs to have ownership information attached
 */
public class OwnershipEvent extends Event {
	private Level level;
	private BlockPos pos;
	private Player player;

	public OwnershipEvent(Level level, BlockPos pos, Player player) {
		this.level = level;
		this.pos = pos;
		this.player = player;
	}

	public Level getLevel() {
		return level;
	}

	public BlockPos getPos() {
		return pos;
	}

	public Player getPlayer() {
		return player;
	}
}

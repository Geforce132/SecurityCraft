package net.geforcemods.securitycraft.misc;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.eventbus.api.Event;

/**
 * Fired when a SecurityCraft block needs to have ownership information attached
 */
public class OwnershipEvent extends Event {
	private World level;
	private BlockPos pos;
	private PlayerEntity player;

	public OwnershipEvent(World level, BlockPos pos, PlayerEntity player) {
		this.level = level;
		this.pos = pos;
		this.player = player;
	}

	public World getLevel() {
		return level;
	}

	public BlockPos getPos() {
		return pos;
	}

	public PlayerEntity getPlayer() {
		return player;
	}
}

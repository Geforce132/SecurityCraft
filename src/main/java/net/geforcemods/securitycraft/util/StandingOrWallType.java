package net.geforcemods.securitycraft.util;

import java.util.function.Function;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.level.block.state.BlockState;

public enum StandingOrWallType implements StringRepresentable {
	NONE(blockItem -> blockItem.getBlock().defaultBlockState()), //theoretically don't need anything here
	STANDING(blockItem -> blockItem.wallBlock.defaultBlockState()), //the new block after changing the property from STANDING to WALL is the wall block
	WALL(blockItem -> blockItem.getBlock().defaultBlockState()); //the new block after changing the property from WALL to STANDING is the standing block

	private final Function<StandingAndWallBlockItem, BlockState> newState;

	private StandingOrWallType(Function<StandingAndWallBlockItem, BlockState> newState) {
		this.newState = newState;
	}

	public BlockState getNewState(StandingAndWallBlockItem blockItem) {
		return newState.apply(blockItem);
	}

	@Override
	public String getSerializedName() {
		return name().toLowerCase();
	}
}
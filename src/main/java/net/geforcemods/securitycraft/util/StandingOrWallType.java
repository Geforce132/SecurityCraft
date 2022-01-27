package net.geforcemods.securitycraft.util;

import java.util.function.Function;

import net.minecraft.block.BlockState;
import net.minecraft.item.WallOrFloorItem;
import net.minecraft.util.IStringSerializable;

public enum StandingOrWallType implements IStringSerializable {
	NONE(blockItem -> blockItem.getBlock().defaultBlockState()), //theoretically don't need anything here
	STANDING(blockItem -> blockItem.wallBlock.defaultBlockState()), //the new block after changing the property from STANDING to WALL is the wall block
	WALL(blockItem -> blockItem.getBlock().defaultBlockState()); //the new block after changing the property from WALL to STANDING is the standing block

	private final Function<WallOrFloorItem, BlockState> newState;

	private StandingOrWallType(Function<WallOrFloorItem, BlockState> newState) {
		this.newState = newState;
	}

	public BlockState getNewState(WallOrFloorItem blockItem) {
		return newState.apply(blockItem);
	}

	@Override
	public String getSerializedName() {
		return name().toLowerCase();
	}
}
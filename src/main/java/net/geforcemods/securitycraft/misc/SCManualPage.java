package net.geforcemods.securitycraft.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BoatItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public record SCManualPage(Item item, PageGroup group, Component title, Component helpInfo, String designedBy, boolean hasRecipeDescription) {
	public Object getInWorldObject() {
		if (item instanceof BlockItem blockItem) {
			Block block = blockItem.getBlock();

			if (block.defaultBlockState().hasBlockEntity())
				return ((EntityBlock) block).newBlockEntity(BlockPos.ZERO, block.defaultBlockState());
		}
		else if (item instanceof BoatItem boatItem)
			return boatItem.getBoat(null, BlockHitResult.miss(Vec3.ZERO, Direction.NORTH, BlockPos.ZERO), item.getDefaultInstance(), null);

		return null;
	}
}

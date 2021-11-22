package net.geforcemods.securitycraft.api;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class LinkedBlock {

	public String blockName;
	public BlockPos blockPos = null;

	public LinkedBlock(String name, BlockPos pos) {
		blockName = name;
		blockPos = pos;
	}

	public LinkedBlock(LinkableBlockEntity blockEntity) {
		blockName = blockEntity.getBlockState().getBlock().getDescriptionId();
		blockPos = blockEntity.getBlockPos();
	}

	public boolean validate(Level world) {
		return !(world == null || (world.isEmptyBlock(blockPos) || !world.getBlockState(blockPos).getBlock().getDescriptionId().equals(blockName)));
	}

	public LinkableBlockEntity asBlockEntity(Level world) {
		if(!validate(world)) return null;

		return (LinkableBlockEntity) world.getBlockEntity(blockPos);
	}

	public String getBlockName() {
		return blockName;
	}

	public void setName(String blockName) {
		this.blockName = blockName;
	}

	public void setPos(BlockPos pos) {
		blockPos = pos;
	}

	public BlockPos getPos() {
		return blockPos;
	}

	public int getX() {
		return blockPos.getX();
	}

	public int getY() {
		return blockPos.getY();
	}

	public int getZ() {
		return blockPos.getZ();
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof LinkedBlock block) {
			return (block.getPos().getX() == blockPos.getX() && block.getPos().getY() == blockPos.getY() && block.getPos().getZ() == blockPos.getZ());
		}

		return false;
	}

	@Override
	public int hashCode() {
		return blockPos.getX() + blockPos.getY() + blockPos.getZ();
	}

	@Override
	public String toString() {
		return (blockName + " | " + blockPos.getX() + " " + blockPos.getY() + " " + blockPos.getZ());
	}

}

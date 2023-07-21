package net.geforcemods.securitycraft.api;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LinkedBlock {
	public String blockName;
	public BlockPos blockPos = null;

	public LinkedBlock(String name, BlockPos pos) {
		blockName = name;
		blockPos = pos;
	}

	public LinkedBlock(LinkableBlockEntity te) {
		blockPos = te.getPos();
		blockName = te.getWorld().getBlockState(blockPos).getBlock().getTranslationKey();
	}

	public boolean validate(World world) {
		return !(world == null || (world.isAirBlock(blockPos) || !world.getBlockState(blockPos).getBlock().getTranslationKey().equals(blockName)));
	}

	public LinkableBlockEntity asTileEntity(World world) {
		if (!validate(world))
			return null;

		return (LinkableBlockEntity) world.getTileEntity(blockPos);
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
		return blockPos != null && o instanceof LinkedBlock && blockPos.equals(((LinkedBlock) o).getPos());
	}

	@Override
	public int hashCode() {
		return blockPos == null ? 0 : blockPos.hashCode();
	}

	@Override
	public String toString() {
		return (blockName + " | " + blockPos.getX() + " " + blockPos.getY() + " " + blockPos.getZ());
	}
}

package net.geforcemods.securitycraft.api;

import net.minecraft.world.World;

public class LinkedBlock {

	public String blockName;
	public int blockX = 0;
	public int blockY = 0;
	public int blockZ = 0;

	public LinkedBlock(String name, int x, int y, int z) {
		blockName = name;
		blockX = x;
		blockY = y;
		blockZ = z;
	}

	public LinkedBlock(CustomizableSCTE tileEntity) {
		blockName = tileEntity.getWorld().getBlock(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord).getUnlocalizedName();
		blockX = tileEntity.xCoord;
		blockY = tileEntity.yCoord;
		blockZ = tileEntity.zCoord;
	}

	public boolean validate(World world) {
		if(world == null || (world.isAirBlock(blockX, blockY, blockZ) || !world.getBlock(blockX, blockY, blockZ).getUnlocalizedName().equals(blockName))) return false;

		return true;
	}

	public CustomizableSCTE asTileEntity(World world) {
		if(!validate(world)) return null;

		return (CustomizableSCTE) world.getTileEntity(blockX, blockY, blockZ);
	}

	public String getBlockName() {
		return blockName;
	}

	public void setName(String blockName) {
		this.blockName = blockName;
	}

	public void setX(int blockX) {
		this.blockX = blockX;
	}

	public void setY(int blockY) {
		this.blockY = blockY;
	}

	public void setZ(int blockZ) {
		this.blockZ = blockZ;
	}

	public int getX() {
		return blockX;
	}

	public int getY() {
		return blockY;
	}

	public int getZ() {
		return blockZ;
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof LinkedBlock) {
			LinkedBlock block = (LinkedBlock) o;
			return (block.blockX == blockX && block.blockY == blockY && block.blockZ == blockZ);
		}

		return false;
	}

	@Override
	public int hashCode() {
		return blockX + blockY + blockZ;
	}

	@Override
	public String toString() {
		return (blockName + " | " + blockX + " " + blockY + " " + blockZ);
	}

}

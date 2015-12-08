package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockOwnable extends BlockContainer {

	public BlockOwnable(Material par1) {
		super(par1);
	}

	public TileEntity createNewTileEntity(World var1, int var2) {
		return new TileEntityOwnable();
	}

}

package org.freeforums.geforce.securitycraft.interfaces;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public interface IIntersectable extends ITileEntityProvider{
			
	public void onEntityIntersected(World world, BlockPos pos, Entity entity);

	public TileEntity createNewTileEntity(World worldIn, int meta);
}

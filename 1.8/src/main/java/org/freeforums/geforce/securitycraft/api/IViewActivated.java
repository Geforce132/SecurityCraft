package org.freeforums.geforce.securitycraft.api;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

/**
 * This interface can be used to run code that gets called
 * whenever a player mouses-over the Block. <p>
 * 
 * Return tileEntitySCTE.viewActivated() in createNewTileEntity() to
 * enable onEntityLookedAtBlock() to be called.
 * 
 * @author Geforce
 */
public interface IViewActivated extends ITileEntityProvider {

	public void onEntityLookedAtBlock(World world, BlockPos pos, EntityPlayer entity);

	public TileEntity createNewTileEntity(World worldIn, int meta);

}

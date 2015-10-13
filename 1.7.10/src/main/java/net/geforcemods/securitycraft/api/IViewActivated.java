package net.geforcemods.securitycraft.api;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * This interface can be used to run code that gets called
 * whenever a player mouses-over a Block. <p>
 * 
 * Return tileEntitySCTE.viewActivated() in createNewTileEntity() to
 * enable onEntityLookedAtBlock() to be called.
 * 
 * @author Geforce
 */
public interface IViewActivated extends ITileEntityProvider {

	public void onEntityLookedAtBlock(World world, int x, int y, int z, EntityLivingBase entity);

	public TileEntity createNewTileEntity(World worldIn, int meta);

}

package net.geforcemods.securitycraft.api;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * This interface can be used to create Blocks that do something when
 * an Entity collides with the Block's bounding box. <p>
 *
 * Return tileEntitySCTE.intersectsEntities() in createNewTileEntity() to
 * enable onEntityIntersected() to be called.
 *
 * @author Geforce
 */
public interface IIntersectable extends ITileEntityProvider {

	/**
	 * Called when an Entity collides with a Block's bounding box.
	 */
	public void onEntityIntersected(World world, BlockPos pos, Entity entity);

	@Override
	public TileEntity createNewTileEntity(World world, int meta);
}

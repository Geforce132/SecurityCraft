package net.geforcemods.securitycraft.api;

import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

/**
 * This interface can be used to create Blocks that do something when
 * an Entity collides with the Block's bounding box. <p>
 *
 * Return tileEntitySCTE.intersectsEntities() in createNewTileEntity() to
 * enable onEntityIntersected() to be called.
 *
 * @author Geforce
 */
public interface IIntersectable {

	/**
	 * Called when an Entity collides with a Block's bounding box.
	 */
	public void onEntityIntersected(Level world, BlockPos pos, Entity entity);
}

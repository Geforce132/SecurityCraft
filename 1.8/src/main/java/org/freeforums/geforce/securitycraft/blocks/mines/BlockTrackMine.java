package org.freeforums.geforce.securitycraft.blocks.mines;

import net.minecraft.block.BlockRail;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import org.freeforums.geforce.securitycraft.main.Utils;

public class BlockTrackMine extends BlockRail{
	
	public BlockTrackMine() {
		super();
	}
	
	public void onMinecartPass(World world, EntityMinecart cart, BlockPos pos)
    {
		Utils.destroyBlock(world, pos, false);
		world.createExplosion(cart, pos.getX(), pos.getY() + 1, pos.getZ(), 8.0F, true);
		cart.setDead();
    }    

}

package org.freeforums.geforce.securitycraft.blocks.mines;

import org.freeforums.geforce.securitycraft.main.Utils.BlockUtils;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;

import net.minecraft.block.BlockRail;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class BlockTrackMine extends BlockRail {
	
	public BlockTrackMine() {
		super();
	}
	
	public void onMinecartPass(World world, EntityMinecart cart, BlockPos pos){
		BlockUtils.destroyBlock(world, pos, false);
		
		if(mod_SecurityCraft.configHandler.smallerMineExplosion){
			world.createExplosion(cart, pos.getX(), pos.getY() + 1, pos.getZ(), 4.0F, true);
		}else{
			world.createExplosion(cart, pos.getX(), pos.getY() + 1, pos.getZ(), 8.0F, true);
		}
		
		cart.setDead();
    }

}

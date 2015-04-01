package org.freeforums.geforce.securitycraft.blocks.mines;

import net.minecraft.block.BlockRail;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import org.freeforums.geforce.securitycraft.interfaces.IHelpInfo;
import org.freeforums.geforce.securitycraft.main.Utils;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;

public class BlockTrackMine extends BlockRail implements IHelpInfo {
	
	public BlockTrackMine() {
		super();
	}
	
	public void onMinecartPass(World world, EntityMinecart cart, BlockPos pos)
    {
		Utils.destroyBlock(world, pos, false);
		
		if(mod_SecurityCraft.configHandler.smallerMineExplosion){
			world.createExplosion(cart, pos.getX(), pos.getY() + 1, pos.getZ(), 4.0F, true);
		}else{
			world.createExplosion(cart, pos.getX(), pos.getY() + 1, pos.getZ(), 8.0F, true);
		}
		
		cart.setDead();
    }

	public String getHelpInfo() {
		return "The track mine explodes when a minecart passes on top of it.";
	}

	public String[] getRecipe() {
		return new String[]{"The track mine requires: 6 iron ingots, 1 stick, 1 gunpowder", "X X", "XYX", "XZX", "X = iron ingot, Y = stick, Z = gunpowder"};
	}    

}

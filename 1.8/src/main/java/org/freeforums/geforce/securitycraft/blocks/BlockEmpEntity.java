package org.freeforums.geforce.securitycraft.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.world.World;

public class BlockEmpEntity extends Block{
	
	public BlockEmpEntity(Material par2Material) {
		super(par2Material);
	}

	public boolean isOpaqueCube(){
		return false;	
	}
	
	/**
     * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
     */
    public boolean renderAsNormalBlock()
    {
        return false;
    }
    
    /**
     * Returns the ID of the items to drop on destruction.
     */
    public Item getItemDropped(int par1, Random par2Random, int par3)
    {
        return null;
    }
    
    public Item getItem(World par1World, int par2, int par3, int par4){
    	return null;
    }
    
  
}

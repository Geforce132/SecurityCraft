package org.freeforums.geforce.securitycraft.blocks;

import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;

public class BlockBogusLava extends BlockDynamicLiquid{		

    public BlockBogusLava(Material par1Material)
    {
        super(par1Material);
    }

    /**
     * Updates the flow for the BlockFlowing object.
     */
    private void placeStaticBlock(World par1World, BlockPos pos, IBlockState state)
    {
    	par1World.setBlockState(pos, getStaticBlock(this.blockMaterial).getDefaultState().withProperty(LEVEL, state.getValue(LEVEL)), 2);
    }

    public static BlockStaticLiquid getStaticBlock(Material materialIn)
    {
        if (materialIn == Material.water)
        {
            return mod_SecurityCraft.bogusWater;
        }
        else if (materialIn == Material.lava)
        {
            return mod_SecurityCraft.bogusLava;
        }
        else
        {
            throw new IllegalArgumentException("Invalid material");
        }
    }
    
    /**
     * Gets an item for the block being called on. Args: world, x, y, z
     */
    @SideOnly(Side.CLIENT)
    public Item getItem(World p_149694_1_, BlockPos pos)
    {
        return null;
    }
}

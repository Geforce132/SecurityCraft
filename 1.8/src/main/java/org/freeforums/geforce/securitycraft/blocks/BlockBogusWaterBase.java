package org.freeforums.geforce.securitycraft.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.freeforums.geforce.securitycraft.interfaces.IIntersectable;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;
import org.freeforums.geforce.securitycraft.misc.CustomDamageSources;
import org.freeforums.geforce.securitycraft.tileentity.TileEntitySCTE;

public class BlockBogusWaterBase extends BlockStaticLiquid implements IIntersectable {

	public BlockBogusWaterBase(Material par2Material)
	{
		super(par2Material);    
	} 
	
	public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock)
    {
        if (!this.checkForMixing(worldIn, pos, state))
        {
            this.updateLiquid(worldIn, pos, state);
        }
    }

    private void updateLiquid(World worldIn, BlockPos p_176370_2_, IBlockState p_176370_3_)
    {
        BlockDynamicLiquid blockdynamicliquid = getFlowingBlock(this.blockMaterial);
        worldIn.setBlockState(p_176370_2_, blockdynamicliquid.getDefaultState().withProperty(LEVEL, p_176370_3_.getValue(LEVEL)), 2);
        worldIn.scheduleUpdate(p_176370_2_, blockdynamicliquid, this.tickRate(worldIn));
    }
    
    public static BlockDynamicLiquid getFlowingBlock(Material materialIn)
    {
        if (materialIn == Material.water)
        {
            return (BlockDynamicLiquid) mod_SecurityCraft.bogusWaterFlowing;
        }
        else if (materialIn == Material.lava)
        {
            return (BlockDynamicLiquid) mod_SecurityCraft.bogusLavaFlowing;
        }
        else
        {
            throw new IllegalArgumentException("Invalid material");
        }
    }

	/**
	 * Triggered whenever an entity collides with this block (enters into the block). Args: world, x, y, z, entity
	 */
	public void onEntityCollidedWithBlock(World par1World, BlockPos pos, Entity par5Entity){
		if(!par1World.isRemote){
			if(par5Entity instanceof EntityPlayer && !((EntityPlayer) par5Entity).capabilities.isCreativeMode){
				//float f = ((EntityPlayer) par5Entity).getHealth();
				//((EntityPlayer) par5Entity).setHealth(f - 0.5F);
				//par1World.playSoundAtEntity(par5Entity, "random.fizz", 1.0F, 1.0F);
				((EntityPlayer) par5Entity).attackEntityFrom(CustomDamageSources.fakeWater, 5F);
			}
		}
	}
	
	public void onEntityIntersected(World world, BlockPos pos, Entity entity) {
		if(!world.isRemote){
			if(entity instanceof EntityPlayer && !((EntityPlayer) entity).capabilities.isCreativeMode){
				//float f = ((EntityPlayer) entity).getHealth();
				//((EntityPlayer) entity).setHealth(f - 0.5F);
				//world.playSoundAtEntity(entity, "random.fizz", 1.0F, 1.0F);
				((EntityPlayer) entity).attackEntityFrom(CustomDamageSources.fakeWater, 5F);
			}
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

	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntitySCTE().intersectsEntities();
	}
}

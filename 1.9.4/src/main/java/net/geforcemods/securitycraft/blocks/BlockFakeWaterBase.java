package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.api.IIntersectable;
import net.geforcemods.securitycraft.api.TileEntitySCTE;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.misc.CustomDamageSources;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

//TODO: look at this class for changed method signatures
public class BlockFakeWaterBase extends BlockStaticLiquid implements IIntersectable {

	public BlockFakeWaterBase(Material par2Material)
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
        if (materialIn == Material.WATER)
        {
            return (BlockDynamicLiquid) mod_SecurityCraft.bogusWaterFlowing;
        }
        else if (materialIn == Material.LAVA)
        {
            return (BlockDynamicLiquid) mod_SecurityCraft.bogusLavaFlowing;
        }
        else
        {
            throw new IllegalArgumentException("Invalid material");
        }
    }
	
	public void onEntityIntersected(World world, BlockPos pos, Entity entity) {
		if(!world.isRemote){
			if(entity instanceof EntityPlayer && !((EntityPlayer) entity).capabilities.isCreativeMode){
				((EntityPlayer) entity).attackEntityFrom(CustomDamageSources.fakeWater, 5F);
			}else{
				entity.attackEntityFrom(CustomDamageSources.fakeWater, 5F);
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

package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.imc.waila.ICustomWailaDisplay;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockReinforcedWood extends BlockPlanks implements ITileEntityProvider, ICustomWailaDisplay {
	
    public BlockReinforcedWood(){
        super();
        setSoundType(SoundType.WOOD);
    }
    
    @Override
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        return this.getDefaultState().withProperty(VARIANT, BlockPlanks.EnumType.byMetadata(meta));
    }
	
	@Override
	public void breakBlock(World par1World, BlockPos pos, IBlockState state){
        super.breakBlock(par1World, pos, state);
        par1World.removeTileEntity(pos);
    }

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityOwnable();
	}

	@Override
	public ItemStack getDisplayStack(World world, IBlockState state, BlockPos pos)
	{
		return new ItemStack(Item.getItemFromBlock(mod_SecurityCraft.reinforcedWoodPlanks), 1, BlockUtils.getBlockMeta(world, pos));
	}

	@Override
	public boolean shouldShowSCInfo(World world, IBlockState state, BlockPos pos)
	{
		return true;
	}
}

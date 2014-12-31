package org.freeforums.geforce.securitycraft.blocks;

import net.minecraft.block.BlockButton;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import org.freeforums.geforce.securitycraft.main.Utils;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityOwnable;

public class BlockPanicButton extends BlockButton implements ITileEntityProvider{

	public BlockPanicButton() {
		super(false);
	}
	
    /**
     * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
     */
    public boolean isNormalCube()
    {
        return false;
    }
    
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (((Boolean)state.getValue(POWERED)).booleanValue())
        {
        	worldIn.setBlockState(pos, state.withProperty(POWERED, Boolean.valueOf(false)), 3);
            worldIn.markBlockRangeForRenderUpdate(pos, pos);
            worldIn.playSoundEffect((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, "random.click", 0.3F, 0.6F);
            this.notifyNeighbors(worldIn, pos, (EnumFacing)state.getValue(FACING));
            return true;
        }
        else
        {
            worldIn.setBlockState(pos, state.withProperty(POWERED, Boolean.valueOf(true)), 3);
            worldIn.markBlockRangeForRenderUpdate(pos, pos);
            worldIn.playSoundEffect((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, "random.click", 0.3F, 0.6F);
            this.notifyNeighbors(worldIn, pos, (EnumFacing)state.getValue(FACING));
            return true;
        }
    }
    
    private void notifyNeighbors(World worldIn, BlockPos pos, EnumFacing facing)
    {
        worldIn.notifyNeighborsOfStateChange(pos, this);
        worldIn.notifyNeighborsOfStateChange(pos.offset(facing.getOpposite()), this);
    }
    
    public void onBlockPlacedBy(World par1World, BlockPos pos, IBlockState state, EntityLivingBase par5EntityLivingBase, ItemStack par6ItemStack){
    	if(par5EntityLivingBase instanceof EntityPlayer){
			((TileEntityOwnable) par1World.getTileEntity(pos)).setOwner(par5EntityLivingBase.getName());
		}
    }
    
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state){
        super.breakBlock(worldIn, pos, state);
        worldIn.removeTileEntity(pos);
    }

    public boolean onBlockEventReceived(World worldIn, BlockPos pos, IBlockState state, int eventID, int eventParam){
        super.onBlockEventReceived(worldIn, pos, state, eventID, eventParam);
        TileEntity tileentity = worldIn.getTileEntity(pos);
        return tileentity == null ? false : tileentity.receiveClientEvent(eventID, eventParam);
    }
    
    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos)
    {
        this.updateBlockBounds(worldIn.getBlockState(pos));
    }
    
    private void updateBlockBounds(IBlockState state)
    {
        EnumFacing enumfacing = (EnumFacing)state.getValue(FACING);
        boolean flag = ((Boolean)state.getValue(POWERED)).booleanValue();
        float f = 0.25F;
        float f1 = 0.375F;
        float f2 = (float)(flag ? 1 : 2) / 16.0F;
        float f3 = 0.125F;
        float f4 = 0.1875F;

        System.out.println(BlockPanicButton.SwitchEnumFacing.FACING_LOOKUP[enumfacing.ordinal()]);
        
        switch (BlockPanicButton.SwitchEnumFacing.FACING_LOOKUP[enumfacing.ordinal()])
        {
            case 1:
                this.setBlockBounds(0.0F, 0.30F, 0.20F, f2, 0.70F, 0.80F);
                break;
            case 2:
                this.setBlockBounds(1.0F - f2, 0.30F, 0.20F, 1.0F, 0.70F, 0.80F);
                break;
            case 3:
                this.setBlockBounds(0.3125F, 0.375F, 0.0F, 0.6875F, 0.625F, f2);
                break;
            case 4:
                this.setBlockBounds(0.3125F, 0.375F, 1.0F - f2, 0.6875F, 0.625F, 1.0F);
                break;
            case 5:
                this.setBlockBounds(0.3125F, 0.0F, 0.375F, 0.6875F, 0.0F + f2, 0.625F);
                break;
            case 6:
                this.setBlockBounds(0.3125F, 1.0F - f2, 0.375F, 0.6875F, 1.0F, 0.625F);
        }
    }
    
//    @SideOnly(Side.CLIENT)
//    public IBlockState getStateForEntityRender(IBlockState state)
//    {
//        return this.getDefaultState().withProperty(FACING, EnumFacing.SOUTH);
//    }
//
//    public IBlockState getStateFromMeta(int meta)
//    {
//    	
//        if(meta <= 5){
//        	EnumFacing enumfacing = EnumFacing.getFront(meta);
//
//            if (enumfacing.getAxis() == EnumFacing.Axis.Y)
//            {
//                enumfacing = EnumFacing.NORTH;
//            }
//            
//        	return this.getDefaultState().withProperty(FACING, enumfacing).withProperty(POWERED, false);
//        }else{
//        	EnumFacing enumfacing = EnumFacing.getFront(meta - 6);
//
//            if (enumfacing.getAxis() == EnumFacing.Axis.Y)
//            {
//                enumfacing = EnumFacing.NORTH;
//            }
//            
//        	return this.getDefaultState().withProperty(FACING, enumfacing).withProperty(POWERED, true);
//        }
//    }
//
//    public int getMetaFromState(IBlockState state)
//    {
//    	if(((Boolean) state.getValue(POWERED)).booleanValue()){
//    		return (((EnumFacing) state.getValue(FACING)).getIndex() + 6);
//    	}else{
//    		return ((EnumFacing) state.getValue(FACING)).getIndex();
//    	}
//    }
//
//    protected BlockState createBlockState()
//    {
//        return new BlockState(this, new IProperty[] {FACING, POWERED});
//    }

	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityOwnable();
	}
	
	static final class SwitchEnumFacing
    {
        static final int[] FACING_LOOKUP = new int[EnumFacing.values().length];
        private static final String __OBFID = "CL_00002131";

        static
        {
            try
            {
                FACING_LOOKUP[EnumFacing.EAST.ordinal()] = 1;
            }
            catch (NoSuchFieldError var6)
            {
                ;
            }

            try
            {
                FACING_LOOKUP[EnumFacing.WEST.ordinal()] = 2;
            }
            catch (NoSuchFieldError var5)
            {
                ;
            }

            try
            {
                FACING_LOOKUP[EnumFacing.SOUTH.ordinal()] = 3;
            }
            catch (NoSuchFieldError var4)
            {
                ;
            }

            try
            {
                FACING_LOOKUP[EnumFacing.NORTH.ordinal()] = 4;
            }
            catch (NoSuchFieldError var3)
            {
                ;
            }

            try
            {
                FACING_LOOKUP[EnumFacing.UP.ordinal()] = 5;
            }
            catch (NoSuchFieldError var2)
            {
                ;
            }

            try
            {
                FACING_LOOKUP[EnumFacing.DOWN.ordinal()] = 6;
            }
            catch (NoSuchFieldError var1)
            {
                ;
            }
        }
    }

}

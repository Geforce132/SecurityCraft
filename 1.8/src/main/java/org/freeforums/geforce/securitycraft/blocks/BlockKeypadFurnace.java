package org.freeforums.geforce.securitycraft.blocks;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.freeforums.geforce.securitycraft.main.Utils;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityKeypadFurnace;

public class BlockKeypadFurnace extends BlockOwnable {
	
	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	public static final PropertyBool OPEN = PropertyBool.create("open");
	public static final PropertyBool COOKING = PropertyBool.create("cooking");
	
	public BlockKeypadFurnace(Material materialIn) {
		super(materialIn);
	}
	
	/**
     * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
     * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
     */
    public boolean isOpaqueCube()
    {
        return false;
    }
    
    /**
     * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
     */
    public boolean isNormalCube()
    {
        return false;
    }
	
	public boolean onBlockActivated(World par1World, BlockPos pos, IBlockState state, EntityPlayer par5EntityPlayer, EnumFacing side, float par7, float par8, float par9){
        if(par1World.isRemote){
            return true;
        }else{
        	if(!((Boolean)Utils.getBlockProperty(par1World, pos, OPEN)).booleanValue()){
	        	TileEntityKeypadFurnace TE = (TileEntityKeypadFurnace) par1World.getTileEntity(pos);
	        	if(TE.getKeypadCode() != null && !TE.getKeypadCode().isEmpty()){
					par5EntityPlayer.openGui(mod_SecurityCraft.instance, 15, par1World, pos.getX(), pos.getY(), pos.getZ());
				}else{
					par5EntityPlayer.openGui(mod_SecurityCraft.instance, 14, par1World, pos.getX(), pos.getY(), pos.getZ());
				}
        	}else{
        		par5EntityPlayer.openGui(mod_SecurityCraft.instance, 16, par1World, pos.getX(), pos.getY(), pos.getZ());
        	}

            return true;
        }
    }
	
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand){
		if(((Boolean) state.getValue(OPEN)).booleanValue()){
			Utils.setBlockProperty(worldIn, pos, BlockKeypadFurnace.OPEN, false);
		}
	}
	
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
		System.out.println(placer.getHorizontalFacing().getOpposite());
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite()).withProperty(OPEN, false).withProperty(COOKING, false);
    }
    
    @SideOnly(Side.CLIENT)
    public IBlockState getStateForEntityRender(IBlockState state)
    {
        return this.getDefaultState().withProperty(FACING, EnumFacing.SOUTH);
    }

    public IBlockState getStateFromMeta(int stateMeta)
    {
    	System.out.println("Using meta: " + stateMeta);
    	if(stateMeta == 0){
    		return this.getDefaultState().withProperty(FACING, EnumFacing.NORTH).withProperty(OPEN, false).withProperty(COOKING, false);
    	}else if(stateMeta == 1){
    		return this.getDefaultState().withProperty(FACING, EnumFacing.NORTH).withProperty(OPEN, true).withProperty(COOKING, true);
    	}else if(stateMeta == 2){
    		return this.getDefaultState().withProperty(FACING, EnumFacing.NORTH).withProperty(OPEN, false).withProperty(COOKING, true);
    	}else if(stateMeta == 3){
    		return this.getDefaultState().withProperty(FACING, EnumFacing.NORTH).withProperty(OPEN, true).withProperty(COOKING, false);
    	}else if(stateMeta == 4){
    		return this.getDefaultState().withProperty(FACING, EnumFacing.SOUTH).withProperty(OPEN, false).withProperty(COOKING, false);
    	}else if(stateMeta == 5){
    		return this.getDefaultState().withProperty(FACING, EnumFacing.SOUTH).withProperty(OPEN, true).withProperty(COOKING, true);
    	}else if(stateMeta == 6){
    		return this.getDefaultState().withProperty(FACING, EnumFacing.SOUTH).withProperty(OPEN, false).withProperty(COOKING, true);
    	}else if(stateMeta == 7){
    		return this.getDefaultState().withProperty(FACING, EnumFacing.SOUTH).withProperty(OPEN, true).withProperty(COOKING, false);
    	}else if(stateMeta == 8){
    		return this.getDefaultState().withProperty(FACING, EnumFacing.WEST).withProperty(OPEN, false).withProperty(COOKING, false);
    	}else if(stateMeta == 9){
    		return this.getDefaultState().withProperty(FACING, EnumFacing.WEST).withProperty(OPEN, true).withProperty(COOKING, true);
    	}else if(stateMeta == 10){
    		return this.getDefaultState().withProperty(FACING, EnumFacing.WEST).withProperty(OPEN, false).withProperty(COOKING, true);
    	}else if(stateMeta == 11){
    		return this.getDefaultState().withProperty(FACING, EnumFacing.WEST).withProperty(OPEN, true).withProperty(COOKING, false);
    	}else if(stateMeta == 12){
    		return this.getDefaultState().withProperty(FACING, EnumFacing.EAST).withProperty(OPEN, false).withProperty(COOKING, false);
    	}else if(stateMeta == 13){
    		return this.getDefaultState().withProperty(FACING, EnumFacing.EAST).withProperty(OPEN, true).withProperty(COOKING, true);
    	}else if(stateMeta == 14){
    		return this.getDefaultState().withProperty(FACING, EnumFacing.EAST).withProperty(OPEN, false).withProperty(COOKING, true);
    	}else if(stateMeta == 15){
    		return this.getDefaultState().withProperty(FACING, EnumFacing.EAST).withProperty(OPEN, true).withProperty(COOKING, false);
    	}
    	
    	return this.getDefaultState();
    }

    public int getMetaFromState(IBlockState state)
    {
    	EnumFacing side = ((EnumFacing) state.getValue(FACING));
    	boolean open = ((Boolean) state.getValue(OPEN)).booleanValue();
    	boolean cooking = ((Boolean) state.getValue(COOKING)).booleanValue();
    	
    	if(side == EnumFacing.NORTH && !open && !cooking){
    		return 0;
    	}else if(side == EnumFacing.NORTH && open && cooking){
    		return 1;
    	}else if(side == EnumFacing.NORTH && !open && cooking){
    		return 2;
    	}else if(side == EnumFacing.NORTH && open && !cooking){
    		return 3;
    	}else if(side == EnumFacing.SOUTH && !open && !cooking){
    		return 4;
    	}else if(side == EnumFacing.SOUTH && open && cooking){
    		return 5;
    	}else if(side == EnumFacing.SOUTH && !open && cooking){
    		return 6;
    	}else if(side == EnumFacing.SOUTH && open && !cooking){
    		return 7;
    	}else if(side == EnumFacing.WEST && !open && !cooking){
    		return 8;
    	}else if(side == EnumFacing.WEST && open && cooking){
    		return 9;
    	}else if(side == EnumFacing.WEST && !open && cooking){
    		return 10;
    	}else if(side == EnumFacing.WEST && open && !cooking){
    		return 11;
    	}else if(side == EnumFacing.EAST && !open && !cooking){
    		return 12;
    	}else if(side == EnumFacing.EAST && open && cooking){
    		return 13;
    	}else if(side == EnumFacing.EAST && !open && cooking){
    		return 14;
    	}else if(side == EnumFacing.EAST && open && !cooking){
    		return 15;
    	}else{
    		return 16;
    	}
    }

    protected BlockState createBlockState()
    {
        return new BlockState(this, new IProperty[] {FACING, OPEN, COOKING});
    }
	
	public TileEntity createNewTileEntity(World var1, int var2) {
		return new TileEntityKeypadFurnace();
	}

}

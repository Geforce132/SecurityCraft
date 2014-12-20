package org.freeforums.geforce.securitycraft.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityAlarm;

public class BlockAlarm extends BlockContainer {
	
	private final boolean isLit;
    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	
	public BlockAlarm(Material par1Material, boolean isLit) {
		super(par1Material);
		float f = 0.2F;
		this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 0.5F, 0.5F + f);
	
		this.isLit = isLit;
		
		if(isLit){
			this.setLightLevel(1.0F);
		}
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
    
    public int getRenderType(){
    	return 3;
    }
	
	/**
     * Called whenever the block is added into the world. Args: world, x, y, z
     */
    public void onBlockAdded(World par1World, BlockPos pos, IBlockState state) {
    	if(par1World.isRemote){
    		return;
    	}else{
    		par1World.scheduleUpdate(pos, state.getBlock(), 1);
    	}
    }
    
    /**
     * Called when the block is placed in the world.
     */
    public void onBlockPlacedBy(World par1World, BlockPos pos, IBlockState state, EntityLivingBase par5EntityLivingBase, ItemStack p_149689_6_)
    {
    	((TileEntityAlarm) par1World.getTileEntity(pos)).setOwner(par5EntityLivingBase.getName());
    }
	
	/**
     * Ticks the block if it's been scheduled
     */
    public void updateTick(World par1World, BlockPos pos, IBlockState state, Random par5Random)
    {
        if(par1World.isRemote){
        	return;
        }else{
    		this.playSoundAndUpdate(par1World, pos);
    		
    		par1World.scheduleUpdate(pos, state.getBlock(), 5);
        }
    }
	
	/**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor Block
     */
    public void onNeighborBlockChange(World par1World, BlockPos pos, IBlockState state, Block par5Block)
    {
    	if(par1World.isRemote){
    		return;
    	}else{
    		this.playSoundAndUpdate(par1World, pos);
    	}
    }
    
    private void playSoundAndUpdate(World par1World, BlockPos pos){
    	if(par1World.isBlockIndirectlyGettingPowered(pos) > 0){
    		boolean isPowered = ((TileEntityAlarm) par1World.getTileEntity(pos)).isPowered();

    		if(!isPowered){
    			((TileEntityAlarm) par1World.getTileEntity(pos)).setPowered(true);
			}
    		
		}else{
    		boolean isPowered = ((TileEntityAlarm) par1World.getTileEntity(pos)).isPowered();

			if(isPowered){
    			((TileEntityAlarm) par1World.getTileEntity(pos)).setPowered(false);
			}
		}
    }
    
    /**
     * Gets an item for the block being called on. Args: world, x, y, z
     */
    @SideOnly(Side.CLIENT)
    public Item getItem(World p_149694_1_, BlockPos pos)
    {
        return Item.getItemFromBlock(mod_SecurityCraft.alarm);
    }
    
    public Item getItemDropped(IBlockState state, Random p_149650_2_, int p_149650_3_)
    {
        return Item.getItemFromBlock(mod_SecurityCraft.alarm);
    }
    
    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }
    
    @SideOnly(Side.CLIENT)
    public IBlockState getStateForEntityRender(IBlockState state)
    {
        return this.getDefaultState().withProperty(FACING, EnumFacing.SOUTH);
    }

    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(FACING, EnumFacing.values()[meta].getAxis() == EnumFacing.Axis.Y ? EnumFacing.NORTH : EnumFacing.values()[meta]);    
    }

    public int getMetaFromState(IBlockState state)
    {   	
    	return ((EnumFacing) state.getValue(FACING)).getIndex(); 	
    }
    
    protected BlockState createBlockState()
    {
        return new BlockState(this, new IProperty[] {FACING});
    }
    
	public TileEntity createNewTileEntity(World var1, int var2) {
		return new TileEntityAlarm();
	}

}

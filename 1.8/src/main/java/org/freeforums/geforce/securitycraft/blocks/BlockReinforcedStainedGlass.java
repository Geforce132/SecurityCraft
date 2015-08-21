package org.freeforums.geforce.securitycraft.blocks;

import java.util.List;
import java.util.Random;

import org.freeforums.geforce.securitycraft.tileentity.TileEntityOwnable;

import net.minecraft.block.BlockBeacon;
import net.minecraft.block.BlockGlass;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockReinforcedStainedGlass extends BlockGlass implements ITileEntityProvider {
    
	public static final PropertyInteger COLOR = PropertyInteger.create("color", 0, 15);

	public BlockReinforcedStainedGlass(Material par1Material) {
		super(par1Material, false);
        this.setDefaultState(this.blockState.getBaseState().withProperty(COLOR, 0));
	}
	
    public boolean isFullCube(){
        return false;
    }
    
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state){
        if(!worldIn.isRemote){
            BlockBeacon.updateColorAsync(worldIn, pos);
        }
    }
	
	public void onBlockPlacedBy(World par1World, BlockPos pos, IBlockState state, EntityLivingBase par5EntityLivingBase, ItemStack par6ItemStack) {
    	if(!par1World.isRemote){
    		if(par5EntityLivingBase instanceof EntityPlayer){
    			((TileEntityOwnable) par1World.getTileEntity(pos)).setOwner(((EntityPlayer) par5EntityLivingBase).getGameProfile().getId().toString(), par5EntityLivingBase.getName());
    		}
    	}
    }
	
	public void breakBlock(World par1World, BlockPos pos, IBlockState state){
        super.breakBlock(par1World, pos, state);
        par1World.removeTileEntity(pos);
        BlockBeacon.updateColorAsync(par1World, pos);
    }

    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item par1Item, CreativeTabs par2CreativeTabs, List par3List){
        for(int i = 0; i < 15; i++){
        	par3List.add(new ItemStack(par1Item, 1, i));
        }
    }
    
    @SideOnly(Side.CLIENT)
    public EnumWorldBlockLayer getBlockLayer(){
        return EnumWorldBlockLayer.CUTOUT;
    }
    
    public int damageDropped(IBlockState state){
        return ((Integer) state.getValue(COLOR)).intValue();
    }

    public int quantityDropped(Random par1Random){
        return 0;
    }
    
    protected boolean canSilkHarvest(){
        return true;
    }
    
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(COLOR, meta);
    }
    
    public int getMetaFromState(IBlockState state)
    {
        return ((Integer) state.getValue(COLOR)).intValue();
    }
    
    protected BlockState createBlockState(){
        return new BlockState(this, new IProperty[] {COLOR});
    }
	
	public TileEntity createNewTileEntity(World var1, int var2) {
		return new TileEntityOwnable();
	}

}

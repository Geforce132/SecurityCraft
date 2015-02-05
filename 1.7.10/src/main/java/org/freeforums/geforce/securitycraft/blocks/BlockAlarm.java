package org.freeforums.geforce.securitycraft.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityAlarm;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockAlarm extends BlockContainer {
	
	private final boolean isLit;
	
	@SideOnly(Side.CLIENT)
	private IIcon topDeactivatedIcon;
	
	@SideOnly(Side.CLIENT)
	private IIcon sidesDeactivatedIcon;
	
	@SideOnly(Side.CLIENT)
	private IIcon topActivatedIcon;
	
	@SideOnly(Side.CLIENT)
	private IIcon sidesActivatedIcon;

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
    public boolean renderAsNormalBlock()
    {
        return false;
    }
	
	/**
     * Called whenever the block is added into the world. Args: world, x, y, z
     */
    public void onBlockAdded(World par1World, int par2, int par3, int par4) {
    	if(par1World.isRemote){
    		return;
    	}else{
    		par1World.scheduleBlockUpdate(par2, par3, par4, this, 1);
    	}
    }
    
    /**
     * Called when the block is placed in the world.
     */
    public void onBlockPlacedBy(World par1World, int par2, int par3, int par4, EntityLivingBase par5EntityLivingBase, ItemStack p_149689_6_)
    {
    	((TileEntityAlarm) par1World.getTileEntity(par2, par3, par4)).setOwner(((EntityPlayer) par5EntityLivingBase).getGameProfile().getId().toString(), par5EntityLivingBase.getCommandSenderName());
    	
        int l = MathHelper.floor_double((double)(par5EntityLivingBase.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

        if (l == 0)
        {
        	par1World.setBlockMetadataWithNotify(par2, par3, par4, 2, 2);
        }

        if (l == 1)
        {
        	par1World.setBlockMetadataWithNotify(par2, par3, par4, 5, 2);
        }

        if (l == 2)
        {
        	par1World.setBlockMetadataWithNotify(par2, par3, par4, 3, 2);
        }

        if (l == 3)
        {
        	par1World.setBlockMetadataWithNotify(par2, par3, par4, 4, 2);
        }
        
        this.checkMetadata(par1World, par2, par3, par4);
    }
	
	private void checkMetadata(World par1World, int par2, int par3, int par4) {
		mod_SecurityCraft.log(par1World.getBlockMetadata(par2, par3, par4) + "");
	}
    

	/**
     * Ticks the block if it's been scheduled
     */
    public void updateTick(World par1World, int par2, int par3, int par4, Random par5Random)
    {
        if(par1World.isRemote){
        	return;
        }else{
    		this.playSoundAndUpdate(par1World, par2, par3, par4);
    		
    		par1World.scheduleBlockUpdate(par2, par3, par4, this, 5);
        }
    }
    
//    public void breakBlock(World par1World, int par2, int par3, int par4, Block par5Block, int par6)
//    {
//        if(par1World.isRemote){
//        	return;
//        }else{
//        	mod_SecurityCraft.network.sendToAll(new PacketCPlaySoundAtPos(par2, par3, par4, ""));
//        }
//    }
	
	/**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor Block
     */
    public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, Block par5Block)
    {
    	if(par1World.isRemote){
    		return;
    	}else{
    		this.playSoundAndUpdate(par1World, par2, par3, par4);
    	}
    }
    
    private void playSoundAndUpdate(World par1World, int par2, int par3, int par4){
    	if(par1World.isBlockIndirectlyGettingPowered(par2, par3, par4)){
    		boolean isPowered = ((TileEntityAlarm) par1World.getTileEntity(par2, par3, par4)).isPowered();

    		if(!isPowered){
    			((TileEntityAlarm) par1World.getTileEntity(par2, par3, par4)).setPowered(true);
			}
    		
		}else{
    		boolean isPowered = ((TileEntityAlarm) par1World.getTileEntity(par2, par3, par4)).isPowered();

			if(isPowered){
    			((TileEntityAlarm) par1World.getTileEntity(par2, par3, par4)).setPowered(false);
			}
		}
    }
    
    /**
     * Gets an item for the block being called on. Args: world, x, y, z
     */
    @SideOnly(Side.CLIENT)
    public Item getItem(World p_149694_1_, int p_149694_2_, int p_149694_3_, int p_149694_4_)
    {
        return Item.getItemFromBlock(mod_SecurityCraft.alarm);
    }
    
    public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_)
    {
        return Item.getItemFromBlock(mod_SecurityCraft.alarm);
    }
    
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister par1IconRegister){
    	this.sidesDeactivatedIcon = par1IconRegister.registerIcon("securitycraft:alarmSidesDeactivated");
    	this.topDeactivatedIcon = par1IconRegister.registerIcon("securitycraft:alarmTopDeactivated");
    	this.sidesActivatedIcon = par1IconRegister.registerIcon("securitycraft:alarmSidesActivated");
    	this.topActivatedIcon = par1IconRegister.registerIcon("securitycraft:alarmTopActivated");
    }
    
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int par1, int par2){
    	if(this.isLit){
    		return (par1 == 0 || par1 == 1) ? this.topActivatedIcon : this.sidesActivatedIcon;
    	}else{
    		return (par1 == 0 || par1 == 1) ? this.topDeactivatedIcon : this.sidesDeactivatedIcon;
    	}
    }

	public TileEntity createNewTileEntity(World var1, int var2) {
		return new TileEntityAlarm();
	}

}

package org.freeforums.geforce.securitycraft.blocks;


import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import org.freeforums.geforce.securitycraft.main.HelpfulMethods;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;
import org.freeforums.geforce.securitycraft.misc.EnumCustomModules;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityKeypad;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityOwnable;
import org.freeforums.geforce.securitycraft.timers.ScheduleUpdate;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockKeypad extends BlockContainer{

	public BlockKeypad(Material par2Material) {
		super(par2Material);

	}
    	
	@SideOnly(Side.CLIENT)
    private IIcon keypadIconTop;
    @SideOnly(Side.CLIENT)
    private IIcon keypadIconFront;
    @SideOnly(Side.CLIENT)
    private IIcon keypadIconFrontActive;
    
	public static boolean canSend = true;

    
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
    
    @SuppressWarnings("static-access")
	public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9){
    	if(par1World.isRemote){
    		return true;
    	}else{
    		if(par1World.getBlockMetadata(par2, par3, par4) > 6 && par1World.getBlockMetadata(par2, par3, par4) < 11){
    			return false;
    		}
    		
    		if(par5EntityPlayer.getCurrentEquippedItem() == null || par5EntityPlayer.getCurrentEquippedItem().getItem() != mod_SecurityCraft.Codebreaker){
    			TileEntityKeypad TEK = (TileEntityKeypad) par1World.getTileEntity(par2, par3, par4);
    			
    			if(HelpfulMethods.checkForModule(par1World, par2, par3, par4, par5EntityPlayer, EnumCustomModules.WHITELIST) || HelpfulMethods.checkForModule(par1World, par2, par3, par4, par5EntityPlayer, EnumCustomModules.BLACKLIST)){
    				return true;
    			}
    			    		
    			if(TEK.getKeypadCode() != null && !TEK.getKeypadCode().isEmpty()){
    				par5EntityPlayer.openGui(mod_SecurityCraft.instance, 0, par1World, par2, par3, par4);
    			}else{
    				par5EntityPlayer.openGui(mod_SecurityCraft.instance, 1, par1World, par2, par3, par4);
    			}
    			
    			return true;       		
        	}else if(par5EntityPlayer.getCurrentEquippedItem().getItem() == mod_SecurityCraft.Codebreaker){
        		if(mod_SecurityCraft.instance.configHandler.allowCodebreakerItem){
    				if(!((TileEntityKeypad)par1World.getTileEntity(par2, par3, par4)).getKeypadCode().isEmpty() && (par1World.getBlock(par2, par3, par4) == mod_SecurityCraft.Keypad && (par1World.getBlockMetadata(par2, par3, par4) <= 6 || par1World.getBlockMetadata(par2, par3, par4) >= 11))){
    					new ScheduleUpdate(par1World, 3, par2, par3, par4);
    				}
    			}else{	
    				HelpfulMethods.sendMessageToPlayer(par5EntityPlayer, "The codebreaker has been disabled through the config file.", null);  				
    			}	
        	}     	    	     	
    	}

    	return false;
    }
    
    /**
     * Called whenever the block is added into the world. Args: world, x, y, z
     */
    public void onBlockAdded(World par1World, int par2, int par3, int par4)
    {
        super.onBlockAdded(par1World, par2, par3, par4);
    }
    
 
    
    /**
     * Can this block provide power. Only wire currently seems to have this change based on its state.
     */
    public boolean canProvidePower()
    {
        return true;
    }
    
    /**
     * Returns true if the block is emitting indirect/weak redstone power on the specified side. If isBlockNormalCube
     * returns true, standard redstone propagation rules will apply instead and this will not be called. Args: World, X,
     * Y, Z, side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
     */
    public int isProvidingWeakPower(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {
    	if(par1IBlockAccess.getBlockMetadata(par2, par3, par4) == 7 || par1IBlockAccess.getBlockMetadata(par2, par3, par4) == 8 || par1IBlockAccess.getBlockMetadata(par2, par3, par4) == 9 || par1IBlockAccess.getBlockMetadata(par2, par3, par4) == 10){
    		return 15;
    	}else{
    		return 0;
    	}
    }
    
    /**
     * Returns true if the block is emitting direct/strong redstone power on the specified side. Args: World, X, Y, Z,
     * side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
     */
    public int isProvidingStrongPower(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {
    	
    	if(par1IBlockAccess.getBlockMetadata(par2, par3, par4) == 7 || par1IBlockAccess.getBlockMetadata(par2, par3, par4) == 8 || par1IBlockAccess.getBlockMetadata(par2, par3, par4) == 9 || par1IBlockAccess.getBlockMetadata(par2, par3, par4) == 10){
    		return 15;
    	}else{
    		return 0;
    	}
    }

    
    @SideOnly(Side.CLIENT)

    /**
     * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
     */
    public IIcon getIcon(int par1, int par2)
    {
    	if(par2 == 7 || par2 == 8 || par2 == 9 || par2 == 10){
    		return par1 == 1 ? this.keypadIconTop : (par1 == 0 ? this.keypadIconTop : (par1 != (par2 - 5) ? this.blockIcon : this.keypadIconFrontActive));
    	}else{
    		return par1 == 1 ? this.keypadIconTop : (par1 == 0 ? this.keypadIconTop : (par1 != par2 ? this.blockIcon : this.keypadIconFront));
    	}
    }

    @SideOnly(Side.CLIENT)
    /**
     * When this method is called, your block should register all the icons it needs with the given IconRegister. This
     * is the only chance you get to register icons.
     */
    public void registerBlockIcons(IIconRegister par1IconRegister)
    {
        this.blockIcon = par1IconRegister.registerIcon("securitycraft:iron_block");
        this.keypadIconFront = par1IconRegister.registerIcon("securitycraft:keypadUnactive");
        this.keypadIconTop = par1IconRegister.registerIcon("securitycraft:iron_block");
        this.keypadIconFrontActive = par1IconRegister.registerIcon("securitycraft:keypadActive");
    }
    
    /**
     * Called when the block is placed in the world.
     */
    public void onBlockPlacedBy(World par1World, int par2, int par3, int par4, EntityLivingBase par5EntityLivingBase, ItemStack par6ItemStack)
    {
        int l = MathHelper.floor_double((double)(par5EntityLivingBase.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

        ((TileEntityOwnable) par1World.getTileEntity(par2, par3, par4)).setOwner(par5EntityLivingBase.getCommandSenderName());
        
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
            
            

    	}else{
    		return;
    	}

       
    }

    /**
     * Returns a new instance of a block's tile entity class. Called on placing the block.
     */
    public TileEntity createNewTileEntity(World par1World, int par2)
    {
        return new TileEntityKeypad();
    }

}

package org.freeforums.geforce.securitycraft.blocks;

import java.util.Random;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import org.freeforums.geforce.securitycraft.items.ItemKeycardBase;
import org.freeforums.geforce.securitycraft.main.HelpfulMethods;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;
import org.freeforums.geforce.securitycraft.misc.EnumCustomModules;
import org.freeforums.geforce.securitycraft.tileentity.CustomizableSCTE;
import org.freeforums.geforce.securitycraft.tileentity.ICustomizable;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityKeycardReader;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityKeypad;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityOwnable;
import org.freeforums.geforce.securitycraft.timers.ScheduleKeycardUpdate;
import org.freeforums.geforce.securitycraft.timers.ScheduleUpdate;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockKeycardReader extends BlockContainer implements ICustomizable{

	@SideOnly(Side.CLIENT)
    private IIcon keypadIconTop;
    @SideOnly(Side.CLIENT)
    private IIcon keypadIconFront;
    @SideOnly(Side.CLIENT)
    private IIcon keypadIconFrontActive;
    
	public BlockKeycardReader(Material par2Material) {
		super(par2Material);
	}
	
    /**
     * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
     */
	@SideOnly(Side.CLIENT)
    public IIcon getIcon(int par1, int par2)
    {
    	if(par2 == 7 || par2 == 8 || par2 == 9 || par2 == 10){
    		return par1 == 1 ? this.keypadIconTop : (par1 == 0 ? this.keypadIconTop : (par1 != (par2 - 5) ? this.blockIcon : this.keypadIconFrontActive));
    	}else{
    		return par1 == 1 ? this.keypadIconTop : (par1 == 0 ? this.keypadIconTop : (par1 != par2 ? this.blockIcon : this.keypadIconFront));
    	}
    }

    /**
     * When this method is called, your block should register all the icons it needs with the given IconRegister. This
     * is the only chance you get to register icons.
     */
	@SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister par1IconRegister)
    {
    	this.blockIcon = par1IconRegister.registerIcon("furnace_side");
        this.keypadIconTop = par1IconRegister.registerIcon("furnace_top");
        this.keypadIconFront = par1IconRegister.registerIcon("securitycraft:keycardReaderFront");
        this.keypadIconFrontActive = par1IconRegister.registerIcon("stone");
    }
	    	     
    /**
     * Called when the block is placed in the world.
     */
    public void onBlockPlacedBy(World par1World, int par2, int par3, int par4, EntityLivingBase par5EntityLivingBase, ItemStack par6ItemStack){
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
	    	
	public void insertCard(World par1World, int par2, int par3, int par4, ItemStack par5ItemStack, EntityPlayer par6EntityPlayer) {
		int meta = par1World.getBlockMetadata(par2, par3, par4);
		
		if(HelpfulMethods.checkForModule(par1World, par2, par3, par4, par6EntityPlayer, EnumCustomModules.WHITELIST) || HelpfulMethods.checkForModule(par1World, par2, par3, par4, par6EntityPlayer, EnumCustomModules.BLACKLIST)){ return; }
		
		if(((TileEntityKeycardReader)par1World.getTileEntity(par2, par3, par4)).getPassLV() != 0 && ((TileEntityKeycardReader)par1World.getTileEntity(par2, par3, par4)).getPassLV() <= ((ItemKeycardBase) par5ItemStack.getItem()).getKeycardLV(par5ItemStack)){
			((TileEntityKeycardReader)par1World.getTileEntity(par2, par3, par4)).setIsProvidingPower(true);
			new ScheduleKeycardUpdate(3, par1World, par2, par3, par4, meta);
			par1World.notifyBlocksOfNeighborChange(par2, par3, par4, this);
		}else{
			if(((TileEntityKeycardReader)par1World.getTileEntity(par2, par3, par4)).getPassLV() != 0){
				HelpfulMethods.sendMessageToPlayer(par6EntityPlayer, "Required security level: " + ((TileEntityKeycardReader)par1World.getTileEntity(par2, par3, par4)).getPassLV() + " Your keycard's level: " + ((ItemKeycardBase) par5ItemStack.getItem()).getKeycardLV(par5ItemStack), null);
			}else{
				HelpfulMethods.sendMessageToPlayer(par6EntityPlayer, "Keycard reader's security level not set!", EnumChatFormatting.RED);
			}
		}
		
	}
	
    public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9){
    	if(par1World.isRemote){
    		return true;
    	}
    	
    	if(par5EntityPlayer.getCurrentEquippedItem() == null || par5EntityPlayer.getCurrentEquippedItem().getItem() != (new ItemStack(mod_SecurityCraft.keycards, 1, 0)).getItem() || par5EntityPlayer.getCurrentEquippedItem().getItem() != (new ItemStack(mod_SecurityCraft.keycards, 1, 1).getItem()) || par5EntityPlayer.getCurrentEquippedItem().getItem() != (new ItemStack(mod_SecurityCraft.keycards, 1, 2).getItem())){
    		if(((TileEntityKeycardReader) par1World.getTileEntity(par2, par3, par4)).getPassLV() == 0){    	
		    	par5EntityPlayer.openGui(mod_SecurityCraft.instance, 4, par1World, par2, par3, par4);
		    	return true;
    		}
    	
    	}
    	
		return false;
    }
    
    /**
     * A randomly called display update to be able to add particles or other items for display
     */
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World par1World, int par2, int par3, int par4, Random par5Random){
        if(((TileEntityKeycardReader)par1World.getTileEntity(par2, par3, par4)).getIsProvidingPower()){
            double d0 = (double)((float)par2 + 0.5F) + (double)(par5Random.nextFloat() - 0.5F) * 0.2D;
            double d1 = (double)((float)par3 + 0.7F) + (double)(par5Random.nextFloat() - 0.5F) * 0.2D;
            double d2 = (double)((float)par4 + 0.5F) + (double)(par5Random.nextFloat() - 0.5F) * 0.2D;
            double d3 = 0.2199999988079071D;
            double d4 = 0.27000001072883606D;

            
            par1World.spawnParticle("reddust", d0 - d4, d1 + d3, d2, 0.0D, 0.0D, 0.0D);
            par1World.spawnParticle("reddust", d0 + d4, d1 + d3, d2, 0.0D, 0.0D, 0.0D); 
            par1World.spawnParticle("reddust", d0, d1 + d3, d2 - d4, 0.0D, 0.0D, 0.0D);
            par1World.spawnParticle("reddust", d0, d1 + d3, d2 + d4, 0.0D, 0.0D, 0.0D);
            par1World.spawnParticle("reddust", d0, d1, d2, 0.0D, 0.0D, 0.0D);
        } 
    }
    
    /**
     * Returns true if the block is emitting indirect/weak redstone power on the specified side. If isBlockNormalCube
     * returns true, standard redstone propagation rules will apply instead and this will not be called. Args: World, X,
     * Y, Z, side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
     */
    public int isProvidingWeakPower(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {
    	if(((TileEntityKeycardReader)par1IBlockAccess.getTileEntity(par2, par3, par4)).getIsProvidingPower()){
    		return 15;
    	}else{
    		return 0;
    	}
    }
    
    /**
     * Can this block provide power. Only wire currently seems to have this change based on its state.
     */
    public boolean canProvidePower()
    {
    	return true;
    }
    
    public TileEntity createNewTileEntity(World world, int par2) {
		return new TileEntityKeycardReader();
	}

	public EnumCustomModules[] getCustomizableOptions() {
		return new EnumCustomModules[]{EnumCustomModules.WHITELIST, EnumCustomModules.BLACKLIST};
	}


}

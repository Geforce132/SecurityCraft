package net.geforcemods.securitycraft.blocks;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.api.IViewActivated;
import net.geforcemods.securitycraft.main.Utils.BlockUtils;
import net.geforcemods.securitycraft.main.Utils.PlayerUtils;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.geforcemods.securitycraft.tileentity.TileEntityRetinalScanner;
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
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockRetinalScanner extends BlockContainer implements IViewActivated {
	
	@SideOnly(Side.CLIENT)
	private IIcon rtIconTop;
	
	@SideOnly(Side.CLIENT)
	private IIcon rtIconFront;
	
	@SideOnly(Side.CLIENT)
	private IIcon rtIconFrontActive;

	public BlockRetinalScanner(Material par1) {
		super(par1);
	}

	/**
     * Called when the block is placed in the world.
     */
    public void onBlockPlacedBy(World par1World, int par2, int par3, int par4, EntityLivingBase par5EntityLivingBase, ItemStack par6ItemStack){
        int l = MathHelper.floor_double((double)(par5EntityLivingBase.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        ((TileEntityOwnable)par1World.getTileEntity(par2, par3, par4)).setOwner(((EntityPlayer) par5EntityLivingBase).getGameProfile().getId().toString(), par5EntityLivingBase.getCommandSenderName());

        if (l == 0){
            par1World.setBlockMetadataWithNotify(par2, par3, par4, 2, 2);          
        }

        if (l == 1){
            par1World.setBlockMetadataWithNotify(par2, par3, par4, 5, 2);            
        }

        if (l == 2){
            par1World.setBlockMetadataWithNotify(par2, par3, par4, 3, 2);    
        }

        if (l == 3){
        	par1World.setBlockMetadataWithNotify(par2, par3, par4, 4, 2);   
    	}  
    } 
    
    public void onEntityLookedAtBlock(World world, int x, int y, int z, EntityLivingBase entity) {
    	if(!world.isRemote && !BlockUtils.isMetadataBetween(world, x, y, z, 7, 10)){
    		world.setBlockMetadataWithNotify(x, y, z, world.getBlockMetadata(x, y, z) + 5, 3);
    		world.scheduleBlockUpdate(x, y, z, mod_SecurityCraft.retinalScanner, 60);
    		
            if(entity instanceof EntityPlayer){
                PlayerUtils.sendMessageToPlayer((EntityPlayer) entity, StatCollector.translateToLocal("tile.retinalScanner.name"), StatCollector.translateToLocal("messages.retinalScanner.hello").replace("#", entity.getCommandSenderName()), EnumChatFormatting.GREEN);
            }
    	}
	}
    
    public void updateTick(World par1World, int par2, int par3, int par4, Random par5Random){
        if (!par1World.isRemote && par1World.getBlockMetadata(par2, par3, par4) >= 7 && par1World.getBlockMetadata(par2, par3, par4) <= 10){
        	par1World.setBlockMetadataWithNotify(par2, par3, par4, par1World.getBlockMetadata(par2, par3, par4) - 5, 3);
        }                       
    }
    
    public boolean canProvidePower(){
        return true;
    }
    
    public int isProvidingWeakPower(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5){
    	if(par1IBlockAccess.getBlockMetadata(par2, par3, par4) == 7 || par1IBlockAccess.getBlockMetadata(par2, par3, par4) == 8 || par1IBlockAccess.getBlockMetadata(par2, par3, par4) == 9 || par1IBlockAccess.getBlockMetadata(par2, par3, par4) == 10){
    		return 15;
    	}else{
    		return 0;
    	}
    }
    
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int par1, int par2){
        if(par1 == 3 && par2 == 0){
    		return this.rtIconFront;
    	}
        
    	if(par2 == 7 || par2 == 8 || par2 == 9 || par2 == 10){
    		return par1 == 1 ? this.rtIconTop : (par1 == 0 ? this.rtIconTop : (par1 != (par2 - 5) ? this.blockIcon : this.rtIconFrontActive));
    	}else{
    		return par1 == 1 ? this.rtIconTop : (par1 == 0 ? this.rtIconTop : (par1 != par2 ? this.blockIcon : this.rtIconFront));
    	}
    }
    
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister par1IconRegister){
    	this.blockIcon = par1IconRegister.registerIcon("furnace_side");
        this.rtIconTop = par1IconRegister.registerIcon("furnace_top");
        this.rtIconFront = par1IconRegister.registerIcon("securitycraft:retinalScannerFront");
        this.rtIconFrontActive = par1IconRegister.registerIcon("securitycraft:retinalScannerFront");
    }
    
    public TileEntity createNewTileEntity(World var1, int var2) {
		return new TileEntityRetinalScanner().activatedByView();
	}
    
}

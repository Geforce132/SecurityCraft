package org.freeforums.geforce.securitycraft.blocks;

import static net.minecraftforge.common.util.ForgeDirection.EAST;
import static net.minecraftforge.common.util.ForgeDirection.NORTH;
import static net.minecraftforge.common.util.ForgeDirection.SOUTH;
import static net.minecraftforge.common.util.ForgeDirection.WEST;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import org.freeforums.geforce.securitycraft.api.CustomizableSCTE;
import org.freeforums.geforce.securitycraft.main.Utils.BlockUtils;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;
import org.freeforums.geforce.securitycraft.misc.EnumCustomModules;
import org.freeforums.geforce.securitycraft.network.packets.PacketCRemoveLGView;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityOwnable;
import org.freeforums.geforce.securitycraft.tileentity.TileEntitySecurityCamera;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockSecurityCamera extends BlockContainer {
	
	private final boolean isLit;

	public BlockSecurityCamera(Material par2Material, boolean isLit) {
		super(par2Material);
		this.isLit = isLit;
	}
	
	public boolean renderAsNormalBlock(){
		return false;
	}

	public boolean isNormalCube(){
		return false;
	}

	public boolean isOpaqueCube(){
		return false;
	}

	public int getRenderType(){
		return -1;
	}
	
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4){
        return null;
    }
	
	public void setBlockBoundsBasedOnState(IBlockAccess par1IBlockAccess, int x, int y, int z){
        int meta = par1IBlockAccess.getBlockMetadata(x, y, z);
        
    	if(meta == 3  || meta == 7){
    		this.setBlockBounds(0.275F, 0.250F, 0.000F, 0.700F, 0.800F, 0.850F);
    	}else if(meta == 1 || meta == 5){
    		this.setBlockBounds(0.275F, 0.250F, 0.150F, 0.700F, 0.800F, 1.000F);
        }else if(meta == 2 || meta == 6){
    		this.setBlockBounds(0.125F, 0.250F, 0.275F, 1.000F, 0.800F, 0.725F);
        }else{
    		this.setBlockBounds(0.000F, 0.250F, 0.275F, 0.850F, 0.800F, 0.725F);
        }
        
    } 
	
    public void onBlockPlacedBy(World par1World, int par2, int par3, int par4, EntityLivingBase par5EntityLivingBase, ItemStack par6ItemStack){
    	int l = MathHelper.floor_double((double)(par5EntityLivingBase.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

        ((TileEntityOwnable) par1World.getTileEntity(par2, par3, par4)).setOwner(((EntityPlayer) par5EntityLivingBase).getGameProfile().getId().toString(), par5EntityLivingBase.getCommandSenderName());

        if(l == 0){
            par1World.setBlockMetadataWithNotify(par2, par3, par4, 1, 2);    
        }

        if(l == 1){
            par1World.setBlockMetadataWithNotify(par2, par3, par4, 4, 2);         
        }

        if(l == 2){
            par1World.setBlockMetadataWithNotify(par2, par3, par4, 3, 2);           
        }

        if(l == 3){
            par1World.setBlockMetadataWithNotify(par2, par3, par4, 2, 2);                   
    	}
    }
    
    public void breakBlock(World par1World, int par2, int par3, int par4, Block par5Block, int par6){
    	mod_SecurityCraft.network.sendToAll(new PacketCRemoveLGView(par2, par3, par4));
    }
    
    public boolean canPlaceBlockAt(World par1World, int par2, int par3, int par4){
        return par1World.isSideSolid(par2 - 1, par3, par4, EAST ) ||
        		par1World.isSideSolid(par2 + 1, par3, par4, WEST ) ||
        		par1World.isSideSolid(par2, par3, par4 - 1, SOUTH) ||
        		par1World.isSideSolid(par2, par3, par4 + 1, NORTH);
    }
	    
    public boolean canProvidePower(){
        return true;
    }
    
    public int isProvidingWeakPower(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5){
    	if(par1IBlockAccess.getTileEntity(par2, par3, par4) != null && ((CustomizableSCTE) par1IBlockAccess.getTileEntity(par2, par3, par4)).hasModule(EnumCustomModules.REDSTONE) && (par1IBlockAccess.getBlockMetadata(par2, par3, par4) == 5 || par1IBlockAccess.getBlockMetadata(par2, par3, par4) == 6 || par1IBlockAccess.getBlockMetadata(par2, par3, par4) == 7 || par1IBlockAccess.getBlockMetadata(par2, par3, par4) == 8)){
    		return 15;
    	}else{
    		return 0;
    	}
    }
    
    public int isProvidingStrongPower(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5){
    	if(par1IBlockAccess.getTileEntity(par2, par3, par4) != null && ((CustomizableSCTE) par1IBlockAccess.getTileEntity(par2, par3, par4)).hasModule(EnumCustomModules.REDSTONE) && (par1IBlockAccess.getBlockMetadata(par2, par3, par4) == 5 || par1IBlockAccess.getBlockMetadata(par2, par3, par4) == 6 || par1IBlockAccess.getBlockMetadata(par2, par3, par4) == 7 || par1IBlockAccess.getBlockMetadata(par2, par3, par4) == 8)){
    		return 15;
    	}else{
    		return 0;
    	}
    }
    
    public int getLightValue(){
        return isLit ? (int)(15.0F * 1.0F) : 0;
    }
    
    public Item getItemDropped(int par1, Random par2Random, int par3){
        return Item.getItemFromBlock(mod_SecurityCraft.securityCamera);
    }
    
    @SideOnly(Side.CLIENT)
    public Item getItem(World par1World, int par2, int par3, int par4){
    	return BlockUtils.getItemFromBlock(mod_SecurityCraft.securityCamera);
    }
    
	public TileEntity createNewTileEntity(World world, int par2) {
		return new TileEntitySecurityCamera();
	}

}

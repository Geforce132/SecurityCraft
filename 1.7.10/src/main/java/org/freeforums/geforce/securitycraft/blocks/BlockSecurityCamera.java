package org.freeforums.geforce.securitycraft.blocks;

import static net.minecraftforge.common.util.ForgeDirection.EAST;
import static net.minecraftforge.common.util.ForgeDirection.NORTH;
import static net.minecraftforge.common.util.ForgeDirection.SOUTH;
import static net.minecraftforge.common.util.ForgeDirection.WEST;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import org.freeforums.geforce.securitycraft.entity.EntitySecurityCamera;
import org.freeforums.geforce.securitycraft.main.HelpfulMethods;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityOwnable;
import org.freeforums.geforce.securitycraft.tileentity.TileEntitySecurityCamera;

public class BlockSecurityCamera extends BlockContainer{

	public BlockSecurityCamera(Material par2Material) {
		super(par2Material);
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
        
    	if(meta == 3){
    		this.setBlockBounds(0.275F, 0.250F, 0.000F, 0.700F, 0.800F, 0.850F);
    	}else if(meta == 1){
    		this.setBlockBounds(0.275F, 0.250F, 0.150F, 0.700F, 0.800F, 1.000F);
        }else if(meta == 2){
    		this.setBlockBounds(0.125F, 0.250F, 0.275F, 1.000F, 0.800F, 0.725F);
        }else{
    		this.setBlockBounds(0.000F, 0.250F, 0.275F, 0.850F, 0.800F, 0.725F);
        }
        
    } 
	
	/**
     * Called when the block is placed in the world.
     */
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
    	}else{
            System.out.println(par1World.getBlockMetadata(par2, par3, par4));
    		return;
    	}   
        
        System.out.println(par1World.getBlockMetadata(par2, par3, par4));
        
    }

    public void mountCamera(World world, int par2, int par3, int par4, EntityPlayer player) {
    	if(player.ridingEntity == null){
			HelpfulMethods.sendMessageToPlayer(player, "You are now mounted to a security camera. Use the arrow keys to move the camera's view, and the +/- buttons to zoom in and out.", EnumChatFormatting.GREEN);
		}
    	
    	EntitySecurityCamera dummyEntity = new EntitySecurityCamera(world, par2, par3, par4, 1);
		world.spawnEntityInWorld(dummyEntity);
		player.mountEntity(dummyEntity);
		
		mod_SecurityCraft.instance.setUsePosition(player.getCommandSenderName(), player.posX, player.posY, player.posZ);
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

	public TileEntity createNewTileEntity(World world, int par2) {
		return new TileEntitySecurityCamera();
	}

}

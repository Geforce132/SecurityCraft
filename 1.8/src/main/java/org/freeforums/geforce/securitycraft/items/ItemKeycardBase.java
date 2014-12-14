package org.freeforums.geforce.securitycraft.items;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.freeforums.geforce.securitycraft.blocks.BlockKeycardReader;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityKeycardReader;

@SuppressWarnings({"unchecked", "rawtypes"})
public class ItemKeycardBase extends Item{
	
	private final int level;
		
	public ItemKeycardBase(int level) {
		this.level = level;
		this.setMaxStackSize(1);
		this.setCreativeTab(mod_SecurityCraft.tabSCTechnical);
	}
    
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {		
		if(this.level == 4){		
			if(par1ItemStack.getTagCompound() == null){
				par1ItemStack.setTagCompound(new NBTTagCompound());
				par1ItemStack.getTagCompound().setInteger("Uses", 5);
			}
			
			par3List.add("Uses remaining: " + par1ItemStack.getTagCompound().getInteger("Uses"));			
			
		}
	}
	
	/**
     * Callback for item usage. If the item does something special on right clicking, he will have one of those. Return
     * True if something happen and false if it don't. This is for ITEMS, not BLOCKS
     */
    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, BlockPos pos, EnumFacing side, float par8, float par9, float par10)
    {
        if (par3World.getBlockState(pos).getBlock() == mod_SecurityCraft.keycardReader)
        {
            if (par3World.isRemote)
            {
            	if(((TileEntityKeycardReader)par3World.getTileEntity(pos)).getPassLV() > 0){
            		((TileEntityKeycardReader)par3World.getTileEntity(pos)).setIsProvidingPower(true);
            	}
                return true;
            }
            else
            {      	
            	if(!((TileEntityKeycardReader) par3World.getTileEntity(pos)).getIsProvidingPower()){              
            		((BlockKeycardReader)mod_SecurityCraft.keycardReader).insertCard(par3World, pos, par1ItemStack, par2EntityPlayer);                
            	}
            	
                return true;
            }
        }
        else
        {
            return false;
        }
    }
    
    public int getKeycardLV(ItemStack par1ItemStack){
		return level;
	}
	

}

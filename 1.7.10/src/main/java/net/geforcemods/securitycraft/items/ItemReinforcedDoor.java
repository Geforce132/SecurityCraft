package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class ItemReinforcedDoor extends Item {

	public ItemReinforcedDoor(Material p_i45334_1_){
		this.maxStackSize = 1;
		this.setCreativeTab(CreativeTabs.tabRedstone);
	}

	public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10){
		if(par3World.isRemote){
			return true;
		}else{
			if (par7 != 1){
				return false;
			}else{
				++par5;
				Block block = mod_SecurityCraft.doorIndestructableIron;

				if (par2EntityPlayer.canPlayerEdit(par4, par5, par6, par7, par1ItemStack) && par2EntityPlayer.canPlayerEdit(par4, par5 + 1, par6, par7, par1ItemStack)){
					if (!block.canPlaceBlockAt(par3World, par4, par5, par6)){
						return false;
					}else{
						int i1 = MathHelper.floor_double((double)((par2EntityPlayer.rotationYaw + 180.0F) * 4.0F / 360.0F) - 0.5D) & 3;
						placeDoorBlock(par3World, par4, par5, par6, i1, block);
						((TileEntityOwnable) par3World.getTileEntity(par4, par5, par6)).setOwner(par2EntityPlayer.getGameProfile().getId().toString(), par2EntityPlayer.getCommandSenderName());
						((TileEntityOwnable) par3World.getTileEntity(par4, par5 + 1, par6)).setOwner(par2EntityPlayer.getGameProfile().getId().toString(), par2EntityPlayer.getCommandSenderName());
						--par1ItemStack.stackSize;
						return true;
					}
				}else{
					return false;
				}
			}
		}
	}

	public static void placeDoorBlock(World par1World, int par2, int par3, int par4, int par5, Block par6Block){
		byte b0 = 0;
		byte b1 = 0;

		if(par5 == 0)
			b1 = 1;
		else if(par5 == 1)
			b0 = -1;
		else if(par5 == 2)
			b1 = -1;
		else if(par5 == 3)
			b0 = 1;

		int i1 = (par1World.getBlock(par2 - b0, par3, par4 - b1).isNormalCube() ? 1 : 0) + (par1World.getBlock(par2 - b0, par3 + 1, par4 - b1).isNormalCube() ? 1 : 0);
		int j1 = (par1World.getBlock(par2 + b0, par3, par4 + b1).isNormalCube() ? 1 : 0) + (par1World.getBlock(par2 + b0, par3 + 1, par4 + b1).isNormalCube() ? 1 : 0);
		boolean flag = par1World.getBlock(par2 - b0, par3, par4 - b1) == par6Block || par1World.getBlock(par2 - b0, par3 + 1, par4 - b1) == par6Block;
		boolean flag1 = par1World.getBlock(par2 + b0, par3, par4 + b1) == par6Block || par1World.getBlock(par2 + b0, par3 + 1, par4 + b1) == par6Block;
		boolean flag2 = false;

		if (flag && !flag1){
			flag2 = true;
		}else if (j1 > i1){
			flag2 = true;
		}

		par1World.setBlock(par2, par3, par4, par6Block, par5, 2);
		par1World.setBlock(par2, par3 + 1, par4, par6Block, 8 | (flag2 ? 1 : 0), 2);
		par1World.notifyBlocksOfNeighborChange(par2, par3, par4, par6Block);
		par1World.notifyBlocksOfNeighborChange(par2, par3 + 1, par4, par6Block);
	}

}
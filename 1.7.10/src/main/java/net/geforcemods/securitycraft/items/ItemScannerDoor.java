package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class ItemScannerDoor extends Item
{
	public ItemScannerDoor()
	{
		maxStackSize = 1;
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int par7, float par8, float par9, float par10)
	{
		if(world.isRemote)
			return true;
		else if (par7 != 1)
			return false;
		else
		{
			Block block = mod_SecurityCraft.scannerDoor;

			y++;

			if(player.canPlayerEdit(x, y, z, par7, stack) && player.canPlayerEdit(x, y + 1, z, par7, stack))
			{
				if(!block.canPlaceBlockAt(world, x, y, z))
					return false;
				else
				{
					int i1 = MathHelper.floor_double((player.rotationYaw + 180.0F) * 4.0F / 360.0F - 0.5D) & 3;

					placeDoorBlock(world, x, y, z, i1, block);
					((TileEntityOwnable) world.getTileEntity(x, y, z)).getOwner().set(player.getGameProfile().getId().toString(), player.getCommandSenderName());
					((TileEntityOwnable) world.getTileEntity(x, y + 1, z)).getOwner().set(player.getGameProfile().getId().toString(), player.getCommandSenderName());
					stack.stackSize--;
					return true;
				}
			}
			else
				return false;
		}
	}

	public static void placeDoorBlock(World par1World, int par2, int par3, int par4, int par5, Block par6Block)
	{
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

		if (flag && !flag1)
			flag2 = true;
		else if (j1 > i1)
			flag2 = true;

		par1World.setBlock(par2, par3, par4, par6Block, par5, 2);
		par1World.setBlock(par2, par3 + 1, par4, par6Block, 8 | (flag2 ? 1 : 0), 2);
		par1World.notifyBlocksOfNeighborChange(par2, par3, par4, par6Block);
		par1World.notifyBlocksOfNeighborChange(par2, par3 + 1, par4, par6Block);
	}
}

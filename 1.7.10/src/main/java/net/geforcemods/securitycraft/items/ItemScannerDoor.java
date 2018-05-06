package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.SCContent;
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
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
	{
		if(world.isRemote)
			return true;
		else if (side != 1)
			return false;
		else
		{
			Block block = SCContent.scannerDoor;

			y++;

			if(player.canPlayerEdit(x, y, z, side, stack) && player.canPlayerEdit(x, y + 1, z, side, stack))
			{
				if(!block.canPlaceBlockAt(world, x, y, z))
					return false;
				else
				{
					int rotation = MathHelper.floor_double((player.rotationYaw + 180.0F) * 4.0F / 360.0F - 0.5D) & 3;

					placeDoorBlock(world, x, y, z, rotation, block);
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

	public static void placeDoorBlock(World world, int x, int y, int z, int rotation, Block block) //wtf is this code
	{
		byte b0 = 0;
		byte b1 = 0;

		if(rotation == 0)
			b1 = 1;
		else if(rotation == 1)
			b0 = -1;
		else if(rotation == 2)
			b1 = -1;
		else if(rotation == 3)
			b0 = 1;

		int i1 = (world.getBlock(x - b0, y, z - b1).isNormalCube() ? 1 : 0) + (world.getBlock(x - b0, y + 1, z - b1).isNormalCube() ? 1 : 0);
		int j1 = (world.getBlock(x + b0, y, z + b1).isNormalCube() ? 1 : 0) + (world.getBlock(x + b0, y + 1, z + b1).isNormalCube() ? 1 : 0);
		boolean flag = world.getBlock(x - b0, y, z - b1) == block || world.getBlock(x - b0, y + 1, z - b1) == block;
		boolean flag1 = world.getBlock(x + b0, y, z + b1) == block || world.getBlock(x + b0, y + 1, z + b1) == block;
		boolean flag2 = false;

		if (flag && !flag1)
			flag2 = true;
		else if (j1 > i1)
			flag2 = true;

		world.setBlock(x, y, z, block, rotation, 2);
		world.setBlock(x, y + 1, z, block, 8 | (flag2 ? 1 : 0), 2);
		world.notifyBlocksOfNeighborChange(x, y, z, block);
		world.notifyBlocksOfNeighborChange(x, y + 1, z, block);
	}
}

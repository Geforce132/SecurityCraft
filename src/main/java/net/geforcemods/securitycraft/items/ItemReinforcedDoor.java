package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class ItemReinforcedDoor extends Item {

	public ItemReinforcedDoor(){
		maxStackSize = 1;
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ){
		if(world.isRemote)
			return true;
		else if (side != 1)
			return false;
		else{
			++y;
			Block block = SCContent.reinforcedDoor;

			if (player.canPlayerEdit(x, y, z, side, stack) && player.canPlayerEdit(x, y + 1, z, side, stack)){
				if (!block.canPlaceBlockAt(world, x, y, z))
					return false;
				else{
					int rotation = MathHelper.floor_double((player.rotationYaw + 180.0F) * 4.0F / 360.0F - 0.5D) & 3;
					placeDoorBlock(world, x, y, z, rotation, block);
					((TileEntityOwnable) world.getTileEntity(x, y, z)).getOwner().set(player.getGameProfile().getId().toString(), player.getCommandSenderName());
					((TileEntityOwnable) world.getTileEntity(x, y + 1, z)).getOwner().set(player.getGameProfile().getId().toString(), player.getCommandSenderName());
					--stack.stackSize;
					return true;
				}
			}
			else
				return false;
		}
	}

	public static void placeDoorBlock(World world, int x, int y, int z, int rotation, Block door){ //naming might not be entirely correct, but it's giving a rough idea
		byte left = 0;
		byte right = 0;

		if(rotation == 0)
			right = 1;
		else if(rotation == 1)
			left = -1;
		else if(rotation == 2)
			right = -1;
		else if(rotation == 3)
			left = 1;

		int rightNormalCubeAmount = (world.getBlock(x - left, y, z - right).isNormalCube() ? 1 : 0) + (world.getBlock(x - left, y + 1, z - right).isNormalCube() ? 1 : 0);
		int leftNormalCubeAmount = (world.getBlock(x + left, y, z + right).isNormalCube() ? 1 : 0) + (world.getBlock(x + left, y + 1, z + right).isNormalCube() ? 1 : 0);
		boolean isRightDoor = world.getBlock(x - left, y, z - right) == door || world.getBlock(x - left, y + 1, z - right) == door;
		boolean isLeftDoor = world.getBlock(x + left, y, z + right) == door || world.getBlock(x + left, y + 1, z + right) == door;
		boolean hingeRight = false;

		if (isRightDoor && !isLeftDoor)
			hingeRight = true;
		else if (leftNormalCubeAmount > rightNormalCubeAmount)
			hingeRight = true;

		world.setBlock(x, y, z, door, rotation, 2);
		world.setBlock(x, y + 1, z, door, 8 | (hingeRight ? 1 : 0), 2);
		world.notifyBlocksOfNeighborChange(x, y, z, door);
		world.notifyBlocksOfNeighborChange(x, y + 1, z, door);
	}

}
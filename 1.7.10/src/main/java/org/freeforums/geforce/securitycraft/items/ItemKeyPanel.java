package org.freeforums.geforce.securitycraft.items;

import org.freeforums.geforce.securitycraft.api.IOwnable;
import org.freeforums.geforce.securitycraft.main.Utils.BlockUtils;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class ItemKeyPanel extends Item {

	public ItemKeyPanel(){
		super();
	}

	//west: -x
	//east: +x
	//north: -z
	//south: +z
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World w, int x, int y, int z, int side, float par8, float par9, float par10){
		if(!w.isRemote){
			if(w.getBlock(x, y, z) == mod_SecurityCraft.frame){
				String owner = ((IOwnable) w.getTileEntity(x, y, z)).getOwnerName();
				String uuid = ((IOwnable) w.getTileEntity(x, y, z)).getOwnerUUID();
				w.setBlock(x, y, z, mod_SecurityCraft.Keypad, w.getBlockMetadata(x, y, z), 3);
				((IOwnable) w.getTileEntity(x, y, z)).setOwner(uuid, owner);
				stack.stackSize -= 1;
			}
			else if(isDoorNextTo(w, x, y, z))
			{
				ForgeDirection clicked = ForgeDirection.getOrientation(side);
				Block door = mod_SecurityCraft.doorIndestructableIron;
				
				if(clicked == ForgeDirection.WEST)
				{
					if(w.getBlock(x - 1, y, z) == door) //check if clicked side is the side where the door is placed
						return false;
					else if(w.getBlock(x + 1, y, z) == door) //check if door is on the other side of the clicked side
						return false;
					
					if(BlockUtils.isBlockAir(w, x - 1, y, z))
					{
						w.setBlock(x - 1, y, z, mod_SecurityCraft.blockKeyPanel);
						((IOwnable)w.getTileEntity(x - 1, y, z)).setOwner(player.getUniqueID().toString(), player.getCommandSenderName());
					}
				}
				else if(clicked == ForgeDirection.EAST)
				{
					if(w.getBlock(x + 1, y, z) == door)
						return false;
					else if(w.getBlock(x - 1, y, z) == door)
						return false;
					
					if(BlockUtils.isBlockAir(w, x + 1, y, z))
					{
						w.setBlock(x + 1, y, z, mod_SecurityCraft.blockKeyPanel);
					}
				}
				else if(clicked == ForgeDirection.NORTH)
				{
					if(w.getBlock(x, y, z - 1) == door)
						return false;
					else if(w.getBlock(x, y, z + 1) == door)
						return false;
					
					if(BlockUtils.isBlockAir(w, x, y, z - 1))
					{
						w.setBlock(x, y, z - 1, mod_SecurityCraft.blockKeyPanel);
					}
				}
				else if(clicked == ForgeDirection.SOUTH)
				{
					if(w.getBlock(x, y, z + 1) == door)
						return false;
					else if(w.getBlock(x, y, z - 1) == door)
						return false;
					
					if(BlockUtils.isBlockAir(w, x, y, z + 1))
					{
						w.setBlock(x, y, z + 1, mod_SecurityCraft.blockKeyPanel);
					}
				}
				else
					return false;
			}

			return true;
		}

		return false;
	}

	public boolean isDoorNextTo(World w, int x, int y, int z)
	{
		Block door = mod_SecurityCraft.doorIndestructableIron;
		
		return w.getBlock(x + 1, y, z) == door || w.getBlock(x - 1, y, z) == door || w.getBlock(x, y, z + 1) == door || w.getBlock(x, y, z - 1) == door;
	}
}

package net.geforcemods.securitycraft.util;

import java.util.ArrayList;
import java.util.List;

import net.geforcemods.securitycraft.api.EnumLinkedAction;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.TileEntityLinkable;
import net.geforcemods.securitycraft.items.ItemModule;
import net.geforcemods.securitycraft.misc.EnumModuleType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ModuleUtils{
	public static List<String> getPlayersFromModule(ItemStack stack)
	{
		List<String> list = new ArrayList<>();

		if(stack.getItem() instanceof ItemModule)
		{
			for(int i = 1; i <= ItemModule.MAX_PLAYERS; i++)
			{
				if(stack.getTagCompound() != null && stack.getTagCompound().getString("Player" + i) != null && !stack.getTagCompound().getString("Player" + i).isEmpty())
					list.add(stack.getTagCompound().getString("Player" + i).toLowerCase());
			}
		}

		return list;
	}

	public static boolean isAllowed(IModuleInventory inv, Entity entity)
	{
		return isAllowed(inv, entity.getName());
	}

	public static boolean isAllowed(IModuleInventory inv, String name)
	{
		ItemStack stack = inv.getModule(EnumModuleType.ALLOWLIST);

		if(stack.hasTagCompound() && stack.getTagCompound().getBoolean("affectEveryone"))
			return true;

		//IModuleInventory#getModule returns ItemStack.EMPTY when the module does not exist, and getPlayersFromModule will then have an empty list
		return getPlayersFromModule(stack).contains(name.toLowerCase());
	}

	public static boolean isDenied(IModuleInventory inv, Entity entity)
	{
		ItemStack stack = inv.getModule(EnumModuleType.DENYLIST);

		if(stack.hasTagCompound() && stack.getTagCompound().getBoolean("affectEveryone"))
		{
			if(inv.getTileEntity() instanceof IOwnable)
			{
				//only deny players that are not the owner
				if(entity instanceof EntityPlayer)
				{
					//if the player IS the owner, fall back to the default handling (check if the name is on the list)
					if(!((IOwnable)inv.getTileEntity()).getOwner().isOwner((EntityPlayer)entity))
						return true;
				}
				else
					return true;
			}
			else
				return true;
		}

		//IModuleInventory#getModule returns ItemStack.EMPTY when the module does not exist, and getPlayersFromModule will then have an empty list
		return getPlayersFromModule(stack).contains(entity.getName().toLowerCase());
	}

	public static void createLinkedAction(EnumLinkedAction action, ItemStack stack, TileEntityLinkable te)
	{
		if(action == EnumLinkedAction.MODULE_INSERTED)
			te.createLinkedBlockAction(action, new Object[] {stack, (ItemModule)stack.getItem()}, te);
		else if(action == EnumLinkedAction.MODULE_REMOVED)
			te.createLinkedBlockAction(action, new Object[] {stack, ((ItemModule)stack.getItem()).getModuleType()}, te);
	}
}
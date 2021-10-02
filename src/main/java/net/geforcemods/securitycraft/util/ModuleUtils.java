package net.geforcemods.securitycraft.util;

import java.util.ArrayList;
import java.util.List;

import net.geforcemods.securitycraft.api.CustomizableTileEntity;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.LinkedAction;
import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.geforcemods.securitycraft.items.ModuleItem;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.tileentity.SecurityCameraTileEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class ModuleUtils{
	public static List<String> getPlayersFromModule(ItemStack stack)
	{
		List<String> list = new ArrayList<>();

		if(stack.getItem() instanceof ModuleItem)
		{
			for(int i = 1; i <= ModuleItem.MAX_PLAYERS; i++)
			{
				if(stack.getTag() != null && stack.getTag().getString("Player" + i) != null && !stack.getTag().getString("Player" + i).isEmpty())
					list.add(stack.getTag().getString("Player" + i).toLowerCase());
			}
		}

		return list;
	}

	public static boolean isAllowed(IModuleInventory inv, Entity entity)
	{
		return isAllowed(inv, entity.getName().getFormattedText());
	}

	public static boolean isAllowed(IModuleInventory inv, String name)
	{
		ItemStack stack = inv.getModule(ModuleType.ALLOWLIST);

		if(stack.hasTag() && stack.getTag().getBoolean("affectEveryone"))
			return true;

		//IModuleInventory#getModule returns ItemStack.EMPTY when the module does not exist, and getPlayersFromModule will then have an empty list
		return getPlayersFromModule(stack).contains(name.toLowerCase());
	}

	public static boolean isDenied(IModuleInventory inv, Entity entity)
	{
		ItemStack stack = inv.getModule(ModuleType.DENYLIST);

		if(stack.hasTag() && stack.getTag().getBoolean("affectEveryone"))
		{
			if(inv.getTileEntity() instanceof IOwnable)
			{
				//only deny players that are not the owner
				if(entity instanceof PlayerEntity)
				{
					//if the player IS the owner, fall back to the default handling (check if the name is on the list)
					if(!((IOwnable)inv.getTileEntity()).getOwner().isOwner((PlayerEntity)entity))
						return true;
				}
				else
					return true;
			}
			else
				return true;
		}

		//IModuleInventory#getModule returns ItemStack.EMPTY when the module does not exist, and getPlayersFromModule will then have an empty list
		return getPlayersFromModule(stack).contains(entity.getName().getFormattedText().toLowerCase());
	}

	public static void createLinkedAction(LinkedAction action, ItemStack stack, CustomizableTileEntity te)
	{
		if(action == LinkedAction.MODULE_INSERTED)
			te.createLinkedBlockAction(action, new Object[] {stack, (ModuleItem)stack.getItem()}, te);
		else if(action == LinkedAction.MODULE_REMOVED)
			te.createLinkedBlockAction(action, new Object[] {stack, ((ModuleItem)stack.getItem()).getModuleType()}, te);

		if(te instanceof SecurityCameraTileEntity)
			te.getWorld().notifyNeighborsOfStateChange(te.getPos().offset(te.getBlockState().get(SecurityCameraBlock.FACING), -1), te.getBlockState().getBlock());
	}
}
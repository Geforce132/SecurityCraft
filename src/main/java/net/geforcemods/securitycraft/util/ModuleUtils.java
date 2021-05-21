package net.geforcemods.securitycraft.util;

import java.util.ArrayList;
import java.util.List;

import net.geforcemods.securitycraft.api.CustomizableTileEntity;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.LinkedAction;
import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.geforcemods.securitycraft.items.ModuleItem;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.tileentity.SecurityCameraTileEntity;
import net.minecraft.entity.Entity;
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
		return isAllowed(inv, entity.getName().getString());
	}

	public static boolean isAllowed(IModuleInventory inv, String name)
	{
		//IModuleInventory#getModule returns ItemStack.EMPTY when the module does not exist, and getPlayersFromModule will then have an empty list
		return getPlayersFromModule(inv.getModule(ModuleType.ALLOWLIST)).contains(name.toLowerCase());
	}

	public static boolean isDenied(IModuleInventory inv, Entity entity)
	{
		//IModuleInventory#getModule returns ItemStack.EMPTY when the module does not exist, and getPlayersFromModule will then have an empty list
		return getPlayersFromModule(inv.getModule(ModuleType.DENYLIST)).contains(entity.getName().getString().toLowerCase());
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
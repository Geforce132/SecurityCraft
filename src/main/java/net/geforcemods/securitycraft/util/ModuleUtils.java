package net.geforcemods.securitycraft.util;

import java.util.ArrayList;
import java.util.List;

import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.EnumLinkedAction;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.blocks.BlockSecurityCamera;
import net.geforcemods.securitycraft.items.ItemModule;
import net.geforcemods.securitycraft.misc.EnumModuleType;
import net.geforcemods.securitycraft.tileentity.TileEntitySecurityCamera;
import net.minecraft.entity.Entity;
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
		//IModuleInventory#getModule returns ItemStack.EMPTY when the module does not exist, and getPlayersFromModule will then have an empty list
		return getPlayersFromModule(inv.getModule(EnumModuleType.ALLOWLIST)).contains(name.toLowerCase());
	}

	public static boolean isDenied(IModuleInventory inv, Entity entity)
	{
		//IModuleInventory#getModule returns ItemStack.EMPTY when the module does not exist, and getPlayersFromModule will then have an empty list
		return getPlayersFromModule(inv.getModule(EnumModuleType.DENYLIST)).contains(entity.getName().toLowerCase());
	}

	public static void createLinkedAction(EnumLinkedAction action, ItemStack stack, CustomizableSCTE te)
	{
		if(action == EnumLinkedAction.MODULE_INSERTED)
			te.createLinkedBlockAction(action, new Object[] {stack, (ItemModule)stack.getItem()}, te);
		else if(action == EnumLinkedAction.MODULE_REMOVED)
			te.createLinkedBlockAction(action, new Object[] {stack, ((ItemModule)stack.getItem()).getModuleType()}, te);

		if(te instanceof TileEntitySecurityCamera)
			te.getWorld().notifyNeighborsOfStateChange(te.getPos().offset(te.getWorld().getBlockState(te.getPos()).getValue(BlockSecurityCamera.FACING), -1), te.getBlockType(), false);
	}
}
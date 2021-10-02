package net.geforcemods.securitycraft.util;

import java.util.ArrayList;
import java.util.List;

import net.geforcemods.securitycraft.api.CustomizableBlockEntity;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.LinkedAction;
import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.geforcemods.securitycraft.items.ModuleItem;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

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
			if(inv.getTileEntity() instanceof IOwnable ownable)
			{
				//only deny players that are not the owner
				if(entity instanceof Player player)
				{
					//if the player IS the owner, fall back to the default handling (check if the name is on the list)
					if(!ownable.getOwner().isOwner(player))
						return true;
				}
				else
					return true;
			}
			else
				return true;
		}

		//IModuleInventory#getModule returns ItemStack.EMPTY when the module does not exist, and getPlayersFromModule will then have an empty list
		return getPlayersFromModule(stack).contains(entity.getName().getString().toLowerCase());
	}

	public static void createLinkedAction(LinkedAction action, ItemStack stack, CustomizableBlockEntity te)
	{
		if(action == LinkedAction.MODULE_INSERTED)
			te.createLinkedBlockAction(action, new Object[] {stack, (ModuleItem)stack.getItem()}, te);
		else if(action == LinkedAction.MODULE_REMOVED)
			te.createLinkedBlockAction(action, new Object[] {stack, ((ModuleItem)stack.getItem()).getModuleType()}, te);

		if(te instanceof SecurityCameraBlockEntity)
			te.getLevel().updateNeighborsAt(te.getBlockPos().relative(te.getBlockState().getValue(SecurityCameraBlock.FACING), -1), te.getBlockState().getBlock());
	}
}
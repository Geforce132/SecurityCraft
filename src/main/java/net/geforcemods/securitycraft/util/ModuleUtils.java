package net.geforcemods.securitycraft.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

import net.geforcemods.securitycraft.api.EnumLinkedAction;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.TileEntityLinkable;
import net.geforcemods.securitycraft.items.ItemModule;
import net.geforcemods.securitycraft.misc.EnumModuleType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

public class ModuleUtils {
	public static List<String> getPlayersFromModule(ItemStack stack) {
		List<String> list = new ArrayList<>();

		if (stack.getItem() instanceof ItemModule && stack.hasTagCompound()) {
			for (int i = 1; i <= ItemModule.MAX_PLAYERS; i++) {
				if (stack.getTagCompound().getString("Player" + i) != null && !stack.getTagCompound().getString("Player" + i).isEmpty())
					list.add(stack.getTagCompound().getString("Player" + i).toLowerCase());
			}
		}

		return list;
	}

	public static boolean isAllowed(IModuleInventory inv, Entity entity) {
		return isAllowed(inv, entity.getName());
	}

	public static boolean isAllowed(IModuleInventory inv, String name) {
		if (!inv.isModuleEnabled(EnumModuleType.ALLOWLIST))
			return false;

		ItemStack stack = inv.getModule(EnumModuleType.ALLOWLIST);

		if (stack.hasTagCompound() && stack.getTagCompound().getBoolean("affectEveryone"))
			return true;

		//IModuleInventory#getModule returns ItemStack.EMPTY when the module does not exist, and getPlayersFromModule will then have an empty list
		return doesModuleHaveTeamOf(name, inv.getTileEntity().getWorld(), stack) || getPlayersFromModule(stack).contains(name.toLowerCase());
	}

	public static boolean isDenied(IModuleInventory inv, Entity entity) {
		if (!inv.isModuleEnabled(EnumModuleType.DENYLIST))
			return false;

		ItemStack stack = inv.getModule(EnumModuleType.DENYLIST);

		if (stack.hasTagCompound() && stack.getTagCompound().getBoolean("affectEveryone")) {
			if (inv.getTileEntity() instanceof IOwnable) {
				//only deny players that are not the owner
				if (entity instanceof EntityPlayer) {
					//if the player IS the owner, fall back to the default handling (check if the name is on the list)
					if (!((IOwnable) inv.getTileEntity()).getOwner().isOwner((EntityPlayer) entity))
						return true;
				}
				else
					return true;
			}
			else
				return true;
		}

		String name = entity.getName();

		//IModuleInventory#getModule returns ItemStack.EMPTY when the module does not exist, and getPlayersFromModule will then have an empty list
		return doesModuleHaveTeamOf(name, inv.getTileEntity().getWorld(), stack) || getPlayersFromModule(stack).contains(name.toLowerCase());
	}

	public static void createLinkedAction(EnumLinkedAction action, ItemStack stack, TileEntityLinkable te, boolean toggled) {
		if (action == EnumLinkedAction.MODULE_INSERTED) {
			te.createLinkedBlockAction(action, new Object[] {
					stack, (ItemModule) stack.getItem(), toggled
			}, te);
		}
		else if (action == EnumLinkedAction.MODULE_REMOVED) {
			te.createLinkedBlockAction(action, new Object[] {
					stack, ((ItemModule) stack.getItem()).getModuleType(), toggled
			}, te);
		}
	}

	public static boolean doesModuleHaveTeamOf(String name, World level, ItemStack module) {
		ScorePlayerTeam team = level.getScoreboard().getPlayersTeam(name);

		if (!module.hasTagCompound())
			module.setTagCompound(new NBTTagCompound());

		//@formatter:off
		return team != null && StreamSupport.stream(module.getTagCompound().getTagList("ListedTeams", Constants.NBT.TAG_STRING).spliterator(), false)
				.filter(tag -> tag instanceof NBTTagString)
				.map(tag -> ((NBTTagString) tag).getString())
				.anyMatch(team.getName()::equals);
		//@formatter:on
	}
}
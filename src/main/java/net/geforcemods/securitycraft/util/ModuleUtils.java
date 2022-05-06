package net.geforcemods.securitycraft.util;

import java.util.ArrayList;
import java.util.List;

import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.LinkableBlockEntity;
import net.geforcemods.securitycraft.api.LinkedAction;
import net.geforcemods.securitycraft.items.ModuleItem;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.StringNBT;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

public class ModuleUtils {
	public static List<String> getPlayersFromModule(ItemStack stack) {
		List<String> list = new ArrayList<>();

		if (stack.getItem() instanceof ModuleItem && stack.hasTag()) {
			for (int i = 1; i <= ModuleItem.MAX_PLAYERS; i++) {
				if (stack.getTag().getString("Player" + i) != null && !stack.getTag().getString("Player" + i).isEmpty()) {
					list.add(stack.getTag().getString("Player" + i).toLowerCase());
				}
			}
		}

		return list;
	}

	public static boolean isAllowed(IModuleInventory inv, Entity entity) {
		return isAllowed(inv, entity.getName().getString());
	}

	public static boolean isAllowed(IModuleInventory inv, String name) {
		if (!inv.isModuleEnabled(ModuleType.ALLOWLIST))
			return false;

		ItemStack stack = inv.getModule(ModuleType.ALLOWLIST);

		if (stack.hasTag() && stack.getTag().getBoolean("affectEveryone"))
			return true;

		//IModuleInventory#getModule returns ItemStack.EMPTY when the module does not exist, and getPlayersFromModule will then have an empty list
		return doesModuleHaveTeamOf(name, inv.getTileEntity().getLevel(), stack) || getPlayersFromModule(stack).contains(name.toLowerCase());
	}

	public static boolean isDenied(IModuleInventory inv, Entity entity) {
		if (!inv.isModuleEnabled(ModuleType.DENYLIST))
			return false;

		ItemStack stack = inv.getModule(ModuleType.DENYLIST);

		if (stack.hasTag() && stack.getTag().getBoolean("affectEveryone")) {
			if (inv.getTileEntity() instanceof IOwnable) {
				//only deny players that are not the owner
				if (entity instanceof PlayerEntity) {
					//if the player IS the owner, fall back to the default handling (check if the name is on the list)
					if (!((IOwnable) inv.getTileEntity()).getOwner().isOwner((PlayerEntity) entity))
						return true;
				}
				else
					return true;
			}
			else
				return true;
		}

		String name = entity.getName().getString();

		//IModuleInventory#getModule returns ItemStack.EMPTY when the module does not exist, and getPlayersFromModule will then have an empty list
		return doesModuleHaveTeamOf(name, inv.getTileEntity().getLevel(), stack) || getPlayersFromModule(stack).contains(name.toLowerCase());
	}

	public static void createLinkedAction(LinkedAction action, ItemStack stack, LinkableBlockEntity te, boolean toggled) {
		if (action == LinkedAction.MODULE_INSERTED) {
			te.createLinkedBlockAction(action, new Object[] {
					stack, (ModuleItem) stack.getItem(), toggled
			}, te);
		}
		else if (action == LinkedAction.MODULE_REMOVED) {
			te.createLinkedBlockAction(action, new Object[] {
					stack, ((ModuleItem) stack.getItem()).getModuleType(), toggled
			}, te);
		}
	}

	public static boolean doesModuleHaveTeamOf(String name, World level, ItemStack module) {
		ScorePlayerTeam team = level.getScoreboard().getPlayersTeam(name);

		//@formatter:off
		return team != null && module.getOrCreateTag().getList("ListedTeams", Constants.NBT.TAG_STRING)
				.stream()
				.filter(tag -> tag instanceof StringNBT)
				.map(tag -> ((StringNBT) tag).getAsString())
				.anyMatch(team.getName()::equals);
		//@formatter:on
	}
}
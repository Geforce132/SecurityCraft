package net.geforcemods.securitycraft.util;

import java.util.ArrayList;
import java.util.List;

import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.items.ModuleItem;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.PlayerTeam;

public class ModuleUtils {
	public static List<String> getPlayersFromModule(ItemStack stack) {
		List<String> list = new ArrayList<>();

		if (stack.getItem() instanceof ModuleItem && stack.hasTag()) {
			for (int i = 1; i <= ModuleItem.MAX_PLAYERS; i++) {
				if (stack.getTag().getString("Player" + i) != null && !stack.getTag().getString("Player" + i).isEmpty())
					list.add(stack.getTag().getString("Player" + i).toLowerCase());
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
		return doesModuleHaveTeamOf(name, inv.getBlockEntity().getLevel(), stack) || getPlayersFromModule(stack).contains(name.toLowerCase());
	}

	public static boolean isDenied(IModuleInventory inv, Entity entity) {
		if (!inv.isModuleEnabled(ModuleType.DENYLIST))
			return false;

		ItemStack stack = inv.getModule(ModuleType.DENYLIST);

		if (stack.hasTag() && stack.getTag().getBoolean("affectEveryone")) {
			if (inv.getBlockEntity() instanceof IOwnable ownable) {
				//only deny players that are not the owner
				if (entity instanceof Player player) {
					//if the player IS the owner, fall back to the default handling (check if the name is on the list)
					if (!ownable.getOwner().isOwner(player))
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
		return doesModuleHaveTeamOf(name, inv.getBlockEntity().getLevel(), stack) || getPlayersFromModule(stack).contains(name.toLowerCase());
	}

	public static boolean doesModuleHaveTeamOf(String name, Level level, ItemStack module) {
		PlayerTeam team = level.getScoreboard().getPlayersTeam(name);

		//@formatter:off
		return team != null && module.getOrCreateTag().getList("ListedTeams", Tag.TAG_STRING)
				.stream()
				.filter(tag -> tag instanceof StringTag)
				.map(tag -> ((StringTag) tag).getAsString())
				.anyMatch(team.getName()::equals);
		//@formatter:on
	}
}
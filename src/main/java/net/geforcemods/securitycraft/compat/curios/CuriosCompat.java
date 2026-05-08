package net.geforcemods.securitycraft.compat.curios;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.geforcemods.securitycraft.util.InventoryUtils.ItemAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

public class CuriosCompat {
	public static boolean hasCuriosInventory(Player player) {
		return CuriosApi.getCuriosInventory(player).isPresent();
	}

	public static ItemAccess[] getCuriosItemAccess(Player player) {
		ICuriosItemHandler curioInventory = CuriosApi.getCuriosInventory(player).orElse(null);

		if (curioInventory == null)
			return new ItemAccess[] {};

		Collection<ICurioStacksHandler> curiosPerSlotType = curioInventory.getCurios().values();
		List<IDynamicStackHandler> stackHandlers = new ArrayList<>();

		for (ICurioStacksHandler curios : curiosPerSlotType) {
			stackHandlers.add(curios.getStacks());
			stackHandlers.add(curios.getCosmeticStacks());
		}

		ItemAccess[] itemAccessList = new ItemAccess[stackHandlers.size()];

		for (int i = 0; i < stackHandlers.size(); i++) {
			IDynamicStackHandler stackHandler = stackHandlers.get(i);

			itemAccessList[i] = new ItemAccess() {
				@Override
				public int size() {
					return stackHandler.getSlots();
				}

				@Override
				public ItemStack getItem(int slot) {
					return stackHandler.getStackInSlot(slot);
				}

				@Override
				public void set(int slot, ItemStack stack) {
					stackHandler.setStackInSlot(slot, stack);
				}
			};
		}

		return itemAccessList;
	}
}

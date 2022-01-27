package net.geforcemods.securitycraft.blockentity;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.inventory.KeypadSmokerMenu;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;

public class KeypadSmokerBlockEntity extends AbstractKeypadFurnaceBlockEntity {
	public KeypadSmokerBlockEntity() {
		super(SCContent.beTypeKeypadSmoker, IRecipeType.SMOKING);
	}

	@Override
	protected int getBurnDuration(ItemStack fuel) {
		return super.getBurnDuration(fuel) / 2;
	}

	@Override
	protected Container createMenu(int windowId, PlayerInventory inv) {
		return new KeypadSmokerMenu(windowId, level, worldPosition, inv, this, dataAccess);
	}
}

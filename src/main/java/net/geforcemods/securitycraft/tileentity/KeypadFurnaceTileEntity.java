package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.containers.KeypadFurnaceContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.crafting.IRecipeType;

public class KeypadFurnaceTileEntity extends AbstractKeypadFurnaceTileEntity {
	public KeypadFurnaceTileEntity() {
		super(SCContent.teTypeKeypadFurnace, IRecipeType.SMELTING);
	}

	@Override
	protected Container createMenu(int windowId, PlayerInventory inv) {
		return new KeypadFurnaceContainer(windowId, world, pos, inv, this, furnaceData);
	}
}

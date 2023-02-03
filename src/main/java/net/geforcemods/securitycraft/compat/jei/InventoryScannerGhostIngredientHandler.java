package net.geforcemods.securitycraft.compat.jei;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import mezz.jei.api.gui.IGhostIngredientHandler;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.inventory.OwnerRestrictedSlot;
import net.geforcemods.securitycraft.network.server.SetGhostSlot;
import net.geforcemods.securitycraft.screen.InventoryScannerScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class InventoryScannerGhostIngredientHandler implements IGhostIngredientHandler<InventoryScannerScreen> {
	@Override
	public <I> List<Target<I>> getTargets(InventoryScannerScreen screen, I ingredient, boolean doStart) {
		if (!screen.tileEntity.isOwnedBy(Minecraft.getMinecraft().player))
			return new ArrayList<>();

		List<Target<I>> targets = new ArrayList<>();

		for (Slot slot : screen.inventorySlots.inventorySlots) {
			if (slot instanceof OwnerRestrictedSlot) {
				Rectangle area = new Rectangle(screen.getGuiLeft() + slot.xPos, screen.getGuiTop() + slot.yPos, 16, 16);

				targets.add(new Target<I>() {
					@Override
					public Rectangle getArea() {
						return area;
					}

					@Override
					public void accept(I ingredient) {
						screen.tileEntity.getContents().set(slot.slotNumber, (ItemStack) ingredient);
						SecurityCraft.network.sendToServer(new SetGhostSlot(slot.slotNumber, (ItemStack) ingredient));
					}
				});
			}
		}

		return targets;
	}

	@Override
	public void onComplete() {}
}

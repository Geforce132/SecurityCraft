package net.geforcemods.securitycraft.compat.jei;

import java.util.ArrayList;
import java.util.List;

import mezz.jei.api.gui.handlers.IGhostIngredientHandler;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.inventory.OwnerRestrictedSlot;
import net.geforcemods.securitycraft.network.server.SetGhostSlot;
import net.geforcemods.securitycraft.screen.InventoryScannerScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Rectangle2d;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

public class InventoryScannerGhostIngredientHandler implements IGhostIngredientHandler<InventoryScannerScreen> {
	@Override
	public <I> List<Target<I>> getTargets(InventoryScannerScreen screen, I ingredient, boolean doStart) {
		if (!screen.tileEntity.getOwner().isOwner(Minecraft.getInstance().player))
			return new ArrayList<>();

		List<Target<I>> targets = new ArrayList<>();

		for (Slot slot : screen.getMenu().slots) {
			if (slot instanceof OwnerRestrictedSlot) {
				Rectangle2d area = new Rectangle2d(screen.getGuiLeft() + slot.x, screen.getGuiTop() + slot.y, 16, 16);

				targets.add(new Target<I>() {
					@Override
					public Rectangle2d getArea() {
						return area;
					}

					@Override
					public void accept(I ingredient) {
						screen.tileEntity.getContents().set(slot.index, (ItemStack) ingredient);
						SecurityCraft.channel.sendToServer(new SetGhostSlot(slot.index, (ItemStack) ingredient));
					}
				});
			}
		}

		return targets;
	}

	@Override
	public void onComplete() {}
}

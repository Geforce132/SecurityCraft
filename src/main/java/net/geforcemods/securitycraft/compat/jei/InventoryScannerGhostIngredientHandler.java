package net.geforcemods.securitycraft.compat.jei;

import java.util.ArrayList;
import java.util.List;

import mezz.jei.api.gui.handlers.IGhostIngredientHandler;
import mezz.jei.api.ingredients.ITypedIngredient;
import net.geforcemods.securitycraft.inventory.OwnerRestrictedSlot;
import net.geforcemods.securitycraft.network.server.SetGhostSlot;
import net.geforcemods.securitycraft.screen.InventoryScannerScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

public class InventoryScannerGhostIngredientHandler implements IGhostIngredientHandler<InventoryScannerScreen> {
	@Override
	public <I> List<Target<I>> getTargetsTyped(InventoryScannerScreen screen, ITypedIngredient<I> ingredient, boolean doStart) {
		if (!screen.be.isOwnedBy(Minecraft.getInstance().player))
			return List.of();

		List<Target<I>> targets = new ArrayList<>();

		for (Slot slot : screen.getMenu().slots) {
			if (slot instanceof OwnerRestrictedSlot) {
				Rect2i area = new Rect2i(screen.getGuiLeft() + slot.x, screen.getGuiTop() + slot.y, 16, 16);

				targets.add(new Target<>() {
					@Override
					public Rect2i getArea() {
						return area;
					}

					@Override
					public void accept(I ingredient) {
						screen.be.getContents().set(slot.index, (ItemStack) ingredient);
						PacketDistributor.sendToServer(new SetGhostSlot(slot.index, (ItemStack) ingredient));
					}
				});
			}
		}

		return targets;
	}

	@Override
	public void onComplete() {}
}

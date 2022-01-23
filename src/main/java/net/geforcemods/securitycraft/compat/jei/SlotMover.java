package net.geforcemods.securitycraft.compat.jei;

import java.util.List;

import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import net.geforcemods.securitycraft.util.IHasExtraAreas;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class SlotMover<M extends AbstractContainerMenu, T extends AbstractContainerScreen<M> & IHasExtraAreas> implements IGuiContainerHandler<T> {
	@Override
	public List<Rect2i> getGuiExtraAreas(T containerScreen) {
		return containerScreen.getExtraAreas();
	}
}

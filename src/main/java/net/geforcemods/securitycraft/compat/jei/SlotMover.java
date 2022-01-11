package net.geforcemods.securitycraft.compat.jei;

import java.util.List;

import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import net.geforcemods.securitycraft.util.IHasExtraAreas;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;

public class SlotMover<T extends AbstractContainerScreen<?> & IHasExtraAreas> implements IGuiContainerHandler<T> {
	@Override
	public List<Rect2i> getGuiExtraAreas(T containerScreen) {
		return containerScreen.getExtraAreas();
	}
}

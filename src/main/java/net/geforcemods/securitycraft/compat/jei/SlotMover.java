package net.geforcemods.securitycraft.compat.jei;

import java.util.List;

import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import net.geforcemods.securitycraft.util.IHasExtraAreas;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.Rectangle2d;
import net.minecraft.inventory.container.Container;

public class SlotMover<M extends Container, T extends ContainerScreen<M> & IHasExtraAreas> implements IGuiContainerHandler<T> {
	@Override
	public List<Rectangle2d> getGuiExtraAreas(T containerScreen) {
		return containerScreen.getExtraAreas();
	}
}

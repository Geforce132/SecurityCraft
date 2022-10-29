package net.geforcemods.securitycraft.compat.jei;

import java.awt.Rectangle;
import java.util.List;

import mezz.jei.api.gui.IAdvancedGuiHandler;
import net.geforcemods.securitycraft.util.IHasExtraAreas;
import net.minecraft.client.gui.inventory.GuiContainer;

public class SlotMover<T extends GuiContainer & IHasExtraAreas> implements IAdvancedGuiHandler<T> {
	private final Class<T> type;

	public SlotMover(Class<T> type) {
		this.type = type;
	}

	@Override
	public Class<T> getGuiContainerClass() {
		return type;
	}

	@Override
	public List<Rectangle> getGuiExtraAreas(T guiContainer) {
		return guiContainer.getGuiExtraAreas();
	}
}

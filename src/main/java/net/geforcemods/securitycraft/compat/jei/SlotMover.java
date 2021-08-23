package net.geforcemods.securitycraft.compat.jei;

import java.util.List;

import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import net.geforcemods.securitycraft.screen.CustomizeBlockScreen;
import net.minecraft.client.renderer.Rect2i;

public class SlotMover implements IGuiContainerHandler<CustomizeBlockScreen>
{
	@Override
	public List<Rect2i> getGuiExtraAreas(CustomizeBlockScreen guiContainer)
	{
		return guiContainer.getGuiExtraAreas();
	}
}

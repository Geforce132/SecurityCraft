package net.geforcemods.securitycraft.compat.jei;

import java.util.List;

import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import net.geforcemods.securitycraft.screen.CustomizeBlockScreen;
import net.minecraft.client.renderer.Rectangle2d;

public class SlotMover implements IGuiContainerHandler<CustomizeBlockScreen>
{
	@Override
	public List<Rectangle2d> getGuiExtraAreas(CustomizeBlockScreen guiContainer)
	{
		return guiContainer.getGuiExtraAreas();
	}
}

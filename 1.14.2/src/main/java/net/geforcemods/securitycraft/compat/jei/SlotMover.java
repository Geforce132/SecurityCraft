package net.geforcemods.securitycraft.compat.jei;

import java.util.List;

import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import net.geforcemods.securitycraft.gui.GuiCustomizeBlock;
import net.minecraft.client.renderer.Rectangle2d;

public class SlotMover implements IGuiContainerHandler<GuiCustomizeBlock>
{
	@Override
	public List<Rectangle2d> getGuiExtraAreas(GuiCustomizeBlock guiContainer)
	{
		return guiContainer.getGuiExtraAreas();
	}
}

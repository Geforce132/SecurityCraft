package net.geforcemods.securitycraft.compat.jei;

import java.awt.Rectangle;
import java.util.List;

import mezz.jei.api.gui.IAdvancedGuiHandler;
import net.geforcemods.securitycraft.gui.GuiCustomizeBlock;

public class SlotMover implements IAdvancedGuiHandler<GuiCustomizeBlock>
{
	@Override
	public Class<GuiCustomizeBlock> getGuiContainerClass()
	{
		return GuiCustomizeBlock.class;
	}

	@Override
	public List<Rectangle> getGuiExtraAreas(GuiCustomizeBlock guiContainer)
	{
		return guiContainer.getGuiExtraAreas();
	}
}

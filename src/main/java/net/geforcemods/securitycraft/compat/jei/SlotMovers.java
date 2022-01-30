package net.geforcemods.securitycraft.compat.jei;

import java.awt.Rectangle;
import java.util.List;

import mezz.jei.api.gui.IAdvancedGuiHandler;
import net.geforcemods.securitycraft.gui.GuiCustomizeBlock;
import net.geforcemods.securitycraft.gui.GuiDisguiseModule;
import net.geforcemods.securitycraft.gui.GuiProjector;

public class SlotMovers {
	public static class CustomizeBlock implements IAdvancedGuiHandler<GuiCustomizeBlock> {
		@Override
		public Class<GuiCustomizeBlock> getGuiContainerClass() {
			return GuiCustomizeBlock.class;
		}

		@Override
		public List<Rectangle> getGuiExtraAreas(GuiCustomizeBlock guiContainer) {
			return guiContainer.getGuiExtraAreas();
		}
	}

	public static class Projector implements IAdvancedGuiHandler<GuiProjector> {
		@Override
		public Class<GuiProjector> getGuiContainerClass() {
			return GuiProjector.class;
		}

		@Override
		public List<Rectangle> getGuiExtraAreas(GuiProjector guiContainer) {
			return guiContainer.getExtraAreas();
		}
	}

	public static class DisguiseModule implements IAdvancedGuiHandler<GuiDisguiseModule> {
		@Override
		public Class<GuiDisguiseModule> getGuiContainerClass() {
			return GuiDisguiseModule.class;
		}

		@Override
		public List<Rectangle> getGuiExtraAreas(GuiDisguiseModule guiContainer) {
			return guiContainer.getExtraAreas();
		}
	}
}

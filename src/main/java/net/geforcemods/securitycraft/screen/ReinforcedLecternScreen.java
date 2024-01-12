package net.geforcemods.securitycraft.screen;

import net.geforcemods.securitycraft.inventory.ReinforcedLecternMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.LecternScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.LecternMenu;

public class ReinforcedLecternScreen extends LecternScreen {
	public ReinforcedLecternScreen(LecternMenu menu, Inventory playerInventory, Component title) {
		super(menu, playerInventory, title);
	}

	@Override
	protected void createMenuControls() {
		if (((ReinforcedLecternMenu) getMenu()).be.isOwnedBy(Minecraft.getInstance().player))
			super.createMenuControls();
		else
			addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> onClose()).bounds(width / 2 - 100, 196, 200, 20).build());
	}
}

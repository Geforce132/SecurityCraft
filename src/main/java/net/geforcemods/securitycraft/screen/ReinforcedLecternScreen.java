package net.geforcemods.securitycraft.screen;

import net.geforcemods.securitycraft.blockentities.ReinforcedLecternBlockEntity;
import net.geforcemods.securitycraft.inventory.ReinforcedLecternMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.LecternScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.LecternContainer;
import net.minecraft.util.text.ITextComponent;

public class ReinforcedLecternScreen extends LecternScreen {
	private final ReinforcedLecternBlockEntity be;
	private final boolean canTurnPage;

	public ReinforcedLecternScreen(LecternContainer menu, PlayerInventory playerInventory, ITextComponent title) {
		super(menu, playerInventory, title);
		be = ((ReinforcedLecternMenu) getMenu()).be;
		canTurnPage = be.isOwnedBy(Minecraft.getInstance().player) || !be.isPageLocked();
	}

	@Override
	protected void createMenuControls() {
		if (be.isOwnedBy(Minecraft.getInstance().player))
			super.createMenuControls();
		else
			addButton(new Button(width / 2 - 100, 196, 200, 20, DialogTexts.GUI_DONE, button -> minecraft.setScreen(null)));
	}

	@Override
	public void updateButtonVisibility() {
		super.updateButtonVisibility();
		forwardButton.visible &= canTurnPage;
		backButton.visible &= canTurnPage;
	}

	@Override
	protected void pageForward() {
		if (canTurnPage)
			super.pageForward();
	}

	@Override
	protected void pageBack() {
		if (canTurnPage)
			super.pageBack();
	}
}

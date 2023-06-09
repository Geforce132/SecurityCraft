package net.geforcemods.securitycraft.screen;

import com.mojang.blaze3d.platform.InputConstants;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.network.server.CheckBriefcasePasscode;
import net.geforcemods.securitycraft.network.server.SetBriefcasePasscodeAndOwner;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class BriefcasePasscodeScreen extends Screen {
	public static final String UP_ARROW = "\u2191";
	public static final String RIGHT_ARROW = "\u2192";
	public static final String DOWN_ARROW = "\u2193";
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private final boolean isSetup;
	private int imageWidth = 176;
	private int imageHeight = 166;
	private int leftPos;
	private int topPos;
	private EditBox[] keycodeTextboxes = new EditBox[4];
	private int[] digits = {
			0, 0, 0, 0
	};

	public BriefcasePasscodeScreen(Component title, boolean isSetup) {
		super(title);
		this.isSetup = isSetup;
	}

	@Override
	public void init() {
		super.init();

		leftPos = (width - imageWidth) / 2;
		topPos = (height - imageHeight) / 2;

		for (int i = 0; i < 4; i++) {
			final int id = i;

			addRenderableWidget(new Button(width / 2 - 40 + (i * 20), height / 2 - 52, 20, 20, Component.literal(UP_ARROW), b -> keycodeButtonClicked(id), Button.DEFAULT_NARRATION));
			addRenderableWidget(new Button(width / 2 - 40 + (i * 20), height / 2, 20, 20, Component.literal(DOWN_ARROW), b -> keycodeButtonClicked(4 + id), Button.DEFAULT_NARRATION));
			//text boxes are not added via addRenderableWidget because they should not be accessible by the player
			keycodeTextboxes[i] = addRenderableOnly(new EditBox(font, (width / 2 - 37) + (i * 20), height / 2 - 22, 14, 12, Component.empty()));
			keycodeTextboxes[i].setMaxLength(1);
			keycodeTextboxes[i].setValue("0");
		}

		addRenderableWidget(new Button((width / 2 + 42), height / 2 - 26, 20, 20, Component.literal(RIGHT_ARROW), this::continueButtonClicked, Button.DEFAULT_NARRATION));
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		renderBackground(guiGraphics);
		guiGraphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
		super.render(guiGraphics, mouseX, mouseY, partialTick);
		guiGraphics.drawString(font, title, width / 2 - font.width(title) / 2, topPos + 6, 4210752, false);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (minecraft.options.keyInventory.isActiveAndMatches(InputConstants.getKey(keyCode, scanCode))) {
			onClose();
			return true;
		}

		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	private void continueButtonClicked(Button button) {
		ItemStack briefcase = PlayerUtils.getSelectedItemStack(ClientHandler.getClientPlayer(), SCContent.BRIEFCASE.get());

		if (!briefcase.isEmpty()) {
			String passcode = digits[0] + "" + digits[1] + "" + digits[2] + "" + digits[3];

			if (isSetup) {
				SecurityCraft.channel.sendToServer(new SetBriefcasePasscodeAndOwner(passcode));
				ClientHandler.displayBriefcasePasscodeScreen(briefcase.getHoverName());
			}
			else
				SecurityCraft.channel.sendToServer(new CheckBriefcasePasscode(passcode));
		}
	}

	private void keycodeButtonClicked(int id) {
		int index = id % 4;

		//java's modulo operator % does not handle negative numbers like it should for some reason, so floorMod needs to be used
		digits[index] = Math.floorMod((id > 3 ? --digits[index] : ++digits[index]), 10);
		keycodeTextboxes[index].setValue(String.valueOf(digits[index]));
	}
}

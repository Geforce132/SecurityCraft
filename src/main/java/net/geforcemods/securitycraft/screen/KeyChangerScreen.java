package net.geforcemods.securitycraft.screen;

import com.mojang.blaze3d.platform.InputConstants;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IPasscodeProtected;
import net.geforcemods.securitycraft.network.server.SetPasscode;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.CommonColors;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

public class KeyChangerScreen extends Screen {
	private static final ResourceLocation TEXTURE = SecurityCraft.resLoc("textures/gui/container/blank.png");
	private final Component ukcName = Utils.localize(SCContent.UNIVERSAL_KEY_CHANGER.get().getDescriptionId());
	private final Component enterPasscode = Utils.localize("gui.securitycraft:universalKeyChanger.enterNewPasscode");
	private final Component confirmPasscode = Utils.localize("gui.securitycraft:universalKeyChanger.confirmNewPasscode");
	private int imageWidth = 176;
	private int imageHeight = 166;
	private int leftPos;
	private int topPos;
	private EditBox textboxNewPasscode;
	private EditBox textboxConfirmPasscode;
	private Button confirmButton;
	private IPasscodeProtected passcodeProtected;

	public KeyChangerScreen(IPasscodeProtected passcodeProtected) {
		super(Component.translatable(SCContent.UNIVERSAL_KEY_CHANGER.get().getDescriptionId()));
		this.passcodeProtected = passcodeProtected;
	}

	@Override
	public void init() {
		super.init();

		leftPos = (width - imageWidth) / 2;
		topPos = (height - imageHeight) / 2;

		confirmButton = addRenderableWidget(new Button(width / 2 - 52, height / 2 + 52, 100, 20, Utils.localize("gui.securitycraft:universalKeyChanger.confirm"), this::confirmButtonClicked, Button.DEFAULT_NARRATION));
		confirmButton.active = false;

		textboxNewPasscode = addRenderableWidget(new EditBox(font, width / 2 - 57, height / 2 - 47, 110, 12, Component.empty()));
		textboxNewPasscode.setMaxLength(20);
		setInitialFocus(textboxNewPasscode);
		textboxNewPasscode.setFilter(s -> s.matches("\\d*"));
		textboxNewPasscode.setResponder(s -> updateConfirmButtonState());

		textboxConfirmPasscode = addRenderableWidget(new EditBox(font, width / 2 - 57, height / 2 - 7, 110, 12, Component.empty()));
		textboxConfirmPasscode.setMaxLength(20);
		textboxConfirmPasscode.setFilter(s -> s.matches("\\d*"));
		textboxConfirmPasscode.setResponder(s -> updateConfirmButtonState());
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		super.render(guiGraphics, mouseX, mouseY, partialTick);
		guiGraphics.drawString(font, ukcName, width / 2 - font.width(ukcName) / 2, topPos + 6, CommonColors.DARK_GRAY, false);
		guiGraphics.drawString(font, enterPasscode, width / 2 - font.width(enterPasscode) / 2, topPos + 25, CommonColors.DARK_GRAY, false);
		guiGraphics.drawString(font, confirmPasscode, width / 2 - font.width(confirmPasscode) / 2, topPos + 65, CommonColors.DARK_GRAY, false);
	}

	@Override
	public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		renderTransparentBackground(guiGraphics);
		guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos, topPos, 0.0F, 0.0F, imageWidth, imageHeight, 256, 256);
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

	private void updateConfirmButtonState() {
		String newPasscode = textboxNewPasscode.getValue();
		String passcodeConfirmation = textboxConfirmPasscode.getValue();

		confirmButton.active = passcodeConfirmation != null && newPasscode != null && !passcodeConfirmation.isEmpty() && !newPasscode.isEmpty() && newPasscode.equals(passcodeConfirmation);
	}

	private void confirmButtonClicked(Button button) {
		if (passcodeProtected instanceof BlockEntity be)
			ClientPacketDistributor.sendToServer(new SetPasscode(be.getBlockPos(), textboxNewPasscode.getValue()));
		else if (passcodeProtected instanceof Entity entity)
			ClientPacketDistributor.sendToServer(new SetPasscode(entity.getId(), textboxNewPasscode.getValue()));

		Minecraft.getInstance().player.closeContainer();
		PlayerUtils.sendMessageToPlayer(Minecraft.getInstance().player, Utils.localize(SCContent.UNIVERSAL_KEY_CHANGER.get().getDescriptionId()), Utils.localize("messages.securitycraft:universalKeyChanger.passcodeChanged"), ChatFormatting.GREEN, true);
	}
}

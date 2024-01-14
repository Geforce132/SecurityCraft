package net.geforcemods.securitycraft.screen;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.network.server.SetPasscode;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.client.gui.widget.ExtendedButton;

public class KeyChangerScreen extends Screen {
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private final TranslatableComponent ukcName = Utils.localize(SCContent.UNIVERSAL_KEY_CHANGER.get().getDescriptionId());
	private final TranslatableComponent enterPasscode = Utils.localize("gui.securitycraft:universalKeyChanger.enterNewPasscode");
	private final TranslatableComponent confirmPasscode = Utils.localize("gui.securitycraft:universalKeyChanger.confirmNewPasscode");
	private int imageWidth = 176;
	private int imageHeight = 166;
	private int leftPos;
	private int topPos;
	private EditBox textboxNewPasscode;
	private EditBox textboxConfirmPasscode;
	private Button confirmButton;
	private BlockEntity be;

	public KeyChangerScreen(BlockEntity be) {
		super(new TranslatableComponent(SCContent.UNIVERSAL_KEY_CHANGER.get().getDescriptionId()));
		this.be = be;
	}

	@Override
	public void init() {
		super.init();

		leftPos = (width - imageWidth) / 2;
		topPos = (height - imageHeight) / 2;

		minecraft.keyboardHandler.setSendRepeatsToGui(true);
		confirmButton = addRenderableWidget(new ExtendedButton(width / 2 - 52, height / 2 + 52, 100, 20, Utils.localize("gui.securitycraft:universalKeyChanger.confirm"), this::confirmButtonClicked));
		confirmButton.active = false;

		textboxNewPasscode = addRenderableWidget(new EditBox(font, width / 2 - 57, height / 2 - 47, 110, 12, TextComponent.EMPTY));
		textboxNewPasscode.setMaxLength(20);
		setInitialFocus(textboxNewPasscode);
		textboxNewPasscode.setFilter(s -> s.matches("\\d*"));
		textboxNewPasscode.setResponder(s -> updateConfirmButtonState());

		textboxConfirmPasscode = addRenderableWidget(new EditBox(font, width / 2 - 57, height / 2 - 7, 110, 12, TextComponent.EMPTY));
		textboxConfirmPasscode.setMaxLength(20);
		textboxConfirmPasscode.setFilter(s -> s.matches("\\d*"));
		textboxConfirmPasscode.setResponder(s -> updateConfirmButtonState());
	}

	@Override
	public void removed() {
		super.removed();
		minecraft.keyboardHandler.setSendRepeatsToGui(false);
	}

	@Override
	public void render(PoseStack pose, int mouseX, int mouseY, float partialTick) {
		renderBackground(pose);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem._setShaderTexture(0, TEXTURE);
		blit(pose, leftPos, topPos, 0, 0, imageWidth, imageHeight);
		super.render(pose, mouseX, mouseY, partialTick);
		font.draw(pose, ukcName, width / 2 - font.width(ukcName) / 2, topPos + 6, 4210752);
		font.draw(pose, enterPasscode, width / 2 - font.width(enterPasscode) / 2, topPos + 25, 4210752);
		font.draw(pose, confirmPasscode, width / 2 - font.width(confirmPasscode) / 2, topPos + 65, 4210752);
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
		SecurityCraft.CHANNEL.sendToServer(new SetPasscode(be.getBlockPos(), textboxNewPasscode.getValue()));
		Minecraft.getInstance().player.closeContainer();
		PlayerUtils.sendMessageToPlayer(Minecraft.getInstance().player, Utils.localize(SCContent.UNIVERSAL_KEY_CHANGER.get().getDescriptionId()), Utils.localize("messages.securitycraft:universalKeyChanger.passcodeChanged"), ChatFormatting.GREEN, true);
	}
}

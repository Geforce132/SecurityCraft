package net.geforcemods.securitycraft.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.inventory.GenericTEMenu;
import net.geforcemods.securitycraft.network.server.SetPassword;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fmlclient.gui.widget.ExtendedButton;

public class KeyChangerScreen extends AbstractContainerScreen<GenericTEMenu> {
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private final TranslatableComponent ukcName = Utils.localize(SCContent.UNIVERSAL_KEY_CHANGER.get().getDescriptionId());
	private final TranslatableComponent enterPasscode = Utils.localize("gui.securitycraft:universalKeyChanger.enterNewPasscode");
	private final TranslatableComponent confirmPasscode = Utils.localize("gui.securitycraft:universalKeyChanger.confirmNewPasscode");
	private EditBox textboxNewPasscode;
	private EditBox textboxConfirmPasscode;
	private Button confirmButton;
	private BlockEntity be;

	public KeyChangerScreen(GenericTEMenu menu, Inventory inv, Component text) {
		super(menu, inv, text);
		be = menu.be;
	}

	@Override
	public void init() {
		super.init();
		minecraft.keyboardHandler.setSendRepeatsToGui(true);
		addRenderableWidget(confirmButton = new ExtendedButton(width / 2 - 52, height / 2 + 52, 100, 20, Utils.localize("gui.securitycraft:universalKeyChanger.confirm"), this::confirmButtonClicked));
		confirmButton.active = false;

		addRenderableWidget(textboxNewPasscode = new EditBox(font, width / 2 - 57, height / 2 - 47, 110, 12, TextComponent.EMPTY));
		textboxNewPasscode.setMaxLength(20);
		setInitialFocus(textboxNewPasscode);
		textboxNewPasscode.setFilter(s -> s.matches("[0-9]*"));
		textboxNewPasscode.setResponder(s -> updateConfirmButtonState());

		addRenderableWidget(textboxConfirmPasscode = new EditBox(font, width / 2 - 57, height / 2 - 7, 110, 12, TextComponent.EMPTY));
		textboxConfirmPasscode.setMaxLength(20);
		textboxConfirmPasscode.setFilter(s -> s.matches("[0-9]*"));
		textboxConfirmPasscode.setResponder(s -> updateConfirmButtonState());
	}

	@Override
	public void removed() {
		super.removed();
		minecraft.keyboardHandler.setSendRepeatsToGui(false);
	}

	@Override
	protected void renderLabels(PoseStack pose, int mouseX, int mouseY) {
		font.draw(pose, ukcName, imageWidth / 2 - font.width(ukcName) / 2, 6, 4210752);
		font.draw(pose, enterPasscode, imageWidth / 2 - font.width(enterPasscode) / 2, 25, 4210752);
		font.draw(pose, confirmPasscode, imageWidth / 2 - font.width(confirmPasscode) / 2, 65, 4210752);
	}

	@Override
	protected void renderBg(PoseStack pose, float partialTicks, int mouseX, int mouseY) {
		int startX = (width - imageWidth) / 2;
		int startY = (height - imageHeight) / 2;

		renderBackground(pose);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem._setShaderTexture(0, TEXTURE);
		blit(pose, startX, startY, 0, 0, imageWidth, imageHeight);
	}

	private void updateConfirmButtonState() {
		String newPasscode = textboxNewPasscode.getValue();
		String confirmPasscode = textboxConfirmPasscode.getValue();

		confirmButton.active = confirmPasscode != null && newPasscode != null && !confirmPasscode.isEmpty() && !newPasscode.isEmpty() && newPasscode.equals(confirmPasscode);
	}

	private void confirmButtonClicked(Button button) {
		((IPasswordProtected) be).setPassword(textboxNewPasscode.getValue());
		SecurityCraft.channel.sendToServer(new SetPassword(be.getBlockPos().getX(), be.getBlockPos().getY(), be.getBlockPos().getZ(), textboxNewPasscode.getValue()));
		Minecraft.getInstance().player.closeContainer();
		PlayerUtils.sendMessageToPlayer(Minecraft.getInstance().player, Utils.localize(SCContent.UNIVERSAL_KEY_CHANGER.get().getDescriptionId()), Utils.localize("messages.securitycraft:universalKeyChanger.passcodeChanged"), ChatFormatting.GREEN, true);
	}
}

package net.geforcemods.securitycraft.screen;

import com.mojang.blaze3d.systems.RenderSystem;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.containers.GenericTEContainer;
import net.geforcemods.securitycraft.network.server.SetPassword;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

@OnlyIn(Dist.CLIENT)
public class KeyChangerScreen extends ContainerScreen<GenericTEContainer> {
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private final String ukcName = Utils.localize(SCContent.UNIVERSAL_KEY_CHANGER.get().getDescriptionId()).getColoredString();
	private final String enterPasscode = Utils.localize("gui.securitycraft:universalKeyChanger.enterNewPasscode").getColoredString();
	private final String confirmPasscode = Utils.localize("gui.securitycraft:universalKeyChanger.confirmNewPasscode").getColoredString();
	private TextFieldWidget textboxNewPasscode;
	private TextFieldWidget textboxConfirmPasscode;
	private Button confirmButton;
	private TileEntity tileEntity;

	public KeyChangerScreen(GenericTEContainer container, PlayerInventory inv, ITextComponent name) {
		super(container, inv, name);
		tileEntity = container.te;
	}

	@Override
	public void init() {
		super.init();
		minecraft.keyboardHandler.setSendRepeatsToGui(true);
		addButton(confirmButton = new ExtendedButton(width / 2 - 52, height / 2 + 52, 100, 20, Utils.localize("gui.securitycraft:universalKeyChanger.confirm").getColoredString(), this::confirmButtonClicked));
		confirmButton.active = false;

		addButton(textboxNewPasscode = new TextFieldWidget(font, width / 2 - 57, height / 2 - 47, 110, 12, ""));
		textboxNewPasscode.setMaxLength(20);
		setInitialFocus(textboxNewPasscode);
		textboxNewPasscode.setFilter(s -> s.matches("[0-9]*"));
		textboxNewPasscode.setResponder(s -> updateConfirmButtonState());

		addButton(textboxConfirmPasscode = new TextFieldWidget(font, width / 2 - 57, height / 2 - 7, 110, 12, ""));
		textboxConfirmPasscode.setMaxLength(20);
		textboxConfirmPasscode.setFilter(s -> s.matches("[0-9]*"));
		textboxConfirmPasscode.setResponder(s -> updateConfirmButtonState());
	}

	@Override
	public void onClose() {
		super.onClose();
		minecraft.keyboardHandler.setSendRepeatsToGui(false);
	}

	@Override
	protected void renderLabels(int mouseX, int mouseY) {
		font.draw(ukcName, imageWidth / 2 - font.width(ukcName) / 2, 6, 4210752);
		font.draw(enterPasscode, imageWidth / 2 - font.width(enterPasscode) / 2, 25, 4210752);
		font.draw(confirmPasscode, imageWidth / 2 - font.width(confirmPasscode) / 2, 65, 4210752);
	}

	@Override
	protected void renderBg(float partialTicks, int mouseX, int mouseY) {
		renderBackground();
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bind(TEXTURE);
		blit(leftPos, topPos, 0, 0, imageWidth, imageHeight);
	}

	private void updateConfirmButtonState() {
		String newPasscode = textboxNewPasscode.getValue();
		String confirmPasscode = textboxConfirmPasscode.getValue();

		confirmButton.active = confirmPasscode != null && newPasscode != null && !confirmPasscode.isEmpty() && !newPasscode.isEmpty() && newPasscode.equals(confirmPasscode);
	}

	private void confirmButtonClicked(Button button) {
		((IPasswordProtected) tileEntity).setPassword(textboxNewPasscode.getValue());
		SecurityCraft.channel.sendToServer(new SetPassword(tileEntity.getBlockPos().getX(), tileEntity.getBlockPos().getY(), tileEntity.getBlockPos().getZ(), textboxNewPasscode.getValue()));

		Minecraft.getInstance().player.closeContainer();
		PlayerUtils.sendMessageToPlayer(Minecraft.getInstance().player, Utils.localize(SCContent.UNIVERSAL_KEY_CHANGER.get().getDescriptionId()), Utils.localize("messages.securitycraft:universalKeyChanger.passcodeChanged"), TextFormatting.GREEN, true);
	}
}

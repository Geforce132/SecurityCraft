package net.geforcemods.securitycraft.screen;

import com.mojang.blaze3d.systems.RenderSystem;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.containers.GenericTEContainer;
import net.geforcemods.securitycraft.network.server.SetPassword;
import net.geforcemods.securitycraft.screen.components.IdButton;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class KeyChangerScreen extends ContainerScreen<GenericTEContainer> {

	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private final String ukcName = Utils.localize(SCContent.UNIVERSAL_KEY_CHANGER.get().getTranslationKey()).getFormattedText();
	private final String enterPasscode = Utils.localize("gui.securitycraft:universalKeyChanger.enterNewPasscode").getFormattedText();
	private final String confirmPasscode = Utils.localize("gui.securitycraft:universalKeyChanger.confirmNewPasscode").getFormattedText();
	private TextFieldWidget textboxNewPasscode;
	private TextFieldWidget textboxConfirmPasscode;
	private IdButton confirmButton;
	private TileEntity tileEntity;

	public KeyChangerScreen(GenericTEContainer container, PlayerInventory inv, ITextComponent name) {
		super(container, inv, name);
		tileEntity = container.te;
	}

	@Override
	public void init(){
		super.init();
		minecraft.keyboardListener.enableRepeatEvents(true);
		addButton(confirmButton = new IdButton(0, width / 2 - 52, height / 2 + 52, 100, 20, Utils.localize("gui.securitycraft:universalKeyChanger.confirm").getFormattedText(), this::actionPerformed));
		confirmButton.active = false;

		addButton(textboxNewPasscode = new TextFieldWidget(font, width / 2 - 57, height / 2 - 47, 110, 12, ""));
		textboxNewPasscode.setMaxStringLength(20);
		setFocusedDefault(textboxNewPasscode);
		textboxNewPasscode.setValidator(s -> s.matches("[0-9]*"));
		textboxNewPasscode.setResponder(s -> updateConfirmButtonState());

		addButton(textboxConfirmPasscode = new TextFieldWidget(font, width / 2 - 57, height / 2 - 7, 110, 12, ""));
		textboxConfirmPasscode.setMaxStringLength(20);
		textboxConfirmPasscode.setValidator(s -> s.matches("[0-9]*"));
		textboxConfirmPasscode.setResponder(s -> updateConfirmButtonState());
	}

	@Override
	public void onClose(){
		super.onClose();
		minecraft.keyboardListener.enableRepeatEvents(false);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY){
		font.drawString(ukcName, xSize / 2 - font.getStringWidth(ukcName) / 2, 6, 4210752);
		font.drawString(enterPasscode, xSize / 2 - font.getStringWidth(enterPasscode) / 2, 25, 4210752);
		font.drawString(confirmPasscode, xSize / 2 - font.getStringWidth(confirmPasscode) / 2, 65, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY){
		renderBackground();
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bindTexture(TEXTURE);
		int startX = (width - xSize) / 2;
		int startY = (height - ySize) / 2;
		this.blit(startX, startY, 0, 0, xSize, ySize);
	}

	private void updateConfirmButtonState() {
		String newPasscode = textboxNewPasscode.getText();
		String confirmPasscode = textboxConfirmPasscode.getText();

		confirmButton.active = confirmPasscode != null && newPasscode != null && !confirmPasscode.isEmpty() && !newPasscode.isEmpty() && newPasscode.equals(confirmPasscode);
	}

	protected void actionPerformed(IdButton button){
		((IPasswordProtected) tileEntity).setPassword(textboxNewPasscode.getText());
		SecurityCraft.channel.sendToServer(new SetPassword(tileEntity.getPos().getX(), tileEntity.getPos().getY(), tileEntity.getPos().getZ(), textboxNewPasscode.getText()));

		Minecraft.getInstance().player.closeScreen();
		PlayerUtils.sendMessageToPlayer(Minecraft.getInstance().player, Utils.localize(SCContent.UNIVERSAL_KEY_CHANGER.get().getTranslationKey()), Utils.localize("messages.securitycraft:universalKeyChanger.passcodeChanged"), TextFormatting.GREEN, true);
	}
}

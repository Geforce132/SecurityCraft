package net.geforcemods.securitycraft.screen;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.containers.GenericTEContainer;
import net.geforcemods.securitycraft.network.server.SetPassword;
import net.geforcemods.securitycraft.screen.components.ClickButton;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

@OnlyIn(Dist.CLIENT)
public class KeyChangerScreen extends ContainerScreen<GenericTEContainer> {

	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private final TranslationTextComponent ukcName = ClientUtils.localize(SCContent.UNIVERSAL_KEY_CHANGER.get().getTranslationKey());
	private final TranslationTextComponent enterPasscode = ClientUtils.localize("gui.securitycraft:universalKeyChanger.enterNewPasscode");
	private final TranslationTextComponent confirmPasscode = ClientUtils.localize("gui.securitycraft:universalKeyChanger.confirmNewPasscode");
	private char[] allowedChars = {'0', '1', '2', '3', '4', '5', '6' ,'7' ,'8', '9', '\u0008', '\u001B'}; //0-9, backspace and escape
	private TextFieldWidget textboxNewPasscode;
	private TextFieldWidget textboxConfirmPasscode;
	private ClickButton confirmButton;
	private TileEntity tileEntity;

	public KeyChangerScreen(GenericTEContainer container, PlayerInventory inv, ITextComponent name) {
		super(container, inv, name);
		tileEntity = container.te;
	}

	@Override
	public void init(){
		super.init();
		minecraft.keyboardListener.enableRepeatEvents(true);
		addButton(confirmButton = new ClickButton(0, width / 2 - 52, height / 2 + 52, 100, 20, ClientUtils.localize("gui.securitycraft:universalKeyChanger.confirm"), this::actionPerformed));
		confirmButton.active = false;

		textboxNewPasscode = new TextFieldWidget(font, width / 2 - 57, height / 2 - 47, 110, 12, StringTextComponent.EMPTY);

		textboxNewPasscode.setTextColor(-1);
		textboxNewPasscode.setDisabledTextColour(-1);
		textboxNewPasscode.setEnableBackgroundDrawing(true);
		textboxNewPasscode.setMaxStringLength(20);
		textboxNewPasscode.setFocused2(true);

		textboxConfirmPasscode = new TextFieldWidget(font, width / 2 - 57, height / 2 - 7, 110, 12, StringTextComponent.EMPTY);

		textboxConfirmPasscode.setTextColor(-1);
		textboxConfirmPasscode.setDisabledTextColour(-1);
		textboxConfirmPasscode.setEnableBackgroundDrawing(true);
		textboxConfirmPasscode.setMaxStringLength(20);

	}

	@Override
	public void onClose(){
		super.onClose();
		minecraft.keyboardListener.enableRepeatEvents(false);
	}

	@Override
	public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks){
		super.render(matrix, mouseX, mouseY, partialTicks);
		RenderSystem.disableLighting();
		textboxNewPasscode.render(matrix, mouseX, mouseY, partialTicks);
		textboxConfirmPasscode.render(matrix, mouseX, mouseY, partialTicks);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(MatrixStack matrix, int mouseX, int mouseY){
		font.func_243248_b(matrix, ukcName, xSize / 2 - font.func_238414_a_(ukcName) / 2, 6, 4210752);
		font.func_243248_b(matrix, enterPasscode, xSize / 2 - font.func_238414_a_(enterPasscode) / 2, 25, 4210752);
		font.func_243248_b(matrix, confirmPasscode, xSize / 2 - font.func_238414_a_(confirmPasscode) / 2, 65, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(MatrixStack matrix, float partialTicks, int mouseX, int mouseY){
		renderBackground(matrix);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bindTexture(TEXTURE);
		int startX = (width - xSize) / 2;
		int startY = (height - ySize) / 2;
		this.blit(matrix, startX, startY, 0, 0, xSize, ySize);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers)
	{
		if(keyCode == GLFW.GLFW_KEY_BACKSPACE){
			TextFieldWidget focusedTextField = textboxNewPasscode.isFocused() ? textboxNewPasscode : (textboxConfirmPasscode.isFocused() ? textboxConfirmPasscode : null);

			if(focusedTextField != null && focusedTextField.getText().length() > 0)
			{
				Minecraft.getInstance().player.playSound(ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("random.click")), 0.15F, 1.0F);
				focusedTextField.setText(Utils.removeLastChar(focusedTextField.getText()));
				setConfirmButtonState();
				return true;
			}
		}

		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean charTyped(char typedChar, int keyCode) {
		if(!isValidChar(typedChar))
			return false;

		if(textboxNewPasscode.isFocused())
			textboxNewPasscode.charTyped(typedChar, keyCode);
		else if(textboxConfirmPasscode.isFocused())
			textboxConfirmPasscode.charTyped(typedChar, keyCode);
		else
			return super.charTyped(typedChar, keyCode);

		setConfirmButtonState();
		return true;
	}

	private boolean isValidChar(char c) {
		for(int x = 1; x <= allowedChars.length; x++)
			if(c == allowedChars[x - 1])
				return true;
			else
				continue;

		return false;
	}

	private void setConfirmButtonState() {
		String newPasscode = textboxNewPasscode.getText();

		confirmButton.active = newPasscode != null && newPasscode.equals(textboxConfirmPasscode.getText());
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		textboxNewPasscode.mouseClicked(mouseX, mouseY, mouseButton);
		textboxConfirmPasscode.mouseClicked(mouseX, mouseY, mouseButton);
		return super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	protected void actionPerformed(ClickButton button){
		if(button.id == 0){
			((IPasswordProtected) tileEntity).setPassword(textboxNewPasscode.getText());
			SecurityCraft.channel.sendToServer(new SetPassword(tileEntity.getPos().getX(), tileEntity.getPos().getY(), tileEntity.getPos().getZ(), textboxNewPasscode.getText()));

			ClientUtils.closePlayerScreen();
			PlayerUtils.sendMessageToPlayer(Minecraft.getInstance().player, ClientUtils.localize(SCContent.UNIVERSAL_KEY_CHANGER.get().getTranslationKey()), ClientUtils.localize("messages.securitycraft:universalKeyChanger.passcodeChanged"), TextFormatting.GREEN);
		}
	}

}

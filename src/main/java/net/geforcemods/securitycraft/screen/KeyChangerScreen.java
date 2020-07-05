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
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

@OnlyIn(Dist.CLIENT)
public class KeyChangerScreen extends ContainerScreen<GenericTEContainer> {

	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
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
	public void func_231160_c_(){
		super.func_231160_c_();
		field_230706_i_.keyboardListener.enableRepeatEvents(true);
		func_230480_a_(confirmButton = new ClickButton(0, field_230708_k_ / 2 - 52, field_230709_l_ / 2 + 52, 100, 20, ClientUtils.localize("gui.securitycraft:universalKeyChanger.confirm"), this::actionPerformed));
		confirmButton.field_230693_o_ = false;

		textboxNewPasscode = new TextFieldWidget(field_230712_o_, field_230708_k_ / 2 - 57, field_230709_l_ / 2 - 47, 110, 12, "");

		textboxNewPasscode.setTextColor(-1);
		textboxNewPasscode.setDisabledTextColour(-1);
		textboxNewPasscode.setEnableBackgroundDrawing(true);
		textboxNewPasscode.setMaxStringLength(20);
		textboxNewPasscode.setFocused2(true);

		textboxConfirmPasscode = new TextFieldWidget(field_230712_o_, field_230708_k_ / 2 - 57, field_230709_l_ / 2 - 7, 110, 12, "");

		textboxConfirmPasscode.setTextColor(-1);
		textboxConfirmPasscode.setDisabledTextColour(-1);
		textboxConfirmPasscode.setEnableBackgroundDrawing(true);
		textboxConfirmPasscode.setMaxStringLength(20);

	}

	@Override
	public void func_231175_as__(){
		super.func_231175_as__();
		field_230706_i_.keyboardListener.enableRepeatEvents(false);
	}

	@Override
	public void func_230430_a_(MatrixStack matrix, int mouseX, int mouseY, float partialTicks){
		super.func_230430_a_(matrix, mouseX, mouseY, partialTicks);
		RenderSystem.disableLighting();
		textboxNewPasscode.func_230430_a_(matrix, mouseX, mouseY, partialTicks);
		textboxConfirmPasscode.func_230430_a_(matrix, mouseX, mouseY, partialTicks);
	}

	@Override
	protected void func_230451_b_(MatrixStack matrix, int mouseX, int mouseY){
		String ukcName = ClientUtils.localize(SCContent.UNIVERSAL_KEY_CHANGER.get().getTranslationKey());

		field_230712_o_.drawString(ukcName, xSize / 2 - field_230712_o_.getStringWidth(ukcName) / 2, 6, 4210752);
		field_230712_o_.drawString(ClientUtils.localize("gui.securitycraft:universalKeyChanger.enterNewPasscode"), xSize / 2 - field_230712_o_.getStringWidth(ClientUtils.localize("gui.securitycraft:universalKeyChanger.enterNewPasscode")) / 2, 25, 4210752);
		field_230712_o_.drawString(ClientUtils.localize("gui.securitycraft:universalKeyChanger.confirmNewPasscode"), xSize / 2 - field_230712_o_.getStringWidth(ClientUtils.localize("gui.securitycraft:universalKeyChanger.confirmNewPasscode")) / 2, 65, 4210752);
	}

	@Override
	protected void func_230450_a_(MatrixStack matrix, float partialTicks, int mouseX, int mouseY){
		func_230446_a_(matrix);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		field_230706_i_.getTextureManager().bindTexture(TEXTURE);
		int startX = (field_230708_k_ - xSize) / 2;
		int startY = (field_230709_l_ - ySize) / 2;
		this.blit(startX, startY, 0, 0, xSize, ySize);
	}

	@Override
	public boolean func_231046_a_(int keyCode, int scanCode, int modifiers)
	{
		if(keyCode == GLFW.GLFW_KEY_BACKSPACE){
			TextFieldWidget focusedTextField = textboxNewPasscode.func_230999_j_() ? textboxNewPasscode : (textboxConfirmPasscode.func_230999_j_() ? textboxConfirmPasscode : null);

			if(focusedTextField != null && focusedTextField.getText().length() > 0)
			{
				Minecraft.getInstance().player.playSound(ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("random.click")), 0.15F, 1.0F);
				focusedTextField.setText(Utils.removeLastChar(focusedTextField.getText()));
				return true;
			}
		}

		return super.func_231046_a_(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean func_231042_a_(char typedChar, int keyCode) {
		if(!isValidChar(typedChar))
			return false;

		if(textboxNewPasscode.func_230999_j_())
			textboxNewPasscode.func_231042_a_(typedChar, keyCode);
		else if(textboxConfirmPasscode.func_230999_j_())
			textboxConfirmPasscode.func_231042_a_(typedChar, keyCode);
		else
			return super.func_231042_a_(typedChar, keyCode);

		checkToEnableSaveButton();
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

	private void checkToEnableSaveButton() {
		String newPasscode = !textboxNewPasscode.getText().isEmpty() ? textboxNewPasscode.getText() : null;
		String confirmedPasscode = !textboxConfirmPasscode.getText().isEmpty() ? textboxConfirmPasscode.getText() : null;

		if(newPasscode == null || confirmedPasscode == null) return;
		if(!newPasscode.equals(confirmedPasscode)) return;

		confirmButton.field_230693_o_ = true;
	}

	@Override
	public boolean func_231044_a_(double mouseX, double mouseY, int mouseButton) {
		textboxNewPasscode.func_231044_a_(mouseX, mouseY, mouseButton);
		textboxConfirmPasscode.func_231044_a_(mouseX, mouseY, mouseButton);
		return super.func_231044_a_(mouseX, mouseY, mouseButton);
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

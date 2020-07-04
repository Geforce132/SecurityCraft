package net.geforcemods.securitycraft.screen;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.systems.RenderSystem;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.containers.GenericTEContainer;
import net.geforcemods.securitycraft.network.server.SetPassword;
import net.geforcemods.securitycraft.screen.components.ClickButton;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

@OnlyIn(Dist.CLIENT)
public class SetPasswordScreen extends ContainerScreen<GenericTEContainer> {

	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private TileEntity tileEntity;
	private char[] allowedChars = {'0', '1', '2', '3', '4', '5', '6' ,'7' ,'8', '9'};
	private String blockName;
	private TextFieldWidget keycodeTextbox;
	private boolean isInvalid = false;
	private ClickButton saveAndContinueButton;

	public SetPasswordScreen(GenericTEContainer container, PlayerInventory inv, ITextComponent name){
		super(container, inv, name);
		this.tileEntity = container.te;
		blockName = ClientUtils.localize(tileEntity.getBlockState().getBlock().getTranslationKey());
	}

	@Override
	public void func_231160_c_(){
		super.func_231160_c_();
		field_230706_i_.keyboardListener.enableRepeatEvents(true);
		func_230480_a_(saveAndContinueButton = new ClickButton(0, field_230708_k_ / 2 - 48, field_230709_l_ / 2 + 30 + 10, 100, 20, !isInvalid ? ClientUtils.localize("gui.securitycraft:keycardSetup.save") : ClientUtils.localize("gui.securitycraft:password.invalidCode"), this::actionPerformed));

		keycodeTextbox = new TextFieldWidget(field_230712_o_, field_230708_k_ / 2 - 37, field_230709_l_ / 2 - 47, 77, 12, "");

		keycodeTextbox.setTextColor(-1);
		keycodeTextbox.setDisabledTextColour(-1);
		keycodeTextbox.setEnableBackgroundDrawing(true);
		keycodeTextbox.setMaxStringLength(11);
		keycodeTextbox.setFocused2(true);

		updateButtonText();
	}

	@Override
	public void func_231175_as__(){
		super.func_231175_as__();
		isInvalid = false;
		field_230706_i_.keyboardListener.enableRepeatEvents(false);
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks){
		super.render(mouseX, mouseY, partialTicks);
		RenderSystem.disableLighting();
		keycodeTextbox.render(mouseX, mouseY, partialTicks);
		drawString(field_230712_o_, "CODE:", field_230708_k_ / 2 - 67, field_230709_l_ / 2 - 47 + 2, 4210752);
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY){
		String setup = ClientUtils.localize("gui.securitycraft:password.setup");
		String combined = blockName + " " + setup;

		if(field_230712_o_.getStringWidth(combined) < xSize - 10)
			field_230712_o_.drawString(combined, xSize / 2 - field_230712_o_.getStringWidth(combined) / 2, 6, 4210752);
		else
		{
			field_230712_o_.drawString(blockName, xSize / 2 - field_230712_o_.getStringWidth(blockName) / 2, 6, 4210752);
			field_230712_o_.drawString(setup, xSize / 2 - field_230712_o_.getStringWidth(setup) / 2, 16, 4210752);
		}
	}

	/**
	 * Draw the background layer for the GuiContainer (everything behind the items)
	 */
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY){
		func_230446_a_();
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		field_230706_i_.getTextureManager().bindTexture(TEXTURE);
		int startX = (field_230708_k_ - xSize) / 2;
		int startY = (field_230709_l_ - ySize) / 2;
		this.blit(startX, startY, 0, 0, xSize, ySize);
	}

	@Override
	public boolean func_231046_a_(int keyCode, int scanCode, int modifiers)
	{
		if(keyCode == GLFW.GLFW_KEY_BACKSPACE && keycodeTextbox.getText().length() > 0){
			Minecraft.getInstance().player.playSound(ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("random.click")), 0.15F, 1.0F);
			keycodeTextbox.setText(keycodeTextbox.getText().substring(0, keycodeTextbox.getText().length() - 1));
			return true;
		}

		return super.func_231046_a_(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean func_231042_a_(char typedChar, int keyCode){
		if(keycodeTextbox.func_230999_j_() && isValidChar(typedChar))
		{
			keycodeTextbox.func_231042_a_(typedChar, keyCode);
			return true;
		}
		else
			return super.func_231042_a_(typedChar, keyCode);
	}

	private boolean isValidChar(char c) {
		for(int i = 0; i < allowedChars.length; i++)
			if(c == allowedChars[i])
				return true;
			else
				continue;

		return false;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		keycodeTextbox.mouseClicked(mouseX, mouseY, mouseButton);
		return super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	private void updateButtonText(){
		saveAndContinueButton.setMessage(!isInvalid ? ClientUtils.localize("gui.securitycraft:keycardSetup.save") : ClientUtils.localize("gui.securitycraft:password.invalidCode"));
	}

	protected void actionPerformed(ClickButton button){
		if(button.id == 0){
			if(keycodeTextbox.getText().isEmpty()){
				isInvalid  = true;
				updateButtonText();
				return;
			}

			((IPasswordProtected) tileEntity).setPassword(keycodeTextbox.getText());
			SecurityCraft.channel.sendToServer(new SetPassword(tileEntity.getPos().getX(), tileEntity.getPos().getY(), tileEntity.getPos().getZ(), keycodeTextbox.getText()));

			ClientUtils.closePlayerScreen();
		}
	}

}

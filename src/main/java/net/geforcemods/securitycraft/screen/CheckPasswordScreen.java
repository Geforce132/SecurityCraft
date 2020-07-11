package net.geforcemods.securitycraft.screen;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.containers.GenericTEContainer;
import net.geforcemods.securitycraft.network.server.CheckPassword;
import net.geforcemods.securitycraft.screen.components.ClickButton;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

@OnlyIn(Dist.CLIENT)
public class CheckPasswordScreen extends ContainerScreen<GenericTEContainer> {

	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private TileEntity tileEntity;
	private char[] allowedChars = {'0', '1', '2', '3', '4', '5', '6' ,'7' ,'8', '9', '\u0008', '\u001B'}; //0-9, backspace and escape
	private TranslationTextComponent blockName;
	private TextFieldWidget keycodeTextbox;
	private String currentString = "";
	private static final int MAX_CHARS = 11;

	public CheckPasswordScreen(GenericTEContainer container, PlayerInventory inv, ITextComponent name){
		super(container, inv, name);
		this.tileEntity = container.te;
		blockName = ClientUtils.localize(tileEntity.getBlockState().getBlock().getTranslationKey());
	}

	@Override
	public void init(){
		super.init();
		minecraft.keyboardListener.enableRepeatEvents(true);

		addButton(new ClickButton(0, width / 2 - 38, height / 2 + 30 + 10, 80, 20, "0", this::actionPerformed));
		addButton(new ClickButton(1, width / 2 - 38, height / 2 - 60 + 10, 20, 20, "1", this::actionPerformed));
		addButton(new ClickButton(2, width / 2 - 8, height / 2 - 60 + 10, 20, 20, "2", this::actionPerformed));
		addButton(new ClickButton(3, width / 2 + 22, height / 2 - 60 + 10, 20, 20, "3", this::actionPerformed));
		addButton(new ClickButton(4, width / 2 - 38, height / 2 - 30 + 10, 20, 20, "4", this::actionPerformed));
		addButton(new ClickButton(5, width / 2 - 8, height / 2 - 30 + 10, 20, 20, "5", this::actionPerformed));
		addButton(new ClickButton(6, width / 2 + 22, height / 2 - 30 + 10, 20, 20, "6", this::actionPerformed));
		addButton(new ClickButton(7, width / 2 - 38, height / 2 + 10, 20, 20, "7", this::actionPerformed));
		addButton(new ClickButton(8, width / 2 - 8, height / 2 + 10, 20, 20, "8", this::actionPerformed));
		addButton(new ClickButton(9, width / 2 + 22, height / 2 + 10, 20, 20, "9", this::actionPerformed));
		addButton(new ClickButton(10, width / 2 + 48, height / 2 + 30 + 10, 25, 20, "<-", this::actionPerformed));

		keycodeTextbox = new TextFieldWidget(font, width / 2 - 37, height / 2 - 67, 77, 12, StringTextComponent.EMPTY);

		keycodeTextbox.setTextColor(-1);
		keycodeTextbox.setDisabledTextColour(-1);
		keycodeTextbox.setEnableBackgroundDrawing(true);
		keycodeTextbox.setMaxStringLength(MAX_CHARS);
		keycodeTextbox.setFocused2(true);
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
		keycodeTextbox.render(matrix, mouseX, mouseY, partialTicks);
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	@Override
	protected void func_230451_b_(MatrixStack matrix, int mouseX, int mouseY){
		font.func_238407_a_(matrix, blockName, xSize / 2 - font.func_238414_a_(blockName) / 2, 6, 4210752);
	}

	/**
	 * Draw the background layer for the GuiContainer (everything behind the items)
	 */
	@Override
	protected void func_230450_a_(MatrixStack matrix, float partialTicks, int mouseX, int mouseY){
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
		if(keyCode == GLFW.GLFW_KEY_BACKSPACE && currentString.length() > 0){
			Minecraft.getInstance().player.playSound(ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("random.click")), 0.15F, 1.0F);
			currentString = Utils.removeLastChar(currentString);
			setTextboxCensoredText(keycodeTextbox, currentString);
			checkCode(currentString);
			return true;
		}

		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean charTyped(char typedChar, int keyCode) {
		if(isValidChar(typedChar) && currentString.length() < MAX_CHARS){
			Minecraft.getInstance().player.playSound(ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("random.click")), 0.15F, 1.0F);
			currentString += typedChar;
			setTextboxCensoredText(keycodeTextbox, currentString);
			checkCode(currentString);
		}
		else
			return super.charTyped(typedChar, keyCode);
		return true;
	}

	private boolean isValidChar(char c) {
		for(int i = 0; i < allowedChars.length; i++)
			if(c == allowedChars[i])
				return true;
			else
				continue;

		return false;
	}

	protected void actionPerformed(ClickButton button){
		if (currentString.length() < MAX_CHARS) {
			if(button.id >= 0 && button.id <= 9) {
				currentString += "" + button.id;
				setTextboxCensoredText(keycodeTextbox, currentString);
				checkCode(currentString);
			}
		}

		if(button.id == 10 && currentString.length() > 0)
		{
			currentString = Utils.removeLastChar(currentString);
			setTextboxCensoredText(keycodeTextbox, currentString);
		}
	}

	private void setTextboxCensoredText(TextFieldWidget textField, String text) {
		String x = "";
		for(int i = 1; i <= text.length(); i++)
			x += "*";

		textField.setText(x);
	}

	public void checkCode(String code) {
		SecurityCraft.channel.sendToServer(new CheckPassword(tileEntity.getPos().getX(), tileEntity.getPos().getY(), tileEntity.getPos().getZ(), code));
	}
}

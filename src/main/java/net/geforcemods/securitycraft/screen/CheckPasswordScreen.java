package net.geforcemods.securitycraft.screen;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.containers.GenericTEContainer;
import net.geforcemods.securitycraft.network.server.CheckPassword;
import net.geforcemods.securitycraft.screen.components.IdButton;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CheckPasswordScreen extends AbstractContainerScreen<GenericTEContainer> {

	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private BlockEntity tileEntity;
	private char[] allowedChars = {'0', '1', '2', '3', '4', '5', '6' ,'7' ,'8', '9', '\u0008', '\u001B'}; //0-9, backspace and escape
	private TranslatableComponent blockName;
	private EditBox keycodeTextbox;
	private String currentString = "";
	private static final int MAX_CHARS = 20;

	public CheckPasswordScreen(GenericTEContainer container, Inventory inv, Component name){
		super(container, inv, name);
		this.tileEntity = container.te;
		blockName = Utils.localize(tileEntity.getBlockState().getBlock().getDescriptionId());
	}

	@Override
	public void init(){
		super.init();
		minecraft.keyboardHandler.setSendRepeatsToGui(true);

		addButton(new IdButton(0, width / 2 - 38, height / 2 + 30 + 10, 80, 20, "0", this::actionPerformed));
		addButton(new IdButton(1, width / 2 - 38, height / 2 - 60 + 10, 20, 20, "1", this::actionPerformed));
		addButton(new IdButton(2, width / 2 - 8, height / 2 - 60 + 10, 20, 20, "2", this::actionPerformed));
		addButton(new IdButton(3, width / 2 + 22, height / 2 - 60 + 10, 20, 20, "3", this::actionPerformed));
		addButton(new IdButton(4, width / 2 - 38, height / 2 - 30 + 10, 20, 20, "4", this::actionPerformed));
		addButton(new IdButton(5, width / 2 - 8, height / 2 - 30 + 10, 20, 20, "5", this::actionPerformed));
		addButton(new IdButton(6, width / 2 + 22, height / 2 - 30 + 10, 20, 20, "6", this::actionPerformed));
		addButton(new IdButton(7, width / 2 - 38, height / 2 + 10, 20, 20, "7", this::actionPerformed));
		addButton(new IdButton(8, width / 2 - 8, height / 2 + 10, 20, 20, "8", this::actionPerformed));
		addButton(new IdButton(9, width / 2 + 22, height / 2 + 10, 20, 20, "9", this::actionPerformed));
		addButton(new IdButton(10, width / 2 + 48, height / 2 + 30 + 10, 25, 20, "<-", this::actionPerformed));

		addButton(keycodeTextbox = new EditBox(font, width / 2 - 37, height / 2 - 67, 77, 12, TextComponent.EMPTY));
		keycodeTextbox.setMaxLength(MAX_CHARS);
		keycodeTextbox.setFilter(s -> s.matches("[0-9]*\\**")); //allow any amount of numbers and any amount of asterisks
		setInitialFocus(keycodeTextbox);
	}

	@Override
	public void removed(){
		super.removed();
		minecraft.keyboardHandler.setSendRepeatsToGui(false);
	}

	@Override
	protected void renderLabels(PoseStack matrix, int mouseX, int mouseY){
		font.draw(matrix, blockName, imageWidth / 2 - font.width(blockName) / 2, 6, 4210752);
	}

	@Override
	protected void renderBg(PoseStack matrix, float partialTicks, int mouseX, int mouseY){
		renderBackground(matrix);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bind(TEXTURE);
		int startX = (width - imageWidth) / 2;
		int startY = (height - imageHeight) / 2;
		this.blit(matrix, startX, startY, 0, 0, imageWidth, imageHeight);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers)
	{
		if(keyCode == GLFW.GLFW_KEY_BACKSPACE && currentString.length() > 0){
			Minecraft.getInstance().player.playSound(SoundEvents.UI_BUTTON_CLICK, 0.15F, 1.0F);
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
			Minecraft.getInstance().player.playSound(SoundEvents.UI_BUTTON_CLICK, 0.15F, 1.0F);
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

	protected void actionPerformed(IdButton button){
		if(currentString.length() < MAX_CHARS && button.id >= 0 && button.id <= 9) {
			currentString += "" + button.id;
			setTextboxCensoredText(keycodeTextbox, currentString);
			checkCode(currentString);
		}
		else if(button.id == 10 && currentString.length() > 0)
		{
			currentString = Utils.removeLastChar(currentString);
			setTextboxCensoredText(keycodeTextbox, currentString);
		}
	}

	private void setTextboxCensoredText(EditBox textField, String text) {
		String x = "";

		for(int i = 1; i <= text.length(); i++)
		{
			x += "*";
		}

		textField.setValue(x);
	}

	public void checkCode(String code) {
		SecurityCraft.channel.sendToServer(new CheckPassword(tileEntity.getBlockPos().getX(), tileEntity.getBlockPos().getY(), tileEntity.getBlockPos().getZ(), code));
	}
}

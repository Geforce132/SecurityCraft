package net.geforcemods.securitycraft.screen;

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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

@OnlyIn(Dist.CLIENT)
public class CheckPasswordScreen extends ContainerScreen<GenericTEContainer> {

	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private TileEntity tileEntity;
	private char[] allowedChars = {'0', '1', '2', '3', '4', '5', '6' ,'7' ,'8', '9', '\u0008', '\u001B'}; //0-9, backspace and escape
	private String blockName;

	private TextFieldWidget keycodeTextbox;
	private String currentString = "";

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

		keycodeTextbox = new TextFieldWidget(font, width / 2 - 37, height / 2 - 67, 77, 12, "");

		keycodeTextbox.setTextColor(-1);
		keycodeTextbox.setDisabledTextColour(-1);
		keycodeTextbox.setEnableBackgroundDrawing(true);
		keycodeTextbox.setMaxStringLength(11);
	}

	@Override
	public void onClose(){
		super.onClose();
		minecraft.keyboardListener.enableRepeatEvents(false);
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks){
		super.render(mouseX, mouseY, partialTicks);
		RenderSystem.disableLighting();
		keycodeTextbox.render(mouseX, mouseY, partialTicks);
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY){
		font.drawString(blockName, xSize / 2 - font.getStringWidth(blockName) / 2, 6, 4210752);
	}

	/**
	 * Draw the background layer for the GuiContainer (everything behind the items)
	 */
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY){
		renderBackground();
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bindTexture(TEXTURE);
		int startX = (width - xSize) / 2;
		int startY = (height - ySize) / 2;
		this.blit(startX, startY, 0, 0, xSize, ySize);
	}

	@Override
	public boolean charTyped(char typedChar, int keyCode) {
		if(isValidChar(typedChar) && typedChar == '\u001B')
			ClientUtils.closePlayerScreen();
		else if(isValidChar(typedChar) && typedChar != ''){
			Minecraft.getInstance().player.playSound(ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("random.click")), 0.15F, 1.0F);
			currentString += typedChar;
			setTextboxCensoredText(keycodeTextbox, currentString);
			checkCode(currentString);
		}else if(isValidChar(typedChar) && typedChar == ''){
			Minecraft.getInstance().player.playSound(ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("random.click")), 0.15F, 1.0F);
			currentString = Utils.removeLastChar(currentString);
			setTextboxCensoredText(keycodeTextbox, currentString);
			checkCode(currentString);
		}
		else
			return super.charTyped(typedChar, keyCode);
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

	protected void actionPerformed(ClickButton button){
		switch(button.id){
			case 0:
				currentString += "0";
				setTextboxCensoredText(keycodeTextbox, currentString);
				checkCode(currentString);
				break;
			case 1:
				currentString += "1";
				setTextboxCensoredText(keycodeTextbox, currentString);
				checkCode(currentString);
				break;
			case 2:
				currentString += "2";
				setTextboxCensoredText(keycodeTextbox, currentString);
				checkCode(currentString);
				break;
			case 3:
				currentString += "3";
				setTextboxCensoredText(keycodeTextbox, currentString);
				checkCode(currentString);
				break;
			case 4:
				currentString += "4";
				setTextboxCensoredText(keycodeTextbox, currentString);
				checkCode(currentString);
				break;
			case 5:
				currentString += "5";
				setTextboxCensoredText(keycodeTextbox, currentString);
				checkCode(currentString);
				break;
			case 6:
				currentString += "6";
				setTextboxCensoredText(keycodeTextbox, currentString);
				checkCode(currentString);
				break;
			case 7:
				currentString += "7";
				setTextboxCensoredText(keycodeTextbox, currentString);
				checkCode(currentString);
				break;
			case 8:
				currentString += "8";
				setTextboxCensoredText(keycodeTextbox, currentString);
				checkCode(currentString);
				break;
			case 9:
				currentString += "9";
				setTextboxCensoredText(keycodeTextbox, currentString);
				checkCode(currentString);
				break;

			case 10:
				currentString = Utils.removeLastChar(currentString);
				setTextboxCensoredText(keycodeTextbox, currentString);
				break;

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

package net.geforcemods.securitycraft.gui;

import java.io.IOException;

import org.lwjgl.input.Keyboard;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.containers.ContainerGeneric;
import net.geforcemods.securitycraft.network.packets.PacketSCheckPassword;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiCheckPassword extends GuiContainer {

	private static final ResourceLocation field_110410_t = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private TileEntity tileEntity;
	private char[] allowedChars = {'0', '1', '2', '3', '4', '5', '6' ,'7' ,'8', '9', '\u0008'}; //0-9, backspace
	private String blockName;

	private GuiTextField keycodeTextbox;
	private String currentString = "";

	public GuiCheckPassword(InventoryPlayer inventoryPlayer, TileEntity tileEntity, Block block){
		super(new ContainerGeneric(inventoryPlayer, tileEntity));
		this.tileEntity = tileEntity;
		blockName = StatCollector.translateToLocal(block.getUnlocalizedName() + ".name");
	}

	@Override
	public void initGui(){
		super.initGui();
		Keyboard.enableRepeatEvents(true);

		buttonList.add(new GuiButton(0, width / 2 - 38, height / 2 + 30 + 10, 80, 20, "0"));
		buttonList.add(new GuiButton(1, width / 2 - 38, height / 2 - 60 + 10, 20, 20, "1"));
		buttonList.add(new GuiButton(2, width / 2 - 8, height / 2 - 60 + 10, 20, 20, "2"));
		buttonList.add(new GuiButton(3, width / 2 + 22, height / 2 - 60 + 10, 20, 20, "3"));
		buttonList.add(new GuiButton(4, width / 2 - 38, height / 2 - 30 + 10, 20, 20, "4"));
		buttonList.add(new GuiButton(5, width / 2 - 8, height / 2 - 30 + 10, 20, 20, "5"));
		buttonList.add(new GuiButton(6, width / 2 + 22, height / 2 - 30 + 10, 20, 20, "6"));
		buttonList.add(new GuiButton(7, width / 2 - 38, height / 2 + 10, 20, 20, "7"));
		buttonList.add(new GuiButton(8, width / 2 - 8, height / 2 + 10, 20, 20, "8"));
		buttonList.add(new GuiButton(9, width / 2 + 22, height / 2 + 10, 20, 20, "9"));
		buttonList.add(new GuiButton(10, width / 2 + 48, height / 2 + 30 + 10, 25, 20, "<-"));

		keycodeTextbox = new GuiTextField(11, fontRendererObj, width / 2 - 37, height / 2 - 67, 77, 12);

		keycodeTextbox.setTextColor(-1);
		keycodeTextbox.setDisabledTextColour(-1);
		keycodeTextbox.setEnableBackgroundDrawing(true);
		keycodeTextbox.setMaxStringLength(11);
	}

	@Override
	public void onGuiClosed(){
		super.onGuiClosed();
		Keyboard.enableRepeatEvents(false);
	}

	@Override
	public void drawScreen(int par1, int par2, float par3){
		super.drawScreen(par1, par2, par3);
		GlStateManager.disableLighting();
		keycodeTextbox.drawTextBox();
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2){
		fontRendererObj.drawString(blockName, xSize / 2 - fontRendererObj.getStringWidth(blockName) / 2, 6, 4210752);
	}

	/**
	 * Draw the background layer for the GuiContainer (everything behind the items)
	 */
	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3){
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(field_110410_t);
		int k = (width - xSize) / 2;
		int l = (height - ySize) / 2;
		this.drawTexturedModalRect(k, l, 0, 0, xSize, ySize);
	}

	@Override
	protected void keyTyped(char par1, int par2) throws IOException {
		if(isValidChar(par1) && par1 != ''){
			Minecraft.getMinecraft().thePlayer.playSound("random.click", 0.15F, 1.0F);
			currentString += par1;
			setTextboxCensoredText(keycodeTextbox, currentString);
			checkCode(currentString);
		}else if(isValidChar(par1) && par1 == ''){
			Minecraft.getMinecraft().thePlayer.playSound("random.click", 0.15F, 1.0F);
			currentString = Utils.removeLastChar(currentString);
			setTextboxCensoredText(keycodeTextbox, currentString);
			checkCode(currentString);
		}
		else
			super.keyTyped(par1, par2);
	}

	private boolean isValidChar(char par1) {
		for(int x = 1; x <= allowedChars.length; x++)
			if(par1 == allowedChars[x - 1])
				return true;
			else
				continue;

		return false;
	}

	@Override
	protected void actionPerformed(GuiButton guibutton){
		switch(guibutton.id){
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

	private void setTextboxCensoredText(GuiTextField textField, String par2) {
		String x = "";
		for(int i = 1; i <= par2.length(); i++)
			x += "*";

		textField.setText(x);
	}

	public void checkCode(String par1String) {
		SecurityCraft.network.sendToServer(new PacketSCheckPassword(tileEntity.getPos().getX(), tileEntity.getPos().getY(), tileEntity.getPos().getZ(), par1String));
	}

}

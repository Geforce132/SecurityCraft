package net.geforcemods.securitycraft.gui;

import java.io.IOException;

import org.lwjgl.input.Keyboard;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.containers.ContainerGeneric;
import net.geforcemods.securitycraft.items.ItemModule;
import net.geforcemods.securitycraft.network.packets.PacketSUpdateNBTTag;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class GuiEditModule extends GuiContainer
{
	private static NBTTagCompound savedModule;
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private ItemStack module;
	private GuiTextField inputField;
	private GuiButton addButton, removeButton, copyButton, pasteButton, clearButton;

	public GuiEditModule(InventoryPlayer inventory, ItemStack item, TileEntity te)
	{
		super(new ContainerGeneric(inventory, te));

		module = item;
	}

	@Override
	public void initGui()
	{
		super.initGui();

		Keyboard.enableRepeatEvents(true);
		inputField = new GuiTextField(5, fontRenderer, width / 2 - 50, height / 2 - 65, 110, 15);
		buttonList.add(addButton = new GuiButton(0, width / 2 - 38, height / 2 - 45, 76, 20, ClientUtils.localize("gui.securitycraft:editModule.add")));
		buttonList.add(removeButton = new GuiButton(1, width / 2 - 38, height / 2 - 20, 76, 20, ClientUtils.localize("gui.securitycraft:editModule.remove")));
		buttonList.add(copyButton = new GuiButton(2, width / 2 - 38, height / 2 + 5, 76, 20, ClientUtils.localize("gui.securitycraft:editModule.copy")));
		buttonList.add(pasteButton = new GuiButton(3, width / 2 - 38, height / 2 + 30, 76, 20, ClientUtils.localize("gui.securitycraft:editModule.paste")));
		buttonList.add(clearButton = new GuiButton(4, width / 2 - 38, height / 2 + 55, 76, 20, ClientUtils.localize("gui.securitycraft:editModule.clear")));

		addButton.enabled = false;
		removeButton.enabled = false;

		if (module.getTagCompound() == null || module.getTagCompound().isEmpty() || (module.getTagCompound() != null && module.getTagCompound().equals(savedModule)))
			copyButton.enabled = false;

		if (savedModule == null || savedModule.isEmpty() || (module.getTagCompound() != null && module.getTagCompound().equals(savedModule)))
			pasteButton.enabled = false;

		if (module.getTagCompound() == null || module.getTagCompound().isEmpty())
			clearButton.enabled = false;

		inputField.setTextColor(-1);
		inputField.setDisabledTextColour(-1);
		inputField.setEnableBackgroundDrawing(true);
		inputField.setMaxStringLength(16);
		inputField.setFocused(true);
	}

	@Override
	public void onGuiClosed(){
		super.onGuiClosed();
		Keyboard.enableRepeatEvents(false);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks){
		super.drawScreen(mouseX, mouseY, partialTicks);
		GlStateManager.disableLighting();
		inputField.drawTextBox();
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY){
		fontRenderer.drawSplitString(ClientUtils.localize("gui.securitycraft:editModule"), xSize / 2 - fontRenderer.getStringWidth(ClientUtils.localize("gui.securitycraft:editModule")) / 2, 6, xSize, 4210752);
	}

	/**
	 * Draw the background layer for the GuiContainer (everything behind the items)
	 */
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY){
		drawDefaultBackground();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(TEXTURE);
		int startX = (width - xSize) / 2;
		int startY = (height - ySize) / 2;
		drawTexturedModalRect(startX, startY, 0, 0, xSize, ySize);
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException{
		if(inputField.isFocused())
		{
			if(keyCode == Keyboard.KEY_SPACE)
				return;

			inputField.textboxKeyTyped(typedChar, keyCode);

			for(int i = 1; i <= ItemModule.MAX_PLAYERS; i++)
			{
				if(module.getTagCompound() != null && module.getTagCompound().getString("Player" + i).equals(inputField.getText())) {
					addButton.enabled = false;
					removeButton.enabled = !inputField.getText().isEmpty();
					break;
				}

				if (i == ItemModule.MAX_PLAYERS) {
					addButton.enabled = !inputField.getText().isEmpty();
					removeButton.enabled = false;
				}
			}
		}
		else
			super.keyTyped(typedChar, keyCode);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException{
		super.mouseClicked(mouseX, mouseY, mouseButton);
		inputField.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	protected void actionPerformed(GuiButton button){
		switch(button.id){
			case 0: //add
				if(inputField.getText().isEmpty())
					return;

				if(module.getTagCompound() == null)
					module.setTagCompound(new NBTTagCompound());

				for(int i = 1; i <= ItemModule.MAX_PLAYERS; i++)
				{
					if(module.getTagCompound().hasKey("Player" + i) && module.getTagCompound().getString("Player" + i).equals(inputField.getText()))
					{
						if (i == 9)
							addButton.enabled = false;
						return;
					}
				}

				module.getTagCompound().setString("Player" + getNextSlot(module.getTagCompound()), inputField.getText());

				if(module.getTagCompound() != null && module.getTagCompound().hasKey("Player" + ItemModule.MAX_PLAYERS))
					addButton.enabled = false;

				inputField.setText("");
				break;
			case 1: //remove
				if(inputField.getText().isEmpty())
					return;

				if(module.getTagCompound() == null)
					module.setTagCompound(new NBTTagCompound());

				for(int i = 1; i <= ItemModule.MAX_PLAYERS; i++)
				{
					if(module.getTagCompound().hasKey("Player" + i) && module.getTagCompound().getString("Player" + i).equals(inputField.getText()))
						module.getTagCompound().removeTag("Player" + i);
				}

				inputField.setText("");
				break;
			case 2: //copy
				savedModule = module.getTagCompound();
				copyButton.enabled = false;
				return;
			case 3: //paste
				module.setTagCompound(savedModule);
				break;
			case 4: //clear
				module.setTagCompound(new NBTTagCompound());
				inputField.setText("");
				break;
			default: return;
		}

		if(module.getTagCompound() != null)
			SecurityCraft.network.sendToServer(new PacketSUpdateNBTTag(module));

		addButton.enabled = module.getTagCompound() != null && !module.getTagCompound().hasKey("Player" + ItemModule.MAX_PLAYERS) && !inputField.getText().isEmpty();
		removeButton.enabled = !(module.getTagCompound() == null || module.getTagCompound().isEmpty() || inputField.getText().isEmpty());
		copyButton.enabled = !(module.getTagCompound() == null || module.getTagCompound().isEmpty() || (module.getTagCompound() != null && module.getTagCompound().equals(savedModule)));
		pasteButton.enabled = !(savedModule == null || savedModule.isEmpty() || (module.getTagCompound() != null && module.getTagCompound().equals(savedModule)));
		clearButton.enabled = !(module.getTagCompound() == null || module.getTagCompound().isEmpty());
	}

	private int getNextSlot(NBTTagCompound tag) {
		for(int i = 1; i <= ItemModule.MAX_PLAYERS; i++)
			if(tag.getString("Player" + i) != null && !tag.getString("Player" + i).isEmpty())
				continue;
			else
				return i;

		return 0;
	}
}

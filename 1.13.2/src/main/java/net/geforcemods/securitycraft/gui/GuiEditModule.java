package net.geforcemods.securitycraft.gui;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.containers.ContainerGeneric;
import net.geforcemods.securitycraft.network.packets.PacketSUpdateNBTTag;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.java.games.input.Keyboard;
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
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private ItemStack module;
	private GuiTextField inputField;

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
		inputField = new GuiTextField(5, fontRenderer, width / 2 - 50, height / 2 - 65, 100, 15);
		buttons.add(new GuiButton(0, width / 2 - 38, height / 2 - 45, 76, 20, ClientUtils.localize("gui.securitycraft:editModule.add")));
		buttons.add(new GuiButton(1, width / 2 - 38, height / 2 - 20, 76, 20, ClientUtils.localize("gui.securitycraft:editModule.remove")));
		buttons.add(new GuiButton(2, width / 2 - 38, height / 2 + 5, 76, 20, ClientUtils.localize("gui.securitycraft:editModule.copy")));
		buttons.add(new GuiButton(3, width / 2 - 38, height / 2 + 30, 76, 20, ClientUtils.localize("gui.securitycraft:editModule.paste")));
		buttons.add(new GuiButton(4, width / 2 - 38, height / 2 + 55, 76, 20, ClientUtils.localize("gui.securitycraft:editModule.clear")));
		inputField.setTextColor(-1);
		inputField.setDisabledTextColour(-1);
		inputField.setEnableBackgroundDrawing(true);
		inputField.setMaxStringLength(16);
	}

	@Override
	public void onGuiClosed(){
		super.onGuiClosed();
		Keyboard.enableRepeatEvents(false);
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks){
		super.render(mouseX, mouseY, partialTicks);
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
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(TEXTURE);
		int startX = (width - xSize) / 2;
		int startY = (height - ySize) / 2;
		drawTexturedModalRect(startX, startY, 0, 0, xSize, ySize);
	}

	@Override
	public boolean charTyped(char typedChar, int keyCode){
		if(inputField.isFocused())
		{
			inputField.charTyped(typedChar, keyCode);
			return true;
		}
		else
			return super.charTyped(typedChar, keyCode);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton){
		inputField.mouseClicked(mouseX, mouseY, mouseButton);
		return super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	protected void actionPerformed(GuiButton button){
		switch(button.id){
			case 0: //add
				if(inputField.getText().isEmpty())
					return;

				if(module.getTag() == null)
					module.setTag(new NBTTagCompound());

				for(int i = 1; i <= 10; i++)
				{
					if(module.getTag().contains("Player" + i) && module.getTag().getString("Player" + i).equals(inputField.getText()))
						return;
				}

				module.getTag().putString("Player" + getNextSlot(module.getTag()), inputField.getText());
				break;
			case 1: //remove
				if(inputField.getText().isEmpty())
					return;

				if(module.getTag() == null)
					module.setTag(new NBTTagCompound());

				for(int i = 1; i <= 10; i++)
				{
					if(module.getTag().contains("Player" + i) && module.getTag().getString("Player" + i).equals(inputField.getText()))
						module.getTag().remove("Player" + i);
				}
				break;
			case 2: //copy
				SecurityCraft.instance.setSavedModule(module.getTag());
				return;
			case 3: //paste
				module.setTag(SecurityCraft.instance.getSavedModule());
				SecurityCraft.instance.setSavedModule(null);
				break;
			case 4:
				module.setTag(new NBTTagCompound());
				break;
			default: return;
		}

		SecurityCraft.network.sendToServer(new PacketSUpdateNBTTag(module));
	}

	private int getNextSlot(NBTTagCompound tag) {
		for(int i = 1; i <= 10; i++)
			if(tag.getString("Player" + i) != null && !tag.getString("Player" + i).isEmpty())
				continue;
			else
				return i;

		return 0;
	}
}

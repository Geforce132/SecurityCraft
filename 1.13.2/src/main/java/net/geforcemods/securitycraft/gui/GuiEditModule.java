package net.geforcemods.securitycraft.gui;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.containers.ContainerGeneric;
import net.geforcemods.securitycraft.gui.components.GuiButtonClick;
import net.geforcemods.securitycraft.network.server.UpdateNBTTag;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiEditModule extends GuiContainer
{
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private ItemStack module;
	private GuiTextField inputField;

	public GuiEditModule(ItemStack item)
	{
		super(new ContainerGeneric());

		module = item;
	}

	@Override
	public void initGui()
	{
		super.initGui();

		mc.keyboardListener.enableRepeatEvents(true);
		inputField = new GuiTextField(5, fontRenderer, width / 2 - 50, height / 2 - 65, 100, 15);
		addButton(new GuiButtonClick(0, width / 2 - 38, height / 2 - 45, 76, 20, ClientUtils.localize("gui.securitycraft:editModule.add"), this::actionPerformed));
		addButton(new GuiButtonClick(1, width / 2 - 38, height / 2 - 20, 76, 20, ClientUtils.localize("gui.securitycraft:editModule.remove"), this::actionPerformed));
		addButton(new GuiButtonClick(2, width / 2 - 38, height / 2 + 5, 76, 20, ClientUtils.localize("gui.securitycraft:editModule.copy"), this::actionPerformed));
		addButton(new GuiButtonClick(3, width / 2 - 38, height / 2 + 30, 76, 20, ClientUtils.localize("gui.securitycraft:editModule.paste"), this::actionPerformed));
		addButton(new GuiButtonClick(4, width / 2 - 38, height / 2 + 55, 76, 20, ClientUtils.localize("gui.securitycraft:editModule.clear"), this::actionPerformed));
		inputField.setTextColor(-1);
		inputField.setDisabledTextColour(-1);
		inputField.setEnableBackgroundDrawing(true);
		inputField.setMaxStringLength(16);
	}

	@Override
	public void onGuiClosed(){
		super.onGuiClosed();
		mc.keyboardListener.enableRepeatEvents(false);
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks){
		super.render(mouseX, mouseY, partialTicks);
		GlStateManager.disableLighting();
		inputField.drawTextField(mouseX, mouseY, partialTicks);
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

		SecurityCraft.channel.sendToServer(new UpdateNBTTag(module));
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

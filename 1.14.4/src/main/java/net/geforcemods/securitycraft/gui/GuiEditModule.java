package net.geforcemods.securitycraft.gui;

import com.mojang.blaze3d.platform.GlStateManager;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.gui.components.GuiButtonClick;
import net.geforcemods.securitycraft.network.server.UpdateNBTTagOnServer;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiEditModule extends Screen
{
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private ItemStack module;
	private TextFieldWidget inputField;
	private int xSize = 176, ySize = 166;

	public GuiEditModule(ItemStack item)
	{
		super(new TranslationTextComponent(item.getTranslationKey()));

		module = item;
	}

	@Override
	public void init()
	{
		super.init();

		minecraft.keyboardListener.enableRepeatEvents(true);
		inputField = new TextFieldWidget(font, width / 2 - 50, height / 2 - 65, 100, 15, "");
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
	public void onClose(){
		super.onClose();
		minecraft.keyboardListener.enableRepeatEvents(false);
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks){
		renderBackground();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bindTexture(TEXTURE);
		int startX = (width - xSize) / 2;
		int startY = (height - ySize) / 2;
		blit(startX, startY, 0, 0, xSize, ySize);
		super.render(mouseX, mouseY, partialTicks);
		GlStateManager.disableLighting();
		inputField.render(mouseX, mouseY, partialTicks);
		font.drawSplitString(ClientUtils.localize("gui.securitycraft:editModule"), startX + xSize / 2 - font.getStringWidth(ClientUtils.localize("gui.securitycraft:editModule")) / 2, startY + 6, width, 4210752);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int p_keyPressed_3_)
	{
		if(inputField.isFocused())
		{
			if(keyCode == Minecraft.getInstance().gameSettings.keyBindInventory.getKey().getKeyCode())
				return false;
			else
				return inputField.keyPressed(keyCode, scanCode, p_keyPressed_3_);
		}
		else return super.keyPressed(keyCode, scanCode, p_keyPressed_3_);
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

	protected void actionPerformed(GuiButtonClick button){
		switch(button.id){
			case 0: //add
				if(inputField.getText().isEmpty())
					return;

				if(module.getTag() == null)
					module.setTag(new CompoundNBT());

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
					module.setTag(new CompoundNBT());

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
				module.setTag(new CompoundNBT());
				break;
			default: return;
		}

		if(module.getTag() != null)
			SecurityCraft.channel.sendToServer(new UpdateNBTTagOnServer(module));
	}

	private int getNextSlot(CompoundNBT tag) {
		for(int i = 1; i <= 10; i++)
			if(tag.getString("Player" + i) != null && !tag.getString("Player" + i).isEmpty())
				continue;
			else
				return i;

		return 0;
	}
}

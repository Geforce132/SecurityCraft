package net.geforcemods.securitycraft.screen;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.systems.RenderSystem;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.items.ModuleItem;
import net.geforcemods.securitycraft.network.server.UpdateNBTTagOnServer;
import net.geforcemods.securitycraft.screen.components.ClickButton;
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
public class EditModuleScreen extends Screen
{
	private static CompoundNBT savedModule;
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private ItemStack module;
	private TextFieldWidget inputField;
	private ClickButton addButton, removeButton, copyButton, pasteButton, clearButton;
	private int xSize = 176, ySize = 166;

	public EditModuleScreen(ItemStack item)
	{
		super(new TranslationTextComponent(item.getTranslationKey()));

		module = item;
	}

	@Override
	public void init()
	{
		super.init();

		minecraft.keyboardListener.enableRepeatEvents(true);
		inputField = new TextFieldWidget(font, width / 2 - 55, height / 2 - 65, 110, 15, "");
		addButton(addButton = new ClickButton(0, width / 2 - 38, height / 2 - 45, 76, 20, ClientUtils.localize("gui.securitycraft:editModule.add"), this::actionPerformed));
		addButton(removeButton = new ClickButton(1, width / 2 - 38, height / 2 - 20, 76, 20, ClientUtils.localize("gui.securitycraft:editModule.remove"), this::actionPerformed));
		addButton(copyButton = new ClickButton(2, width / 2 - 38, height / 2 + 5, 76, 20, ClientUtils.localize("gui.securitycraft:editModule.copy"), this::actionPerformed));
		addButton(pasteButton = new ClickButton(3, width / 2 - 38, height / 2 + 30, 76, 20, ClientUtils.localize("gui.securitycraft:editModule.paste"), this::actionPerformed));
		addButton(clearButton = new ClickButton(4, width / 2 - 38, height / 2 + 55, 76, 20, ClientUtils.localize("gui.securitycraft:editModule.clear"), this::actionPerformed));
		addButton(clearButton);

		addButton.active = false;
		removeButton.active = false;

		if (module.getTag() == null || module.getTag().isEmpty() || (module.getTag() != null && module.getTag().equals(savedModule)))
			copyButton.active = false;

		if (savedModule == null || savedModule.isEmpty() || (module.getTag() != null && module.getTag().equals(savedModule)))
			pasteButton.active = false;

		if (module.getTag() == null || module.getTag().isEmpty())
			clearButton.active = false;

		inputField.setTextColor(-1);
		inputField.setDisabledTextColour(-1);
		inputField.setEnableBackgroundDrawing(true);
		inputField.setMaxStringLength(16);
		inputField.setFocused2(true);
	}

	@Override
	public void onClose(){
		super.onClose();
		minecraft.keyboardListener.enableRepeatEvents(false);
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks){
		renderBackground();
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bindTexture(TEXTURE);
		int startX = (width - xSize) / 2;
		int startY = (height - ySize) / 2;
		blit(startX, startY, 0, 0, xSize, ySize);
		super.render(mouseX, mouseY, partialTicks);
		RenderSystem.disableLighting();
		inputField.render(mouseX, mouseY, partialTicks);
		font.drawSplitString(ClientUtils.localize("gui.securitycraft:editModule"), startX + xSize / 2 - font.getStringWidth(ClientUtils.localize("gui.securitycraft:editModule")) / 2, startY + 6, width, 4210752);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int p_keyPressed_3_)
	{
		if(inputField.isFocused())
		{
			if (keyCode == GLFW.GLFW_KEY_BACKSPACE)
			{
				for(int i = 1; i <= ModuleItem.MAX_PLAYERS; i++)
				{
					if(!inputField.getText().isEmpty() && module.getTag() != null)
					{
						if(module.getTag().getString("Player" + i).equals(inputField.getText().substring(0, inputField.getText().length() - 1))){
							addButton.active = false;
							removeButton.active = !(inputField.getText().length() <= 1);
							break;
						}
					}

					if (i == ModuleItem.MAX_PLAYERS) {
						addButton.active = !(inputField.getText().length() <= 1);
						removeButton.active = false;
					}
				}

				if (inputField.getText().isEmpty())
					return false;
			}

			if(keyCode == Minecraft.getInstance().gameSettings.keyBindInventory.getKey().getKeyCode())
				return false;
			else if(keyCode == GLFW.GLFW_KEY_ESCAPE)
				return super.keyPressed(keyCode, scanCode, p_keyPressed_3_);
			else
				return inputField.keyPressed(keyCode, scanCode, p_keyPressed_3_);
		}
		else return super.keyPressed(keyCode, scanCode, p_keyPressed_3_);
	}

	@Override
	public boolean charTyped(char typedChar, int keyCode){
		if(inputField.isFocused())
		{
			if (keyCode == GLFW.GLFW_KEY_SPACE)
				return false;

			inputField.charTyped(typedChar, keyCode);

			for(int i = 1; i <= ModuleItem.MAX_PLAYERS; i++)
			{
				if(module.getTag() != null && module.getTag().getString("Player" + i).equals(inputField.getText())) {
					addButton.active = false;
					removeButton.active = !inputField.getText().isEmpty();
					break;
				}

				if (i == ModuleItem.MAX_PLAYERS) {
					addButton.active = !inputField.getText().isEmpty();
					removeButton.active = false;
				}
			}
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

	protected void actionPerformed(ClickButton button){
		switch(button.id){
			case 0: //add
				if(inputField.getText().isEmpty())
					return;

				if(module.getTag() == null)
					module.setTag(new CompoundNBT());

				for(int i = 1; i <= ModuleItem.MAX_PLAYERS; i++)
				{
					if(module.getTag().contains("Player" + i) && module.getTag().getString("Player" + i).equals(inputField.getText()))
					{
						if (i == 9)
							addButton.active = false;
						return;
					}
				}

				module.getTag().putString("Player" + getNextSlot(module.getTag()), inputField.getText());

				if(module.getTag() != null && module.getTag().contains("Player" + ModuleItem.MAX_PLAYERS))
					addButton.active = false;

				inputField.setText("");
				break;
			case 1: //remove
				if(inputField.getText().isEmpty())
					return;

				if(module.getTag() == null)
					module.setTag(new CompoundNBT());

				for(int i = 1; i <= ModuleItem.MAX_PLAYERS; i++)
				{
					if(module.getTag().contains("Player" + i) && module.getTag().getString("Player" + i).equals(inputField.getText()))
						module.getTag().remove("Player" + i);
				}

				inputField.setText("");
				break;
			case 2: //copy
				savedModule = module.getTag();
				copyButton.active = false;
				return;
			case 3: //paste
				module.setTag(savedModule);
				break;
			case 4: //clear
				module.setTag(new CompoundNBT());
				inputField.setText("");
				break;
			default: return;
		}

		if(module.getTag() != null)
			SecurityCraft.channel.sendToServer(new UpdateNBTTagOnServer(module));

		addButton.active = module.getTag() != null && !module.getTag().contains("Player" + ModuleItem.MAX_PLAYERS) && !inputField.getText().isEmpty();
		removeButton.active = !(module.getTag() == null || module.getTag().isEmpty() || inputField.getText().isEmpty());
		copyButton.active = !(module.getTag() == null || module.getTag().isEmpty() || (module.getTag() != null && module.getTag().equals(savedModule)));
		pasteButton.active = !(savedModule == null || savedModule.isEmpty() || (module.getTag() != null && module.getTag().equals(savedModule)));
		clearButton.active = !(module.getTag() == null || module.getTag().isEmpty());
	}

	private int getNextSlot(CompoundNBT tag) {
		for(int i = 1; i <= ModuleItem.MAX_PLAYERS; i++)
			if(tag.getString("Player" + i) != null && !tag.getString("Player" + i).isEmpty())
				continue;
			else
				return i;

		return 0;
	}
}

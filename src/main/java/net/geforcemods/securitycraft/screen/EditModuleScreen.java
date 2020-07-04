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
	public void func_231160_c_()
	{
		super.func_231160_c_();

		field_230706_i_.keyboardListener.enableRepeatEvents(true);
		inputField = new TextFieldWidget(field_230712_o_, field_230708_k_ / 2 - 55, field_230709_l_ / 2 - 65, 110, 15, "");
		func_230480_a_(addButton = new ClickButton(0, field_230708_k_ / 2 - 38, field_230709_l_ / 2 - 45, 76, 20, ClientUtils.localize("gui.securitycraft:editModule.add"), this::actionPerformed));
		func_230480_a_(removeButton = new ClickButton(1, field_230708_k_ / 2 - 38, field_230709_l_ / 2 - 20, 76, 20, ClientUtils.localize("gui.securitycraft:editModule.remove"), this::actionPerformed));
		func_230480_a_(copyButton = new ClickButton(2, field_230708_k_ / 2 - 38, field_230709_l_ / 2 + 5, 76, 20, ClientUtils.localize("gui.securitycraft:editModule.copy"), this::actionPerformed));
		func_230480_a_(pasteButton = new ClickButton(3, field_230708_k_ / 2 - 38, field_230709_l_ / 2 + 30, 76, 20, ClientUtils.localize("gui.securitycraft:editModule.paste"), this::actionPerformed));
		func_230480_a_(clearButton = new ClickButton(4, field_230708_k_ / 2 - 38, field_230709_l_ / 2 + 55, 76, 20, ClientUtils.localize("gui.securitycraft:editModule.clear"), this::actionPerformed));
		func_230480_a_(clearButton);

		addButton.field_230693_o_ = false;
		removeButton.field_230693_o_ = false;

		if (module.getTag() == null || module.getTag().isEmpty() || (module.getTag() != null && module.getTag().equals(savedModule)))
			copyButton.field_230693_o_ = false;

		if (savedModule == null || savedModule.isEmpty() || (module.getTag() != null && module.getTag().equals(savedModule)))
			pasteButton.field_230693_o_ = false;

		if (module.getTag() == null || module.getTag().isEmpty())
			clearButton.field_230693_o_ = false;

		inputField.setTextColor(-1);
		inputField.setDisabledTextColour(-1);
		inputField.setEnableBackgroundDrawing(true);
		inputField.setMaxStringLength(16);
		inputField.setFocused2(true);
	}

	@Override
	public void func_231175_as__(){
		super.func_231175_as__();
		field_230706_i_.keyboardListener.enableRepeatEvents(false);
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks){
		func_230446_a_();
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		field_230706_i_.getTextureManager().bindTexture(TEXTURE);
		int startX = (field_230708_k_ - xSize) / 2;
		int startY = (field_230709_l_ - ySize) / 2;
		blit(startX, startY, 0, 0, xSize, ySize);
		super.render(mouseX, mouseY, partialTicks);
		RenderSystem.disableLighting();
		inputField.render(mouseX, mouseY, partialTicks);
		field_230712_o_.drawSplitString(ClientUtils.localize("gui.securitycraft:editModule"), startX + xSize / 2 - field_230712_o_.getStringWidth(ClientUtils.localize("gui.securitycraft:editModule")) / 2, startY + 6, field_230708_k_, 4210752);
	}

	@Override
	public boolean func_231046_a_(int keyCode, int scanCode, int p_keyPressed_3_)
	{
		if(inputField.func_230999_j_())
		{
			if (keyCode == GLFW.GLFW_KEY_BACKSPACE)
			{
				for(int i = 1; i <= ModuleItem.MAX_PLAYERS; i++)
				{
					if(!inputField.getText().isEmpty() && module.getTag() != null)
					{
						if(module.getTag().getString("Player" + i).equals(inputField.getText().substring(0, inputField.getText().length() - 1))){
							addButton.field_230693_o_ = false;
							removeButton.field_230693_o_ = !(inputField.getText().length() <= 1);
							break;
						}
					}

					if (i == ModuleItem.MAX_PLAYERS) {
						addButton.field_230693_o_ = !(inputField.getText().length() <= 1);
						removeButton.field_230693_o_ = false;
					}
				}

				if (inputField.getText().isEmpty())
					return false;
			}

			if(keyCode == Minecraft.getInstance().gameSettings.keyBindInventory.getKey().getKeyCode())
				return false;
			else if(keyCode == GLFW.GLFW_KEY_ESCAPE)
				return super.func_231046_a_(keyCode, scanCode, p_keyPressed_3_);
			else
				return inputField.func_231046_a_(keyCode, scanCode, p_keyPressed_3_);
		}
		else return super.func_231046_a_(keyCode, scanCode, p_keyPressed_3_);
	}

	@Override
	public boolean func_231042_a_(char typedChar, int keyCode){
		if(inputField.func_230999_j_())
		{
			if (keyCode == GLFW.GLFW_KEY_SPACE)
				return false;

			inputField.func_231042_a_(typedChar, keyCode);

			for(int i = 1; i <= ModuleItem.MAX_PLAYERS; i++)
			{
				if(module.getTag() != null && module.getTag().getString("Player" + i).equals(inputField.getText())) {
					addButton.field_230693_o_ = false;
					removeButton.field_230693_o_ = !inputField.getText().isEmpty();
					break;
				}

				if (i == ModuleItem.MAX_PLAYERS) {
					addButton.field_230693_o_ = !inputField.getText().isEmpty();
					removeButton.field_230693_o_ = false;
				}
			}
			return true;
		}
		else
			return super.func_231042_a_(typedChar, keyCode);
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
							addButton.field_230693_o_ = false;
						return;
					}
				}

				module.getTag().putString("Player" + getNextSlot(module.getTag()), inputField.getText());

				if(module.getTag() != null && module.getTag().contains("Player" + ModuleItem.MAX_PLAYERS))
					addButton.field_230693_o_ = false;

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
				copyButton.field_230693_o_ = false;
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

		addButton.field_230693_o_ = module.getTag() != null && !module.getTag().contains("Player" + ModuleItem.MAX_PLAYERS) && !inputField.getText().isEmpty();
		removeButton.field_230693_o_ = !(module.getTag() == null || module.getTag().isEmpty() || inputField.getText().isEmpty());
		copyButton.field_230693_o_ = !(module.getTag() == null || module.getTag().isEmpty() || (module.getTag() != null && module.getTag().equals(savedModule)));
		pasteButton.field_230693_o_ = !(savedModule == null || savedModule.isEmpty() || (module.getTag() != null && module.getTag().equals(savedModule)));
		clearButton.field_230693_o_ = !(module.getTag() == null || module.getTag().isEmpty());
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

package net.geforcemods.securitycraft.screen;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.ICustomizable;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.DoubleOption;
import net.geforcemods.securitycraft.api.Option.IntOption;
import net.geforcemods.securitycraft.containers.CustomizeBlockContainer;
import net.geforcemods.securitycraft.network.server.ToggleOption;
import net.geforcemods.securitycraft.screen.components.ClickButton;
import net.geforcemods.securitycraft.screen.components.HoverChecker;
import net.geforcemods.securitycraft.screen.components.NamedSlider;
import net.geforcemods.securitycraft.screen.components.PictureButton;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.Rectangle2d;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.gui.widget.Slider.ISlider;

@OnlyIn(Dist.CLIENT)
public class CustomizeBlockScreen extends ContainerScreen<CustomizeBlockContainer>{
	private static final ResourceLocation[] TEXTURES = {
			new ResourceLocation("securitycraft:textures/gui/container/customize0.png"),
			new ResourceLocation("securitycraft:textures/gui/container/customize1.png"),
			new ResourceLocation("securitycraft:textures/gui/container/customize2.png"),
			new ResourceLocation("securitycraft:textures/gui/container/customize3.png"),
			new ResourceLocation("securitycraft:textures/gui/container/customize4.png"),
			new ResourceLocation("securitycraft:textures/gui/container/customize5.png")
	};
	private final List<Rectangle2d> extraAreas = new ArrayList<>();
	private IModuleInventory moduleInv;
	private PictureButton[] descriptionButtons = new PictureButton[5];
	private Button[] optionButtons = new Button[5];
	private HoverChecker[] hoverCheckers = new HoverChecker[10];
	private final String blockName;

	public CustomizeBlockScreen(CustomizeBlockContainer container, PlayerInventory inv, ITextComponent name)
	{
		super(container, inv, name);
		moduleInv = container.moduleInv;
		blockName = BlockUtils.getBlock(Minecraft.getInstance().world, moduleInv.getTileEntity().getPos()).getTranslationKey().substring(5);
	}

	@Override
	public void func_231160_c_(){
		super.func_231160_c_();

		final int numberOfColumns = 2;

		for(int i = 0; i < moduleInv.getMaxNumberOfModules(); i++){
			int column = i % numberOfColumns;

			descriptionButtons[i] = new PictureButton(i, guiLeft + 127 + column * 22, (guiTop + 16) + (Math.floorDiv(i, numberOfColumns) * 22), 20, 20, field_230707_j_, new ItemStack(moduleInv.acceptedModules()[i].getItem()));
			func_230480_a_(descriptionButtons[i]);
			hoverCheckers[i] = new HoverChecker(descriptionButtons[i]);
		}

		TileEntity te = moduleInv.getTileEntity();

		if(te instanceof ICustomizable && ((ICustomizable)te).customOptions() != null)
		{
			ICustomizable customizableTe = (ICustomizable)te;

			for(int i = 0; i < customizableTe.customOptions().length; i++){
				Option<?> option = customizableTe.customOptions()[i];

				if(option instanceof ISlider && option.isSlider())
				{
					if(option instanceof DoubleOption)
						optionButtons[i] = new NamedSlider((ClientUtils.localize("option" + blockName + "." + option.getName()) + " ").replace("#", option.toString()), blockName, i, guiLeft + 178, (guiTop + 10) + (i * 25), 120, 20, "", "", ((DoubleOption)option).getMin(), ((DoubleOption)option).getMax(), ((DoubleOption)option).get(), true, false, (ISlider)option);
					else if(option instanceof IntOption)
						optionButtons[i] = new NamedSlider((ClientUtils.localize("option" + blockName + "." + option.getName()) + " ").replace("#", option.toString()), blockName, i, guiLeft + 178, (guiTop + 10) + (i * 25), 120, 20, "", "", ((IntOption)option).getMin(), ((IntOption)option).getMax(), ((IntOption)option).get(), true, false, (ISlider)option);

					optionButtons[i].setFGColor(14737632);
				}
				else
				{
					optionButtons[i] = new ClickButton(i, guiLeft + 178, (guiTop + 10) + (i * 25), 120, 20, getOptionButtonTitle(option), this::actionPerformed);
					optionButtons[i].setFGColor(option.toString().equals(option.getDefaultValue().toString()) ? 16777120 : 14737632);
				}

				func_230480_a_(optionButtons[i]);
				hoverCheckers[i + moduleInv.getMaxNumberOfModules()] = new HoverChecker(optionButtons[i]);
			}
		}

		for(Button button : optionButtons)
		{
			if(button == null)
				continue;

			extraAreas.add(new Rectangle2d(button.field_230690_l_, button.field_230691_m_, button.func_230998_h_(), button.getHeight()));
		}
	}

	@Override
	public void func_230430_a_(MatrixStack matrix, int mouseX, int mouseY, float partialTicks){
		super.func_230430_a_(matrix, mouseX, mouseY, partialTicks);

		if(getSlotUnderMouse() != null && !getSlotUnderMouse().getStack().isEmpty())
			func_230457_a_(matrix, getSlotUnderMouse().getStack(), mouseX, mouseY);

		for(int i = 0; i < hoverCheckers.length; i++)
			if(hoverCheckers[i] != null && hoverCheckers[i].checkHover(mouseX, mouseY))
				if(i < moduleInv.getMaxNumberOfModules())
					this.renderTooltip(field_230706_i_.fontRenderer.listFormattedStringToWidth(getModuleDescription(i), 150), mouseX, mouseY, field_230712_o_);
				else
					this.renderTooltip(field_230706_i_.fontRenderer.listFormattedStringToWidth(getOptionDescription(i), 150), mouseX, mouseY, field_230712_o_);
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	@Override
	protected void func_230451_b_(MatrixStack matrix, int mouseX, int mouseY)
	{
		String s = ClientUtils.localize(moduleInv.getTileEntity().getBlockState().getBlock().getTranslationKey());
		field_230712_o_.drawString(s, xSize / 2 - field_230712_o_.getStringWidth(s) / 2, 6, 4210752);
		field_230712_o_.drawString(ClientUtils.localize("container.inventory"), 8, ySize - 96 + 2, 4210752);
	}

	@Override
	protected void func_230450_a_(MatrixStack matrix, float partialTicks, int mouseX, int mouseY)
	{
		func_230446_a_(matrix);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		field_230706_i_.getTextureManager().bindTexture(TEXTURES[moduleInv.getMaxNumberOfModules()]);
		int startX = (field_230708_k_ - xSize) / 2;
		int startY = (field_230709_l_ - ySize) / 2;
		this.blit(startX, startY, 0, 0, xSize, ySize);
	}

	protected void actionPerformed(ClickButton button) {
		Option<?> tempOption = ((ICustomizable)moduleInv.getTileEntity()).customOptions()[button.id]; //safe cast, as this method is only called when it can be casted
		tempOption.toggle();
		button.setFGColor(tempOption.toString().equals(tempOption.getDefaultValue().toString()) ? 16777120 : 14737632);
		button.func_238482_a_(getOptionButtonTitle(tempOption));
		SecurityCraft.channel.sendToServer(new ToggleOption(moduleInv.getTileEntity().getPos().getX(), moduleInv.getTileEntity().getPos().getY(), moduleInv.getTileEntity().getPos().getZ(), button.id));
	}

	private String getModuleDescription(int buttonID) {
		String moduleDescription = "module" + blockName + "." + descriptionButtons[buttonID].getItemStack().getTranslationKey().substring(5).replace("securitycraft.", "") + ".description";

		return ClientUtils.localize(descriptionButtons[buttonID].getItemStack().getTranslationKey()) + ":" + TextFormatting.RESET + "\n\n" + ClientUtils.localize(moduleDescription);
	}

	private String getOptionDescription(int buttonID) {
		String optionDescription = "option" + blockName + "." +  ((ICustomizable)moduleInv.getTileEntity()).customOptions()[buttonID - moduleInv.getSlots()].getName() + ".description";

		return ClientUtils.localize(optionDescription);
	}

	private String getOptionButtonTitle(Option<?> option) {
		return (ClientUtils.localize("option" + blockName + "." + option.getName()) + " ").replace("#", option.toString());
	}

	public List<Rectangle2d> getGuiExtraAreas()
	{
		return extraAreas;
	}
}
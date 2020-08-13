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
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
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
	public void init(){
		super.init();

		final int numberOfColumns = 2;

		for(int i = 0; i < moduleInv.getMaxNumberOfModules(); i++){
			int column = i % numberOfColumns;

			descriptionButtons[i] = new PictureButton(i, guiLeft + 127 + column * 22, (guiTop + 16) + (Math.floorDiv(i, numberOfColumns) * 22), 20, 20, itemRenderer, new ItemStack(moduleInv.acceptedModules()[i].getItem()));
			addButton(descriptionButtons[i]);
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
					TranslationTextComponent translatedBlockName = ClientUtils.localize(blockName);

					if(option instanceof DoubleOption)
						optionButtons[i] = new NamedSlider(ClientUtils.localize("option" + blockName + "." + option.getName(), option.toString()), translatedBlockName, i, guiLeft + 178, (guiTop + 10) + (i * 25), 120, 20, StringTextComponent.EMPTY, "", ((DoubleOption)option).getMin(), ((DoubleOption)option).getMax(), ((DoubleOption)option).get(), true, false, (ISlider)option, null);
					else if(option instanceof IntOption)
						optionButtons[i] = new NamedSlider(ClientUtils.localize("option" + blockName + "." + option.getName(), option.toString()), translatedBlockName, i, guiLeft + 178, (guiTop + 10) + (i * 25), 120, 20, StringTextComponent.EMPTY, "", ((IntOption)option).getMin(), ((IntOption)option).getMax(), ((IntOption)option).get(), true, false, (ISlider)option, null);

					optionButtons[i].setFGColor(14737632);
				}
				else
				{
					optionButtons[i] = new ClickButton(i, guiLeft + 178, (guiTop + 10) + (i * 25), 120, 20, getOptionButtonTitle(option), this::actionPerformed);
					optionButtons[i].setFGColor(option.toString().equals(option.getDefaultValue().toString()) ? 16777120 : 14737632);
				}

				addButton(optionButtons[i]);
				hoverCheckers[i + moduleInv.getMaxNumberOfModules()] = new HoverChecker(optionButtons[i]);
			}
		}

		for(Button button : optionButtons)
		{
			if(button == null)
				continue;

			extraAreas.add(new Rectangle2d(button.x, button.y, button.getWidth(), button.getHeight()));
		}
	}

	@Override
	public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks){
		super.render(matrix, mouseX, mouseY, partialTicks);

		if(getSlotUnderMouse() != null && !getSlotUnderMouse().getStack().isEmpty())
			renderTooltip(matrix, getSlotUnderMouse().getStack(), mouseX, mouseY);

		for(int i = 0; i < hoverCheckers.length; i++)
			if(hoverCheckers[i] != null && hoverCheckers[i].checkHover(mouseX, mouseY))
				if(i < moduleInv.getMaxNumberOfModules())
					renderTooltip(matrix, minecraft.fontRenderer.func_238425_b_(getModuleDescription(i), 150), mouseX, mouseY);
				else
					renderTooltip(matrix, minecraft.fontRenderer.func_238425_b_(getOptionDescription(i), 150), mouseX, mouseY);
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(MatrixStack matrix, int mouseX, int mouseY)
	{
		TranslationTextComponent s = ClientUtils.localize(moduleInv.getTileEntity().getBlockState().getBlock().getTranslationKey());
		font.func_243248_b(matrix, s, xSize / 2 - font.func_238414_a_(s) / 2, 6, 4210752);
		font.func_243248_b(matrix, ClientUtils.localize("container.inventory"), 8, ySize - 96 + 2, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(MatrixStack matrix, float partialTicks, int mouseX, int mouseY)
	{
		renderBackground(matrix);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bindTexture(TEXTURES[moduleInv.getMaxNumberOfModules()]);
		int startX = (width - xSize) / 2;
		int startY = (height - ySize) / 2;
		this.blit(matrix, startX, startY, 0, 0, xSize, ySize);
	}

	protected void actionPerformed(ClickButton button) {
		Option<?> tempOption = ((ICustomizable)moduleInv.getTileEntity()).customOptions()[button.id]; //safe cast, as this method is only called when it can be casted
		tempOption.toggle();
		button.setFGColor(tempOption.toString().equals(tempOption.getDefaultValue().toString()) ? 16777120 : 14737632);
		button.setMessage(getOptionButtonTitle(tempOption));
		SecurityCraft.channel.sendToServer(new ToggleOption(moduleInv.getTileEntity().getPos().getX(), moduleInv.getTileEntity().getPos().getY(), moduleInv.getTileEntity().getPos().getZ(), button.id));
	}

	private ITextComponent getModuleDescription(int buttonID) {
		String moduleDescription = "module" + blockName + "." + descriptionButtons[buttonID].getItemStack().getTranslationKey().substring(5).replace("securitycraft.", "") + ".description";

		return ClientUtils.localize(descriptionButtons[buttonID].getItemStack().getTranslationKey())
				.append(new StringTextComponent(":"))
				.mergeStyle(TextFormatting.RESET)
				.append(new StringTextComponent("\n\n"))
				.append(ClientUtils.localize(moduleDescription));
	}

	private TranslationTextComponent getOptionDescription(int buttonID) {
		String optionDescription = "option" + blockName + "." +  ((ICustomizable)moduleInv.getTileEntity()).customOptions()[buttonID - moduleInv.getSlots()].getName() + ".description";

		return ClientUtils.localize(optionDescription);
	}

	private ITextComponent getOptionButtonTitle(Option<?> option) {
		return ClientUtils.localize("option" + blockName + "." + option.getName(), option.toString());
	}

	public List<Rectangle2d> getGuiExtraAreas()
	{
		return extraAreas;
	}
}
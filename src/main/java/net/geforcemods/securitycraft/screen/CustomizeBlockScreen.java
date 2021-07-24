package net.geforcemods.securitycraft.screen;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.ICustomizable;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.api.Option.DoubleOption;
import net.geforcemods.securitycraft.api.Option.IntOption;
import net.geforcemods.securitycraft.containers.CustomizeBlockContainer;
import net.geforcemods.securitycraft.network.server.ToggleOption;
import net.geforcemods.securitycraft.screen.components.HoverChecker;
import net.geforcemods.securitycraft.screen.components.IdButton;
import net.geforcemods.securitycraft.screen.components.NamedSlider;
import net.geforcemods.securitycraft.screen.components.PictureButton;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fmlclient.gui.widget.Slider;
import net.minecraftforge.fmlclient.gui.widget.Slider.ISlider;

@OnlyIn(Dist.CLIENT)
public class CustomizeBlockScreen extends AbstractContainerScreen<CustomizeBlockContainer>{
	private static final ResourceLocation[] TEXTURES = {
			new ResourceLocation("securitycraft:textures/gui/container/customize0.png"),
			new ResourceLocation("securitycraft:textures/gui/container/customize1.png"),
			new ResourceLocation("securitycraft:textures/gui/container/customize2.png"),
			new ResourceLocation("securitycraft:textures/gui/container/customize3.png"),
			new ResourceLocation("securitycraft:textures/gui/container/customize4.png"),
			new ResourceLocation("securitycraft:textures/gui/container/customize5.png")
	};
	private final List<Rect2i> extraAreas = new ArrayList<>();
	private IModuleInventory moduleInv;
	private PictureButton[] descriptionButtons = new PictureButton[5];
	private Button[] optionButtons = new Button[5];
	private HoverChecker[] hoverCheckers = new HoverChecker[10];
	private final String blockName;

	public CustomizeBlockScreen(CustomizeBlockContainer container, Inventory inv, Component name)
	{
		super(container, inv, name);
		moduleInv = container.moduleInv;
		blockName = container.moduleInv.getTileEntity().getBlockState().getBlock().getDescriptionId().substring(5);
	}

	@Override
	public void init(){
		super.init();

		final int numberOfColumns = 2;

		for(int i = 0; i < moduleInv.getMaxNumberOfModules(); i++){
			int column = i % numberOfColumns;

			addRenderableWidget(descriptionButtons[i] = new PictureButton(i, leftPos + 127 + column * 22, (topPos + 16) + (Math.floorDiv(i, numberOfColumns) * 22), 20, 20, itemRenderer, new ItemStack(moduleInv.acceptedModules()[i].getItem())));
			hoverCheckers[i] = new HoverChecker(descriptionButtons[i]);
		}

		BlockEntity te = moduleInv.getTileEntity();

		if(te instanceof ICustomizable customizableTe && ((ICustomizable)te).customOptions() != null)
		{
			for(int i = 0; i < customizableTe.customOptions().length; i++){
				Option<?> option = customizableTe.customOptions()[i];

				if(option instanceof ISlider && option.isSlider())
				{
					TranslatableComponent translatedBlockName = Utils.localize(blockName);

					if(option instanceof DoubleOption)
						optionButtons[i] = new NamedSlider(Utils.localize("option" + blockName + "." + option.getName(), option.toString()), translatedBlockName, i, leftPos + 178, (topPos + 10) + (i * 25), 120, 20, TextComponent.EMPTY, "", ((DoubleOption)option).getMin(), ((DoubleOption)option).getMax(), ((DoubleOption)option).get(), true, false, (ISlider)option, null);
					else if(option instanceof IntOption)
						optionButtons[i] = new NamedSlider(Utils.localize("option" + blockName + "." + option.getName(), option.toString()), translatedBlockName, i, leftPos + 178, (topPos + 10) + (i * 25), 120, 20, TextComponent.EMPTY, "", ((IntOption)option).getMin(), ((IntOption)option).getMax(), ((IntOption)option).get(), true, false, (ISlider)option, null);

					optionButtons[i].setFGColor(14737632);
				}
				else
				{
					optionButtons[i] = new IdButton(i, leftPos + 178, (topPos + 10) + (i * 25), 120, 20, getOptionButtonTitle(option), this::actionPerformed);
					optionButtons[i].setFGColor(option.toString().equals(option.getDefaultValue().toString()) ? 16777120 : 14737632);
				}

				addRenderableWidget(optionButtons[i]);
				hoverCheckers[i + moduleInv.getMaxNumberOfModules()] = new HoverChecker(optionButtons[i]);
			}
		}

		for(Button button : optionButtons)
		{
			if(button == null)
				continue;

			extraAreas.add(new Rect2i(button.x, button.y, button.getWidth(), button.getHeight()));
		}
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button)
	{
		for(Button b : optionButtons)
		{
			if(b instanceof Slider && ((Slider)b).dragging)
				((Slider)b).mouseReleased(mouseX, mouseY, button);
		}

		return super.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public void render(PoseStack matrix, int mouseX, int mouseY, float partialTicks){
		super.render(matrix, mouseX, mouseY, partialTicks);

		if(getSlotUnderMouse() != null && !getSlotUnderMouse().getItem().isEmpty())
			renderTooltip(matrix, getSlotUnderMouse().getItem(), mouseX, mouseY);

		for(int i = 0; i < hoverCheckers.length; i++)
			if(hoverCheckers[i] != null && hoverCheckers[i].checkHover(mouseX, mouseY))
				if(i < moduleInv.getMaxNumberOfModules())
					renderTooltip(matrix, minecraft.font.split(getModuleDescription(i), 150), mouseX, mouseY);
				else
					renderTooltip(matrix, minecraft.font.split(getOptionDescription(i), 150), mouseX, mouseY);
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	@Override
	protected void renderLabels(PoseStack matrix, int mouseX, int mouseY)
	{
		TranslatableComponent s = Utils.localize(moduleInv.getTileEntity().getBlockState().getBlock().getDescriptionId());
		font.draw(matrix, s, imageWidth / 2 - font.width(s) / 2, 6, 4210752);
		font.draw(matrix, Utils.localize("container.inventory"), 8, imageHeight - 96 + 2, 4210752);
	}

	@Override
	protected void renderBg(PoseStack matrix, float partialTicks, int mouseX, int mouseY)
	{
		renderBackground(matrix);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bindForSetup(TEXTURES[moduleInv.getMaxNumberOfModules()]);
		int startX = (width - imageWidth) / 2;
		int startY = (height - imageHeight) / 2;
		this.blit(matrix, startX, startY, 0, 0, imageWidth, imageHeight);
	}

	protected void actionPerformed(IdButton button) {
		Option<?> tempOption = ((ICustomizable)moduleInv.getTileEntity()).customOptions()[button.id]; //safe cast, as this method is only called when it can be casted
		tempOption.toggle();
		button.setFGColor(tempOption.toString().equals(tempOption.getDefaultValue().toString()) ? 16777120 : 14737632);
		button.setMessage(getOptionButtonTitle(tempOption));
		SecurityCraft.channel.sendToServer(new ToggleOption(moduleInv.getTileEntity().getBlockPos().getX(), moduleInv.getTileEntity().getBlockPos().getY(), moduleInv.getTileEntity().getBlockPos().getZ(), button.id));
	}

	private Component getModuleDescription(int buttonID) {
		String moduleDescription = "module" + blockName + "." + descriptionButtons[buttonID].getItemStack().getDescriptionId().substring(5).replace("securitycraft.", "") + ".description";

		return Utils.localize(descriptionButtons[buttonID].getItemStack().getDescriptionId())
				.append(new TextComponent(":"))
				.withStyle(ChatFormatting.RESET)
				.append(new TextComponent("\n\n"))
				.append(Utils.localize(moduleDescription));
	}

	private TranslatableComponent getOptionDescription(int buttonID) {
		Option<?> option = ((ICustomizable)moduleInv.getTileEntity()).customOptions()[buttonID - moduleInv.getSlots()];
		String optionDescription = "option" + blockName + "." +  option.getName() + ".description";

		return Utils.localize("gui.securitycraft:customize.tooltip", new TranslatableComponent(optionDescription), new TranslatableComponent("gui.securitycraft:customize.currentSetting", getValueText(option)));
	}

	private Component getOptionButtonTitle(Option<?> option) {
		return Utils.localize("option" + blockName + "." + option.getName(), getValueText(option));
	}

	private Component getValueText(Option<?> option)
	{
		if(option instanceof BooleanOption)
			return new TranslatableComponent(((BooleanOption)option).get() ? "gui.securitycraft:invScan.yes" : "gui.securitycraft:invScan.no");
		else
			return new TextComponent(option.toString());
	}

	public List<Rect2i> getGuiExtraAreas()
	{
		return extraAreas;
	}
}
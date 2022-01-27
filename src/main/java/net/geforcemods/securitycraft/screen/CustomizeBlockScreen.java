package net.geforcemods.securitycraft.screen;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.ICustomizable;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.api.Option.DoubleOption;
import net.geforcemods.securitycraft.api.Option.IntOption;
import net.geforcemods.securitycraft.inventory.CustomizeBlockMenu;
import net.geforcemods.securitycraft.network.server.ToggleOption;
import net.geforcemods.securitycraft.screen.components.HoverChecker;
import net.geforcemods.securitycraft.screen.components.NamedSlider;
import net.geforcemods.securitycraft.screen.components.PictureButton;
import net.geforcemods.securitycraft.util.IHasExtraAreas;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.Rectangle2d;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;
import net.minecraftforge.fml.client.gui.widget.Slider;
import net.minecraftforge.fml.client.gui.widget.Slider.ISlider;

@OnlyIn(Dist.CLIENT)
public class CustomizeBlockScreen extends ContainerScreen<CustomizeBlockMenu> implements IHasExtraAreas {
	//@formatter:off
	private static final ResourceLocation[] TEXTURES = {
			new ResourceLocation("securitycraft:textures/gui/container/customize0.png"),
			new ResourceLocation("securitycraft:textures/gui/container/customize1.png"),
			new ResourceLocation("securitycraft:textures/gui/container/customize2.png"),
			new ResourceLocation("securitycraft:textures/gui/container/customize3.png"),
			new ResourceLocation("securitycraft:textures/gui/container/customize4.png"),
			new ResourceLocation("securitycraft:textures/gui/container/customize5.png")
	};
	//@formatter:on
	private final List<Rectangle2d> extraAreas = new ArrayList<>();
	private IModuleInventory moduleInv;
	private PictureButton[] descriptionButtons = new PictureButton[5];
	private Button[] optionButtons = new Button[5];
	private HoverChecker[] hoverCheckers = new HoverChecker[10];
	private final String blockName;

	public CustomizeBlockScreen(CustomizeBlockMenu container, PlayerInventory inv, ITextComponent name) {
		super(container, inv, name);
		moduleInv = container.moduleInv;
		blockName = container.moduleInv.getTileEntity().getBlockState().getBlock().getDescriptionId().substring(5);
	}

	@Override
	public void init() {
		super.init();

		final int numberOfColumns = 2;

		for (int i = 0; i < moduleInv.getMaxNumberOfModules(); i++) {
			int column = i % numberOfColumns;

			addButton(descriptionButtons[i] = new PictureButton(leftPos + 127 + column * 22, (topPos + 16) + (Math.floorDiv(i, numberOfColumns) * 22), 20, 20, itemRenderer, new ItemStack(moduleInv.acceptedModules()[i].getItem())));
			hoverCheckers[i] = new HoverChecker(descriptionButtons[i]);
		}

		TileEntity te = moduleInv.getTileEntity();

		if (te instanceof ICustomizable && ((ICustomizable) te).customOptions() != null) {
			ICustomizable customizableTe = (ICustomizable) te;

			for (int i = 0; i < customizableTe.customOptions().length; i++) {
				Option<?> option = customizableTe.customOptions()[i];

				if (option instanceof ISlider && option.isSlider()) {
					if (option instanceof DoubleOption)
						optionButtons[i] = new NamedSlider((Utils.localize("option" + blockName + "." + option.getName()).getColoredString() + " ").replace("#", option.toString()), blockName, leftPos + 178, (topPos + 10) + (i * 25), 120, 20, "", "", ((DoubleOption) option).getMin(), ((DoubleOption) option).getMax(), ((DoubleOption) option).get(), true, false, (ISlider) option);
					else if (option instanceof IntOption)
						optionButtons[i] = new NamedSlider((Utils.localize("option" + blockName + "." + option.getName()).getColoredString() + " ").replace("#", option.toString()), blockName, leftPos + 178, (topPos + 10) + (i * 25), 120, 20, "", "", ((IntOption) option).getMin(), ((IntOption) option).getMax(), ((IntOption) option).get(), true, false, (ISlider) option);

					optionButtons[i].setFGColor(14737632);
				}
				else {
					optionButtons[i] = new ExtendedButton(leftPos + 178, (topPos + 10) + (i * 25), 120, 20, getOptionButtonTitle(option), this::optionButtonClicked);
					optionButtons[i].setFGColor(option.toString().equals(option.getDefaultValue().toString()) ? 16777120 : 14737632);
				}

				addButton(optionButtons[i]);
				hoverCheckers[i + moduleInv.getMaxNumberOfModules()] = new HoverChecker(optionButtons[i]);
			}
		}

		for (Button button : optionButtons) {
			if (button == null)
				continue;

			extraAreas.add(new Rectangle2d(button.x, button.y, button.getWidth(), button.getHeight()));
		}
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		for (Button b : optionButtons) {
			if (b instanceof Slider && ((Slider) b).dragging)
				((Slider) b).mouseReleased(mouseX, mouseY, button);
		}

		return super.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		super.render(mouseX, mouseY, partialTicks);

		if (getSlotUnderMouse() != null && !getSlotUnderMouse().getItem().isEmpty())
			renderTooltip(getSlotUnderMouse().getItem(), mouseX, mouseY);

		for (int i = 0; i < hoverCheckers.length; i++)
			if (hoverCheckers[i] != null && hoverCheckers[i].checkHover(mouseX, mouseY))
				if (i < moduleInv.getMaxNumberOfModules())
					renderTooltip(minecraft.font.split(getModuleDescription(i), 150), mouseX, mouseY, font);
				else
					renderTooltip(minecraft.font.split(getOptionDescription(i), 150), mouseX, mouseY, font);
	}

	@Override
	protected void renderLabels(int mouseX, int mouseY) {
		String s = Utils.localize(moduleInv.getTileEntity().getBlockState().getBlock().getDescriptionId()).getColoredString();
		font.draw(s, imageWidth / 2 - font.width(s) / 2, 6, 4210752);
		font.draw(Utils.localize("container.inventory").getColoredString(), 8, imageHeight - 96 + 2, 4210752);
	}

	@Override
	protected void renderBg(float partialTicks, int mouseX, int mouseY) {
		renderBackground();
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bind(TEXTURES[moduleInv.getMaxNumberOfModules()]);
		blit(leftPos, topPos, 0, 0, imageWidth, imageHeight);
	}

	protected void optionButtonClicked(Button button) {
		for (int i = 0; i < optionButtons.length; i++) {
			if (button != optionButtons[i])
				continue;

			Option<?> tempOption = ((ICustomizable) moduleInv.getTileEntity()).customOptions()[i]; //safe cast, as this method is only called when it can be casted

			tempOption.toggle();
			button.setFGColor(tempOption.toString().equals(tempOption.getDefaultValue().toString()) ? 16777120 : 14737632);
			button.setMessage(getOptionButtonTitle(tempOption));
			SecurityCraft.channel.sendToServer(new ToggleOption(moduleInv.getTileEntity().getBlockPos().getX(), moduleInv.getTileEntity().getBlockPos().getY(), moduleInv.getTileEntity().getBlockPos().getZ(), i));
			return;
		}
	}

	private String getModuleDescription(int buttonID) {
		String moduleDescription = "module" + blockName + "." + descriptionButtons[buttonID].getItemStack().getDescriptionId().substring(5).replace("securitycraft.", "") + ".description";

		return Utils.localize(descriptionButtons[buttonID].getItemStack().getDescriptionId()).getColoredString() + ":" + TextFormatting.RESET + "\n\n" + Utils.localize(moduleDescription).getColoredString();
	}

	private String getOptionDescription(int buttonID) {
		Option<?> option = ((ICustomizable) moduleInv.getTileEntity()).customOptions()[buttonID - moduleInv.getSlots()];
		String optionDescription = "option" + blockName + "." + option.getName() + ".description";

		return Utils.localize("gui.securitycraft:customize.tooltip", new TranslationTextComponent(optionDescription), new TranslationTextComponent("gui.securitycraft:customize.currentSetting", getValueText(option))).getColoredString();
	}

	private String getOptionButtonTitle(Option<?> option) {
		return (Utils.localize("option" + blockName + "." + option.getName()).getColoredString() + " ").replace("#", getValueText(option));
	}

	private String getValueText(Option<?> option) {
		if (option instanceof BooleanOption)
			return new TranslationTextComponent(((BooleanOption) option).get() ? "gui.securitycraft:invScan.yes" : "gui.securitycraft:invScan.no").getColoredString();
		else
			return option.toString();
	}

    @Override
	public List<Rectangle2d> getExtraAreas() {
		return extraAreas;
	}
}
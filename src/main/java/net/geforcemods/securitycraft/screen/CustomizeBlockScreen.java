package net.geforcemods.securitycraft.screen;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.function.Consumer;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.ICustomizable;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.OptionBoolean;
import net.geforcemods.securitycraft.api.Option.OptionDouble;
import net.geforcemods.securitycraft.api.Option.OptionInt;
import net.geforcemods.securitycraft.inventory.CustomizeBlockMenu;
import net.geforcemods.securitycraft.items.ModuleItem;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.network.server.ToggleModule;
import net.geforcemods.securitycraft.network.server.ToggleOption;
import net.geforcemods.securitycraft.screen.components.ClickButton;
import net.geforcemods.securitycraft.screen.components.PictureButton;
import net.geforcemods.securitycraft.screen.components.Slider;
import net.geforcemods.securitycraft.screen.components.HoverChecker;
import net.geforcemods.securitycraft.screen.components.Slider.ISlider;
import net.geforcemods.securitycraft.util.IHasExtraAreas;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CustomizeBlockScreen extends GuiContainer implements IContainerListener, IHasExtraAreas {
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
	private static final ResourceLocation BEACON_GUI = new ResourceLocation("textures/gui/container/beacon.png");
	private final List<Rectangle> extraAreas = new ArrayList<>();
	private IModuleInventory moduleInv;
	private PictureButton[] descriptionButtons = new PictureButton[5];
	private GuiButton[] optionButtons;
	private HoverChecker[] hoverCheckers = new HoverChecker[10];
	private final Block block;
	private final String title;
	private EnumMap<ModuleType, Boolean> indicators = new EnumMap<>(ModuleType.class);

	public CustomizeBlockScreen(InventoryPlayer inventory, IModuleInventory te) {
		super(new CustomizeBlockMenu(inventory, te));

		moduleInv = te;
		block = te.getTileEntity().getBlockType();
		title = Utils.localize(block).getFormattedText();
		inventorySlots.addListener(this);

		for (ModuleType type : ModuleType.values()) {
			if (moduleInv.hasModule(type))
				indicators.put(type, moduleInv.isModuleEnabled(type));
			else //newly inserted modules will be true by default
				indicators.put(type, true);
		}
	}

	@Override
	public void initGui() {
		super.initGui();

		final int numberOfColumns = 2;

		for (int i = 0; i < moduleInv.getMaxNumberOfModules(); i++) {
			int column = i % numberOfColumns;

			descriptionButtons[i] = new ModuleButton(i, guiLeft + 127 + column * 22, (guiTop + 16) + (Math.floorDiv(i, numberOfColumns) * 22), 20, 20, itemRender, moduleInv.acceptedModules()[i].getItem(), this::moduleButtonClicked);
			buttonList.add(descriptionButtons[i]);
			hoverCheckers[i] = new HoverChecker(descriptionButtons[i]);
			descriptionButtons[i].enabled = moduleInv.hasModule(moduleInv.acceptedModules()[i]);
		}

		TileEntity te = moduleInv.getTileEntity();

		if (te instanceof ICustomizable) {
			ICustomizable customizableTe = (ICustomizable) te;
			Option<?>[] options = customizableTe.customOptions();

			if (options != null) {
				optionButtons = new GuiButton[options.length];

				for (int i = 0; i < options.length; i++) {
					Option<?> option = options[i];

					if (option instanceof ISlider && option.isSlider()) {
						if (option instanceof OptionDouble)
							optionButtons[i] = new Slider((Utils.localize(option.getKey(block)).getFormattedText() + " ").replace("#", option.toString()), block, i, guiLeft + 178, (guiTop + 10) + (i * 25), 120, 20, "", ((OptionDouble) option).getMin(), ((OptionDouble) option).getMax(), ((OptionDouble) option).get(), true, true, (ISlider) option);
						else if (option instanceof OptionInt)
							optionButtons[i] = new Slider((Utils.localize(option.getKey(block)).getFormattedText() + " ").replace("#", option.toString()), block, i, guiLeft + 178, (guiTop + 10) + (i * 25), 120, 20, "", ((OptionInt) option).getMin(), ((OptionInt) option).getMax(), ((OptionInt) option).get(), true, true, (ISlider) option);

						optionButtons[i].packedFGColour = 14737632;
					}
					else {
						optionButtons[i] = new GuiButton(i, guiLeft + 178, (guiTop + 10) + (i * 25), 120, 20, getOptionButtonTitle(option));
						optionButtons[i].packedFGColour = option.toString().equals(option.getDefaultValue().toString()) ? 16777120 : 14737632;
					}

					buttonList.add(optionButtons[i]);
					hoverCheckers[i + moduleInv.getMaxNumberOfModules()] = new HoverChecker(optionButtons[i]);
				}
			}
		}

		for (GuiButton button : optionButtons) {
			if (button == null)
				continue;

			extraAreas.add(new Rectangle(button.x, button.y, button.width, button.height));
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);

		GlStateManager.color(1.0F, 1.0F, 1.0F);
		GlStateManager.disableLighting();
		mc.getTextureManager().bindTexture(BEACON_GUI);

		for (int i = 36; i < ((CustomizeBlockMenu) inventorySlots).maxSlots; i++) {
			Slot slot = inventorySlots.inventorySlots.get(i);

			if (!slot.getStack().isEmpty()) {
				ModuleType type = ((ModuleItem) slot.getStack().getItem()).getModuleType();

				if (indicators.containsKey(type))
					drawScaledCustomSizeModalRect(guiLeft + slot.xPos - 2, guiTop + slot.yPos + 16, indicators.get(type) ? 88 : 110, 219, 21, 22, 20, 20, 256, 256);
			}
		}

		if (getSlotUnderMouse() != null && !getSlotUnderMouse().getStack().isEmpty())
			renderToolTip(getSlotUnderMouse().getStack(), mouseX, mouseY);

		for (int i = 0; i < hoverCheckers.length; i++)
			if (hoverCheckers[i] != null && hoverCheckers[i].checkHover(mouseX, mouseY))
				if (i < moduleInv.getMaxNumberOfModules())
					drawHoveringText(mc.fontRenderer.listFormattedStringToWidth(getModuleDescription(i), 150), mouseX, mouseY, mc.fontRenderer);
				else
					drawHoveringText(mc.fontRenderer.listFormattedStringToWidth(getOptionDescription(i), 150), mouseX, mouseY, mc.fontRenderer);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRenderer.drawString(title, xSize / 2 - fontRenderer.getStringWidth(title) / 2, 6, 4210752);
		fontRenderer.drawString(Utils.localize("container.inventory").getFormattedText(), 8, ySize - 96 + 2, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		int startX = (width - xSize) / 2;
		int startY = (height - ySize) / 2;

		drawDefaultBackground();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(TEXTURES[moduleInv.getMaxNumberOfModules()]);
		drawTexturedModalRect(startX, startY, 0, 0, xSize, ySize);
	}

	@Override
	public void sendSlotContents(Container menu, int slotIndex, ItemStack stack) {
		if (slotIndex < 36)
			return;

		//when removing a stack from a slot, it's not possible to reliably get the module type, so just loop through all possible types
		for (int i = 0; i < moduleInv.getMaxNumberOfModules(); i++) {
			ModuleType type = moduleInv.acceptedModules()[i];

			if (descriptionButtons[i] != null) {
				descriptionButtons[i].enabled = moduleInv.hasModule(type);

				if (!descriptionButtons[i].enabled)
					indicators.remove(type);
				else if (!indicators.containsKey(type))
					indicators.put(type, true);
			}
		}
	}

	private void moduleButtonClicked(ClickButton button) {
		ModuleType moduleType = ((ModuleButton) button).getModule().getModuleType();

		if (moduleInv.isModuleEnabled(moduleType)) {
			indicators.put(moduleType, false);
			moduleInv.removeModule(moduleType, true);
		}
		else {
			indicators.put(moduleType, true);
			moduleInv.insertModule(moduleInv.getModule(moduleType), true);
		}

		SecurityCraft.network.sendToServer(new ToggleModule(moduleInv.getTileEntity().getPos(), moduleType));
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		if (!(button instanceof PictureButton)) {
			Option<?> tempOption = ((ICustomizable) moduleInv.getTileEntity()).customOptions()[button.id];

			tempOption.toggle();
			button.packedFGColour = tempOption.toString().equals(tempOption.getDefaultValue().toString()) ? 16777120 : 14737632;
			button.displayString = getOptionButtonTitle(tempOption);
			SecurityCraft.network.sendToServer(new ToggleOption(moduleInv.getTileEntity().getPos().getX(), moduleInv.getTileEntity().getPos().getY(), moduleInv.getTileEntity().getPos().getZ(), button.id));
		}
		else
			((PictureButton) button).onClick();
	}

	private String getModuleDescription(int buttonID) {
		String moduleDescription = "module." + block.getTranslationKey().substring(5) + "." + descriptionButtons[buttonID].getItemStack().getTranslationKey().substring(5).replace("securitycraft:", "") + ".description";

		return Utils.localize(descriptionButtons[buttonID].getItemStack().getTranslationKey() + ".name").getFormattedText() + ":" + TextFormatting.RESET + "\n\n" + Utils.localize(moduleDescription).getFormattedText();
	}

	private String getOptionDescription(int buttonID) {
		Option<?> option = ((ICustomizable) moduleInv.getTileEntity()).customOptions()[buttonID - moduleInv.getSlots()];

		return Utils.localize("gui.securitycraft:customize.tooltip", new TextComponentTranslation(option.getDescriptionKey(block)), new TextComponentTranslation("gui.securitycraft:customize.currentSetting", getValueText(option))).getFormattedText();
	}

	private String getOptionButtonTitle(Option<?> option) {
		return (Utils.localize(option.getKey(block)).getFormattedText() + " ").replace("#", getValueText(option));
	}

	private String getValueText(Option<?> option) {
		if (option instanceof OptionBoolean)
			return new TextComponentTranslation(((OptionBoolean) option).get() ? "gui.securitycraft:invScan.yes" : "gui.securitycraft:invScan.no").getFormattedText();
		else
			return option.toString();
	}

	@Override
	public List<Rectangle> getGuiExtraAreas() {
		return extraAreas;
	}

	@Override
	public void sendAllContents(Container container, NonNullList<ItemStack> stacks) {}

	@Override
	public void sendWindowProperty(Container container, int varToUpdate, int newValue) {}

	@Override
	public void sendAllWindowProperties(Container container, IInventory inventory) {}

	private class ModuleButton extends PictureButton {
		private final ModuleItem module;

		public ModuleButton(int id, int xPos, int yPos, int width, int height, RenderItem renderItem, ModuleItem itemToRender, Consumer<ClickButton> onClick) {
			super(id, xPos, yPos, width, height, renderItem, new ItemStack(itemToRender), onClick);

			module = itemToRender;
		}

		public ModuleItem getModule() {
			return module;
		}
	}
}
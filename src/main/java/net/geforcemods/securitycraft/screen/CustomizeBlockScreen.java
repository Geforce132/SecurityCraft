package net.geforcemods.securitycraft.screen;

import java.util.ArrayList;
import java.util.EnumMap;
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
import net.geforcemods.securitycraft.inventory.CustomizeBlockMenu;
import net.geforcemods.securitycraft.items.ModuleItem;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.network.server.ToggleModule;
import net.geforcemods.securitycraft.network.server.ToggleOption;
import net.geforcemods.securitycraft.network.server.UpdateSliderValue;
import net.geforcemods.securitycraft.screen.components.CallbackSlider;
import net.geforcemods.securitycraft.screen.components.PictureButton;
import net.geforcemods.securitycraft.screen.components.TextHoverChecker;
import net.geforcemods.securitycraft.util.IHasExtraAreas;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.gui.widget.ExtendedButton;

public class CustomizeBlockScreen extends AbstractContainerScreen<CustomizeBlockMenu> implements IHasExtraAreas, ContainerListener {
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
	private final List<Rect2i> extraAreas = new ArrayList<>();
	private IModuleInventory moduleInv;
	private PictureButton[] descriptionButtons = new PictureButton[5];
	private AbstractWidget[] optionButtons = new AbstractWidget[5];
	private List<TextHoverChecker> hoverCheckers = new ArrayList<>();
	private final Block block;
	private final String blockName;
	private final Component name;
	private final int maxNumberOfModules;
	private EnumMap<ModuleType, Boolean> indicators = new EnumMap<>(ModuleType.class);

	public CustomizeBlockScreen(CustomizeBlockMenu menu, Inventory inv, Component title) {
		super(menu, inv, title);
		moduleInv = menu.moduleInv;
		block = menu.moduleInv.getBlockEntity().getBlockState().getBlock();
		blockName = block.getDescriptionId().substring(5);
		name = Utils.localize(block.getDescriptionId());
		maxNumberOfModules = moduleInv.getMaxNumberOfModules();
		menu.addSlotListener(this);

		for (ModuleType type : ModuleType.values()) {
			if (moduleInv.hasModule(type))
				indicators.put(type, moduleInv.isModuleEnabled(type));
			else //newly inserted modules will be true by default
				indicators.put(type, true);
		}
	}

	@Override
	public void init() {
		super.init();

		final int numberOfColumns = 2;

		for (int i = 0; i < maxNumberOfModules; i++) {
			int column = i % numberOfColumns;

			addRenderableWidget(descriptionButtons[i] = new ModuleButton(leftPos + 127 + column * 22, (topPos + 16) + (Math.floorDiv(i, numberOfColumns) * 22), 20, 20, itemRenderer, moduleInv.acceptedModules()[i].getItem(), this::moduleButtonClicked));
			hoverCheckers.add(new TextHoverChecker(descriptionButtons[i], getModuleDescription(i)));
			descriptionButtons[i].active = moduleInv.hasModule(moduleInv.acceptedModules()[i]);
		}

		if (moduleInv.getBlockEntity() instanceof ICustomizable customizable) {
			Option<?>[] options = customizable.customOptions();

			if (options != null) {
				for (int i = 0; i < options.length; i++) {
					Option<?> option = options[i];

					if (option.isSlider()) {
						if (option instanceof DoubleOption doubleOption) {
							final int sliderIndex = i;

							optionButtons[i] = new CallbackSlider(leftPos + 178, (topPos + 10) + (i * 25), 120, 20, Utils.localize(option.getKey(block), ""), Component.empty(), doubleOption.getMin(), doubleOption.getMax(), doubleOption.get(), doubleOption.getIncrement(), 0, true, slider -> {
								doubleOption.setValue(slider.getValue());
								hoverCheckers.set(sliderIndex, new TextHoverChecker(optionButtons[sliderIndex], getOptionDescription(sliderIndex)));
								SecurityCraft.channel.sendToServer(new UpdateSliderValue(doubleOption.getPos(), option, doubleOption.get()));
							});
						}
						else if (option instanceof IntOption intOption) {
							final int sliderIndex = i;

							optionButtons[i] = new CallbackSlider(leftPos + 178, (topPos + 10) + (i * 25), 120, 20, Utils.localize(option.getKey(block), ""), Component.empty(), intOption.getMin(), intOption.getMax(), intOption.get(), true, slider -> {
								intOption.setValue(slider.getValueInt());
								hoverCheckers.set(sliderIndex, new TextHoverChecker(optionButtons[sliderIndex], getOptionDescription(sliderIndex)));
								SecurityCraft.channel.sendToServer(new UpdateSliderValue(intOption.getPos(), option, intOption.get()));
							});
						}

						optionButtons[i].setFGColor(14737632);
					}
					else {
						optionButtons[i] = new ExtendedButton(leftPos + 178, (topPos + 10) + (i * 25), 120, 20, getOptionButtonTitle(option), this::optionButtonClicked);
						optionButtons[i].setFGColor(option.toString().equals(option.getDefaultValue().toString()) ? 16777120 : 14737632);
					}

					addRenderableWidget(optionButtons[i]);
					hoverCheckers.add(new TextHoverChecker(optionButtons[i], getOptionDescription(i)));
				}
			}
		}

		for (AbstractWidget button : optionButtons) {
			if (button != null)
				extraAreas.add(new Rect2i(button.x, button.y, button.getWidth(), button.getHeight()));
		}
	}

	@Override
	public void render(PoseStack pose, int mouseX, int mouseY, float partialTicks) {
		super.render(pose, mouseX, mouseY, partialTicks);

		RenderSystem._setShaderTexture(0, BEACON_GUI);

		for (int i = 36; i < menu.maxSlots; i++) {
			Slot slot = menu.slots.get(i);

			if (!slot.getItem().isEmpty()) {
				ModuleType type = ((ModuleItem) slot.getItem().getItem()).getModuleType();

				if (indicators.containsKey(type))
					blit(pose, leftPos + slot.x - 2, topPos + slot.y + 16, 20, 20, indicators.get(type) ? 88 : 110, 219, 21, 22, 256, 256);
			}
		}

		if (getSlotUnderMouse() != null && !getSlotUnderMouse().getItem().isEmpty())
			renderTooltip(pose, getSlotUnderMouse().getItem(), mouseX, mouseY);

		for (TextHoverChecker hoverChecker : hoverCheckers) {
			//last check hides the tooltip when a slider is being dragged
			if (hoverChecker != null && hoverChecker.checkHover(mouseX, mouseY) && (!(hoverChecker.getWidget() instanceof CallbackSlider) || !isDragging())) {
				renderTooltip(pose, minecraft.font.split(hoverChecker.getName(), 150), mouseX, mouseY);
				break;
			}
		}
	}

	@Override
	protected void renderLabels(PoseStack pose, int mouseX, int mouseY) {
		font.draw(pose, name, imageWidth / 2 - font.width(name) / 2, 6, 4210752);
		font.draw(pose, Utils.INVENTORY_TEXT, 8, imageHeight - 96 + 2, 4210752);
	}

	@Override
	protected void renderBg(PoseStack pose, float partialTicks, int mouseX, int mouseY) {
		renderBackground(pose);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem._setShaderTexture(0, TEXTURES[maxNumberOfModules]);
		blit(pose, leftPos, topPos, 0, 0, imageWidth, imageHeight);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
		return (getFocused() != null && isDragging() && button == 0 ? getFocused().mouseDragged(mouseX, mouseY, button, dragX, dragY) : false) || super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
	}

	@Override
	public void slotChanged(AbstractContainerMenu menu, int slotIndex, ItemStack stack) {
		if (slotIndex < 36)
			return;

		//when removing a stack from a slot, it's not possible to reliably get the module type, so just loop through all possible types
		for (int i = 0; i < moduleInv.getMaxNumberOfModules(); i++) {
			if (descriptionButtons[i] != null) {
				ModuleType type = moduleInv.acceptedModules()[i];

				descriptionButtons[i].active = moduleInv.hasModule(type);

				if (!descriptionButtons[i].active)
					indicators.remove(type);
				else if (!indicators.containsKey(type))
					indicators.put(type, true);
			}
		}
	}

	private void moduleButtonClicked(Button button) {
		ModuleType moduleType = ((ModuleButton) button).getModule().getModuleType();

		if (moduleInv.isModuleEnabled(moduleType)) {
			indicators.put(moduleType, false);
			moduleInv.removeModule(moduleType, true);
		}
		else {
			indicators.put(moduleType, true);
			moduleInv.insertModule(moduleInv.getModule(moduleType), true);
		}

		SecurityCraft.channel.sendToServer(new ToggleModule(moduleInv.getBlockEntity().getBlockPos(), moduleType));
	}

	private void optionButtonClicked(Button button) {
		for (int i = 0; i < optionButtons.length; i++) {
			if (button != optionButtons[i])
				continue;

			Option<?> tempOption = ((ICustomizable) moduleInv.getBlockEntity()).customOptions()[i]; //safe cast, as this method is only called when it can be casted

			tempOption.toggle();
			button.setFGColor(tempOption.toString().equals(tempOption.getDefaultValue().toString()) ? 16777120 : 14737632);
			button.setMessage(getOptionButtonTitle(tempOption));
			hoverCheckers.set(i, new TextHoverChecker(optionButtons[i], getOptionDescription(i)));
			SecurityCraft.channel.sendToServer(new ToggleOption(moduleInv.getBlockEntity().getBlockPos().getX(), moduleInv.getBlockEntity().getBlockPos().getY(), moduleInv.getBlockEntity().getBlockPos().getZ(), i));
			return;
		}
	}

	private Component getModuleDescription(int moduleId) {
		//@formatter:off
		String moduleDescription = "module" + blockName + "." + descriptionButtons[moduleId].getItemStack().getDescriptionId().substring(5).replace("securitycraft.", "") + ".description";

		return Utils.localize(descriptionButtons[moduleId].getItemStack().getDescriptionId())
				.append(Component.literal(":"))
				.withStyle(ChatFormatting.RESET)
				.append(Component.literal("\n\n"))
				.append(Utils.localize(moduleDescription));
		//@formatter:on
	}

	private Component getOptionDescription(int optionId) {
		Option<?> option = ((ICustomizable) moduleInv.getBlockEntity()).customOptions()[optionId];

		return Utils.localize("gui.securitycraft:customize.tooltip", Component.translatable(option.getDescriptionKey(block)), Component.translatable("gui.securitycraft:customize.currentSetting", getValueText(option)));
	}

	private Component getOptionButtonTitle(Option<?> option) {
		return Utils.localize(option.getKey(block), getValueText(option));
	}

	private Component getValueText(Option<?> option) {
		if (option instanceof BooleanOption booleanOption)
			return Component.translatable(booleanOption.get() ? "gui.securitycraft:invScan.yes" : "gui.securitycraft:invScan.no");
		else
			return Component.literal(option.toString());
	}

	@Override
	public List<Rect2i> getExtraAreas() {
		return extraAreas;
	}

	@Override
	public void dataChanged(AbstractContainerMenu menu, int slotIndex, int value) {}

	private class ModuleButton extends PictureButton {
		private final ModuleItem module;

		public ModuleButton(int xPos, int yPos, int width, int height, ItemRenderer itemRenderer, ModuleItem itemToRender, OnPress onPress) {
			super(xPos, yPos, width, height, itemRenderer, new ItemStack(itemToRender), onPress);

			module = itemToRender;
		}

		public ModuleItem getModule() {
			return module;
		}
	}
}
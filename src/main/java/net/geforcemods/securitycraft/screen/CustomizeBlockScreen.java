package net.geforcemods.securitycraft.screen;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import net.geforcemods.securitycraft.api.ICustomizable;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.DoubleOption;
import net.geforcemods.securitycraft.api.Option.EntityDataWrappedOption;
import net.geforcemods.securitycraft.api.Option.IntOption;
import net.geforcemods.securitycraft.inventory.CustomizeBlockMenu;
import net.geforcemods.securitycraft.items.ModuleItem;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.network.server.ToggleModule;
import net.geforcemods.securitycraft.network.server.ToggleOption;
import net.geforcemods.securitycraft.network.server.UpdateSliderValue;
import net.geforcemods.securitycraft.screen.components.CallbackSlider;
import net.geforcemods.securitycraft.screen.components.PictureButton;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.IHasExtraAreas;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

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
	private static final ResourceLocation CONFIRM_SPRITE = new ResourceLocation("container/beacon/confirm");
	private static final ResourceLocation CANCEL_SPRITE = new ResourceLocation("container/beacon/cancel");
	private final List<Rect2i> extraAreas = new ArrayList<>();
	private IModuleInventory moduleInv;
	private PictureButton[] descriptionButtons = new PictureButton[5];
	private AbstractWidget[] optionButtons;
	private final int maxNumberOfModules;
	private EnumMap<ModuleType, Boolean> indicators = new EnumMap<>(ModuleType.class);

	public CustomizeBlockScreen(CustomizeBlockMenu menu, Inventory inv, Component title) {
		super(menu, inv, title);
		moduleInv = menu.moduleInv;
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

			descriptionButtons[i] = addRenderableWidget(new ModuleButton(leftPos + 127 + column * 22, (topPos + 16) + (Math.floorDiv(i, numberOfColumns) * 22), 20, 20, moduleInv.acceptedModules()[i].getItem(), this::moduleButtonClicked));
			descriptionButtons[i].setTooltip(Tooltip.create(getModuleTooltipText(i)));
			descriptionButtons[i].active = moduleInv.hasModule(moduleInv.acceptedModules()[i]);
		}

		if (moduleInv instanceof ICustomizable customizable) {
			Option<?>[] options = customizable.customOptions();

			if (options.length > 0) {
				optionButtons = new AbstractWidget[options.length];

				for (int i = 0; i < options.length; i++) {
					Option<?> option = options[i] instanceof EntityDataWrappedOption wrapped ? wrapped.getWrapped() : options[i];

					if (option.isSlider()) {
						if (option instanceof DoubleOption doubleOption) {
							final int sliderIndex = i;

							optionButtons[i] = new CallbackSlider(leftPos + 178, (topPos + 10) + (i * 25), 120, 20, Utils.localize(option.getKey(BlockUtils.getLanguageKeyDenotation(moduleInv)), ""), Component.empty(), doubleOption.getMin(), doubleOption.getMax(), doubleOption.get(), doubleOption.getIncrement(), 0, true, slider -> {
								doubleOption.setValue(slider.getValue());
								optionButtons[sliderIndex].setTooltip(Tooltip.create(getOptionDescription(sliderIndex)));

								if (menu.entityId == -1)
									PacketDistributor.sendToServer(new UpdateSliderValue(moduleInv.myPos(), option, doubleOption.get()));
								else
									PacketDistributor.sendToServer(new UpdateSliderValue(menu.entityId, option, doubleOption.get()));
							});
						}
						else if (option instanceof IntOption intOption) {
							final int sliderIndex = i;

							optionButtons[i] = new CallbackSlider(leftPos + 178, (topPos + 10) + (i * 25), 120, 20, Utils.localize(option.getKey(BlockUtils.getLanguageKeyDenotation(moduleInv)), ""), Component.empty(), intOption.getMin(), intOption.getMax(), intOption.get(), true, slider -> {
								intOption.setValue(slider.getValueInt());
								optionButtons[sliderIndex].setTooltip(Tooltip.create(getOptionDescription(sliderIndex)));

								if (menu.entityId == -1)
									PacketDistributor.sendToServer(new UpdateSliderValue(moduleInv.myPos(), option, intOption.get()));
								else
									PacketDistributor.sendToServer(new UpdateSliderValue(menu.entityId, option, intOption.get()));
							});
						}

						optionButtons[i].setFGColor(14737632);
					}
					else {
						optionButtons[i] = new Button(leftPos + 178, (topPos + 10) + (i * 25), 120, 20, getOptionButtonTitle(option), this::optionButtonClicked, Button.DEFAULT_NARRATION);
						optionButtons[i].setFGColor(option.toString().equals(option.getDefaultValue().toString()) ? 16777120 : 14737632);
					}

					addRenderableWidget(optionButtons[i]);
					optionButtons[i].setTooltip(Tooltip.create(getOptionDescription(i)));
				}

				for (AbstractWidget button : optionButtons) {
					extraAreas.add(new Rect2i(button.getX(), button.getY(), button.getWidth(), button.getHeight()));
				}
			}
		}
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		super.render(guiGraphics, mouseX, mouseY, partialTicks);

		for (int i = 36; i < menu.getMaxSlots(); i++) {
			Slot slot = menu.slots.get(i);

			if (!slot.getItem().isEmpty()) {
				ModuleType type = ((ModuleItem) slot.getItem().getItem()).getModuleType();

				if (indicators.containsKey(type))
					guiGraphics.blitSprite(indicators.get(type) ? CONFIRM_SPRITE : CANCEL_SPRITE, leftPos + slot.x, topPos + slot.y + 16, 18, 18);
			}
		}

		renderTooltip(guiGraphics, mouseX, mouseY);
	}

	@Override
	protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		guiGraphics.drawString(font, title, imageWidth / 2 - font.width(title) / 2, 6, 4210752, false);
		guiGraphics.drawString(font, Utils.INVENTORY_TEXT, 8, imageHeight - 96 + 2, 4210752, false);
	}

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
		guiGraphics.blit(TEXTURES[maxNumberOfModules], leftPos, topPos, 0, 0, imageWidth, imageHeight);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
		return getFocused() != null && isDragging() && button == 0 && getFocused().mouseDragged(mouseX, mouseY, button, dragX, dragY) || super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
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
				else
					indicators.computeIfAbsent(type, t -> true);
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

		if (menu.entityId == -1)
			PacketDistributor.sendToServer(new ToggleModule(moduleInv.myPos(), moduleType));
		else
			PacketDistributor.sendToServer(new ToggleModule(menu.entityId, moduleType));
	}

	private void optionButtonClicked(Button button) {
		for (int i = 0; i < optionButtons.length; i++) {
			if (button != optionButtons[i])
				continue;

			Option<?> tempOption = ((ICustomizable) moduleInv).customOptions()[i]; //safe cast, as this method is only called when it can be casted

			tempOption.toggle();
			button.setFGColor(tempOption.toString().equals(tempOption.getDefaultValue().toString()) ? 16777120 : 14737632);
			button.setMessage(getOptionButtonTitle(tempOption));
			optionButtons[i].setTooltip(Tooltip.create(getOptionDescription(i)));

			if (menu.entityId == -1)
				PacketDistributor.sendToServer(new ToggleOption(moduleInv.myPos(), i));
			else
				PacketDistributor.sendToServer(new ToggleOption(menu.entityId, i));

			return;
		}
	}

	private Component getModuleTooltipText(int moduleId) {
		//@formatter:off
		return Utils.localize(descriptionButtons[moduleId].getItemStack().getDescriptionId())
				.append(Component.literal(":"))
				.withStyle(ChatFormatting.RESET)
				.append(Component.literal("\n\n"))
				.append(Utils.localize(moduleInv.getModuleDescriptionId(BlockUtils.getLanguageKeyDenotation(moduleInv), ((ModuleItem) descriptionButtons[moduleId].getItemStack().getItem()).getModuleType())));
		//@formatter:on
	}

	private Component getOptionDescription(int optionId) {
		Option<?> option = ((ICustomizable) moduleInv).customOptions()[optionId];

		return Utils.localize("gui.securitycraft:customize.tooltip", Component.translatable(option.getDescriptionKey(BlockUtils.getLanguageKeyDenotation(moduleInv))), Component.translatable("gui.securitycraft:customize.currentSetting", option.getValueText()));
	}

	private Component getOptionButtonTitle(Option<?> option) {
		return Utils.localize(option.getKey(BlockUtils.getLanguageKeyDenotation(moduleInv)), option.getValueText());
	}

	@Override
	public List<Rect2i> getExtraAreas() {
		return extraAreas;
	}

	@Override
	public void dataChanged(AbstractContainerMenu menu, int slotIndex, int value) {}

	private class ModuleButton extends PictureButton {
		private final ModuleItem module;

		public ModuleButton(int xPos, int yPos, int width, int height, ModuleItem itemToRender, OnPress onPress) {
			super(xPos, yPos, width, height, new ItemStack(itemToRender), onPress);

			module = itemToRender;
		}

		public ModuleItem getModule() {
			return module;
		}
	}
}
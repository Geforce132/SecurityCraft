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
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.widget.ExtendedButton;

public class CustomizeBlockScreen extends AbstractContainerScreen<CustomizeBlockMenu> implements IHasExtraAreas, ContainerListener {
	private static final ResourceLocation BEACON_GUI = new ResourceLocation("textures/gui/container/beacon.png");
	private final List<Rect2i> extraAreas = new ArrayList<>();
	private final int maxNumberOfModules;
	private final ResourceLocation texture;
	private final PictureButton[] descriptionButtons;
	private IModuleInventory moduleInv;
	private AbstractWidget[] optionButtons;
	private List<TextHoverChecker> hoverCheckers = new ArrayList<>();
	private EnumMap<ModuleType, Boolean> indicators = new EnumMap<>(ModuleType.class);

	public CustomizeBlockScreen(CustomizeBlockMenu menu, Inventory inv, Component title) {
		super(menu, inv, title);
		moduleInv = menu.moduleInv;
		maxNumberOfModules = moduleInv.getMaxNumberOfModules();
		texture = new ResourceLocation(SecurityCraft.MODID, "textures/gui/container/customize" + maxNumberOfModules + ".png");
		descriptionButtons = new PictureButton[maxNumberOfModules];
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

			descriptionButtons[i] = addRenderableWidget(new ModuleButton(leftPos + 127 + column * 22, (topPos + 16) + (Math.floorDiv(i, numberOfColumns) * 22), 20, 20, itemRenderer, moduleInv.acceptedModules()[i].getItem(), this::moduleButtonClicked));
			hoverCheckers.add(new TextHoverChecker(descriptionButtons[i], getModuleTooltipText(i)));
			descriptionButtons[i].active = moduleInv.hasModule(moduleInv.acceptedModules()[i]);
		}

		if (moduleInv instanceof ICustomizable customizable) {
			Option<?>[] options = customizable.customOptions();

			if (options.length > 0) {
				optionButtons = new AbstractWidget[options.length];

				for (int i = 0; i < options.length; i++) {
					Option<?> option = options[i];

					if (option.isSlider()) {
						if (option instanceof DoubleOption doubleOption) {
							final int sliderIndex = i;

							optionButtons[i] = new CallbackSlider(leftPos + 178, (topPos + 10) + (i * 25), 120, 20, Utils.localize(option.getKey(Utils.getLanguageKeyDenotation(moduleInv)), ""), TextComponent.EMPTY, doubleOption.getMin(), doubleOption.getMax(), doubleOption.get(), doubleOption.getIncrement(), 0, true, slider -> {
								doubleOption.setValue(slider.getValue());
								hoverCheckers.set(sliderIndex, new TextHoverChecker(optionButtons[sliderIndex], getOptionDescription(sliderIndex)));
								SecurityCraft.CHANNEL.sendToServer(new UpdateSliderValue(moduleInv.myPos(), option, doubleOption.get()));
							});
						}
						else if (option instanceof IntOption intOption) {
							final int sliderIndex = i;

							optionButtons[i] = new CallbackSlider(leftPos + 178, (topPos + 10) + (i * 25), 120, 20, Utils.localize(option.getKey(Utils.getLanguageKeyDenotation(moduleInv)), ""), TextComponent.EMPTY, intOption.getMin(), intOption.getMax(), intOption.get(), true, slider -> {
								intOption.setValue(slider.getValueInt());
								hoverCheckers.set(sliderIndex, new TextHoverChecker(optionButtons[sliderIndex], getOptionDescription(sliderIndex)));
								SecurityCraft.CHANNEL.sendToServer(new UpdateSliderValue(moduleInv.myPos(), option, intOption.get()));
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

				for (AbstractWidget button : optionButtons) {
					extraAreas.add(new Rect2i(button.x, button.y, button.getWidth(), button.getHeight()));
				}
			}
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

		renderTooltip(pose, mouseX, mouseY);

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
		font.draw(pose, title, imageWidth / 2 - font.width(title) / 2, 6, 4210752);
		font.draw(pose, Utils.INVENTORY_TEXT, 8, imageHeight - 96 + 2, 4210752);
	}

	@Override
	protected void renderBg(PoseStack pose, float partialTicks, int mouseX, int mouseY) {
		renderBackground(pose);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem._setShaderTexture(0, texture);
		blit(pose, leftPos, topPos, 0, 0, imageWidth, imageHeight);
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

		SecurityCraft.CHANNEL.sendToServer(new ToggleModule(moduleInv.myPos(), moduleType));
	}

	private void optionButtonClicked(Button button) {
		for (int i = 0; i < optionButtons.length; i++) {
			if (button != optionButtons[i])
				continue;

			Option<?> tempOption = ((ICustomizable) moduleInv).customOptions()[i]; //safe cast, as this method is only called when it can be casted

			tempOption.toggle();
			button.setFGColor(tempOption.toString().equals(tempOption.getDefaultValue().toString()) ? 16777120 : 14737632);
			button.setMessage(getOptionButtonTitle(tempOption));
			hoverCheckers.set(i, new TextHoverChecker(optionButtons[i], getOptionDescription(i)));
			SecurityCraft.CHANNEL.sendToServer(new ToggleOption(moduleInv.myPos(), i));
			return;
		}
	}

	private Component getModuleTooltipText(int moduleId) {
		//@formatter:off
		return Utils.localize(descriptionButtons[moduleId].getItemStack().getDescriptionId())
				.append(new TextComponent(":"))
				.withStyle(ChatFormatting.RESET)
				.append(new TextComponent("\n\n"))
				.append(Utils.localize(moduleInv.getModuleDescriptionId(Utils.getLanguageKeyDenotation(moduleInv), ((ModuleItem) descriptionButtons[moduleId].getItemStack().getItem()).getModuleType())));
		//@formatter:on
	}

	private TranslatableComponent getOptionDescription(int optionId) {
		Option<?> option = ((ICustomizable) moduleInv).customOptions()[optionId];

		return Utils.localize("gui.securitycraft:customize.tooltip", new TranslatableComponent(option.getDescriptionKey(Utils.getLanguageKeyDenotation(moduleInv))), new TranslatableComponent("gui.securitycraft:customize.currentSetting", option.getValueText()));
	}

	private Component getOptionButtonTitle(Option<?> option) {
		return Utils.localize(option.getKey(Utils.getLanguageKeyDenotation(moduleInv)), option.getValueText());
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
package net.geforcemods.securitycraft.screen;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

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
import net.geforcemods.securitycraft.screen.components.NamedSlider;
import net.geforcemods.securitycraft.screen.components.PictureButton;
import net.geforcemods.securitycraft.screen.components.TextHoverChecker;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.IHasExtraAreas;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Rectangle2d;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;
import net.minecraftforge.fml.client.gui.widget.Slider;
import net.minecraftforge.fml.client.gui.widget.Slider.ISlider;

@OnlyIn(Dist.CLIENT)
public class CustomizeBlockScreen extends ContainerScreen<CustomizeBlockMenu> implements IHasExtraAreas, IContainerListener {
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
	private final List<Rectangle2d> extraAreas = new ArrayList<>();
	private IModuleInventory moduleInv;
	private PictureButton[] descriptionButtons = new PictureButton[5];
	private Button[] optionButtons = {};
	private List<TextHoverChecker> hoverCheckers = new ArrayList<>();
	private final int maxNumberOfModules;
	private EnumMap<ModuleType, Boolean> indicators = new EnumMap<>(ModuleType.class);

	public CustomizeBlockScreen(CustomizeBlockMenu menu, PlayerInventory inv, ITextComponent title) {
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

			descriptionButtons[i] = addButton(new ModuleButton(leftPos + 127 + column * 22, (topPos + 16) + (Math.floorDiv(i, numberOfColumns) * 22), 20, 20, itemRenderer, moduleInv.acceptedModules()[i].getItem(), this::moduleButtonClicked));
			hoverCheckers.add(new TextHoverChecker(descriptionButtons[i], getModuleTooltipText(i)));
			descriptionButtons[i].active = moduleInv.hasModule(moduleInv.acceptedModules()[i]);
		}

		if (moduleInv instanceof ICustomizable) {
			ICustomizable customizable = (ICustomizable) moduleInv;
			Option<?>[] options = customizable.customOptions();

			if (options.length > 0) {
				optionButtons = new Button[options.length];

				for (int i = 0; i < options.length; i++) {
					Option<?> option = options[i];

					if (option instanceof ISlider && option.isSlider()) {
						final int sliderIndex = i;
						String denotation = BlockUtils.getLanguageKeyDenotation(customizable);

						if (option instanceof DoubleOption)
							optionButtons[i] = new NamedSlider(Utils.localize(option.getKey(denotation), option.toString()), denotation, leftPos + 178, (topPos + 10) + (i * 25), 120, 20, StringTextComponent.EMPTY, "", ((DoubleOption) option).getMin(), ((DoubleOption) option).getMax(), ((DoubleOption) option).get(), true, false, (ISlider) option, slider -> updateOptionTooltip(sliderIndex));
						else if (option instanceof IntOption)
							optionButtons[i] = new NamedSlider(Utils.localize(option.getKey(denotation), option.toString()), denotation, leftPos + 178, (topPos + 10) + (i * 25), 120, 20, StringTextComponent.EMPTY, "", ((IntOption) option).getMin(), ((IntOption) option).getMax(), ((IntOption) option).get(), true, false, (ISlider) option, slider -> updateOptionTooltip(sliderIndex));

						optionButtons[i].setFGColor(14737632);
					}
					else {
						optionButtons[i] = new ExtendedButton(leftPos + 178, (topPos + 10) + (i * 25), 120, 20, getOptionButtonTitle(option), this::optionButtonClicked);
						optionButtons[i].setFGColor(option.toString().equals(option.getDefaultValue().toString()) ? 16777120 : 14737632);
					}

					addButton(optionButtons[i]);
					hoverCheckers.add(new TextHoverChecker(optionButtons[i], getOptionDescription(i)));
				}

				for (Button button : optionButtons) {
					extraAreas.add(new Rectangle2d(button.x, button.y, button.getWidth(), button.getHeight()));
				}
			}
		}
	}

	private void updateOptionTooltip(int i) {
		hoverCheckers.set(i, new TextHoverChecker(optionButtons[i], getOptionDescription(i)));
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		for (Button b : optionButtons) {
			if (b instanceof Slider && ((Slider) b).dragging)
				b.mouseReleased(mouseX, mouseY, button);
		}

		return super.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public void render(MatrixStack pose, int mouseX, int mouseY, float partialTicks) {
		super.render(pose, mouseX, mouseY, partialTicks);

		minecraft.textureManager.bind(BEACON_GUI);

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
			if (hoverChecker != null && hoverChecker.checkHover(mouseX, mouseY)) {
				renderTooltip(pose, minecraft.font.split(hoverChecker.getName(), 150), mouseX, mouseY);
				break;
			}
		}
	}

	@Override
	protected void renderLabels(MatrixStack pose, int mouseX, int mouseY) {
		font.draw(pose, title, imageWidth / 2 - font.width(title) / 2, 6, 4210752);
		font.draw(pose, Utils.INVENTORY_TEXT, 8, imageHeight - 96 + 2, 4210752);
	}

	@Override
	protected void renderBg(MatrixStack pose, float partialTicks, int mouseX, int mouseY) {
		renderBackground(pose);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bind(TEXTURES[maxNumberOfModules]);
		blit(pose, leftPos, topPos, 0, 0, imageWidth, imageHeight);
	}

	@Override
	public void slotChanged(Container menu, int slotIndex, ItemStack stack) {
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

		SecurityCraft.channel.sendToServer(new ToggleModule(moduleInv.myPos(), moduleType));
	}

	protected void optionButtonClicked(Button button) {
		for (int i = 0; i < optionButtons.length; i++) {
			if (button != optionButtons[i])
				continue;

			Option<?> tempOption = ((ICustomizable) moduleInv).customOptions()[i]; //safe cast, as this method is only called when it can be casted

			tempOption.toggle();
			button.setFGColor(tempOption.toString().equals(tempOption.getDefaultValue().toString()) ? 16777120 : 14737632);
			button.setMessage(getOptionButtonTitle(tempOption));
			updateOptionTooltip(i);
			SecurityCraft.channel.sendToServer(new ToggleOption(moduleInv.myPos().getX(), moduleInv.myPos().getY(), moduleInv.myPos().getZ(), i));
			return;
		}
	}

	private ITextComponent getModuleTooltipText(int moduleId) {
		//@formatter:off
		return Utils.localize(descriptionButtons[moduleId].getItemStack().getDescriptionId())
				.append(new StringTextComponent(":"))
				.withStyle(TextFormatting.RESET)
				.append(new StringTextComponent("\n\n"))
				.append(Utils.localize(moduleInv.getModuleDescriptionId(BlockUtils.getLanguageKeyDenotation(moduleInv), ((ModuleItem) descriptionButtons[moduleId].getItemStack().getItem()).getModuleType())));
		//@formatter:on
	}

	private TranslationTextComponent getOptionDescription(int optionId) {
		Option<?> option = ((ICustomizable) moduleInv).customOptions()[optionId];

		return Utils.localize("gui.securitycraft:customize.tooltip", new TranslationTextComponent(option.getDescriptionKey(BlockUtils.getLanguageKeyDenotation(moduleInv))), new TranslationTextComponent("gui.securitycraft:customize.currentSetting", option.getValueText()));
	}

	private ITextComponent getOptionButtonTitle(Option<?> option) {
		return Utils.localize(option.getKey(BlockUtils.getLanguageKeyDenotation(moduleInv)), option.getValueText());
	}

	@Override
	public List<Rectangle2d> getExtraAreas() {
		return extraAreas;
	}

	@Override
	public void setContainerData(Container menu, int slotIndex, int value) {}

	@Override
	public void refreshContainer(Container menu, NonNullList<ItemStack> stacks) {}

	private class ModuleButton extends PictureButton {
		private final ModuleItem module;

		public ModuleButton(int xPos, int yPos, int width, int height, ItemRenderer itemRenderer, ModuleItem itemToRender, IPressable onPress) {
			super(xPos, yPos, width, height, itemRenderer, new ItemStack(itemToRender), onPress);

			module = itemToRender;
		}

		public ModuleItem getModule() {
			return module;
		}
	}
}
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
import net.geforcemods.securitycraft.api.Option.BooleanOption;
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
import net.minecraft.tileentity.TileEntity;
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
	private Button[] optionButtons = new Button[5];
	private List<TextHoverChecker> hoverCheckers = new ArrayList<>();
	private final String blockName;
	private final int maxNumberOfModules;
	private EnumMap<ModuleType, Boolean> indicators = new EnumMap<>(ModuleType.class);

	public CustomizeBlockScreen(CustomizeBlockMenu container, PlayerInventory inv, ITextComponent name) {
		super(container, inv, name);
		moduleInv = container.moduleInv;
		blockName = container.moduleInv.getTileEntity().getBlockState().getBlock().getDescriptionId().substring(5);
		maxNumberOfModules = moduleInv.getMaxNumberOfModules();
		container.addSlotListener(this);

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

			addButton(descriptionButtons[i] = new ModuleButton(leftPos + 127 + column * 22, (topPos + 16) + (Math.floorDiv(i, numberOfColumns) * 22), 20, 20, itemRenderer, moduleInv.acceptedModules()[i].getItem(), this::moduleButtonClicked));
			hoverCheckers.add(new TextHoverChecker(descriptionButtons[i], getModuleDescription(i)));
			descriptionButtons[i].active = moduleInv.hasModule(moduleInv.acceptedModules()[i]);
		}

		TileEntity te = moduleInv.getTileEntity();

		if (te instanceof ICustomizable) {
			ICustomizable customizableTe = (ICustomizable) te;
			Option<?>[] options = customizableTe.customOptions();

			if (options != null) {
				for (int i = 0; i < options.length; i++) {
					Option<?> option = options[i];

					if (option instanceof ISlider && option.isSlider()) {
						TranslationTextComponent translatedBlockName = Utils.localize(blockName);

						if (option instanceof DoubleOption)
							optionButtons[i] = new NamedSlider(Utils.localize("option" + blockName + "." + option.getName(), option.toString()), translatedBlockName, leftPos + 178, (topPos + 10) + (i * 25), 120, 20, StringTextComponent.EMPTY, "", ((DoubleOption) option).getMin(), ((DoubleOption) option).getMax(), ((DoubleOption) option).get(), true, false, (ISlider) option, null);
						else if (option instanceof IntOption)
							optionButtons[i] = new NamedSlider(Utils.localize("option" + blockName + "." + option.getName(), option.toString()), translatedBlockName, leftPos + 178, (topPos + 10) + (i * 25), 120, 20, StringTextComponent.EMPTY, "", ((IntOption) option).getMin(), ((IntOption) option).getMax(), ((IntOption) option).get(), true, false, (ISlider) option, null);

						optionButtons[i].setFGColor(14737632);
					}
					else {
						optionButtons[i] = new ExtendedButton(leftPos + 178, (topPos + 10) + (i * 25), 120, 20, getOptionButtonTitle(option), this::optionButtonClicked);
						optionButtons[i].setFGColor(option.toString().equals(option.getDefaultValue().toString()) ? 16777120 : 14737632);
					}

					addButton(optionButtons[i]);
					hoverCheckers.add(new TextHoverChecker(optionButtons[i], getOptionDescription(i)));
				}
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
	public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
		super.render(matrix, mouseX, mouseY, partialTicks);

		minecraft.textureManager.bind(BEACON_GUI);

		for (int i = 36; i < menu.maxSlots; i++) {
			Slot slot = menu.slots.get(i);

			if (!slot.getItem().isEmpty()) {
				ModuleType type = ((ModuleItem) slot.getItem().getItem()).getModuleType();

				if (indicators.containsKey(type))
					blit(matrix, leftPos + slot.x - 2, topPos + slot.y + 16, 20, 20, indicators.get(type) ? 88 : 110, 219, 21, 22, 256, 256);
			}
		}

		if (getSlotUnderMouse() != null && !getSlotUnderMouse().getItem().isEmpty())
			renderTooltip(matrix, getSlotUnderMouse().getItem(), mouseX, mouseY);

		for (TextHoverChecker hoverChecker : hoverCheckers) {
			if (hoverChecker != null && hoverChecker.checkHover(mouseX, mouseY))
				renderTooltip(matrix, minecraft.font.split(hoverChecker.getName(), 150), mouseX, mouseY);
		}
	}

	@Override
	protected void renderLabels(MatrixStack matrix, int mouseX, int mouseY) {
		TranslationTextComponent s = Utils.localize(moduleInv.getTileEntity().getBlockState().getBlock().getDescriptionId());

		font.draw(matrix, s, imageWidth / 2 - font.width(s) / 2, 6, 4210752);
		font.draw(matrix, Utils.INVENTORY_TEXT, 8, imageHeight - 96 + 2, 4210752);
	}

	@Override
	protected void renderBg(MatrixStack matrix, float partialTicks, int mouseX, int mouseY) {
		renderBackground(matrix);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bind(TEXTURES[maxNumberOfModules]);
		blit(matrix, leftPos, topPos, 0, 0, imageWidth, imageHeight);
	}

	@Override
	public void slotChanged(Container menu, int slotIndex, ItemStack stack) {
		if (slotIndex < 36)
			return;

		//when removing a stack from a slot, it's not possible to reliably get the module type, so just loop through all possible types
		for (int i = 0; i < moduleInv.getMaxNumberOfModules(); i++) {
			if (descriptionButtons[i] != null) {
				descriptionButtons[i].active = moduleInv.hasModule(moduleInv.acceptedModules()[i]);

				if (!descriptionButtons[i].active)
					indicators.remove(moduleInv.acceptedModules()[i]);
				else
					indicators.put(moduleInv.acceptedModules()[i], true);
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

		SecurityCraft.channel.sendToServer(new ToggleModule(moduleInv.getTileEntity().getBlockPos(), moduleType));
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

	private ITextComponent getModuleDescription(int moduleId) {
		String moduleDescription = "module" + blockName + "." + descriptionButtons[moduleId].getItemStack().getDescriptionId().substring(5).replace("securitycraft.", "") + ".description";

		//@formatter:off
		return Utils.localize(descriptionButtons[moduleId].getItemStack().getDescriptionId())
				.append(new StringTextComponent(":"))
				.withStyle(TextFormatting.RESET)
				.append(new StringTextComponent("\n\n"))
				.append(Utils.localize(moduleDescription));
		//@formatter:on
	}

	private TranslationTextComponent getOptionDescription(int optionId) {
		Option<?> option = ((ICustomizable) moduleInv.getTileEntity()).customOptions()[optionId];
		String optionDescription = "option" + blockName + "." + option.getName() + ".description";

		return Utils.localize("gui.securitycraft:customize.tooltip", new TranslationTextComponent(optionDescription), new TranslationTextComponent("gui.securitycraft:customize.currentSetting", getValueText(option)));
	}

	private ITextComponent getOptionButtonTitle(Option<?> option) {
		return Utils.localize("option" + blockName + "." + option.getName(), getValueText(option));
	}

	private ITextComponent getValueText(Option<?> option) {
		if (option instanceof BooleanOption)
			return new TranslationTextComponent(((BooleanOption) option).get() ? "gui.securitycraft:invScan.yes" : "gui.securitycraft:invScan.no");
		else
			return new StringTextComponent(option.toString());
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
package net.geforcemods.securitycraft.screen;

import com.mojang.blaze3d.platform.InputConstants;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.TrophySystemBlockEntity;
import net.geforcemods.securitycraft.inventory.TrophySystemMenu;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.screen.components.ToggleScrollList;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;

public class TrophySystemScreen extends AbstractContainerScreen<TrophySystemMenu> {
	private static final ResourceLocation GUI_TEXTURE = SecurityCraft.resLoc("textures/gui/container/trophy_system.png");
	public final Component scrollListTitle, smartModuleTooltip;
	private boolean hasSmartModule;
	private boolean hasRedstoneModule;
	private TrophySystemBlockEntity be;
	private ToggleScrollList<EntityType<?>> scrollList;

	public TrophySystemScreen(TrophySystemMenu menu, Inventory playerInventory, Component title) {
		super(menu, playerInventory, title);

		imageHeight = 248;
		this.be = menu.be;
		hasSmartModule = be.isModuleEnabled(ModuleType.SMART);
		hasRedstoneModule = be.isModuleEnabled(ModuleType.REDSTONE);
		scrollListTitle = Utils.localize("gui.securitycraft:trophy_system.targetableProjectiles");
		smartModuleTooltip = hasSmartModule ? Utils.localize("gui.securitycraft:trophy_system.toggle") : Utils.localize("gui.securitycraft:trophy_system.moduleRequired");
	}

	@Override
	protected void init() {
		super.init();
		inventoryLabelY = imageHeight - 94;
		titleLabelX = imageWidth / 2 - font.width(title) / 2;
		scrollList = addRenderableWidget(new ToggleScrollList<>(be, hasSmartModule, hasRedstoneModule, minecraft, imageWidth - 24, 106, topPos + 40, leftPos + 12));
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
		if (scrollList != null && scrollList.mouseDragged(mouseX, mouseY, button, dragX, dragY))
			return true;
		else
			return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
		if (super.mouseScrolled(mouseX, mouseY, scrollX, scrollY))
			return true;

		return scrollList.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
	}

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
		guiGraphics.blit(RenderType::guiTextured, GUI_TEXTURE, leftPos, topPos, 0.0F, 0.0F, imageWidth, imageHeight, 256, 256);
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		super.render(guiGraphics, mouseX, mouseY, partialTick);
		renderTooltip(guiGraphics, mouseX, mouseY);
		ClientUtils.renderModuleInfo(guiGraphics, font, ModuleType.SMART, smartModuleTooltip, hasSmartModule, leftPos + 5, topPos + 5, mouseX, mouseY);
	}

	@Override
	public void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		super.renderLabels(guiGraphics, mouseX, mouseY);
		guiGraphics.drawString(font, scrollListTitle, imageWidth / 2 - font.width(scrollListTitle) / 2, 31, 4210752, false);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (minecraft.options.keyInventory.isActiveAndMatches(InputConstants.getKey(keyCode, scanCode))) {
			onClose();
			return true;
		}

		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}
}

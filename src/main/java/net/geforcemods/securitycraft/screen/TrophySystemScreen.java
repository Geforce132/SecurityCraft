package net.geforcemods.securitycraft.screen;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.TrophySystemBlockEntity;
import net.geforcemods.securitycraft.inventory.TrophySystemMenu;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.screen.components.ToggleScrollList;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;

public class TrophySystemScreen extends AbstractContainerScreen<TrophySystemMenu> {
	private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(SecurityCraft.MODID, "textures/gui/container/trophy_system.png");
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
		scrollList = addRenderableWidget(new ToggleScrollList<>(be, hasSmartModule, hasRedstoneModule, minecraft, imageWidth - 24, 106, topPos + 40, leftPos + 12, this));
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
		if (scrollList != null && scrollList.mouseDragged(mouseX, mouseY, button, dragX, dragY))
			return true;
		else
			return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
	}

	@Override
	protected void renderBg(PoseStack pose, float partialTick, int mouseX, int mouseY) {
		renderBackground(pose);
		RenderSystem.setShaderTexture(0, GUI_TEXTURE);
		blit(pose, leftPos, topPos, 0, 0, imageWidth, imageHeight);
	}

	@Override
	public void render(PoseStack pose, int mouseX, int mouseY, float partialTick) {
		super.render(pose, mouseX, mouseY, partialTick);
		renderTooltip(pose, mouseX, mouseY);
		ClientUtils.renderModuleInfo(pose, ModuleType.SMART, smartModuleTooltip, hasSmartModule, leftPos + 5, topPos + 5, mouseX, mouseY);
	}

	@Override
	public void renderLabels(PoseStack pose, int mouseX, int mouseY) {
		super.renderLabels(pose, mouseX, mouseY);
		font.draw(pose, scrollListTitle, imageWidth / 2 - font.width(scrollListTitle) / 2, 31, 4210752);
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

package net.geforcemods.securitycraft.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.TrophySystemBlockEntity;
import net.geforcemods.securitycraft.inventory.TrophySystemMenu;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.screen.components.ToggleScrollList;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class TrophySystemScreen extends ContainerScreen<TrophySystemMenu> {
	private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(SecurityCraft.MODID, "textures/gui/container/trophy_system.png");
	public final ITextComponent scrollListTitle, smartModuleTooltip;
	private boolean hasSmartModule;
	private boolean hasRedstoneModule;
	private TrophySystemBlockEntity be;
	private ToggleScrollList<EntityType<?>> scrollList;

	public TrophySystemScreen(TrophySystemMenu menu, PlayerInventory playerInventory, ITextComponent title) {
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
		children.add(scrollList = new ToggleScrollList<>(be, hasSmartModule, hasRedstoneModule, minecraft, imageWidth - 24, 106, topPos + 40, leftPos + 12, this));
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
		if (scrollList != null && scrollList.mouseDragged(mouseX, mouseY, button, dragX, dragY))
			return true;
		else
			return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
	}

	@Override
	protected void renderBg(MatrixStack pose, float partialTick, int mouseX, int mouseY) {
		renderBackground(pose);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bind(GUI_TEXTURE);
		blit(pose, leftPos, topPos, 0, 0, imageWidth, imageHeight);
	}

	@Override
	public void render(MatrixStack pose, int mouseX, int mouseY, float partialTick) {
		super.render(pose, mouseX, mouseY, partialTick);

		if (scrollList != null)
			scrollList.render(pose, mouseX, mouseY, partialTick);

		renderTooltip(pose, mouseX, mouseY);
		ClientUtils.renderModuleInfo(pose, ModuleType.SMART, smartModuleTooltip, hasSmartModule, leftPos + 5, topPos + 5, width, height, mouseX, mouseY);
	}

	@Override
	public void renderLabels(MatrixStack pose, int mouseX, int mouseY) {
		super.renderLabels(pose, mouseX, mouseY);
		font.draw(pose, scrollListTitle, imageWidth / 2 - font.width(scrollListTitle) / 2, 31, 4210752);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (minecraft.options.keyInventory.isActiveAndMatches(InputMappings.getKey(keyCode, scanCode))) {
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

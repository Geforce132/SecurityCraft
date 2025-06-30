package net.geforcemods.securitycraft.screen;

import java.util.List;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.components.SavedBlockState;
import net.geforcemods.securitycraft.inventory.DisguiseModuleMenu;
import net.geforcemods.securitycraft.network.server.SetStateOnDisguiseModule;
import net.geforcemods.securitycraft.screen.components.StateSelector;
import net.geforcemods.securitycraft.util.IHasExtraAreas;
import net.geforcemods.securitycraft.util.StandingOrWallType;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.CommonColors;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

public class DisguiseModuleScreen extends AbstractContainerScreen<DisguiseModuleMenu> implements IHasExtraAreas {
	private static final ResourceLocation TEXTURE = SecurityCraft.resLoc("textures/gui/container/customize1.png");
	private final Component disguiseModuleName = Utils.localize(SCContent.DISGUISE_MODULE.get().getDescriptionId());
	private StateSelector stateSelector;

	public DisguiseModuleScreen(DisguiseModuleMenu menu, Inventory inv, Component title) {
		super(menu, inv, title);
	}

	@Override
	protected void init() {
		super.init();

		leftPos += 90;
		stateSelector = addRenderableWidget(new StateSelector(menu, title, leftPos - 190, topPos + 7, 0, 200, 15, 176, -45));
		stateSelector.init(minecraft, width, height);
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		super.render(guiGraphics, mouseX, mouseY, partialTicks);
		renderTooltip(guiGraphics, mouseX, mouseY);
	}

	@Override
	protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		guiGraphics.drawString(font, disguiseModuleName, imageWidth / 2 - font.width(disguiseModuleName) / 2, 6, CommonColors.DARK_GRAY, false);
	}

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
		guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos, topPos, 0.0F, 0.0F, imageWidth, imageHeight, 256, 256);

		if (stateSelector != null)
			stateSelector.renderBackground(guiGraphics, mouseX, mouseY, partialTicks);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
		if (stateSelector != null && stateSelector.mouseDragged(mouseX, mouseY, button, dragX, dragY))
			return true;

		return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
	}

	@Override
	public void onClose() {
		super.onClose();

		ItemStack module = menu.getInventory().getModule();
		BlockState state = Blocks.AIR.defaultBlockState();
		StandingOrWallType standingOrWall = StandingOrWallType.NONE;

		if (!menu.getSlot(0).getItem().isEmpty() && stateSelector.getState() != null) {
			state = stateSelector.getState();
			standingOrWall = stateSelector.getStandingOrWallType();
		}

		module.set(SCContent.SAVED_BLOCK_STATE, new SavedBlockState(state, standingOrWall));
		ClientPacketDistributor.sendToServer(new SetStateOnDisguiseModule(state, standingOrWall));
	}

	@Override
	public List<Rect2i> getExtraAreas() {
		if (stateSelector != null)
			return stateSelector.getGuiExtraAreas();
		else
			return List.of();
	}
}

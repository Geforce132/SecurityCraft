package net.geforcemods.securitycraft.screen;

import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.inventory.DisguiseModuleMenu;
import net.geforcemods.securitycraft.network.server.SetStateOnDisguiseModule;
import net.geforcemods.securitycraft.screen.components.StateSelector;
import net.geforcemods.securitycraft.util.IHasExtraAreas;
import net.geforcemods.securitycraft.util.StandingOrWallType;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class DisguiseModuleScreen extends AbstractContainerScreen<DisguiseModuleMenu> implements IHasExtraAreas {
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/customize1.png");
	private final Component disguiseModuleName = Utils.localize(SCContent.DISGUISE_MODULE.get().getDescriptionId());
	private StateSelector stateSelector;

	public DisguiseModuleScreen(DisguiseModuleMenu menu, Inventory inv, Component title) {
		super(menu, inv, title);
	}

	@Override
	protected void init() {
		super.init();

		leftPos += 90;
		stateSelector = addRenderableWidget(new StateSelector(menu, title, leftPos - 190, topPos + 7, 0, 200, 15, -2.725F, -1.2F));
		stateSelector.init(minecraft, width, height);
	}

	@Override
	public void render(PoseStack pose, int mouseX, int mouseY, float partialTicks) {
		super.render(pose, mouseX, mouseY, partialTicks);
		renderTooltip(pose, mouseX, mouseY);
	}

	@Override
	protected void renderLabels(PoseStack pose, int mouseX, int mouseY) {
		font.draw(pose, disguiseModuleName, imageWidth / 2 - font.width(disguiseModuleName) / 2, 6, 0x404040);
	}

	@Override
	protected void renderBg(PoseStack pose, float partialTicks, int mouseX, int mouseY) {
		renderBackground(pose);
		RenderSystem._setShaderTexture(0, TEXTURE);
		blit(pose, leftPos, topPos, 0, 0, imageWidth, imageHeight);
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
		CompoundTag moduleTag = module.getOrCreateTag();
		BlockState state = Blocks.AIR.defaultBlockState();
		StandingOrWallType standingOrWall = StandingOrWallType.NONE;

		if (!menu.getSlot(0).getItem().isEmpty() && stateSelector.getState() != null) {
			state = stateSelector.getState();
			standingOrWall = stateSelector.getStandingOrWallType();
		}

		moduleTag.put("SavedState", NbtUtils.writeBlockState(state));
		moduleTag.putInt("StandingOrWall", standingOrWall.ordinal());
		SecurityCraft.CHANNEL.sendToServer(new SetStateOnDisguiseModule(state, standingOrWall));
	}

	@Override
	public List<Rect2i> getExtraAreas() {
		if (stateSelector != null)
			return stateSelector.getGuiExtraAreas();
		else
			return List.of();
	}
}

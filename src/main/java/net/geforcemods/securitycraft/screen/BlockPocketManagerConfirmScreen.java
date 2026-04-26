package net.geforcemods.securitycraft.screen;

import com.mojang.blaze3d.platform.InputConstants;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.BlockPocketManagerBlockEntity;
import net.geforcemods.securitycraft.network.server.ToggleBlockPocketManager;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.CommonColors;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

public class BlockPocketManagerConfirmScreen extends Screen {
	private static final ResourceLocation GUI_TEXTURE = SecurityCraft.resLoc("textures/gui/container/block_pocket_manager_confirm.png");
	private final BlockPocketManagerBlockEntity be;
	private int imageWidth = 226, imageHeight = 60, leftPos, topPos;

	public BlockPocketManagerConfirmScreen(BlockPocketManagerBlockEntity be) {
		super(Utils.localize("gui.securitycraft:blockPocketManager.confirmDisassembly"));
		this.be = be;
	}

	@Override
	protected void init() {
		super.init();
		leftPos = (width - imageWidth) / 2;
		topPos = (height - imageHeight) / 2;

		int buttonX = leftPos + 10;
		int buttonY = topPos + 30;

		addRenderableWidget(Button.builder(Component.translatable("gui.securitycraft:blockPocketManager.cancel"), b -> Minecraft.getInstance().popGuiLayer()).pos(buttonX, buttonY).size(98, 20).build());
		addRenderableWidget(Button.builder(Component.translatable("gui.securitycraft:blockPocketManager.confirm"), this::confirmDisassemblyButtonClicked).pos(buttonX + 106, buttonY).size(98, 20).build());
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		super.render(guiGraphics, mouseX, mouseY, partialTicks);
		guiGraphics.drawString(font, title, width / 2 - font.width(title) / 2, topPos + 6, CommonColors.DARK_GRAY, false);
	}

	@Override
	public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float a) {
		guiGraphics.blit(RenderPipelines.GUI_TEXTURED, GUI_TEXTURE, leftPos, topPos, 0.0F, 0.0F, imageWidth, imageHeight, 256, 256);
	}

	@Override
	public boolean keyPressed(KeyEvent event) {
		if (minecraft.options.keyInventory.isActiveAndMatches(InputConstants.getKey(event))) {
			onClose();
			return true;
		}

		return super.keyPressed(event);
	}

	public void confirmDisassemblyButtonClicked(Button button) {
		ClientPacketDistributor.sendToServer(new ToggleBlockPocketManager(be.getBlockPos(), be.getSize(), ToggleBlockPocketManager.Action.DISASSEMBLE));
		Minecraft.getInstance().player.closeContainer();
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}
}

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
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.PacketDistributor;

public class BlockPocketManagerConfirmScreen extends Screen {
	private static final ResourceLocation GUI_TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/block_pocket_manager_confirm.png");
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
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		guiGraphics.blit(GUI_TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
		super.render(guiGraphics, mouseX, mouseY, partialTick);
		guiGraphics.drawString(font, title, width / 2 - font.width(title) / 2, topPos + 6, 0x404040, false);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (minecraft.options.keyInventory.isActiveAndMatches(InputConstants.getKey(keyCode, scanCode))) {
			onClose();
			return true;
		}

		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	public void confirmDisassemblyButtonClicked(Button button) {
		SecurityCraft.CHANNEL.send(PacketDistributor.SERVER.noArg(), new ToggleBlockPocketManager(be, ToggleBlockPocketManager.Action.DISASSEMBLE));
		Minecraft.getInstance().player.closeContainer();
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}
}

package net.geforcemods.securitycraft.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.BlockChangeDetectorBlockEntity;
import net.geforcemods.securitycraft.inventory.GenericBEMenu;
import net.geforcemods.securitycraft.network.server.ClearChangeDetectorServer;
import net.geforcemods.securitycraft.screen.components.TextHoverChecker;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.client.gui.ScrollPanel;
import net.minecraftforge.client.gui.widget.ExtendedButton;

public class BlockChangeDetectorScreen extends AbstractContainerScreen<GenericBEMenu> {
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/state_selector.png");
	private static final TranslatableComponent CLEAR = Utils.localize("gui.securitycraft:editModule.clear");
	private static final TranslatableComponent BLOCK_NAME = Utils.localize(SCContent.BLOCK_CHANGE_DETECTOR.get().getDescriptionId());
	private BlockChangeDetectorBlockEntity be;
	private ChangeEntryList changeEntryList;
	TextHoverChecker hoverChecker;

	public BlockChangeDetectorScreen(GenericBEMenu menu, Inventory inv, Component title) {
		super(menu, inv, title);
		be = (BlockChangeDetectorBlockEntity) menu.be;
	}

	@Override
	protected void init() {
		super.init();

		Button clearButton = addRenderableWidget(new ExtendedButton(leftPos + 4, topPos + 4, 8, 8, new TextComponent("x"), b -> {
			be.getEntries().clear();
			be.setChanged();
			SecurityCraft.channel.sendToServer(new ClearChangeDetectorServer(be.getBlockPos()));
		}));

		clearButton.active = be.getOwner().isOwner(minecraft.player);
		hoverChecker = new TextHoverChecker(clearButton, CLEAR);
		addRenderableWidget(changeEntryList = new ChangeEntryList(minecraft, 200, 500, topPos + 20, leftPos + 12));
	}

	@Override
	protected void renderLabels(PoseStack pose, int mouseX, int mouseY) {
		font.draw(pose, BLOCK_NAME, imageWidth / 2 - font.width(BLOCK_NAME) / 2, 6, 0x404040);

		if (hoverChecker != null && hoverChecker.checkHover(mouseX, mouseY))
			renderTooltip(pose, CLEAR, mouseX - leftPos, mouseY - topPos);
	}

	@Override
	protected void renderBg(PoseStack pose, float partialTick, int mouseX, int mouseY) {
		renderBackground(pose);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem._setShaderTexture(0, TEXTURE);
		blit(pose, leftPos, topPos, 0, 0, imageWidth, imageHeight);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (changeEntryList != null)
			changeEntryList.mouseClicked(mouseX, mouseY, button);

		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if (changeEntryList != null)
			changeEntryList.mouseReleased(mouseX, mouseY, button);

		return super.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		if (changeEntryList != null)
			changeEntryList.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);

		return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
	}

	class ChangeEntryList extends ScrollPanel {
		public ChangeEntryList(Minecraft client, int width, int height, int top, int left) {
			super(client, width, height, top, left);
		}

		@Override
		protected int getContentHeight() {
			int height = 50 + (be.getEntries().size() * font.lineHeight);

			if (height < bottom - top - 8)
				height = bottom - top - 8;

			return height;
		}

		@Override
		protected void drawPanel(PoseStack pose, int entryRight, int relativeY, Tesselator tesselator, int mouseX, int mouseY) {
			int mouseListY = (int) (mouseY - top + scrollDistance - border);
			int slotIndex = mouseListY / 12;

			if (slotIndex >= 0 && slotIndex < be.getEntries().size())
				font.draw(pose, be.getEntries().get(slotIndex).toString(), left + width / 2 - font.width(be.getEntries().get(slotIndex).toString()) / 2, relativeY + (12 * slotIndex), 0xc6c6c6);
		}

		@Override
		public NarrationPriority narrationPriority() {
			return NarrationPriority.NONE;
		}

		@Override
		public void updateNarration(NarrationElementOutput narrationElementOutput) {}
	}
}

package net.geforcemods.securitycraft.screen;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.UsernameLoggerBlockEntity;
import net.geforcemods.securitycraft.network.server.ClearLoggerServer;
import net.geforcemods.securitycraft.screen.components.SmallXButton;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.widget.ScrollPanel;

public class UsernameLoggerScreen extends Screen {
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private final Component logged = Utils.localize("gui.securitycraft:logger.logged");
	private int imageWidth = 176;
	private int imageHeight = 166;
	private int leftPos;
	private int topPos;
	private UsernameLoggerBlockEntity be;
	private PlayerList playerList;

	public UsernameLoggerScreen(UsernameLoggerBlockEntity be) {
		super(be.getDisplayName());
		this.be = be;
	}

	@Override
	protected void init() {
		super.init();

		leftPos = (width - imageWidth) / 2;
		topPos = (height - imageHeight) / 2;

		Button clearButton = addRenderableWidget(clearButton = new SmallXButton(leftPos + 4, topPos + 4, b -> {
			be.players = new String[100];
			SecurityCraft.channel.sendToServer(new ClearLoggerServer(be.getBlockPos()));
		}));

		clearButton.active = be.isOwnedBy(minecraft.player);
		clearButton.setTooltip(Tooltip.create(Utils.localize("gui.securitycraft:editModule.clear")));
		addRenderableWidget(playerList = new PlayerList(minecraft, imageWidth - 24, imageHeight - 40, topPos + 20, leftPos + 12));
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		renderBackground(guiGraphics);
		guiGraphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
		super.render(guiGraphics, mouseX, mouseY, partialTick);
		guiGraphics.drawString(font, logged, width / 2 - font.width(logged) / 2, topPos + 6, 4210752, false);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (playerList != null)
			playerList.mouseClicked(mouseX, mouseY, button);

		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if (playerList != null)
			playerList.mouseReleased(mouseX, mouseY, button);

		return super.mouseReleased(mouseX, mouseY, button);
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

	class PlayerList extends ScrollPanel {
		private final DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.getDefault());
		private final int slotHeight = 12, listLength = 100;

		public PlayerList(Minecraft client, int width, int height, int top, int left) {
			super(client, width, height, top, left);
		}

		@Override
		protected int getContentHeight() {
			int height = be.players.length * (font.lineHeight + 3);

			if (height < bottom - top - 4)
				height = bottom - top - 4;

			return height;
		}

		@Override
		public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
			super.render(guiGraphics, mouseX, mouseY, partialTicks);

			if (be.isOwnedBy(minecraft.player)) {
				int mouseListY = (int) (mouseY - top + scrollDistance - border);
				int slotIndex = mouseListY / slotHeight;

				if (mouseX >= left && mouseX < right - 6 && slotIndex >= 0 && mouseListY >= 0 && slotIndex < listLength && mouseY >= top && mouseY <= bottom) {
					if (be.players[slotIndex] != null && !be.players[slotIndex].isEmpty()) {
						Component localized = Utils.localize("gui.securitycraft:logger.date", dateFormat.format(new Date(be.timestamps[slotIndex])));

						if (be.uuids[slotIndex] != null && !be.uuids[slotIndex].isEmpty())
							guiGraphics.renderTooltip(font, Component.literal(be.uuids[slotIndex]), mouseX, mouseY);

						guiGraphics.drawString(font, localized, leftPos + (imageWidth / 2 - font.width(localized) / 2), bottom + 5, 4210752, false);
					}
				}
			}
		}

		@Override
		protected void drawPanel(GuiGraphics guiGraphics, int entryRight, int relativeY, Tesselator tess, int mouseX, int mouseY) {
			int baseY = top + border - (int) scrollDistance;
			int slotBuffer = slotHeight - 4;
			int mouseListY = (int) (mouseY - top + scrollDistance - border);
			int slotIndex = mouseListY / slotHeight;

			//highlight hovered slot
			if (mouseX >= left && mouseX <= right - 6 && slotIndex >= 0 && mouseListY >= 0 && slotIndex < listLength && mouseY >= top && mouseY <= bottom) {
				if (be.players[slotIndex] != null && !be.players[slotIndex].isEmpty()) {
					int min = left;
					int max = entryRight - 6; //6 is the width of the scrollbar
					int slotTop = baseY + slotIndex * slotHeight;
					BufferBuilder bufferBuilder = tess.getBuilder();

					RenderSystem.enableBlend();
					RenderSystem.defaultBlendFunc();
					bufferBuilder.begin(Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
					bufferBuilder.vertex(min, slotTop + slotBuffer + 2, 0).uv(0, 1).color(0x80, 0x80, 0x80, 0xFF).endVertex();
					bufferBuilder.vertex(max, slotTop + slotBuffer + 2, 0).uv(1, 1).color(0x80, 0x80, 0x80, 0xFF).endVertex();
					bufferBuilder.vertex(max, slotTop - 2, 0).uv(1, 0).color(0x80, 0x80, 0x80, 0xFF).endVertex();
					bufferBuilder.vertex(min, slotTop - 2, 0).uv(0, 0).color(0x80, 0x80, 0x80, 0xFF).endVertex();
					bufferBuilder.vertex(min + 1, slotTop + slotBuffer + 1, 0).uv(0, 1).color(0x00, 0x00, 0x00, 0xFF).endVertex();
					bufferBuilder.vertex(max - 1, slotTop + slotBuffer + 1, 0).uv(1, 1).color(0x00, 0x00, 0x00, 0xFF).endVertex();
					bufferBuilder.vertex(max - 1, slotTop - 1, 0).uv(1, 0).color(0x00, 0x00, 0x00, 0xFF).endVertex();
					bufferBuilder.vertex(min + 1, slotTop - 1, 0).uv(0, 0).color(0x00, 0x00, 0x00, 0xFF).endVertex();
					BufferUploader.drawWithShader(bufferBuilder.end());
					RenderSystem.disableBlend();
				}
			}

			//draw entry strings
			for (int i = 0; i < be.players.length; i++) {
				if (be.players[i] != null && !be.players[i].equals(""))
					guiGraphics.drawString(font, be.players[i], left + width / 2 - font.width(be.players[i]) / 2, relativeY + (slotHeight * i), 0xC6C6C6, false);
			}
		}

		@Override
		public NarrationPriority narrationPriority() {
			return NarrationPriority.NONE;
		}

		@Override
		public void updateNarration(NarrationElementOutput narrationElementOutput) {}
	}
}

package net.geforcemods.securitycraft.screen;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.UsernameLoggerBlockEntity;
import net.geforcemods.securitycraft.network.server.ClearLoggerServer;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.widget.ExtendedButton;
import net.minecraftforge.client.gui.widget.ScrollPanel;

public class UsernameLoggerScreen extends Screen {
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private final Component logged = Utils.localize("gui.securitycraft:logger.logged");
	private final Component clear = Utils.localize("gui.securitycraft:editModule.clear");
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
		addRenderableWidget(new ExtendedButton(leftPos + 4, topPos + 4, 8, 8, Component.literal("x"), b -> {
			be.players = new String[100];
			SecurityCraft.channel.sendToServer(new ClearLoggerServer(be.getBlockPos()));
		})).active = be.getOwner().isOwner(minecraft.player);
		addRenderableWidget(playerList = new PlayerList(minecraft, imageWidth - 24, imageHeight - 40, topPos + 20, leftPos + 12));
	}

	@Override
	public void render(PoseStack pose, int mouseX, int mouseY, float partialTick) {
		renderBackground(pose);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem._setShaderTexture(0, TEXTURE);
		blit(pose, leftPos, topPos, 0, 0, imageWidth, imageHeight);
		super.render(pose, mouseX, mouseY, partialTick);
		font.draw(pose, logged, width / 2 - font.width(logged) / 2, topPos + 6, 4210752);

		if (mouseX >= leftPos + 4 && mouseY >= topPos + 4 && mouseX < leftPos + 4 + 8 && mouseY < topPos + 4 + 8)
			renderTooltip(pose, clear, mouseX, mouseY);
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
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		if (playerList != null)
			playerList.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);

		return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
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
			int height = 50 + (be.players.length * font.lineHeight);

			if (height < bottom - top - 8)
				height = bottom - top - 8;

			return height;
		}

		@Override
		public void render(PoseStack pose, int mouseX, int mouseY, float partialTicks) {
			super.render(pose, mouseX, mouseY, partialTicks);

			if (be.getOwner().isOwner(minecraft.player)) {
				int mouseListY = (int) (mouseY - top + scrollDistance - border);
				int slotIndex = mouseListY / slotHeight;

				if (mouseX >= left && mouseX < right - 6 && slotIndex >= 0 && mouseListY >= 0 && slotIndex < listLength && mouseY >= top && mouseY <= bottom) {
					if (be.players[slotIndex] != null && !be.players[slotIndex].isEmpty()) {
						Component localized = Utils.localize("gui.securitycraft:logger.date", dateFormat.format(new Date(be.timestamps[slotIndex])));

						if (be.uuids[slotIndex] != null && !be.uuids[slotIndex].isEmpty())
							renderTooltip(pose, Component.literal(be.uuids[slotIndex]), mouseX, mouseY);

						font.draw(pose, localized, leftPos + (imageWidth / 2 - font.width(localized) / 2), bottom + 5, 4210752);
					}
				}
			}
		}

		@Override
		protected void drawPanel(PoseStack pose, int entryRight, int relativeY, Tesselator tess, int mouseX, int mouseY) {
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
					RenderSystem.disableTexture();
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
					RenderSystem.enableTexture();
					RenderSystem.disableBlend();
				}
			}

			//draw entry strings
			for (int i = 0; i < be.players.length; i++) {
				if (be.players[i] != null && !be.players[i].equals(""))
					font.draw(pose, be.players[i], left + width / 2 - font.width(be.players[i]) / 2, relativeY + (slotHeight * i), 0xC6C6C6);
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

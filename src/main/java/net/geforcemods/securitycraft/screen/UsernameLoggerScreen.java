package net.geforcemods.securitycraft.screen;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.UsernameLoggerBlockEntity;
import net.geforcemods.securitycraft.network.server.ClearLoggerServer;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.client.gui.ScrollPanel;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

public class UsernameLoggerScreen extends Screen {
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private final TranslationTextComponent logged = Utils.localize("gui.securitycraft:logger.logged");
	private final TranslationTextComponent clear = Utils.localize("gui.securitycraft:editModule.clear");
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
		addButton(new ExtendedButton(leftPos + 4, topPos + 4, 8, 8, new StringTextComponent("x"), b -> {
			be.setPlayers(new String[100]);
			SecurityCraft.channel.sendToServer(new ClearLoggerServer(be.getBlockPos()));
		})).active = be.isOwnedBy(minecraft.player);
		children.add(playerList = new PlayerList(minecraft, imageWidth - 24, imageHeight - 40, topPos + 20, leftPos + 12));
	}

	@Override
	public void render(MatrixStack pose, int mouseX, int mouseY, float partialTicks) {
		renderBackground(pose);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bind(TEXTURE);
		blit(pose, leftPos, topPos, 0, 0, imageWidth, imageHeight);
		super.render(pose, mouseX, mouseY, partialTicks);

		if (playerList != null)
			playerList.render(pose, mouseX, mouseY, partialTicks);

		font.draw(pose, logged, width / 2 - font.width(logged) / 2, topPos + 6, 4210752);

		if (mouseX >= leftPos + 4 && mouseY >= topPos + 4 && mouseX < leftPos + 4 + 8 && mouseY < topPos + 4 + 8)
			renderTooltip(pose, clear, mouseX, mouseY);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scroll) {
		if (playerList != null && playerList.isMouseOver(mouseX, mouseY))
			playerList.mouseScrolled(mouseX, mouseY, scroll);

		return super.mouseScrolled(mouseX, mouseY, scroll);
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

	class PlayerList extends ScrollPanel {
		private final DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.getDefault());
		private static final int SLOTH_HEIGHT = 12, LIST_LENGTH = 100;

		public PlayerList(Minecraft client, int width, int height, int top, int left) {
			super(client, width, height, top, left);
		}

		@Override
		protected int getContentHeight() {
			int height = be.getPlayers().length * (font.lineHeight + 3);

			if (height < bottom - top - 4)
				height = bottom - top - 4;

			return height;
		}

		@Override
		public void render(MatrixStack pose, int mouseX, int mouseY, float partialTicks) {
			super.render(pose, mouseX, mouseY, partialTicks);

			if (be.isOwnedBy(minecraft.player)) {
				int mouseListY = (int) (mouseY - top + scrollDistance - border);
				int slotIndex = mouseListY / SLOTH_HEIGHT;

				if (mouseX >= left && mouseX < right - 6 && slotIndex >= 0 && mouseListY >= 0 && slotIndex < LIST_LENGTH && mouseY >= top && mouseY <= bottom) {
					String[] players = be.getPlayers();

					if (players[slotIndex] != null && !players[slotIndex].isEmpty()) {
						TranslationTextComponent localized = Utils.localize("gui.securitycraft:logger.date", dateFormat.format(new Date(be.getTimestamps()[slotIndex])));
						String[] uuids = be.getUuids();

						if (uuids[slotIndex] != null && !uuids[slotIndex].isEmpty())
							renderTooltip(pose, new StringTextComponent(uuids[slotIndex]), mouseX, mouseY);

						font.draw(pose, localized, leftPos + (imageWidth / 2 - font.width(localized) / 2), bottom + 5, 4210752);
					}
				}
			}
		}

		@Override
		protected void drawPanel(MatrixStack matrix, int entryRight, int relativeY, Tessellator tess, int mouseX, int mouseY) {
			int baseY = top + border - (int) scrollDistance;
			int slotBuffer = SLOTH_HEIGHT - 4;
			int mouseListY = (int) (mouseY - top + scrollDistance - border);
			int slotIndex = mouseListY / SLOTH_HEIGHT;
			String[] players = be.getPlayers();

			//highlight hovered slot
			if (mouseX >= left && mouseX <= right - 6 && slotIndex >= 0 && mouseListY >= 0 && slotIndex < LIST_LENGTH && mouseY >= top && mouseY <= bottom && players[slotIndex] != null && !players[slotIndex].isEmpty()) {
				int min = left;
				int max = entryRight - 6; //6 is the width of the scrollbar
				int slotTop = baseY + slotIndex * SLOTH_HEIGHT;
				BufferBuilder bufferBuilder = tess.getBuilder();

				RenderSystem.enableBlend();
				RenderSystem.disableTexture();
				RenderSystem.defaultBlendFunc();
				bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
				bufferBuilder.vertex(min, slotTop + slotBuffer + 2, 0).color(0x80, 0x80, 0x80, 0xFF).endVertex();
				bufferBuilder.vertex(max, slotTop + slotBuffer + 2, 0).color(0x80, 0x80, 0x80, 0xFF).endVertex();
				bufferBuilder.vertex(max, slotTop - 2, 0).color(0x80, 0x80, 0x80, 0xFF).endVertex();
				bufferBuilder.vertex(min, slotTop - 2, 0).color(0x80, 0x80, 0x80, 0xFF).endVertex();
				bufferBuilder.vertex(min + 1, slotTop + slotBuffer + 1, 0).color(0x00, 0x00, 0x00, 0xFF).endVertex();
				bufferBuilder.vertex(max - 1, slotTop + slotBuffer + 1, 0).color(0x00, 0x00, 0x00, 0xFF).endVertex();
				bufferBuilder.vertex(max - 1, slotTop - 1, 0).color(0x00, 0x00, 0x00, 0xFF).endVertex();
				bufferBuilder.vertex(min + 1, slotTop - 1, 0).color(0x00, 0x00, 0x00, 0xFF).endVertex();
				bufferBuilder.end();
				WorldVertexBufferUploader.end(bufferBuilder);
				RenderSystem.enableTexture();
				RenderSystem.disableBlend();
			}

			//draw entry strings
			for (int i = 0; i < players.length; i++) {
				if (players[i] != null && !players[i].equals(""))
					font.draw(matrix, players[i], left + width / 2 - font.width(players[i]) / 2, relativeY + (SLOTH_HEIGHT * i), 0xC6C6C6);
			}
		}
	}
}

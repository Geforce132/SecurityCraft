package net.geforcemods.securitycraft.screen.components;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;

import net.geforcemods.securitycraft.screen.components.SSSConnectionList.ConnectionAccessor;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Nameable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.client.gui.ScrollPanel;

public class SSSConnectionList<T extends Screen & ConnectionAccessor> extends ScrollPanel {
	private static final ResourceLocation BEACON_GUI = new ResourceLocation("textures/gui/container/beacon.png");
	private final int slotHeight = 12;
	private final T parent;
	private final List<ConnectionInfo> connectionInfo = new ArrayList<>();
	private final Font font;

	public SSSConnectionList(T parent, Minecraft client, int width, int height, int top, int left) {
		super(client, width, height, top, left);
		this.parent = parent;
		font = client.font;
		refreshPositions();
	}

	public void refreshPositions() {
		Level level = Minecraft.getInstance().level;

		connectionInfo.clear();

		for (BlockPos pos : parent.getPositions()) {
			BlockEntity be = level.getBlockEntity(pos);
			Component blockName;

			if (be instanceof Nameable nameable)
				blockName = nameable.getDisplayName();
			else
				blockName = Utils.localize(be.getBlockState().getBlock().getDescriptionId());

			connectionInfo.add(new ConnectionInfo(pos, blockName));
		}
	}

	@Override
	protected int getContentHeight() {
		int height = connectionInfo.size() * 12;

		if (height < bottom - top - 8)
			height = bottom - top - 8;

		return height;
	}

	@Override
	protected void drawPanel(PoseStack pose, int entryRight, int relativeY, Tesselator tesselator, int mouseX, int mouseY) {
		int baseY = top + border - (int) scrollDistance;
		int slotBuffer = slotHeight - 4;
		int mouseListY = (int) (mouseY - top + scrollDistance - (border / 2));
		int slotIndex = mouseListY / slotHeight;

		//highlight hovered slot
		if (mouseX >= left && mouseX <= right - 7 && slotIndex >= 0 && mouseListY >= 0 && slotIndex < connectionInfo.size() && mouseY >= top && mouseY <= bottom) {
			int min = left;
			int max = entryRight - 6; //6 is the width of the scrollbar
			int slotTop = baseY + slotIndex * slotHeight;
			BufferBuilder bufferBuilder = tesselator.getBuilder();

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

			RenderSystem._setShaderTexture(0, BEACON_GUI);
			blit(pose, left, slotTop - 3, 14, 14, 110, 219, 21, 22, 256, 256);
		}

		int i = 0;

		for (ConnectionInfo info : connectionInfo) {
			int yStart = relativeY + (slotHeight * i++);

			font.draw(pose, info.blockName, left + 13, yStart, 0xC6C6C6);
		}
	}

	@Override
	public void render(PoseStack pose, int mouseX, int mouseY, float partialTicks) {
		super.render(pose, mouseX, mouseY, partialTicks);

		//draw tooltip for long block names
		int mouseListY = (int) (mouseY - top + scrollDistance - border);
		int slotIndex = mouseListY / slotHeight;

		if (mouseX >= left && mouseX < right - 6 && slotIndex >= 0 && mouseListY >= 0 && slotIndex < connectionInfo.size() && mouseY >= top && mouseY <= bottom) {
			Component blockName = connectionInfo.get(slotIndex).blockName;
			int length = font.width(blockName);
			int baseY = top + border - (int) scrollDistance;

			if (length >= width - 6) //6 = barWidth
				parent.renderTooltip(pose, List.of(blockName), Optional.empty(), left + 1, baseY + (slotHeight * slotIndex + slotHeight));

			font.draw(pose, Utils.getFormattedCoordinates(connectionInfo.get(slotIndex).pos), left + 13, top + height + 5, 4210752);
		}
	}

	@Override
	protected boolean clickPanel(double mouseX, double mouseY, int button) {
		int slotIndex = (int) (mouseY + (border / 2)) / slotHeight;

		if (slotIndex >= 0 && mouseY >= 0 && mouseX < 13 && slotIndex < connectionInfo.size()) {
			parent.removePosition(connectionInfo.get(slotIndex).pos);
			Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
			return true;
		}

		return false;
	}

	@Override
	public NarrationPriority narrationPriority() {
		return NarrationPriority.NONE;
	}

	@Override
	public void updateNarration(NarrationElementOutput narrationElementOutput) {}

	public interface ConnectionAccessor {
		public Set<BlockPos> getPositions();

		public void removePosition(BlockPos pos);
	}

	private record ConnectionInfo(BlockPos pos, Component blockName) {}
}
package net.geforcemods.securitycraft.screen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.IToggleableEntries;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.client.gui.ScrollPanel;

public class ToggleListScreen<T> extends Screen {
	private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(SecurityCraft.MODID, "textures/gui/container/blank.png");
	public final ITextComponent scrollListTitle, smartModuleTooltip;
	private final int imageWidth = 176;
	private final int imageHeight = 166;
	private int leftPos;
	private int topPos;
	private final boolean hasSmartModule, hasRedstoneModule;
	private final IToggleableEntries<T> be;
	private ToggleScrollList toggleList;

	public ToggleListScreen(IToggleableEntries<T> be, String title, ITextComponent scrollListTitle, ITextComponent noSmartModule, ITextComponent smartModule) {
		super(new TranslationTextComponent(title));

		this.be = be;
		hasSmartModule = be instanceof IModuleInventory && ((IModuleInventory) be).isModuleEnabled(ModuleType.SMART);
		hasRedstoneModule = be instanceof IModuleInventory && ((IModuleInventory) be).isModuleEnabled(ModuleType.REDSTONE);
		this.scrollListTitle = scrollListTitle;
		smartModuleTooltip = hasSmartModule ? smartModule : noSmartModule;
	}

	@Override
	protected void init() {
		super.init();

		leftPos = (width - imageWidth) / 2;
		topPos = (height - imageHeight) / 2;
		children.add(toggleList = new ToggleScrollList(minecraft, imageWidth - 24, imageHeight - 60, topPos + 40, leftPos + 12));
	}

	@Override
	public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
		renderBackground(matrix);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bind(GUI_TEXTURE);
		blit(matrix, leftPos, topPos, 0, 0, imageWidth, imageHeight);
		super.render(matrix, mouseX, mouseY, partialTicks);

		if (toggleList != null)
			toggleList.render(matrix, mouseX, mouseY, partialTicks);

		font.draw(matrix, title, width / 2 - font.width(title) / 2, topPos + 6, 4210752);
		font.draw(matrix, scrollListTitle, width / 2 - font.width(scrollListTitle) / 2, topPos + 31, 4210752);
		ClientUtils.renderModuleInfo(matrix, ModuleType.SMART, smartModuleTooltip, hasSmartModule, leftPos + 5, topPos + 5, width, height, mouseX, mouseY);
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

	public class ToggleScrollList extends ScrollPanel {
		private final ResourceLocation BEACON_GUI = new ResourceLocation("textures/gui/container/beacon.png");
		private final int slotHeight = 12, listLength;
		private final List<T> orderedFilterList;
		private final Map<T, ITextComponent> typeNames = new HashMap<>();

		public ToggleScrollList(Minecraft client, int width, int height, int top, int left) {
			super(client, width, height, top, left);
			orderedFilterList = new ArrayList<>(be.getFilters().keySet());
			orderedFilterList.sort((e1, e2) -> {
				//the default entry always shows at the bottom of the list
				if (e1 == be.getDefaultType())
					return 1;
				else if (e2 == be.getDefaultType())
					return -1;
				else
					return Utils.localize(e1.toString()).getString().compareTo(Utils.localize(e2.toString()).getString());
			});
			listLength = orderedFilterList.size();
		}

		@Override
		protected int getContentHeight() {
			int height = listLength * (font.lineHeight + 3);

			if (height < bottom - top - 4)
				height = bottom - top - 4;

			return height;
		}

		@Override
		protected boolean clickPanel(double mouseX, double mouseY, int button) {
			if (hasSmartModule) {
				int slotIndex = (int) (mouseY + (border / 2)) / slotHeight;

				if (slotIndex >= 0 && slotIndex < listLength) {
					Minecraft mc = Minecraft.getInstance();
					double relativeMouseY = mc.mouseHandler.ypos() * mc.getWindow().getGuiScaledHeight() / mc.getWindow().getScreenHeight();

					if (relativeMouseY >= top && relativeMouseY <= bottom) {
						be.toggleFilter(orderedFilterList.get(slotIndex));
						Minecraft.getInstance().getSoundManager().play(SimpleSound.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
						return true;
					}
				}
			}

			return false;
		}

		@Override
		public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTick) {
			super.render(matrix, mouseX, mouseY, partialTick);

			int baseY = top + border - (int) scrollDistance;
			int mouseListY = (int) (mouseY - top + scrollDistance - (border / 2));
			int slotIndex = mouseListY / slotHeight;
			int slotBottom = baseY + (slotIndex + 1) * slotHeight;

			if (hasRedstoneModule && mouseX >= left && mouseX <= right - 7 && slotIndex >= 0 && mouseListY >= 0 && slotIndex < listLength && mouseY >= top && mouseY <= bottom) {
				int comparatorOutput = be.getComparatorOutputFunction().applyAsInt(orderedFilterList.get(slotIndex));

				if (comparatorOutput > 0)
					renderTooltip(matrix, new TranslationTextComponent("gui.securitycraft:toggleList.comparatorOutput", comparatorOutput), right - 8, slotBottom);
			}
		}

		@Override
		protected void drawPanel(MatrixStack matrix, int entryRight, int relativeY, Tessellator tess, int mouseX, int mouseY) {
			FontRenderer font = Minecraft.getInstance().font;
			int baseY = top + border - (int) scrollDistance;
			int slotBuffer = slotHeight - 4;
			int mouseListY = (int) (mouseY - top + scrollDistance - (border / 2));
			int slotIndex = mouseListY / slotHeight;

			//highlight hovered slot
			if (hasSmartModule && mouseX >= left && mouseX <= right - 7 && slotIndex >= 0 && mouseListY >= 0 && slotIndex < listLength && mouseY >= top && mouseY <= bottom) {
				int min = left;
				int max = entryRight - 6; //6 is the width of the scrollbar
				int slotTop = baseY + slotIndex * slotHeight;
				BufferBuilder bufferBuilder = tess.getBuilder();

				RenderSystem.enableBlend();
				RenderSystem.disableTexture();
				RenderSystem.defaultBlendFunc();
				bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
				bufferBuilder.vertex(min, slotTop + slotBuffer + 2, 0).uv(0, 1).color(0x80, 0x80, 0x80, 0xFF).endVertex();
				bufferBuilder.vertex(max, slotTop + slotBuffer + 2, 0).uv(1, 1).color(0x80, 0x80, 0x80, 0xFF).endVertex();
				bufferBuilder.vertex(max, slotTop - 2, 0).uv(1, 0).color(0x80, 0x80, 0x80, 0xFF).endVertex();
				bufferBuilder.vertex(min, slotTop - 2, 0).uv(0, 0).color(0x80, 0x80, 0x80, 0xFF).endVertex();
				bufferBuilder.vertex(min + 1, slotTop + slotBuffer + 1, 0).uv(0, 1).color(0x00, 0x00, 0x00, 0xFF).endVertex();
				bufferBuilder.vertex(max - 1, slotTop + slotBuffer + 1, 0).uv(1, 1).color(0x00, 0x00, 0x00, 0xFF).endVertex();
				bufferBuilder.vertex(max - 1, slotTop - 1, 0).uv(1, 0).color(0x00, 0x00, 0x00, 0xFF).endVertex();
				bufferBuilder.vertex(min + 1, slotTop - 1, 0).uv(0, 0).color(0x00, 0x00, 0x00, 0xFF).endVertex();
				bufferBuilder.end();
				WorldVertexBufferUploader.end(bufferBuilder);
				RenderSystem.enableTexture();
				RenderSystem.disableBlend();
			}

			int i = 0;

			//draw entry strings and indicators whether the filter is enabled
			for (T type : orderedFilterList) {
				ITextComponent name = typeNames.computeIfAbsent(type, t -> Utils.localize(t == be.getDefaultType() ? be.getDefaultTypeName() : t.toString()));
				int yStart = relativeY + (slotHeight * i);

				font.draw(matrix, name, left + width / 2 - font.width(name) / 2, yStart, 0xC6C6C6);
				minecraft.getTextureManager().bind(BEACON_GUI);
				blit(matrix, left, yStart - 3, 14, 14, be.getFilter(type) ? 88 : 110, 219, 21, 22, 256, 256);
				i++;
			}
		}
	}
}

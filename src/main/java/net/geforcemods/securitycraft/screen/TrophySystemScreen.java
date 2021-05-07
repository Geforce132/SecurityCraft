package net.geforcemods.securitycraft.screen;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.containers.GenericTEContainer;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.tileentity.TrophySystemTileEntity;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.client.gui.ScrollPanel;

public class TrophySystemScreen extends ContainerScreen<GenericTEContainer> {

	private static final ResourceLocation FILTER_ENABLED_TEXTURE = new ResourceLocation(SecurityCraft.MODID, "textures/gui/item_bound.png");
	private static final ResourceLocation FILTER_DISABLED_TEXTURE = new ResourceLocation(SecurityCraft.MODID, "textures/gui/item_not_bound.png");
	private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(SecurityCraft.MODID, "textures/gui/container/blank.png");
	private static final ResourceLocation SMART_MODULE_TEXTURE = new ResourceLocation(SecurityCraft.MODID, "textures/item/smart_module.png");
	private final TranslationTextComponent projectiles = ClientUtils.localize("gui.securitycraft:trophy_system.targetableProjectiles");
	private final TranslationTextComponent moduleRequired = ClientUtils.localize("gui.securitycraft:trophy_system.moduleRequired");
	private final TranslationTextComponent toggle = ClientUtils.localize("gui.securitycraft:trophy_system.toggle");
	private final TranslationTextComponent moddedProjectiles = ClientUtils.localize("gui.securitycraft:trophy_system.moddedProjectiles");
	private final boolean isSmart;
	private final List<EntityType<?>> orderedFilterList;
	private TrophySystemTileEntity tileEntity;
	private ProjectileScrollList projectileList;

	public TrophySystemScreen(GenericTEContainer container, PlayerInventory inv, ITextComponent name) {
		super(container, inv, name);

		this.tileEntity = (TrophySystemTileEntity)container.te;
		isSmart = tileEntity.hasModule(ModuleType.SMART);
		orderedFilterList = new ArrayList<>(tileEntity.getFilters().keySet());
		orderedFilterList.sort((e1, e2) -> {
			//the entry for modded projectiles always shows at the bottom of the list
			if(e1 == EntityType.PIG)
				return 1;
			else if(e2 == EntityType.PIG)
				return -1;
			else return e1.getName().getString().compareTo(e2.getName().getString());
		});
	}

	@Override
	protected void init() {
		super.init();

		children.add(projectileList = new ProjectileScrollList(minecraft, xSize - 24, ySize - 60, guiTop + 40, guiLeft + 12));
	}

	@Override
	protected void drawGuiContainerForegroundLayer(MatrixStack matrix, int mouseX, int mouseY) {
		font.drawText(matrix, title, xSize / 2 - font.getStringPropertyWidth(title) / 2, titleY, 4210752);
		font.drawText(matrix, projectiles, xSize / 2 - font.getStringPropertyWidth(projectiles) / 2, titleY + 25, 4210752);
	}

	@Override
	public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks)
	{
		super.render(matrix, mouseX, mouseY, partialTicks);

		if(projectileList != null)
			projectileList.render(matrix, mouseX, mouseY, partialTicks);

		float alpha = isSmart ? 1.0F : 0.5F;
		int moduleLeft = guiLeft + 5;
		int moduleRight = moduleLeft + 16;
		int moduleTop = guiTop + 5;
		int moduleBottom = moduleTop + 16;
		Matrix4f m4f = matrix.getLast().getMatrix();
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();

		RenderSystem.enableAlphaTest();
		RenderSystem.enableBlend();
		RenderSystem.defaultAlphaFunc();
		RenderSystem.defaultBlendFunc();
		minecraft.getTextureManager().bindTexture(SMART_MODULE_TEXTURE);
		bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX);
		bufferBuilder.pos(m4f, moduleLeft, moduleBottom, 0).color(1.0F, 1.0F, 1.0F, alpha).tex(0, 1).endVertex();
		bufferBuilder.pos(m4f, moduleRight, moduleBottom, 0).color(1.0F, 1.0F, 1.0F, alpha).tex(1, 1).endVertex();
		bufferBuilder.pos(m4f, moduleRight, moduleTop, 0).color(1.0F, 1.0F, 1.0F, alpha).tex(1, 0).endVertex();
		bufferBuilder.pos(m4f, moduleLeft, moduleTop, 0).color(1.0F, 1.0F, 1.0F, alpha).tex(0, 0).endVertex();
		bufferBuilder.finishDrawing();
		WorldVertexBufferUploader.draw(bufferBuilder);
		RenderSystem.disableBlend();

		if(mouseX >= moduleLeft && mouseX < moduleRight && mouseY >= moduleTop && mouseY <= moduleBottom)
			renderTooltip(matrix, isSmart ? toggle : moduleRequired, mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(MatrixStack matrix, float partialTicks, int mouseX, int mouseY) {
		int startX = (width - xSize) / 2;
		int startY = (height - ySize) / 2;

		renderBackground(matrix);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bindTexture(GUI_TEXTURE);
		this.blit(matrix, startX, startY, 0, 0, xSize, ySize);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY)
	{
		if(projectileList != null)
			projectileList.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);

		return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
	}

	class ProjectileScrollList extends ScrollPanel
	{
		private final int slotHeight = 12, listLength = orderedFilterList.size();

		public ProjectileScrollList(Minecraft client, int width, int height, int top, int left)
		{
			super(client, width, height, top, left);
		}

		@Override
		protected int getContentHeight()
		{
			int height = 50 + (listLength * font.FONT_HEIGHT);

			if(height < bottom - top - 8)
				height = bottom - top - 8;

			return height;
		}

		@Override
		protected boolean clickPanel(double mouseX, double mouseY, int button) {
			int slotIndex = (int)mouseY / slotHeight;

			if(isSmart && slotIndex >= 0 && mouseY >= 0 && slotIndex < listLength) {
				tileEntity.toggleFilter(orderedFilterList.get(slotIndex));
				return true;
			}

			return false;
		}

		@Override
		protected void drawPanel(MatrixStack matrix, int entryRight, int relativeY, Tessellator tess, int mouseX, int mouseY)
		{
			int baseY = top + border - (int)scrollDistance;
			int slotBuffer = slotHeight - 4;
			int mouseListY = (int)(mouseY - top + scrollDistance - (border / 2));
			int slotIndex = mouseListY / slotHeight;

			//highlight hovered slot
			if(isSmart && mouseX >= left && mouseX <= right - 7 && slotIndex >= 0 && mouseListY >= 0 && slotIndex < listLength && mouseY >= top && mouseY <= bottom) {
				int min = left;
				int max = entryRight - 6; //6 is the width of the scrollbar
				int slotTop = baseY + slotIndex * slotHeight;
				BufferBuilder bufferBuilder = tess.getBuffer();

				RenderSystem.enableBlend();
				RenderSystem.disableTexture();
				RenderSystem.defaultBlendFunc();
				bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
				bufferBuilder.pos(min, slotTop + slotBuffer + 2, 0).tex(0, 1).color(0x80, 0x80, 0x80, 0xFF).endVertex();
				bufferBuilder.pos(max, slotTop + slotBuffer + 2, 0).tex(1, 1).color(0x80, 0x80, 0x80, 0xFF).endVertex();
				bufferBuilder.pos(max, slotTop - 2, 0).tex(1, 0).color(0x80, 0x80, 0x80, 0xFF).endVertex();
				bufferBuilder.pos(min, slotTop - 2, 0).tex(0, 0).color(0x80, 0x80, 0x80, 0xFF).endVertex();
				bufferBuilder.pos(min + 1, slotTop + slotBuffer + 1, 0).tex(0, 1).color(0x00, 0x00, 0x00, 0xFF).endVertex();
				bufferBuilder.pos(max - 1, slotTop + slotBuffer + 1, 0).tex(1, 1).color(0x00, 0x00, 0x00, 0xFF).endVertex();
				bufferBuilder.pos(max - 1, slotTop - 1, 0).tex(1, 0).color(0x00, 0x00, 0x00, 0xFF).endVertex();
				bufferBuilder.pos(min + 1, slotTop - 1, 0).tex(0, 0).color(0x00, 0x00, 0x00, 0xFF).endVertex();
				bufferBuilder.finishDrawing();
				WorldVertexBufferUploader.draw(bufferBuilder);
				RenderSystem.enableTexture();
				RenderSystem.disableBlend();
			}

			int i = 0;

			//draw entry strings and indicators whether the filter is enabled
			for(EntityType<?> projectileType : orderedFilterList) {
				ITextComponent projectileName = projectileType == EntityType.PIG ? moddedProjectiles : projectileType.getName();
				int yStart = relativeY + (slotHeight * i);

				font.drawText(matrix, projectileName, left + width / 2 - font.getStringPropertyWidth(projectileName) / 2, yStart, 0xC6C6C6);
				minecraft.getTextureManager().bindTexture(tileEntity.getFilter(projectileType) ? FILTER_ENABLED_TEXTURE : FILTER_DISABLED_TEXTURE);
				blit(matrix, left, yStart - 2, 0, 0, 12, 12, 12, 12);
				i++;
			}
		}
	}
}

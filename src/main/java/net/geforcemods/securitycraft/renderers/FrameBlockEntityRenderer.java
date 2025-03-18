package net.geforcemods.securitycraft.renderers;

import org.lwjgl.opengl.GL11;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.FrameBlockEntity;
import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.blocks.FrameBlock;
import net.geforcemods.securitycraft.entity.camera.CameraController;
import net.geforcemods.securitycraft.items.ColorableItem;
import net.geforcemods.securitycraft.misc.GlobalPos;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

public class FrameBlockEntityRenderer extends TileEntitySpecialRenderer<FrameBlockEntity> {
	private static final ResourceLocation CAMERA_NOT_FOUND = new ResourceLocation(SecurityCraft.MODID, "textures/entity/frame/camera_not_found.png");
	private static final ResourceLocation INACTIVE = new ResourceLocation(SecurityCraft.MODID, "textures/entity/frame/inactive.png");
	private static final ResourceLocation NO_REDSTONE_SIGNAL = new ResourceLocation(SecurityCraft.MODID, "textures/entity/frame/no_redstone_signal.png");
	private static final ResourceLocation NOISE_BACKGROUND = new ResourceLocation(SecurityCraft.MODID, "entity/frame/noise_background");
	private static final ResourceLocation SELECT_CAMERA = new ResourceLocation(SecurityCraft.MODID, "textures/entity/frame/select_camera.png");
	private static final ResourceLocation WHITE = new ResourceLocation(SecurityCraft.MODID, "textures/entity/frame/white.png");

	@Override
	public void render(FrameBlockEntity be, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		EntityPlayerSP player = Minecraft.getMinecraft().player;

		if (be.isDisabled() || (!be.isOwnedBy(player) && !be.isAllowed(player)) || be.getCameraPositions().isEmpty())
			return;

		World level = be.getWorld();
		IBlockState state = be.getBlockState();
		GlobalPos cameraPos = be.getCurrentCamera();
		EnumFacing direction = state.getValue(FrameBlock.FACING);
		Vec3i normal = direction.getDirectionVec();
		final float margin = 0.0625F;
		float xStart = 0.0F;
		float xEnd = 1.0F;
		float zStart = 0.0F;
		float zEnd = 1.0F;

		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);

		switch (direction) {
			case NORTH:
				xStart = margin;
				xEnd = 1 - margin;
				zStart = zEnd = 0.05F;
				break;
			case SOUTH:
				xStart = 1 - margin;
				xEnd = margin;
				zStart = zEnd = 0.95F;
				break;
			case WEST:
				zStart = 1 - margin;
				zEnd = margin;
				xStart = xEnd = 0.05F;
				break;
			case EAST:
				zStart = margin;
				zEnd = 1 - margin;
				xStart = xEnd = 0.95F;
				break;
			default:
		}

		if (cameraPos == null)
			renderSolidTexture(SELECT_CAMERA, xStart, xEnd, zStart, zEnd, normal, margin);
		else if (be.redstoneSignalDisabled()) {
			renderNoise(xStart, xEnd, zStart, zEnd, normal, margin);
			renderCutoutTexture(NO_REDSTONE_SIGNAL, xStart, xEnd, zStart, zEnd, normal, margin);
		}
		else if (!be.hasClientInteracted()) {
			renderNoise(xStart, xEnd, zStart, zEnd, normal, margin);
			renderCutoutTexture(INACTIVE, xStart, xEnd, zStart, zEnd, normal, margin);
		}
		else if (!CameraController.isLinked(be, cameraPos) || !level.isBlockLoaded(cameraPos.pos()) || !(level.getTileEntity(cameraPos.pos()) instanceof SecurityCameraBlockEntity))
			renderSolidTexture(CAMERA_NOT_FOUND, xStart, xEnd, zStart, zEnd, normal, margin);
		else if (CameraController.currentlyCapturedCamera == null) { //Only when no camera is being captured, the frame may render, to prevent screen-in-screen rendering
			SecurityCameraBlockEntity cameraBlockEntity = (SecurityCameraBlockEntity) level.getTileEntity(cameraPos.pos());
			Framebuffer target = CameraController.getViewForFrame(cameraPos);

			if (target == null) {
				renderSolidTexture(CAMERA_NOT_FOUND, xStart, xEnd, zStart, zEnd, normal, margin);
				return;
			}

			ItemStack lens = cameraBlockEntity.getLensContainer().getStackInSlot(0);
			Tessellator tessellator = Tessellator.getInstance(); //ImmPtl ViewAreaRenderer#drawPortalViewTriangle (adapted for quads)
			BufferBuilder bufferBuilder = tessellator.getBuffer();

			target.bindFramebufferTexture();
			bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
			bufferBuilder.pos(xStart, margin, zStart).tex(1, 0).color(0xFF, 0xFF, 0xFF, 0xFF).endVertex();
			bufferBuilder.pos(xStart, 1 - margin, zStart).tex(1, 1).color(0xFF, 0xFF, 0xFF, 0xFF).endVertex();
			bufferBuilder.pos(xEnd, 1 - margin, zEnd).tex(0, 1).color(0xFF, 0xFF, 0xFF, 0xFF).endVertex();
			bufferBuilder.pos(xEnd, margin, zEnd).tex(0, 0).color(0xFF, 0xFF, 0xFF, 0xFF).endVertex();
			tessellator.draw();
			target.unbindFramebufferTexture();

			if (lens.getItem() instanceof ColorableItem && ((ColorableItem) lens.getItem()).hasColor(lens))
				renderOverlay(xStart, xEnd, zStart, zEnd, ((ColorableItem) lens.getItem()).getColor(lens) + (cameraBlockEntity.getOpacity() << 24), normal, margin);
		}

		GlStateManager.popMatrix();
	}

	private void renderNoise(float xStart, float xEnd, float zStart, float zEnd, Vec3i normal, float margin) {
		TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(NOISE_BACKGROUND.toString());

		renderTexture(TextureMap.LOCATION_BLOCKS_TEXTURE, xStart, xEnd, zStart, zEnd, sprite.getMinU(), sprite.getMinV(), sprite.getMaxU(), sprite.getMaxV(), 0xFFFFFF, normal, margin);
	}

	private void renderSolidTexture(ResourceLocation texture, float xStart, float xEnd, float zStart, float zEnd, Vec3i normal, float margin) {
		renderTexture(texture, xStart, xEnd, zStart, zEnd, 0, 0, 1, 1, 0xFFFFFFFF, normal, margin);
	}

	private void renderCutoutTexture(ResourceLocation texture, float xStart, float xEnd, float zStart, float zEnd, Vec3i normal, float margin) {
		GlStateManager.enableBlend();
		renderTexture(texture, xStart, xEnd, zStart, zEnd, 0, 0, 1, 1, 0xFFFFFFFF, normal, margin);
		GlStateManager.disableBlend();
	}

	private void renderOverlay(float xStart, float xEnd, float zStart, float zEnd, int color, Vec3i normal, float margin) {
		GlStateManager.enableBlend();
		renderTexture(WHITE, xStart, xEnd, zStart, zEnd, 0, 0, 1, 1, color, normal, margin);
		GlStateManager.disableBlend();
	}

	private void renderTexture(ResourceLocation texture, float xStart, float xEnd, float zStart, float zEnd, float u0, float v0, float u1, float v1, int color, Vec3i normal, float margin) {
		Tessellator tessellator = Tessellator.getInstance(); //ImmPtl ViewAreaRenderer#drawPortalViewTriangle (adapted for quads)
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		int nx = normal.getX();
		int ny = normal.getY();
		int nz = normal.getZ();
		int r = color >> 16 & 255;
		int g = color >> 8 & 255;
		int b = color & 255;
		int a = color >> 24;

		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
		bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
		bufferBuilder.pos(xStart, margin, zStart).tex(u1, v1).color(r, g, b, a).normal(nx, ny, nz).endVertex();
		bufferBuilder.pos(xStart, 1 - margin, zStart).tex(u1, v0).color(r, g, b, a).normal(nx, ny, nz).endVertex();
		bufferBuilder.pos(xEnd, 1 - margin, zEnd).tex(u0, v0).color(r, g, b, a).normal(nx, ny, nz).endVertex();
		bufferBuilder.pos(xEnd, margin, zEnd).tex(u0, v1).color(r, g, b, a).normal(nx, ny, nz).endVertex();
		tessellator.draw();
	}
}

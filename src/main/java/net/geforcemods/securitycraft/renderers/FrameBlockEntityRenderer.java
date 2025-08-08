package net.geforcemods.securitycraft.renderers;

import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import org.lwjgl.opengl.GL11;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.FrameBlockEntity;
import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.blocks.FrameBlock;
import net.geforcemods.securitycraft.entity.camera.CameraFeed;
import net.geforcemods.securitycraft.entity.camera.FrameFeedHandler;
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
		Vector4f innerVertices; //Both vectors have the following format: xStart, xEnd, zStart, zEnd
		Vector4f outerVertices;

		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);

		switch (direction) {
			case NORTH:
				innerVertices = new Vector4f(margin, 1 - margin, 0.05F, 0.05F);
				outerVertices = new Vector4f(margin, 1 - margin, 0.045F, 0.045F);
				break;
			case SOUTH:
				innerVertices = new Vector4f(1 - margin, margin, 0.95F, 0.95F);
				outerVertices = new Vector4f(1 - margin, margin, 0.955F, 0.955F);
				break;
			case WEST:
				innerVertices = new Vector4f(0.05F, 0.05F, 1 - margin, margin);
				outerVertices = new Vector4f(0.045F, 0.045F, 1 - margin, margin);
				break;
			case EAST:
				innerVertices = new Vector4f(0.95F, 0.95F, margin, 1 - margin);
				outerVertices = new Vector4f(0.955F, 0.955F, margin, 1 - margin);
				break;
			default:
				innerVertices = new Vector4f(0.0F, 1.0F, 0.0F, 1.0F);
				outerVertices = new Vector4f(0.0F, 1.0F, 0.0F, 1.0F);
				break;
		}

		if (cameraPos == null)
			renderSolidTexture(SELECT_CAMERA, innerVertices, normal, margin);
		else if (be.redstoneSignalDisabled()) {
			renderNoise(innerVertices, normal, margin);
			renderCutoutTexture(NO_REDSTONE_SIGNAL, outerVertices, normal, margin);
		}
		else if (!be.hasClientInteracted()) {
			renderNoise(innerVertices, normal, margin);
			renderCutoutTexture(INACTIVE, outerVertices, normal, margin);
		}
		else {
			CameraFeed feed = FrameFeedHandler.getFeed(cameraPos);

			if (feed == null || !feed.isFrameLinked(be) || !level.isBlockLoaded(cameraPos.pos()) || !(level.getTileEntity(cameraPos.pos()) instanceof SecurityCameraBlockEntity))
				renderSolidTexture(CAMERA_NOT_FOUND, innerVertices, normal, margin);
			else if (!FrameFeedHandler.isCapturingCamera()) { //Only rendering the frame when no camera is being captured prevents screen-in-screen rendering
				SecurityCameraBlockEntity cameraBlockEntity = (SecurityCameraBlockEntity) level.getTileEntity(cameraPos.pos());
				Framebuffer target = feed.renderTarget();
				Tessellator tessellator = Tessellator.getInstance(); //ImmPtl ViewAreaRenderer#drawPortalViewTriangle (adapted for quads)
				float xStart = innerVertices.x;
				float xEnd = innerVertices.y;
				float zStart = innerVertices.z;
				float zEnd = innerVertices.w;
				BufferBuilder bufferBuilder = tessellator.getBuffer();
				Vector3f backgroundColor = feed.backgroundColor();

				GlStateManager.disableLighting();
				GlStateManager.disableAlpha();
				renderOverlay(innerVertices, (int) (backgroundColor.x * 255.0F), (int) (backgroundColor.y * 255.0F), (int) (backgroundColor.z * 255.0F), 255, normal, margin);
				target.bindFramebufferTexture();
				bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
				bufferBuilder.pos(xStart, margin, zStart).tex(1, 0).color(0xFF, 0xFF, 0xFF, 0xFF).endVertex();
				bufferBuilder.pos(xStart, 1 - margin, zStart).tex(1, 1).color(0xFF, 0xFF, 0xFF, 0xFF).endVertex();
				bufferBuilder.pos(xEnd, 1 - margin, zEnd).tex(0, 1).color(0xFF, 0xFF, 0xFF, 0xFF).endVertex();
				bufferBuilder.pos(xEnd, margin, zEnd).tex(0, 0).color(0xFF, 0xFF, 0xFF, 0xFF).endVertex();
				tessellator.draw();
				target.unbindFramebufferTexture();

				ItemStack lens = cameraBlockEntity.getLensContainer().getStackInSlot(0);

				if (lens.getItem() instanceof ColorableItem && ((ColorableItem) lens.getItem()).hasColor(lens))
					renderOverlay(innerVertices, ((ColorableItem) lens.getItem()).getColor(lens) + (cameraBlockEntity.getOpacity() << 24), normal, margin);

				GlStateManager.enableAlpha();
				GlStateManager.enableLighting();
			}
		}

		GlStateManager.popMatrix();
	}

	private void renderNoise(Vector4f vertices, Vec3i normal, float margin) {
		TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(NOISE_BACKGROUND.toString());

		renderTexture(TextureMap.LOCATION_BLOCKS_TEXTURE, vertices, sprite.getMinU(), sprite.getMinV(), sprite.getMaxU(), sprite.getMaxV(), 255, 255, 255, 255, normal, margin);
	}

	private void renderSolidTexture(ResourceLocation texture, Vector4f vertices, Vec3i normal, float margin) {
		renderTexture(texture, vertices, 0, 0, 1, 1, 255, 255, 255, 255, normal, margin);
	}

	private void renderCutoutTexture(ResourceLocation texture, Vector4f vertices, Vec3i normal, float margin) {
		GlStateManager.enableBlend();
		renderTexture(texture, vertices, 0, 0, 1, 1, 255, 255, 255, 255, normal, margin);
		GlStateManager.disableBlend();
	}

	private void renderOverlay(Vector4f vertices, int color, Vec3i normal, float margin) {
		renderOverlay(vertices, color >> 16 & 255, color >> 8 & 255, color & 255, color >> 24, normal, margin);
	}

	private void renderOverlay(Vector4f vertices, int r, int g, int b, int a, Vec3i normal, float margin) {
		GlStateManager.enableBlend();
		renderTexture(WHITE, vertices, 0, 0, 1, 1, r, g, b, a, normal, margin);
		GlStateManager.disableBlend();
	}

	private void renderTexture(ResourceLocation texture, Vector4f vertices, float u0, float v0, float u1, float v1, int r, int g, int b, int a, Vec3i normal, float margin) {
		Tessellator tessellator = Tessellator.getInstance(); //ImmPtl ViewAreaRenderer#drawPortalViewTriangle (adapted for quads)
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		float xStart = vertices.x;
		float xEnd = vertices.y;
		float zStart = vertices.z;
		float zEnd = vertices.w;
		int nx = normal.getX();
		int ny = normal.getY();
		int nz = normal.getZ();

		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
		bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
		bufferBuilder.pos(xStart, margin, zStart).tex(u1, v1).color(r, g, b, a).normal(nx, ny, nz).endVertex();
		bufferBuilder.pos(xStart, 1 - margin, zStart).tex(u1, v0).color(r, g, b, a).normal(nx, ny, nz).endVertex();
		bufferBuilder.pos(xEnd, 1 - margin, zEnd).tex(u0, v0).color(r, g, b, a).normal(nx, ny, nz).endVertex();
		bufferBuilder.pos(xEnd, margin, zEnd).tex(u0, v1).color(r, g, b, a).normal(nx, ny, nz).endVertex();
		tessellator.draw();
	}
}

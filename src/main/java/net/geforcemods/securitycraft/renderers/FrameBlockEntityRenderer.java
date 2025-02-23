package net.geforcemods.securitycraft.renderers;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.FrameBlockEntity;
import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.blocks.FrameBlock;
import net.geforcemods.securitycraft.entity.camera.CameraController;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.IDyeableArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ColorHelper;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.World;

public class FrameBlockEntityRenderer extends TileEntityRenderer<FrameBlockEntity> {
	private static final ResourceLocation CAMERA_NOT_FOUND = new ResourceLocation(SecurityCraft.MODID, "textures/entity/frame/camera_not_found.png");
	private static final ResourceLocation INACTIVE = new ResourceLocation(SecurityCraft.MODID, "textures/entity/frame/inactive.png");
	private static final ResourceLocation NO_REDSTONE_SIGNAL = new ResourceLocation(SecurityCraft.MODID, "textures/entity/frame/no_redstone_signal.png");
	private static final RenderMaterial NOISE_BACKGROUND = new RenderMaterial(PlayerContainer.BLOCK_ATLAS, new ResourceLocation(SecurityCraft.MODID, "entity/frame/noise_background"));
	private static final ResourceLocation SELECT_CAMERA = new ResourceLocation(SecurityCraft.MODID, "textures/entity/frame/select_camera.png");
	private static final ResourceLocation WHITE = new ResourceLocation(SecurityCraft.MODID, "textures/entity/frame/white.png");

	public FrameBlockEntityRenderer(TileEntityRendererDispatcher terd) {
		super(terd);
	}

	@Override
	public void render(FrameBlockEntity be, float partialTicks, MatrixStack pose, IRenderTypeBuffer buffer, int packedLight, int packedOverlay) {
		PlayerEntity player = Minecraft.getInstance().player;

		if (be.isDisabled() || (!be.isOwnedBy(player) && !be.isAllowed(player)) || be.getCameraPositions().isEmpty())
			return;

		World level = be.getLevel();
		BlockState state = be.getBlockState();
		GlobalPos cameraPos = be.getCurrentCamera();
		Direction direction = state.getValue(FrameBlock.FACING);
		Vector3i normal = direction.getNormal();
		final float margin = 0.0625F;
		float xStart = 0.0F;
		float xEnd = 1.0F;
		float zStart = 0.0F;
		float zEnd = 1.0F;

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
			renderSolidTexture(pose, buffer, SELECT_CAMERA, xStart, xEnd, zStart, zEnd, packedLight, normal, margin);
		else if (be.redstoneSignalDisabled()) {
			renderNoise(pose, buffer, xStart, xEnd, zStart, zEnd, packedLight, normal, margin);
			renderCutoutTexture(pose, buffer, NO_REDSTONE_SIGNAL, xStart, xEnd, zStart, zEnd, packedLight, normal, margin);
		}
		else if (!be.hasClientInteracted()) {
			renderNoise(pose, buffer, xStart, xEnd, zStart, zEnd, packedLight, normal, margin);
			renderCutoutTexture(pose, buffer, INACTIVE, xStart, xEnd, zStart, zEnd, packedLight, normal, margin);
		}
		else if (!CameraController.isLinked(be, cameraPos) || !level.isLoaded(cameraPos.pos()) || !(level.getBlockEntity(cameraPos.pos()) instanceof SecurityCameraBlockEntity))
			renderSolidTexture(pose, buffer, CAMERA_NOT_FOUND, xStart, xEnd, zStart, zEnd, packedLight, normal, margin);
		else if (CameraController.currentlyCapturedCamera == null) { //Only when no camera is being captured, the frame may render, to prevent screen-in-screen rendering
			SecurityCameraBlockEntity cameraBlockEntity = (SecurityCameraBlockEntity) level.getBlockEntity(cameraPos.pos());
			Framebuffer target = CameraController.getViewForFrame(cameraPos);

			if (target == null) {
				renderSolidTexture(pose, buffer, CAMERA_NOT_FOUND, xStart, xEnd, zStart, zEnd, packedLight, normal, margin);
				return;
			}

			ItemStack lens = cameraBlockEntity.getLensContainer().getItem(0);
			target.bindRead();
			GlStateManager._enableDepthTest();

			Tessellator tessellator = Tessellator.getInstance(); //ImmPtl ViewAreaRenderer#drawPortalViewTriangle (adapted for quads)
			BufferBuilder bufferBuilder = tessellator.getBuilder();
			Matrix4f lastPose = pose.last().pose();

			bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX);
			bufferBuilder.vertex(lastPose, xStart, margin, zStart).color(0xFF, 0xFF, 0xFF, 0xFF).uv(1, 0).endVertex(); //TODO These vertices are the most prone-to-break point
			bufferBuilder.vertex(lastPose, xStart, 1 - margin, zStart).color(0xFF, 0xFF, 0xFF, 0xFF).uv(1, 1).endVertex();
			bufferBuilder.vertex(lastPose, xEnd, 1 - margin, zEnd).color(0xFF, 0xFF, 0xFF, 0xFF).uv(0, 1).endVertex();
			bufferBuilder.vertex(lastPose, xEnd, margin, zEnd).color(0xFF, 0xFF, 0xFF, 0xFF).uv(0, 0).endVertex();
			tessellator.end();

			target.unbindRead();
			GlStateManager._disableDepthTest();

			if (lens.getItem() instanceof IDyeableArmorItem && ((IDyeableArmorItem) lens.getItem()).hasCustomColor(lens))
				renderOverlay(pose, buffer, xStart, xEnd, zStart, zEnd, ((IDyeableArmorItem) lens.getItem()).getColor(lens) + (cameraBlockEntity.getOpacity() << 24), packedLight, normal, margin);
		}
	}

	private void renderNoise(MatrixStack pose, IRenderTypeBuffer buffer, float xStart, float xEnd, float zStart, float zEnd, int packedLight, Vector3i normal, float margin) {
		TextureAtlasSprite sprite = NOISE_BACKGROUND.sprite();

		renderTexture(pose, buffer, buffer.getBuffer(Atlases.solidBlockSheet()), xStart, xEnd, zStart, zEnd, sprite.getU0(), sprite.getV0(), sprite.getU1(), sprite.getV1(), 0xFFFFFF, packedLight, normal, margin);
	}

	private void renderSolidTexture(MatrixStack pose, IRenderTypeBuffer buffer, ResourceLocation texture, float xStart, float xEnd, float zStart, float zEnd, int packedLight, Vector3i normal, float margin) {
		renderTexture(pose, buffer, buffer.getBuffer(RenderType.entitySolid(texture)), xStart, xEnd, zStart, zEnd, 0, 0, 1, 1, 0xFFFFFFFF, packedLight, normal, margin);
	}

	private void renderCutoutTexture(MatrixStack pose, IRenderTypeBuffer buffer, ResourceLocation texture, float xStart, float xEnd, float zStart, float zEnd, int packedLight, Vector3i normal, float margin) {
		renderTexture(pose, buffer, buffer.getBuffer(RenderType.entityCutout(texture)), xStart, xEnd, zStart, zEnd, 0, 0, 1, 1, 0xFFFFFFFF, packedLight, normal, margin);
	}

	private void renderOverlay(MatrixStack pose, IRenderTypeBuffer buffer, float xStart, float xEnd, float zStart, float zEnd, int color, int packedLight, Vector3i normal, float margin) {
		renderTexture(pose, buffer, buffer.getBuffer(RenderType.entityTranslucent(WHITE)), xStart, xEnd, zStart, zEnd, 0, 0, 1, 1, color, packedLight, normal, margin);
	}

	private void renderTexture(MatrixStack pose, IRenderTypeBuffer buffer, IVertexBuilder bufferBuilder, float xStart, float xEnd, float zStart, float zEnd, float u0, float v0, float u1, float v1, int color, int packedLight, Vector3i normal, float margin) {
		Matrix4f lastPose = pose.last().pose();
		int nx = normal.getX();
		int ny = normal.getY();
		int nz = normal.getZ();
		int r = ColorHelper.PackedColor.red(color);
		int g = ColorHelper.PackedColor.green(color);
		int b = ColorHelper.PackedColor.blue(color);
		int a = ColorHelper.PackedColor.alpha(color);

		bufferBuilder.vertex(lastPose, xStart, margin, zStart).color(r, g, b, a).uv(u1, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(nx, ny, nz).endVertex();
		bufferBuilder.vertex(lastPose, xStart, 1 - margin, zStart).color(r, g, b, a).uv(u1, v0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(nx, ny, nz).endVertex();
		bufferBuilder.vertex(lastPose, xEnd, 1 - margin, zEnd).color(r, g, b, a).uv(u0, v0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(nx, ny, nz).endVertex();
		bufferBuilder.vertex(lastPose, xEnd, margin, zEnd).color(r, g, b, a).uv(u0, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(nx, ny, nz).endVertex();

		if (buffer instanceof IRenderTypeBuffer.Impl)
			((IRenderTypeBuffer.Impl) buffer).endBatch();
	}
}

package net.geforcemods.securitycraft.renderers;

import org.joml.Matrix4f;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.FrameBlockEntity;
import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.blocks.FrameBlock;
import net.geforcemods.securitycraft.entity.camera.CameraController;
import net.geforcemods.securitycraft.entity.camera.CameraFeed;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class FrameBlockEntityRenderer implements BlockEntityRenderer<FrameBlockEntity> {
	private static final ResourceLocation CAMERA_NOT_FOUND = new ResourceLocation(SecurityCraft.MODID, "textures/entity/frame/camera_not_found.png");
	private static final ResourceLocation INACTIVE = new ResourceLocation(SecurityCraft.MODID, "textures/entity/frame/inactive.png");
	private static final ResourceLocation NO_REDSTONE_SIGNAL = new ResourceLocation(SecurityCraft.MODID, "textures/entity/frame/no_redstone_signal.png");
	private static final Material NOISE_BACKGROUND = new Material(InventoryMenu.BLOCK_ATLAS, new ResourceLocation(SecurityCraft.MODID, "entity/frame/noise_background"));
	private static final ResourceLocation SELECT_CAMERA = new ResourceLocation(SecurityCraft.MODID, "textures/entity/frame/select_camera.png");
	private static final ResourceLocation WHITE = new ResourceLocation(SecurityCraft.MODID, "textures/entity/frame/white.png");
	//@formatter:off
	private static final RenderType CAMERA_IN_FRAME_RENDER_TYPE = RenderType.create(
			"frame_shader",
			DefaultVertexFormat.POSITION_TEX,
			VertexFormat.Mode.QUADS,
			1536,
			false,
			false,
			RenderType.CompositeState
				.builder()
				.setShaderState(new RenderStateShard.ShaderStateShard(() -> CameraController.cameraMonitorShader))
				.createCompositeState(false));
	//@formatter:on

	public FrameBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {}

	@Override
	public void render(FrameBlockEntity be, float partialTicks, PoseStack pose, MultiBufferSource buffer, int packedLight, int packedOverlay) {
		Player player = Minecraft.getInstance().player;

		if (be.isDisabled() || (!be.isOwnedBy(player) && !be.isAllowed(player)) || be.getCameraPositions().isEmpty())
			return;

		Level level = be.getLevel();
		BlockState state = be.getBlockState();
		GlobalPos cameraPos = be.getCurrentCamera();
		Direction direction = state.getValue(FrameBlock.FACING);
		Vec3i normal = direction.getNormal();
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
		else {
			CameraFeed feed = CameraController.FRAME_CAMERA_FEEDS.get(cameraPos);

			if (feed == null || !feed.isFrameLinked(be) || !level.isLoaded(cameraPos.pos()) || !(level.getBlockEntity(cameraPos.pos()) instanceof SecurityCameraBlockEntity cameraBlockEntity))
				renderSolidTexture(pose, buffer, CAMERA_NOT_FOUND, xStart, xEnd, zStart, zEnd, packedLight, normal, margin);
			else if (CameraController.currentlyCapturedCamera == null) { //Only when no camera is being captured, the frame may render, to prevent screen-in-screen rendering
				RenderTarget target = feed.renderTarget();
				ShaderInstance shader = CameraController.cameraMonitorShader;
				VertexConsumer bufferBuilder;
				Matrix4f lastPose;

				shader.setSampler("DiffuseSampler", target.getColorTextureId());

				if (shader.MODEL_VIEW_MATRIX != null)
					shader.MODEL_VIEW_MATRIX.set(pose.last().pose());

				if (shader.PROJECTION_MATRIX != null)
					shader.PROJECTION_MATRIX.set(RenderSystem.getProjectionMatrix());

				shader.apply();
				lastPose = pose.last().pose();
				bufferBuilder = buffer.getBuffer(CAMERA_IN_FRAME_RENDER_TYPE);
				bufferBuilder.vertex(lastPose, xStart, margin, zStart).uv(1, 0).color(0xFFFFFF).endVertex();
				bufferBuilder.vertex(lastPose, xStart, 1 - margin, zStart).uv(1, 1).color(0xFFFFFF).endVertex();
				bufferBuilder.vertex(lastPose, xEnd, 1 - margin, zEnd).uv(0, 1).color(0xFFFFFF).endVertex();
				bufferBuilder.vertex(lastPose, xEnd, margin, zEnd).uv(0, 0).color(0xFFFFFF).endVertex();
				shader.clear();

				if (buffer instanceof MultiBufferSource.BufferSource bufferSource)
					bufferSource.endBatch();

				ItemStack lens = cameraBlockEntity.getLensContainer().getItem(0);

				if (lens.getItem() instanceof DyeableLeatherItem item && item.hasCustomColor(lens))
					renderOverlay(pose, buffer, xStart, xEnd, zStart, zEnd, item.getColor(lens) + (cameraBlockEntity.getOpacity() << 24), packedLight, normal, margin);
			}
		}
	}

	private void renderNoise(PoseStack pose, MultiBufferSource buffer, float xStart, float xEnd, float zStart, float zEnd, int packedLight, Vec3i normal, float margin) {
		TextureAtlasSprite sprite = NOISE_BACKGROUND.sprite();

		renderTexture(pose, buffer, buffer.getBuffer(Sheets.solidBlockSheet()), xStart, xEnd, zStart, zEnd, sprite.getU0(), sprite.getV0(), sprite.getU1(), sprite.getV1(), 0xFFFFFF, packedLight, normal, margin);
	}

	private void renderSolidTexture(PoseStack pose, MultiBufferSource buffer, ResourceLocation texture, float xStart, float xEnd, float zStart, float zEnd, int packedLight, Vec3i normal, float margin) {
		renderTexture(pose, buffer, buffer.getBuffer(RenderType.entitySolid(texture)), xStart, xEnd, zStart, zEnd, 0, 0, 1, 1, 0xFFFFFF, packedLight, normal, margin);
	}

	private void renderCutoutTexture(PoseStack pose, MultiBufferSource buffer, ResourceLocation texture, float xStart, float xEnd, float zStart, float zEnd, int packedLight, Vec3i normal, float margin) {
		renderTexture(pose, buffer, buffer.getBuffer(RenderType.entityCutout(texture)), xStart, xEnd, zStart, zEnd, 0, 0, 1, 1, 0xFFFFFF, packedLight, normal, margin);
	}

	private void renderOverlay(PoseStack pose, MultiBufferSource buffer, float xStart, float xEnd, float zStart, float zEnd, int color, int packedLight, Vec3i normal, float margin) {
		renderTexture(pose, buffer, buffer.getBuffer(RenderType.entityTranslucent(WHITE)), xStart, xEnd, zStart, zEnd, 0, 0, 1, 1, color, packedLight, normal, margin);
	}

	private void renderTexture(PoseStack pose, MultiBufferSource buffer, VertexConsumer bufferBuilder, float xStart, float xEnd, float zStart, float zEnd, float u0, float v0, float u1, float v1, int color, int packedLight, Vec3i normal, float margin) {
		Matrix4f lastPose = pose.last().pose();
		int nx = normal.getX();
		int ny = normal.getY();
		int nz = normal.getZ();

		bufferBuilder.vertex(lastPose, xStart, margin, zStart).color(color).uv(u1, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(nx, ny, nz).endVertex();
		bufferBuilder.vertex(lastPose, xStart, 1 - margin, zStart).color(color).uv(u1, v0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(nx, ny, nz).endVertex();
		bufferBuilder.vertex(lastPose, xEnd, 1 - margin, zEnd).color(color).uv(u0, v0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(nx, ny, nz).endVertex();
		bufferBuilder.vertex(lastPose, xEnd, margin, zEnd).color(color).uv(u0, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(nx, ny, nz).endVertex();

		if (buffer instanceof MultiBufferSource.BufferSource bufferSource)
			bufferSource.endBatch();
	}
}

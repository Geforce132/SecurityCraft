package net.geforcemods.securitycraft.renderers;

import org.joml.Matrix4f;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.FrameBlockEntity;
import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.blocks.FrameBlock;
import net.geforcemods.securitycraft.entity.camera.CameraController;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class FrameBlockEntityRenderer implements BlockEntityRenderer<FrameBlockEntity> {
	private static final ResourceLocation SELECT_CAMERA = SecurityCraft.resLoc("textures/entity/frame/select_camera.png");
	private static final ResourceLocation INACTIVE = SecurityCraft.resLoc("textures/entity/frame/inactive.png");
	private static final ResourceLocation CAMERA_NOT_FOUND = SecurityCraft.resLoc("textures/entity/frame/camera_not_found.png");
	//@formatter:off
	public static final RenderType CAMERA_IN_FRAME_RENDER_TYPE = RenderType.create(
			"frame_shader",
			DefaultVertexFormat.POSITION_TEX,
			VertexFormat.Mode.QUADS,
			1536,
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
		float bottom = 0.125F;
		float top = 1.0F - bottom;
		float xStart = 0.0F;
		float xEnd = 1.0F;
		float zStart = 0.0F;
		float zEnd = 1.0F;

		switch (direction) {
			case Direction.NORTH:
				xStart = bottom;
				xEnd = top;
				zStart = zEnd = 0.05F;
				break;
			case Direction.SOUTH:
				xStart = top;
				xEnd = bottom;
				zStart = zEnd = 0.95F;
				break;
			case Direction.WEST:
				zStart = top;
				zEnd = bottom;
				xStart = xEnd = 0.05F;
				break;
			case Direction.EAST:
				zStart = bottom;
				zEnd = top;
				xStart = xEnd = 0.95F;
				break;
			default:
		}

		if (cameraPos == null)
			renderTexture(pose, buffer, SELECT_CAMERA, xStart, xEnd, zStart, zEnd, packedLight, normal);
		else if (be.redstoneSignalDisabled())
			renderOverlay(pose, buffer, 0xFF000000, xStart, xEnd, zStart, zEnd); //TODO: maybe render a background related to the frame needing to be activated by redstone
		else if (!be.hasClientInteracted())
			renderTexture(pose, buffer, INACTIVE, xStart, xEnd, zStart, zEnd, packedLight, normal);
		else if (!CameraController.isLinked(be, cameraPos) || !level.isLoaded(cameraPos.pos()) || !(level.getBlockEntity(cameraPos.pos()) instanceof SecurityCameraBlockEntity cameraBlockEntity))
			renderTexture(pose, buffer, CAMERA_NOT_FOUND, xStart, xEnd, zStart, zEnd, packedLight, normal);
		else if (CameraController.currentlyCapturedCamera == null) { //Only when no camera is being captured, the frame may render, to prevent screen-in-screen rendering
			ShaderInstance shader = CameraController.cameraMonitorShader;
			RenderTarget target = CameraController.getViewForFrame(cameraPos);
			ItemStack lens = cameraBlockEntity.getLensContainer().getItem(0);
			VertexConsumer bufferBuilder;
			Matrix4f lastPose;

			if (target == null) {
				renderTexture(pose, buffer, CAMERA_NOT_FOUND, xStart, xEnd, zStart, zEnd, packedLight, normal);
				return;
			}

			shader.setSampler("DiffuseSampler", target.getColorTextureId());

			if (shader.MODEL_VIEW_MATRIX != null)
				shader.MODEL_VIEW_MATRIX.set(pose.last().pose());

			if (shader.PROJECTION_MATRIX != null)
				shader.PROJECTION_MATRIX.set(RenderSystem.getProjectionMatrix());

			shader.apply();
			lastPose = pose.last().pose();
			bufferBuilder = buffer.getBuffer(CAMERA_IN_FRAME_RENDER_TYPE);
			bufferBuilder.addVertex(lastPose, xStart, bottom, zStart).setUv(1, 0).setColor(0xFFFFFF);
			bufferBuilder.addVertex(lastPose, xStart, top, zStart).setUv(1, 1).setColor(0xFFFFFF);
			bufferBuilder.addVertex(lastPose, xEnd, top, zEnd).setUv(0, 1).setColor(0xFFFFFF);
			bufferBuilder.addVertex(lastPose, xEnd, bottom, zEnd).setUv(0, 0).setColor(0xFFFFFF);
			shader.clear();

			if (buffer instanceof MultiBufferSource.BufferSource bufferSource)
				bufferSource.endBatch();

			if (lens.has(DataComponents.DYED_COLOR))
				renderOverlay(pose, buffer, lens.get(DataComponents.DYED_COLOR).rgb() + (cameraBlockEntity.getOpacity() << 24), xStart, xEnd, zStart, zEnd);
		}
	}

	private void renderTexture(PoseStack pose, MultiBufferSource buffer, ResourceLocation texture, float xStart, float xEnd, float zStart, float zEnd, int packedLight, Vec3i normal) {
		VertexConsumer bufferBuilder = buffer.getBuffer(RenderType.entitySolid(texture));
		Pose last = pose.last();
		Matrix4f lastPose = last.pose();
		int nx = normal.getX();
		int ny = normal.getY();
		int nz = normal.getZ();

		bufferBuilder.addVertex(lastPose, xStart, 0.125F, zStart).setUv(1, 1).setColor(0xFFFFFF).setLight(packedLight).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(last, nx, ny, nz);
		bufferBuilder.addVertex(lastPose, xStart, 0.875F, zStart).setUv(1, 0).setColor(0xFFFFFF).setLight(packedLight).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(last, nx, ny, nz);
		bufferBuilder.addVertex(lastPose, xEnd, 0.875F, zEnd).setUv(0, 0).setColor(0xFFFFFF).setLight(packedLight).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(last, nx, ny, nz);
		bufferBuilder.addVertex(lastPose, xEnd, 0.125F, zEnd).setUv(0, 1).setColor(0xFFFFFF).setLight(packedLight).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(last, nx, ny, nz);

		if (buffer instanceof MultiBufferSource.BufferSource bufferSource)
			bufferSource.endBatch();
	}

	private void renderOverlay(PoseStack pose, MultiBufferSource buffer, int color, float xStart, float xEnd, float zStart, float zEnd) {
		VertexConsumer bufferBuilder = buffer.getBuffer(RenderType.gui());

		bufferBuilder.addVertex(pose.last().pose(), xStart, 0.125F, zStart).setColor(color);
		bufferBuilder.addVertex(pose.last().pose(), xStart, 0.875F, zStart).setColor(color);
		bufferBuilder.addVertex(pose.last().pose(), xEnd, 0.875F, zEnd).setColor(color);
		bufferBuilder.addVertex(pose.last().pose(), xEnd, 0.125F, zEnd).setColor(color);

		if (buffer instanceof MultiBufferSource.BufferSource bufferSource)
			bufferSource.endBatch();
	}
}

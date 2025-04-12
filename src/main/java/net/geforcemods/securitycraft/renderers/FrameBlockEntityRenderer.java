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
import net.geforcemods.securitycraft.entity.camera.CameraFeed;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.CompiledShaderProgram;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderProgram;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class FrameBlockEntityRenderer implements BlockEntityRenderer<FrameBlockEntity> {
	private static final ResourceLocation CAMERA_NOT_FOUND = SecurityCraft.resLoc("textures/entity/frame/camera_not_found.png");
	private static final ResourceLocation INACTIVE = SecurityCraft.resLoc("textures/entity/frame/inactive.png");
	private static final ResourceLocation NO_REDSTONE_SIGNAL = SecurityCraft.resLoc("textures/entity/frame/no_redstone_signal.png");
	private static final Material NOISE_BACKGROUND = new Material(InventoryMenu.BLOCK_ATLAS, SecurityCraft.resLoc("entity/frame/noise_background"));
	private static final ResourceLocation SELECT_CAMERA = SecurityCraft.resLoc("textures/entity/frame/select_camera.png");
	//@formatter:off
	private static final RenderType CAMERA_IN_FRAME_RENDER_TYPE = RenderType.create(
			"frame_shader",
			DefaultVertexFormat.POSITION_TEX,
			VertexFormat.Mode.QUADS,
			1536,
			RenderType.CompositeState
				.builder()
				.setShaderState(new RenderStateShard.ShaderStateShard(CameraController.cameraMonitorShader))
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
		Vec3i normal = direction.getUnitVec3i();
		final float margin = 0.0625F;
		float xStart = 0.0F;
		float xEnd = 1.0F;
		float zStart = 0.0F;
		float zEnd = 1.0F;

		switch (direction) {
			case Direction.NORTH:
				xStart = margin;
				xEnd = 1 - margin;
				zStart = zEnd = 0.05F;
				break;
			case Direction.SOUTH:
				xStart = 1 - margin;
				xEnd = margin;
				zStart = zEnd = 0.95F;
				break;
			case Direction.WEST:
				zStart = 1 - margin;
				zEnd = margin;
				xStart = xEnd = 0.05F;
				break;
			case Direction.EAST:
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
				ShaderProgram shader = CameraController.cameraMonitorShader;
				VertexConsumer bufferBuilder;
				Matrix4f lastPose;
				CompiledShaderProgram compiledShader = RenderSystem.setShader(shader);

				compiledShader.bindSampler("DiffuseSampler", target.getColorTextureId());

				if (compiledShader.MODEL_VIEW_MATRIX != null)
					compiledShader.MODEL_VIEW_MATRIX.set(pose.last().pose());

				if (compiledShader.PROJECTION_MATRIX != null)
					compiledShader.PROJECTION_MATRIX.set(RenderSystem.getProjectionMatrix());

				compiledShader.apply();
				lastPose = pose.last().pose();
				bufferBuilder = buffer.getBuffer(CAMERA_IN_FRAME_RENDER_TYPE);
				bufferBuilder.addVertex(lastPose, xStart, margin, zStart).setUv(1, 0).setColor(0xFF0000);
				bufferBuilder.addVertex(lastPose, xStart, 1 - margin, zStart).setUv(1, 1).setColor(0xFFFFFF);
				bufferBuilder.addVertex(lastPose, xEnd, 1 - margin, zEnd).setUv(0, 1).setColor(0xFFFFFF);
				bufferBuilder.addVertex(lastPose, xEnd, margin, zEnd).setUv(0, 0).setColor(0xFFFFFF);
				compiledShader.clear();
				RenderSystem.clearShader();

				if (buffer instanceof MultiBufferSource.BufferSource bufferSource)
					bufferSource.endBatch();

				ItemStack lens = cameraBlockEntity.getLensContainer().getItem(0);

				if (lens.has(DataComponents.DYED_COLOR))
					renderOverlay(pose, buffer, lens.get(DataComponents.DYED_COLOR).rgb() + (cameraBlockEntity.getOpacity() << 24), xStart, xEnd, zStart, zEnd, margin);
			}
		}
	}

	private void renderNoise(PoseStack pose, MultiBufferSource buffer, float xStart, float xEnd, float zStart, float zEnd, int packedLight, Vec3i normal, float margin) {
		renderTexture(pose, buffer, NOISE_BACKGROUND.buffer(buffer, RenderType::entitySolid), xStart, xEnd, zStart, zEnd, packedLight, normal, margin);
	}

	private void renderSolidTexture(PoseStack pose, MultiBufferSource buffer, ResourceLocation texture, float xStart, float xEnd, float zStart, float zEnd, int packedLight, Vec3i normal, float margin) {
		renderTexture(pose, buffer, buffer.getBuffer(RenderType.entitySolid(texture)), xStart, xEnd, zStart, zEnd, packedLight, normal, margin);
	}

	private void renderCutoutTexture(PoseStack pose, MultiBufferSource buffer, ResourceLocation texture, float xStart, float xEnd, float zStart, float zEnd, int packedLight, Vec3i normal, float margin) {
		renderTexture(pose, buffer, buffer.getBuffer(RenderType.entityCutout(texture)), xStart, xEnd, zStart, zEnd, packedLight, normal, margin);
	}

	private void renderTexture(PoseStack pose, MultiBufferSource buffer, VertexConsumer bufferBuilder, float xStart, float xEnd, float zStart, float zEnd, int packedLight, Vec3i normal, float margin) {
		Pose last = pose.last();
		Matrix4f lastPose = last.pose();
		int nx = normal.getX();
		int ny = normal.getY();
		int nz = normal.getZ();

		bufferBuilder.addVertex(lastPose, xStart, margin, zStart).setUv(1, 1).setColor(0xFFFFFF).setLight(packedLight).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(last, nx, ny, nz);
		bufferBuilder.addVertex(lastPose, xStart, 1 - margin, zStart).setUv(1, 0).setColor(0xFFFFFF).setLight(packedLight).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(last, nx, ny, nz);
		bufferBuilder.addVertex(lastPose, xEnd, 1 - margin, zEnd).setUv(0, 0).setColor(0xFFFFFF).setLight(packedLight).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(last, nx, ny, nz);
		bufferBuilder.addVertex(lastPose, xEnd, margin, zEnd).setUv(0, 1).setColor(0xFFFFFF).setLight(packedLight).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(last, nx, ny, nz);

		if (buffer instanceof MultiBufferSource.BufferSource bufferSource)
			bufferSource.endBatch();
	}

	private void renderOverlay(PoseStack pose, MultiBufferSource buffer, int color, float xStart, float xEnd, float zStart, float zEnd, float margin) {
		VertexConsumer bufferBuilder = buffer.getBuffer(RenderType.gui());
		Matrix4f lastPose = pose.last().pose();

		bufferBuilder.addVertex(lastPose, xStart, margin, zStart).setColor(color);
		bufferBuilder.addVertex(lastPose, xStart, 1 - margin, zStart).setColor(color);
		bufferBuilder.addVertex(lastPose, xEnd, 1 - margin, zEnd).setColor(color);
		bufferBuilder.addVertex(lastPose, xEnd, margin, zEnd).setColor(color);

		if (buffer instanceof MultiBufferSource.BufferSource bufferSource)
			bufferSource.endBatch();
	}
}

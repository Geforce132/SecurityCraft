package net.geforcemods.securitycraft.renderers;

import java.util.OptionalDouble;
import java.util.OptionalInt;

import org.joml.Matrix4f;

import com.mojang.blaze3d.buffers.BufferType;
import com.mojang.blaze3d.buffers.BufferUsage;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.shaders.UniformType;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexSorting;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.FrameBlockEntity;
import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.blocks.FrameBlock;
import net.geforcemods.securitycraft.entity.camera.CameraController;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class FrameBlockEntityRenderer implements BlockEntityRenderer<FrameBlockEntity> {
	private static final ResourceLocation CAMERA_NOT_FOUND = SecurityCraft.resLoc("textures/entity/frame/camera_not_found.png");
	private static final ResourceLocation INACTIVE = SecurityCraft.resLoc("textures/entity/frame/inactive.png");
	private static final ResourceLocation NO_REDSTONE_SIGNAL = SecurityCraft.resLoc("textures/entity/frame/no_redstone_signal.png");
	private static final Material NOISE_BACKGROUND = new Material(TextureAtlas.LOCATION_BLOCKS, SecurityCraft.resLoc("entity/frame/noise_background"));
	private static final ResourceLocation SELECT_CAMERA = SecurityCraft.resLoc("textures/entity/frame/select_camera.png");
	//@formatter:off
	public static final RenderPipeline FRAME_PIPELINE = RenderPipeline.builder()
			.withLocation(SecurityCraft.resLoc("pipeline/frame_draw_fb_in_area"))
			.withVertexShader(SecurityCraft.resLoc("frame_draw_fb_in_area"))
			.withFragmentShader(SecurityCraft.resLoc("frame_draw_fb_in_area"))
			.withVertexFormat(DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS)
			.withSampler("InSampler")
			.withUniform("ModelViewMat", UniformType.MATRIX4X4)
			.withUniform("ProjMat", UniformType.MATRIX4X4)
			.withBlend(BlendFunction.TRANSLUCENT)
			.withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
			.build();
	//@formatter:on

	public FrameBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {}

	@Override
	public void render(FrameBlockEntity be, float partialTicks, PoseStack pose, MultiBufferSource buffer, int packedLight, int packedOverlay, Vec3 viewPos) {
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
		else if (!CameraController.isLinked(be, cameraPos) || !level.isLoaded(cameraPos.pos()) || !(level.getBlockEntity(cameraPos.pos()) instanceof SecurityCameraBlockEntity cameraBlockEntity))
			renderSolidTexture(pose, buffer, CAMERA_NOT_FOUND, xStart, xEnd, zStart, zEnd, packedLight, normal, margin);
		else if (CameraController.currentlyCapturedCamera == null) { //Only when no camera is being captured, the frame may render, to prevent screen-in-screen rendering
			RenderTarget target = CameraController.getViewForFrame(cameraPos);

			if (target == null) {
				renderSolidTexture(pose, buffer, CAMERA_NOT_FOUND, xStart, xEnd, zStart, zEnd, packedLight, normal, margin);
				return;
			}

			ItemStack lens = cameraBlockEntity.getLensContainer().getItem(0);

			try (ByteBufferBuilder byteBufferBuilder = new ByteBufferBuilder(DefaultVertexFormat.POSITION_TEX.getVertexSize() * 4)) {
				BufferBuilder bufferBuilder = new BufferBuilder(byteBufferBuilder, VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

				bufferBuilder.addVertex(pose.last().pose(), xStart, margin, zStart).setUv(1, 0);
				bufferBuilder.addVertex(pose.last().pose(), xStart, 1 - margin, zStart).setUv(1, 1);
				bufferBuilder.addVertex(pose.last().pose(), xEnd, 1 - margin, zEnd).setUv(0, 1);
				bufferBuilder.addVertex(pose.last().pose(), xEnd, margin, zEnd).setUv(0, 0);

				try (MeshData meshData = bufferBuilder.buildOrThrow()) {
					meshData.sortQuads(byteBufferBuilder, VertexSorting.DISTANCE_TO_ORIGIN);

					GpuDevice device = RenderSystem.getDevice();
					GpuBuffer vertexBuffer = device.createBuffer(() -> "Frame Vertex", BufferType.VERTICES, BufferUsage.STATIC_WRITE, meshData.vertexBuffer());
					GpuBuffer indexBuffer = device.createBuffer(() -> "Frame Index", BufferType.INDICES, BufferUsage.STATIC_WRITE, meshData.indexBuffer());

					try (RenderPass pass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(target.getColorTexture(), OptionalInt.of(0xFFFFFFFF), target.getDepthTexture(), OptionalDouble.empty())) {
						pass.setPipeline(FRAME_PIPELINE);
						pass.setVertexBuffer(0, vertexBuffer);
						pass.setIndexBuffer(indexBuffer, meshData.drawState().indexType());
						pass.setUniform("ModelViewMat", pose.last().pose());
						pass.setUniform("ProjMat", Minecraft.getInstance().gameRenderer.getProjectionMatrix(90.0F));
						pass.bindSampler("InSampler", RenderSystem.getShaderTexture(0));
						pass.drawIndexed(0, 6);
					}

					vertexBuffer.close();
				}
			}

			if (lens.has(DataComponents.DYED_COLOR))
				renderOverlay(pose, buffer, lens.get(DataComponents.DYED_COLOR).rgb() + (cameraBlockEntity.getOpacity() << 24), xStart, xEnd, zStart, zEnd, margin);
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

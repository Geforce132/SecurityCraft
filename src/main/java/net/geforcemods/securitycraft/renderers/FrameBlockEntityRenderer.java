package net.geforcemods.securitycraft.renderers;

import java.util.OptionalDouble;
import java.util.OptionalInt;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexSorting;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.FrameBlockEntity;
import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.blocks.FrameBlock;
import net.geforcemods.securitycraft.entity.camera.CameraFeed;
import net.geforcemods.securitycraft.entity.camera.FrameFeedHandler;
import net.geforcemods.securitycraft.renderers.state.FrameRenderState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class FrameBlockEntityRenderer implements BlockEntityRenderer<FrameBlockEntity, FrameRenderState> {
	private static final ResourceLocation CAMERA_NOT_FOUND = SecurityCraft.resLoc("textures/entity/frame/camera_not_found.png");
	private static final ResourceLocation INACTIVE = SecurityCraft.resLoc("textures/entity/frame/inactive.png");
	private static final ResourceLocation NO_REDSTONE_SIGNAL = SecurityCraft.resLoc("textures/entity/frame/no_redstone_signal.png");
	private static final Material NOISE_BACKGROUND = new Material(TextureAtlas.LOCATION_BLOCKS, SecurityCraft.resLoc("entity/frame/noise_background"));
	private static final ResourceLocation SELECT_CAMERA = SecurityCraft.resLoc("textures/entity/frame/select_camera.png");
	private static final ResourceLocation WHITE = SecurityCraft.resLoc("textures/entity/frame/white.png");
	//@formatter:off
	public static final RenderPipeline FRAME_PIPELINE = RenderPipeline.builder(RenderPipelines.MATRICES_PROJECTION_SNIPPET)
			.withLocation(SecurityCraft.resLoc("pipeline/frame_draw_fb_in_area"))
			.withVertexShader(SecurityCraft.resLoc("frame_draw_fb_in_area"))
			.withFragmentShader(SecurityCraft.resLoc("frame_draw_fb_in_area"))
			.withVertexFormat(DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS)
			.withSampler("InSampler")
			.withBlend(BlendFunction.TRANSLUCENT)
			.withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
			.build();
	//@formatter:on

	public FrameBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {}

	@Override
	public void submit(FrameRenderState state, PoseStack pose, SubmitNodeCollector collector, CameraRenderState camera) {
		if (state.isDisabled || !state.canSeeFeed || !state.hasCamerasLinked)
			return;

		final float margin = 0.0625F;
		Vector4f innerVertices = state.innerVertices;
		Vector4f outerVertices = state.outerVertices;
		Vec3i normal = state.normal;
		int lightCoords = state.lightCoords;

		if (state.isCameraSelected)
			submitSolidTexture(pose, collector, SELECT_CAMERA, innerVertices, lightCoords, normal, margin);
		else if (state.isRedstoneSignalDisabled) {
			submitNoise(pose, collector, state.innerVertices, lightCoords, normal, margin);
			submitCutoutTexture(pose, collector, NO_REDSTONE_SIGNAL, outerVertices, lightCoords, normal, margin);
		}
		else if (!state.hasClientInteracted) {
			submitNoise(pose, collector, innerVertices, lightCoords, normal, margin);
			submitCutoutTexture(pose, collector, INACTIVE, outerVertices, lightCoords, normal, margin);
		}
		else {
			if (!state.isCameraPresent)
				submitSolidTexture(pose, collector, CAMERA_NOT_FOUND, innerVertices, lightCoords, normal, margin);
			else if (!FrameFeedHandler.isCapturingCamera()) { //Only rendering the frame when no camera is being captured prevents screen-in-screen rendering
				//RenderTarget target = feed.renderTarget();
				Vector3f backgroundColor = state.backgroundColor;
				float xStart = innerVertices.x;
				float xEnd = innerVertices.y;
				float zStart = innerVertices.z;
				float zEnd = innerVertices.w;

				submitOverlay(pose, collector, RenderType.entityShadow(WHITE), ARGB.colorFromFloat(1.0F, backgroundColor.x, backgroundColor.y, backgroundColor.z), xStart, xEnd, zStart, zEnd, margin, lightCoords, normal);

				try (ByteBufferBuilder byteBufferBuilder = new ByteBufferBuilder(DefaultVertexFormat.POSITION_TEX.getVertexSize() * 4)) {
					BufferBuilder bufferBuilder = new BufferBuilder(byteBufferBuilder, VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

					bufferBuilder.addVertex(pose.last().pose(), xStart, margin, zStart).setUv(1, 0);
					bufferBuilder.addVertex(pose.last().pose(), xStart, 1 - margin, zStart).setUv(1, 1);
					bufferBuilder.addVertex(pose.last().pose(), xEnd, 1 - margin, zEnd).setUv(0, 1);
					bufferBuilder.addVertex(pose.last().pose(), xEnd, margin, zEnd).setUv(0, 0);

					try (MeshData meshData = bufferBuilder.buildOrThrow()) {
						meshData.sortQuads(byteBufferBuilder, VertexSorting.DISTANCE_TO_ORIGIN);

						GpuDevice device = RenderSystem.getDevice();
						GpuBuffer vertexBuffer = device.createBuffer(() -> "Frame Vertex", 32, meshData.vertexBuffer());
						GpuBuffer indexBuffer = device.createBuffer(() -> "Frame Index", 72, meshData.indexBuffer());
						RenderTarget mainRenderTarget = Minecraft.getInstance().getMainRenderTarget();
						GpuBufferSlice dynamicTransforms = RenderSystem.getDynamicUniforms().writeTransform(RenderSystem.getModelViewMatrix(), new Vector4f(), new Vector3f(), new Matrix4f(), 0.0F);

						try (RenderPass pass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "SC camera frame at " + state.blockPos, mainRenderTarget.getColorTextureView(), OptionalInt.empty(), mainRenderTarget.getDepthTextureView(), OptionalDouble.empty())) {
							pass.setPipeline(FRAME_PIPELINE);
							pass.setVertexBuffer(0, vertexBuffer);
							pass.setIndexBuffer(indexBuffer, meshData.drawState().indexType());
							pass.setUniform("DynamicTransforms", dynamicTransforms);
							pass.setUniform("Projection", RenderSystem.getProjectionMatrixBuffer());
							pass.bindSampler("InSampler", state.renderTargetColorTexture);
							pass.drawIndexed(0, 0, 6, 1);
						}

						vertexBuffer.close();
						indexBuffer.close();
					}
				}

				if (state.hasLens)
					submitOverlay(pose, collector, state.lensColor, xStart, xEnd, zStart, zEnd, margin, lightCoords, normal);
			}
		}

	}

	@Override
	public FrameRenderState createRenderState() {
		return new FrameRenderState();
	}

	@Override
	public void extractRenderState(FrameBlockEntity be, FrameRenderState state, float partialTick, Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
		BlockEntityRenderer.super.extractRenderState(be, state, partialTick, cameraPos, crumblingOverlay);

		Player player = Minecraft.getInstance().player;
		Level level = be.getLevel();
		GlobalPos securityCameraPos = be.getCurrentCamera();
		Direction direction = be.getBlockState().getValue(FrameBlock.FACING);
		final float margin = 0.0625F;

		switch (direction) {
			case Direction.NORTH:
				state.innerVertices = new Vector4f(margin, 1 - margin, 0.05F, 0.05F);
				state.outerVertices = new Vector4f(margin, 1 - margin, 0.045F, 0.045F);
				break;
			case Direction.SOUTH:
				state.innerVertices = new Vector4f(1 - margin, margin, 0.95F, 0.95F);
				state.outerVertices = new Vector4f(1 - margin, margin, 0.955F, 0.955F);
				break;
			case Direction.WEST:
				state.innerVertices = new Vector4f(0.05F, 0.05F, 1 - margin, margin);
				state.outerVertices = new Vector4f(0.045F, 0.045F, 1 - margin, margin);
				break;
			case Direction.EAST:
				state.innerVertices = new Vector4f(0.95F, 0.95F, margin, 1 - margin);
				state.outerVertices = new Vector4f(0.955F, 0.955F, margin, 1 - margin);
				break;
			default:
				state.innerVertices = new Vector4f(0.0F, 1.0F, 0.0F, 1.0F);
				state.outerVertices = new Vector4f(0.0F, 1.0F, 0.0F, 1.0F);
				break;
		}

		state.normal = direction.getUnitVec3i();
		state.isDisabled = be.isDisabled();
		state.canSeeFeed = be.isOwnedBy(player) || be.isAllowed(player);
		state.hasCamerasLinked = !be.getCameraPositions().isEmpty();
		state.isCameraSelected = securityCameraPos != null;
		state.isRedstoneSignalDisabled = be.redstoneSignalDisabled();
		state.hasClientInteracted = be.hasClientInteracted();

		if (state.isCameraSelected) {
			CameraFeed feed = FrameFeedHandler.getFeed(securityCameraPos);

			if (feed != null && feed.isFrameLinked(be) && level.isLoaded(securityCameraPos.pos()) && level.getBlockEntity(securityCameraPos.pos()) instanceof SecurityCameraBlockEntity cameraBlockEntity) {
				state.isCameraPresent = true;
				state.renderTargetColorTexture = feed.renderTarget().getColorTextureView();
				state.backgroundColor = feed.backgroundColor();

				ItemStack lens = cameraBlockEntity.getLensContainer().getItem(0);

				if (lens.has(DataComponents.DYED_COLOR)) {
					state.hasLens = true;
					state.lensColor = ARGB.color(cameraBlockEntity.getOpacity(), lens.get(DataComponents.DYED_COLOR).rgb());
				}
				else
					state.hasLens = false;
			}
			else
				state.isCameraPresent = false;
		}
	}

	private void submitNoise(PoseStack pose, SubmitNodeCollector collector, Vector4f vertices, int packedLight, Vec3i normal, float margin) {
		//renderTexture(pose, collector, NOISE_BACKGROUND.buffer(collector, RenderType::entitySolid), vertices, packedLight, normal, margin); TODO I don't know how to link the material to the SubmitNodeCollector
	}

	private void submitSolidTexture(PoseStack pose, SubmitNodeCollector collector, ResourceLocation texture, Vector4f vertices, int packedLight, Vec3i normal, float margin) {
		submitTexture(pose, collector, RenderType.entitySolid(texture), vertices, packedLight, normal, margin);
	}

	private void submitCutoutTexture(PoseStack pose, SubmitNodeCollector collector, ResourceLocation texture, Vector4f vertices, int packedLight, Vec3i normal, float margin) {
		submitTexture(pose, collector, RenderType.entityCutout(texture), vertices, packedLight, normal, margin);
	}

	private void submitTexture(PoseStack poseStack, SubmitNodeCollector collector, RenderType renderType, Vector4f vertices, int packedLight, Vec3i normal, float margin) {
		Pose last = poseStack.last();
		float xStart = vertices.x;
		float xEnd = vertices.y;
		float zStart = vertices.z;
		float zEnd = vertices.w;
		int nx = normal.getX();
		int ny = normal.getY();
		int nz = normal.getZ();

		collector.submitCustomGeometry(poseStack, renderType, (pose, builder) -> {
			builder.addVertex(pose, xStart, margin, zStart).setUv(1, 1).setColor(0xFFFFFFFF).setLight(packedLight).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(last, nx, ny, nz);
			builder.addVertex(pose, xStart, 1 - margin, zStart).setUv(1, 0).setColor(0xFFFFFFFF).setLight(packedLight).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(last, nx, ny, nz);
			builder.addVertex(pose, xEnd, 1 - margin, zEnd).setUv(0, 0).setColor(0xFFFFFFFF).setLight(packedLight).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(last, nx, ny, nz);
			builder.addVertex(pose, xEnd, margin, zEnd).setUv(0, 1).setColor(0xFFFFFFFF).setLight(packedLight).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(last, nx, ny, nz);
		});

		//if (collector instanceof MultiBufferSource.BufferSource bufferSource)
		//	bufferSource.endBatch(); TODO is that even needed
	}

	private void submitOverlay(PoseStack pose, SubmitNodeCollector collector, int color, float xStart, float xEnd, float zStart, float zEnd, float margin, int packedLight, Vec3i normal) {
		submitOverlay(pose, collector, RenderType.entityTranslucent(WHITE), color, xStart, xEnd, zStart, zEnd, margin, packedLight, normal);
	}

	private void submitOverlay(PoseStack poseStack, SubmitNodeCollector collector, RenderType renderType, int color, float xStart, float xEnd, float zStart, float zEnd, float margin, int packedLight, Vec3i normal) {
		int nx = normal.getX();
		int ny = normal.getY();
		int nz = normal.getZ();

		collector.submitCustomGeometry(poseStack, renderType, (pose, builder) -> {
			builder.addVertex(pose, xStart, margin, zStart).setColor(color).setUv(0, 0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(pose, nx, ny, nz);
			builder.addVertex(pose, xStart, 1 - margin, zStart).setColor(color).setUv(0, 1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(pose, nx, ny, nz);
			builder.addVertex(pose, xEnd, 1 - margin, zEnd).setColor(color).setUv(1, 1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(pose, nx, ny, nz);
			builder.addVertex(pose, xEnd, margin, zEnd).setColor(color).setUv(1, 0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(pose, nx, ny, nz);
		});

		//if (collector instanceof MultiBufferSource.BufferSource bufferSource)
		//	bufferSource.endBatch(); TODO is that even needed
	}
}
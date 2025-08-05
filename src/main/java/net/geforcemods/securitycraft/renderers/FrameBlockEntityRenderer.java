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
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexSorting;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.FrameBlockEntity;
import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.blocks.FrameBlock;
import net.geforcemods.securitycraft.entity.camera.CameraFeed;
import net.geforcemods.securitycraft.entity.camera.FrameFeedHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderPipelines;
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
import net.minecraft.util.ARGB;
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
	public void render(FrameBlockEntity be, float partialTicks, PoseStack pose, MultiBufferSource buffer, int packedLight, int packedOverlay, Vec3 viewPos) {
		Minecraft mc = Minecraft.getInstance();
		Player player = mc.player;

		if (be.isDisabled() || (!be.isOwnedBy(player) && !be.isAllowed(player)) || be.getCameraPositions().isEmpty())
			return;

		Level level = be.getLevel();
		BlockState state = be.getBlockState();
		GlobalPos cameraPos = be.getCurrentCamera();
		Direction direction = state.getValue(FrameBlock.FACING);
		Vec3i normal = direction.getUnitVec3i();
		final float margin = 0.0625F;
		Vector4f innerVertices; //Both vectors have the following format: xStart, xEnd, zStart, zEnd
		Vector4f outerVertices;

		switch (direction) {
			case Direction.NORTH:
				innerVertices = new Vector4f(margin, 1 - margin, 0.05F, 0.05F);
				outerVertices = new Vector4f(margin, 1 - margin, 0.045F, 0.045F);
				break;
			case Direction.SOUTH:
				innerVertices = new Vector4f(1 - margin, margin, 0.95F, 0.95F);
				outerVertices = new Vector4f(1 - margin, margin, 0.955F, 0.955F);
				break;
			case Direction.WEST:
				innerVertices = new Vector4f(0.05F, 0.05F, 1 - margin, margin);
				outerVertices = new Vector4f(0.045F, 0.045F, 1 - margin, margin);
				break;
			case Direction.EAST:
				innerVertices = new Vector4f(0.95F, 0.95F, margin, 1 - margin);
				outerVertices = new Vector4f(0.955F, 0.955F, margin, 1 - margin);
				break;
			default:
				innerVertices = new Vector4f(0.0F, 1.0F, 0.0F, 1.0F);
				outerVertices = new Vector4f(0.0F, 1.0F, 0.0F, 1.0F);
				break;
		}

		if (cameraPos == null)
			renderSolidTexture(pose, buffer, SELECT_CAMERA, innerVertices, packedLight, normal, margin);
		else if (be.redstoneSignalDisabled()) {
			renderNoise(pose, buffer, innerVertices, packedLight, normal, margin);
			renderCutoutTexture(pose, buffer, NO_REDSTONE_SIGNAL, outerVertices, packedLight, normal, margin);
		}
		else if (!be.hasClientInteracted()) {
			renderNoise(pose, buffer, innerVertices, packedLight, normal, margin);
			renderCutoutTexture(pose, buffer, INACTIVE, outerVertices, packedLight, normal, margin);
		}
		else {
			CameraFeed feed = FrameFeedHandler.getFeed(cameraPos);

			if (feed == null || !feed.isFrameLinked(be) || !level.isLoaded(cameraPos.pos()) || !(level.getBlockEntity(cameraPos.pos()) instanceof SecurityCameraBlockEntity cameraBlockEntity))
				renderSolidTexture(pose, buffer, CAMERA_NOT_FOUND, innerVertices, packedLight, normal, margin);
			else if (!FrameFeedHandler.isCapturingCamera()) { //Only rendering the frame when no camera is being captured prevents screen-in-screen rendering
				RenderTarget target = feed.renderTarget();
				Vector3f backgroundColor = feed.backgroundColor();
				float xStart = innerVertices.x;
				float xEnd = innerVertices.y;
				float zStart = innerVertices.z;
				float zEnd = innerVertices.w;

				renderOverlay(direction, pose, buffer, buffer.getBuffer(RenderType.entityShadow(WHITE)), ARGB.colorFromFloat(1.0F, backgroundColor.x, backgroundColor.y, backgroundColor.z), xStart, xEnd, zStart, zEnd, margin, packedLight);

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
						RenderTarget mainRenderTarget = mc.getMainRenderTarget();
						GpuBufferSlice dynamicTransforms = RenderSystem.getDynamicUniforms().writeTransform(RenderSystem.getModelViewMatrix(), new Vector4f(), new Vector3f(), new Matrix4f(), 0.0F);

						try (RenderPass pass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "SC camera frame at " + be.getBlockPos(), mainRenderTarget.getColorTextureView(), OptionalInt.empty(), mainRenderTarget.getDepthTextureView(), OptionalDouble.empty())) {
							pass.setPipeline(FRAME_PIPELINE);
							pass.setVertexBuffer(0, vertexBuffer);
							pass.setIndexBuffer(indexBuffer, meshData.drawState().indexType());
							pass.setUniform("DynamicTransforms", dynamicTransforms);
							pass.setUniform("Projection", RenderSystem.getProjectionMatrixBuffer());
							pass.bindSampler("InSampler", target.getColorTextureView());
							pass.drawIndexed(0, 0, 6, 1);
						}

						vertexBuffer.close();
						indexBuffer.close();
					}
				}

				ItemStack lens = cameraBlockEntity.getLensContainer().getItem(0);

				if (lens.has(DataComponents.DYED_COLOR))
					renderOverlay(direction, pose, buffer, lens.get(DataComponents.DYED_COLOR).rgb() + (cameraBlockEntity.getOpacity() << 24), xStart, xEnd, zStart, zEnd, margin, packedLight);
			}
		}
	}

	private void renderNoise(PoseStack pose, MultiBufferSource buffer, Vector4f vertices, int packedLight, Vec3i normal, float margin) {
		renderTexture(pose, buffer, NOISE_BACKGROUND.buffer(buffer, RenderType::entitySolid), vertices, packedLight, normal, margin);
	}

	private void renderSolidTexture(PoseStack pose, MultiBufferSource buffer, ResourceLocation texture, Vector4f vertices, int packedLight, Vec3i normal, float margin) {
		renderTexture(pose, buffer, buffer.getBuffer(RenderType.entitySolid(texture)), vertices, packedLight, normal, margin);
	}

	private void renderCutoutTexture(PoseStack pose, MultiBufferSource buffer, ResourceLocation texture, Vector4f vertices, int packedLight, Vec3i normal, float margin) {
		renderTexture(pose, buffer, buffer.getBuffer(RenderType.entityCutout(texture)), vertices, packedLight, normal, margin);
	}

	private void renderTexture(PoseStack pose, MultiBufferSource buffer, VertexConsumer bufferBuilder, Vector4f vertices, int packedLight, Vec3i normal, float margin) {
		Pose last = pose.last();
		Matrix4f lastPose = last.pose();
		float xStart = vertices.x;
		float xEnd = vertices.y;
		float zStart = vertices.z;
		float zEnd = vertices.w;
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

	private void renderOverlay(Direction direction, PoseStack pose, MultiBufferSource buffer, int color, float xStart, float xEnd, float zStart, float zEnd, float margin, int packedLight) {
		renderOverlay(direction, pose, buffer, buffer.getBuffer(RenderType.entityTranslucent(WHITE)), color, xStart, xEnd, zStart, zEnd, margin, packedLight);
	}

	private void renderOverlay(Direction direction, PoseStack poseStack, MultiBufferSource buffer, VertexConsumer bufferBuilder, int color, float xStart, float xEnd, float zStart, float zEnd, float margin, int packedLight) {
		Vector3f normal = direction.getUnitVec3().toVector3f();
		Pose pose = poseStack.last();
		Matrix4f lastPose = poseStack.last().pose();

		bufferBuilder.addVertex(lastPose, xStart, margin, zStart).setColor(color).setUv(0, 0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(pose, normal);
		bufferBuilder.addVertex(lastPose, xStart, 1 - margin, zStart).setColor(color).setUv(0, 1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(pose, normal);
		bufferBuilder.addVertex(lastPose, xEnd, 1 - margin, zEnd).setColor(color).setUv(1, 1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(pose, normal);
		bufferBuilder.addVertex(lastPose, xEnd, margin, zEnd).setColor(color).setUv(1, 0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(pose, normal);

		if (buffer instanceof MultiBufferSource.BufferSource bufferSource)
			bufferSource.endBatch();
	}
}

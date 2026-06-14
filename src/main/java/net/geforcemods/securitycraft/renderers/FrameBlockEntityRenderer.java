package net.geforcemods.securitycraft.renderers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;

import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector4f;

import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.CompareOp;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.FrameBlockEntity;
import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.blocks.FrameBlock;
import net.geforcemods.securitycraft.entity.camera.CameraFeed;
import net.geforcemods.securitycraft.entity.camera.FrameFeedHandler;
import net.geforcemods.securitycraft.renderers.FrameBlockEntityRenderer.FeatureRenderer.Submit;
import net.geforcemods.securitycraft.renderers.state.FrameRenderState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BindGroupLayouts;
import net.minecraft.client.renderer.StagedVertexBuffer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.FeatureFrameContext;
import net.minecraft.client.renderer.feature.FeatureRendererType;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.feature.submit.SubmitNode;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.submit.RenderPhaseKeys;

@EventBusSubscriber
public class FrameBlockEntityRenderer implements BlockEntityRenderer<FrameBlockEntity, FrameRenderState> {
	private static final Identifier CAMERA_NOT_FOUND = SecurityCraft.resLoc("textures/entity/frame/camera_not_found.png");
	private static final Identifier INACTIVE = SecurityCraft.resLoc("textures/entity/frame/inactive.png");
	private static final Identifier NO_REDSTONE_SIGNAL = SecurityCraft.resLoc("textures/entity/frame/no_redstone_signal.png");
	private static final Identifier SELECT_CAMERA = SecurityCraft.resLoc("textures/entity/frame/select_camera.png");
	private static final Identifier WHITE = SecurityCraft.resLoc("textures/entity/frame/white.png");
	//@formatter:off
	public static final RenderPipeline FRAME_PIPELINE = RenderPipeline.builder()
			.withBindGroupLayout(BindGroupLayouts.MATRICES_PROJECTION)
			.withBindGroupLayout(BindGroupLayouts.SAMPLER0)
			.withLocation(SecurityCraft.resLoc("pipeline/frame_draw_fb_in_area"))
			.withVertexShader(SecurityCraft.resLoc("frame_draw_fb_in_area"))
			.withFragmentShader(SecurityCraft.resLoc("frame_draw_fb_in_area"))
			.withVertexBinding(0, DefaultVertexFormat.POSITION_TEX)
			.withPrimitiveTopology(PrimitiveTopology.QUADS)
			.withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
			.withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, true))
			.build();
	//@formatter:on
	private static final float MARGIN = 0.0625F;
	private static TextureAtlasSprite noiseBackground = null;

	public FrameBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {}

	@Override
	public void submit(FrameRenderState state, PoseStack pose, SubmitNodeCollector collector, CameraRenderState camera) {
		if (state.isDisabled || !state.canSeeFeed || !state.hasCamerasLinked)
			return;

		Vector4f innerVertices = state.innerVertices;
		Vector4f outerVertices = state.outerVertices;
		Vec3i normal = state.normal;
		int lightCoords = state.lightCoords;

		if (!state.isCameraSelected)
			submitSolidTexture(pose, collector, SELECT_CAMERA, innerVertices, lightCoords, normal, MARGIN);
		else if (state.isRedstoneSignalDisabled) {
			submitNoise(pose, collector, innerVertices, lightCoords, normal, MARGIN);
			submitCutoutTexture(pose, collector, NO_REDSTONE_SIGNAL, outerVertices, lightCoords, normal, MARGIN);
		}
		else if (!state.hasClientInteracted) {
			submitNoise(pose, collector, innerVertices, lightCoords, normal, MARGIN);
			submitCutoutTexture(pose, collector, INACTIVE, outerVertices, lightCoords, normal, MARGIN);
		}
		else {
			if (!state.isCameraPresent)
				submitSolidTexture(pose, collector, CAMERA_NOT_FOUND, innerVertices, lightCoords, normal, MARGIN);
			else if (!FrameFeedHandler.isCapturingCamera()) { //Only rendering the frame when no camera is being captured prevents screen-in-screen rendering
				collector.submitSpecial(RenderPhaseKeys.SOLID, new Submit(new Matrix4f(pose.last().pose()), state));

				if (state.hasLens)
					submitOverlay(pose, collector, state.lensColor, outerVertices, lightCoords, normal);
			}
		}
	}

	public static class FeatureRenderer implements net.minecraft.client.renderer.feature.FeatureRenderer<FeatureRenderer.Submit> {
		public static final FeatureRendererType<Submit> TYPE = FeatureRendererType.create("securitycraft:frame");
		private final List<List<Frame>> groups = new ArrayList<>();

		@Override
		public void prepareGroup(FeatureFrameContext context, List<Submit> submits, boolean strictlyOrdered) {
			if (!submits.isEmpty()) {
				List<Frame> frames = new ArrayList<>();
				StagedVertexBuffer buffer = context.stagedVertexBuffer();

				for (Submit submit : submits) {
					FrameRenderState state = submit.state;
					Vector4f innerVertices = state.innerVertices;
					float xStartO = innerVertices.x;
					float xEndO = innerVertices.y;
					float zStartO = innerVertices.z;
					float zEndO = innerVertices.w;
					Matrix4fStack modelViewStack = RenderSystem.getModelViewStack();
					StagedVertexBuffer.Draw draw = buffer.appendDraw(DefaultVertexFormat.POSITION_TEX, PrimitiveTopology.QUADS, null);
					VertexConsumer builder = buffer.getVertexBuilder(draw);

					builder.addVertex(xStartO, MARGIN, zStartO).setUv(1, 0);
					builder.addVertex(xStartO, 1 - MARGIN, zStartO).setUv(1, 1);
					builder.addVertex(xEndO, 1 - MARGIN, zEndO).setUv(0, 1);
					builder.addVertex(xEndO, MARGIN, zEndO).setUv(0, 0);
					modelViewStack.pushMatrix();
					modelViewStack.mul(submit.pos);
					frames.add(new Frame(draw, new Matrix4f(modelViewStack), state.renderTargetColorTexture));
					modelViewStack.popMatrix();
				}

				groups.add(frames);
			}
		}

		@Override
		public void executeGroup(FeatureFrameContext context, int groupIndex, List<Submit> submits, boolean strictlyOrdered) {
			StagedVertexBuffer buffer = context.stagedVertexBuffer();
			RenderTarget mainRenderTarget = Minecraft.getInstance().gameRenderer.mainRenderTarget();
			GpuTextureView color = mainRenderTarget.getColorTextureView();
			GpuTextureView depth = mainRenderTarget.getDepthTextureView();

			try (RenderPass pass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "SecurityCraft camera frames", color, Optional.empty(), depth, OptionalDouble.empty())) {
				for (Frame frame : groups.get(groupIndex)) {
					StagedVertexBuffer.ExecuteInfo info = buffer.getExecuteInfo(frame.draw);

					pass.setPipeline(FRAME_PIPELINE);
					RenderSystem.bindDefaultUniforms(pass);
					pass.setUniform("DynamicTransforms", RenderSystem.getDynamicUniforms().writeTransform(frame.dynamicTransforms));
					pass.bindTexture("Sampler0", frame.texture, RenderSystem.getSamplerCache().getSampler(AddressMode.REPEAT, AddressMode.REPEAT, FilterMode.NEAREST, FilterMode.LINEAR, false));
					pass.setVertexBuffer(0, info.vertexBuffer().slice());
					pass.setIndexBuffer(info.indexBuffer(), info.indexType());
					pass.drawIndexed(info.indexCount(), 1, info.firstIndex(), info.baseVertex(), 0);
				}
			}
		}

		@Override
		public void finishExecute(FeatureFrameContext context) {
			groups.clear();
		}

		public record Submit(Matrix4f pos, FrameRenderState state) implements SubmitNode {
			@Override
			public FeatureRendererType<Submit> featureType() {
				return TYPE;
			}
		}

		public record Frame(StagedVertexBuffer.Draw draw, Matrix4f dynamicTransforms, GpuTextureView texture) {}
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

	private void submitNoise(PoseStack poseStack, SubmitNodeCollector collector, Vector4f vertices, int packedLight, Vec3i normal, float margin) {
		Pose last = poseStack.last();
		TextureAtlasSprite noiseBackground = getNoiseBackground();
		float xStart = vertices.x;
		float xEnd = vertices.y;
		float zStart = vertices.z - 0.0001f;
		float zEnd = vertices.w;
		int nx = normal.getX();
		int ny = normal.getY();
		int nz = normal.getZ();
		float u0 = noiseBackground.getU(0.0625F);
		float u1 = noiseBackground.getU(0.9375F);
		float v0 = noiseBackground.getV(0.0625F);
		float v1 = noiseBackground.getV(0.9375F);

		collector.submitCustomGeometry(poseStack, RenderTypes.entitySolid(noiseBackground.atlasLocation()), (pose, builder) -> {
			builder.addVertex(pose, xStart, margin, zStart).setUv(u1, v1).setColor(0xFFFFFFFF).setLight(packedLight).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(last, nx, ny, nz);
			builder.addVertex(pose, xStart, 1 - margin, zStart).setUv(u1, v0).setColor(0xFFFFFFFF).setLight(packedLight).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(last, nx, ny, nz);
			builder.addVertex(pose, xEnd, 1 - margin, zEnd).setUv(u0, v0).setColor(0xFFFFFFFF).setLight(packedLight).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(last, nx, ny, nz);
			builder.addVertex(pose, xEnd, margin, zEnd).setUv(u0, v1).setColor(0xFFFFFFFF).setLight(packedLight).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(last, nx, ny, nz);
		});
	}

	private static TextureAtlasSprite getNoiseBackground() {
		if (noiseBackground == null)
			noiseBackground = Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.BLOCKS).getSprite(SecurityCraft.resLoc("entity/frame/noise_background"));

		return noiseBackground;
	}

	public static void invalidateNoiseBackground() {
		noiseBackground = null;
	}

	private void submitSolidTexture(PoseStack pose, SubmitNodeCollector collector, Identifier texture, Vector4f vertices, int packedLight, Vec3i normal, float margin) {
		submitTexture(pose, collector, RenderTypes.entitySolid(texture), vertices, packedLight, normal, margin);
	}

	private void submitCutoutTexture(PoseStack pose, SubmitNodeCollector collector, Identifier texture, Vector4f vertices, int packedLight, Vec3i normal, float margin) {
		submitTexture(pose, collector, RenderTypes.entityCutout(texture), vertices, packedLight, normal, margin);
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
	}

	private void submitOverlay(PoseStack pose, SubmitNodeCollector collector, int color, Vector4f vertices, int packedLight, Vec3i normal) {
		RenderType renderType = RenderTypes.entityTranslucent(WHITE);
		float xStart = vertices.x;
		float xEnd = vertices.y;
		float zStart = vertices.z;
		float zEnd = vertices.w;
		int nx = normal.getX();
		int ny = normal.getY();
		int nz = normal.getZ();

		collector.submitCustomGeometry(pose, renderType, (pose1, builder) -> {
			builder.addVertex(pose1, xStart, MARGIN, zStart).setColor(color).setUv(0, 0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(pose1, nx, ny, nz);
			builder.addVertex(pose1, xStart, 1 - MARGIN, zStart).setColor(color).setUv(0, 1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(pose1, nx, ny, nz);
			builder.addVertex(pose1, xEnd, 1 - MARGIN, zEnd).setColor(color).setUv(1, 1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(pose1, nx, ny, nz);
			builder.addVertex(pose1, xEnd, MARGIN, zEnd).setColor(color).setUv(1, 0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight).setNormal(pose1, nx, ny, nz);
		});
	}

}
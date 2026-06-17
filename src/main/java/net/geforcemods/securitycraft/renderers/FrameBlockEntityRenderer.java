package net.geforcemods.securitycraft.renderers;

import org.joml.Matrix4f;
import org.joml.Vector4f;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.FrameBlockEntity;
import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.blocks.FrameBlock;
import net.geforcemods.securitycraft.entity.camera.CameraFeed;
import net.geforcemods.securitycraft.entity.camera.FrameFeedHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
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
				.setShaderState(new RenderStateShard.ShaderStateShard(ClientHandler::getFrameFeedShader))
				.createCompositeState(false));
	//@formatter:on
	public static final float MARGIN = 0.0625F;

	public FrameBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {}

	@Override
	public void render(FrameBlockEntity be, float partialTicks, PoseStack pose, MultiBufferSource buffer, int packedLight, int packedOverlay) {
		Minecraft mc = Minecraft.getInstance();
		Player player = mc.player;

		if (be.isDisabled() || (!be.isOwnedBy(player) && !be.isAllowed(player)) || be.getCameraPositions().isEmpty())
			return;

		Level level = be.getLevel();
		BlockState state = be.getBlockState();
		GlobalPos cameraPos = be.getCurrentCamera();
		Direction direction = state.getValue(FrameBlock.FACING);
		Vec3i normal = direction.getNormal();
		Vector4f innerVertices; //Both vectors have the following format: xStart, xEnd, zStart, zEnd
		Vector4f outerVertices;

		switch (direction) {
			case NORTH:
				innerVertices = new Vector4f(MARGIN, 1 - MARGIN, 0.05F, 0.05F);
				outerVertices = new Vector4f(MARGIN, 1 - MARGIN, 0.045F, 0.045F);
				break;
			case SOUTH:
				innerVertices = new Vector4f(1 - MARGIN, MARGIN, 0.95F, 0.95F);
				outerVertices = new Vector4f(1 - MARGIN, MARGIN, 0.955F, 0.955F);
				break;
			case WEST:
				innerVertices = new Vector4f(0.05F, 0.05F, 1 - MARGIN, MARGIN);
				outerVertices = new Vector4f(0.045F, 0.045F, 1 - MARGIN, MARGIN);
				break;
			case EAST:
				innerVertices = new Vector4f(0.95F, 0.95F, MARGIN, 1 - MARGIN);
				outerVertices = new Vector4f(0.955F, 0.955F, MARGIN, 1 - MARGIN);
				break;
			default:
				innerVertices = new Vector4f(0.0F, 1.0F, 0.0F, 1.0F);
				outerVertices = new Vector4f(0.0F, 1.0F, 0.0F, 1.0F);
				break;
		}

		if (cameraPos == null)
			renderSolidTexture(pose, buffer, SELECT_CAMERA, innerVertices, packedLight, normal);
		else if (be.redstoneSignalDisabled()) {
			renderNoise(pose, buffer, innerVertices, packedLight, normal);
			renderCutoutTexture(pose, buffer, NO_REDSTONE_SIGNAL, outerVertices, packedLight, normal);
		}
		else if (!be.hasClientInteracted()) {
			renderNoise(pose, buffer, innerVertices, packedLight, normal);
			renderCutoutTexture(pose, buffer, INACTIVE, outerVertices, packedLight, normal);
		}
		else {
			CameraFeed feed = FrameFeedHandler.getFeed(cameraPos);

			if (feed == null || !feed.isFrameLinked(be) || !level.isLoaded(cameraPos.pos()) || !(level.getBlockEntity(cameraPos.pos()) instanceof SecurityCameraBlockEntity cameraBlockEntity))
				renderSolidTexture(pose, buffer, CAMERA_NOT_FOUND, innerVertices, packedLight, normal);
			else if (!FrameFeedHandler.isCapturingCamera()) { //Only rendering the frame when no camera is being captured prevents screen-in-screen rendering
				RenderTarget target = feed.renderTarget();
				ShaderInstance shader = ClientHandler.getFrameFeedShader();
				float xStart = innerVertices.x;
				float xEnd = innerVertices.y;
				float zStart = innerVertices.z;
				float zEnd = innerVertices.w;
				VertexConsumer bufferBuilder;
				Matrix4f lastPose;

				shader.setSampler("DiffuseSampler", target.getColorTextureId());

				if (shader.MODEL_VIEW_MATRIX != null)
					shader.MODEL_VIEW_MATRIX.set(pose.last().pose());

				if (shader.PROJECTION_MATRIX != null)
					shader.PROJECTION_MATRIX.set(mc.gameRenderer.getProjectionMatrix(90.0F));

				shader.apply();
				lastPose = pose.last().pose();
				bufferBuilder = buffer.getBuffer(CAMERA_IN_FRAME_RENDER_TYPE);
				bufferBuilder.vertex(lastPose, xStart, MARGIN, zStart).uv(1, 0).color(0xFFFFFF).endVertex();
				bufferBuilder.vertex(lastPose, xStart, 1 - MARGIN, zStart).uv(1, 1).color(0xFFFFFF).endVertex();
				bufferBuilder.vertex(lastPose, xEnd, 1 - MARGIN, zEnd).uv(0, 1).color(0xFFFFFF).endVertex();
				bufferBuilder.vertex(lastPose, xEnd, MARGIN, zEnd).uv(0, 0).color(0xFFFFFF).endVertex();
				shader.clear();

				if (buffer instanceof MultiBufferSource.BufferSource bufferSource)
					bufferSource.endBatch();
				else //If another mod messes with (e.g. wraps) the buffer source available here, it is assumed that the original buffer source can safely be obtained through the level renderer.
					mc.levelRenderer.renderBuffers.bufferSource().endBatch();

				ItemStack lens = cameraBlockEntity.getLensContainer().getItem(0);

				if (lens.getItem() instanceof DyeableLeatherItem item && item.hasCustomColor(lens))
					renderOverlay(pose, buffer, item.getColor(lens) + (cameraBlockEntity.getOpacity() << 24), innerVertices);
			}
		}
	}

	private void renderNoise(PoseStack pose, MultiBufferSource buffer, Vector4f vertices, int packedLight, Vec3i normal) {
		renderTexture(pose, buffer, NOISE_BACKGROUND.buffer(buffer, RenderType::entitySolid), vertices, packedLight, normal, 0.0625f, 0.9375f);
	}

	private void renderSolidTexture(PoseStack pose, MultiBufferSource buffer, ResourceLocation texture, Vector4f vertices, int packedLight, Vec3i normal) {
		renderTexture(pose, buffer, buffer.getBuffer(RenderType.entitySolid(texture)), vertices, packedLight, normal);
	}

	private void renderCutoutTexture(PoseStack pose, MultiBufferSource buffer, ResourceLocation texture, Vector4f vertices, int packedLight, Vec3i normal) {
		renderTexture(pose, buffer, buffer.getBuffer(RenderType.entityCutout(texture)), vertices, packedLight, normal);
	}

	private void renderTexture(PoseStack pose, MultiBufferSource buffer, VertexConsumer bufferBuilder, Vector4f vertices, int packedLight, Vec3i normal) {
		renderTexture(pose, buffer, bufferBuilder, vertices, packedLight, normal, 0, 1);
	}

	private void renderTexture(PoseStack pose, MultiBufferSource buffer, VertexConsumer bufferBuilder, Vector4f vertices, int packedLight, Vec3i normal, float minUv, float maxUv) {
		Pose last = pose.last();
		Matrix4f lastPose = last.pose();
		float xStart = vertices.x;
		float xEnd = vertices.y;
		float zStart = vertices.z;
		float zEnd = vertices.w;
		int nx = normal.getX();
		int ny = normal.getY();
		int nz = normal.getZ();

		bufferBuilder.vertex(lastPose, xStart, MARGIN, zStart).color(0xFFFFFF).uv(maxUv, maxUv).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(nx, ny, nz).endVertex();
		bufferBuilder.vertex(lastPose, xStart, 1 - MARGIN, zStart).color(0xFFFFFF).uv(maxUv, minUv).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(nx, ny, nz).endVertex();
		bufferBuilder.vertex(lastPose, xEnd, 1 - MARGIN, zEnd).color(0xFFFFFF).uv(minUv, minUv).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(nx, ny, nz).endVertex();
		bufferBuilder.vertex(lastPose, xEnd, MARGIN, zEnd).color(0xFFFFFF).uv(minUv, maxUv).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(nx, ny, nz).endVertex();

		if (buffer instanceof MultiBufferSource.BufferSource bufferSource)
			bufferSource.endBatch();
	}

	private void renderOverlay(PoseStack pose, MultiBufferSource buffer, int color, Vector4f vertices) {
		VertexConsumer bufferBuilder = buffer.getBuffer(RenderType.gui());
		float xStart = vertices.x;
		float xEnd = vertices.y;
		float zStart = vertices.z;
		float zEnd = vertices.w;
		Matrix4f lastPose = pose.last().pose();

		bufferBuilder.vertex(lastPose, xStart, MARGIN, zStart).color(color).endVertex();
		bufferBuilder.vertex(lastPose, xStart, 1 - MARGIN, zStart).color(color).endVertex();
		bufferBuilder.vertex(lastPose, xEnd, 1 - MARGIN, zEnd).color(color).endVertex();
		bufferBuilder.vertex(lastPose, xEnd, MARGIN, zEnd).color(color).endVertex();

		if (buffer instanceof MultiBufferSource.BufferSource bufferSource)
			bufferSource.endBatch();
	}
}

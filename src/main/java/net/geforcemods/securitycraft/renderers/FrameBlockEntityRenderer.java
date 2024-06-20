package net.geforcemods.securitycraft.renderers;

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
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
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

public class FrameBlockEntityRenderer {
	public static final RenderType CAMERA_IN_FRAME_TYPE = RenderType.create("portal_shader", DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, 1536, RenderType.CompositeState.builder().setShaderState(new RenderStateShard.ShaderStateShard(() -> CameraController.cameraMonitorShader)).createCompositeState(false));

	public FrameBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {}

	public static void render(FrameBlockEntity be, float partialTick, PoseStack pose, MultiBufferSource buffer, int packedLight, int packedOverlay) {
		Level level = be.getLevel();
		Player player = Minecraft.getInstance().player;
		BlockState state = be.getBlockState();
		GlobalPos cameraPos = be.getCurrentCamera();
		Direction direction = state.getValue(FrameBlock.FACING);
		Vec3i normal = direction.getNormal();
		float bottom = 0.125F;
		float top = 1 - bottom;
		float xStart = 0;
		float xEnd = 1;
		float zStart = 0;
		float zEnd = 1;

		switch (direction) {
			case Direction.NORTH:
				xStart = bottom;
				xEnd = top;
				zStart = zEnd = 0.0624F;
				break;
			case Direction.SOUTH:
				xStart = top;
				xEnd = bottom;
				zStart = zEnd = 0.9376F;
				break;
			case Direction.WEST:
				zStart = top;
				zEnd = bottom;
				xStart = xEnd = 0.0624F;
				break;
			case Direction.EAST:
				zStart = bottom;
				zEnd = top;
				xStart = xEnd = 0.9376F;
				break;
		}

		if (be.isDisabled() || (!be.isOwnedBy(player) && !be.isAllowed(player)) || be.getCameraPositions().isEmpty())
			return;

		if (cameraPos == null)
			renderTexture(pose, buffer, SecurityCraft.resLoc("textures/entity/frame/select_camera.png"), xStart, xEnd, zStart, zEnd, packedLight, normal);
		else if (!be.isActivated())
			renderTexture(pose, buffer, SecurityCraft.resLoc("textures/entity/frame/inactive.png"), xStart, xEnd, zStart, zEnd, packedLight, normal);
		else if (!CameraController.isLinked(be, cameraPos) || !level.isLoaded(cameraPos.pos()) || !(level.getBlockEntity(cameraPos.pos()) instanceof SecurityCameraBlockEntity cameraBlockEntity) || !(cameraBlockEntity.isOwnedBy(player) || cameraBlockEntity.isAllowed(player)))
			renderTexture(pose, buffer, SecurityCraft.resLoc("textures/entity/frame/camera_not_found.png"), xStart, xEnd, zStart, zEnd, packedLight, normal);
		else if (CameraController.currentlyCapturedCamera == null) { //Only when no camera is being captured, the frame may render, to prevent screen-in-screen rendering
			ShaderInstance shader = CameraController.cameraMonitorShader;
			RenderTarget target = CameraController.getViewForFrame(cameraPos);
			ItemStack lens = cameraBlockEntity.getLensContainer().getItem(0);

			if (target == null) {
				renderTexture(pose, buffer, SecurityCraft.resLoc("textures/entity/frame/camera_not_found.png"), xStart, xEnd, zStart, zEnd, packedLight, normal);
				return;
			}

			shader.setSampler("DiffuseSampler", target.getColorTextureId());

			if (shader.MODEL_VIEW_MATRIX != null)
				shader.MODEL_VIEW_MATRIX.set(pose.last().pose());

			if (shader.PROJECTION_MATRIX != null)
				shader.PROJECTION_MATRIX.set(RenderSystem.getProjectionMatrix());

			shader.apply();
			VertexConsumer bufferbuilder = buffer.getBuffer(CAMERA_IN_FRAME_TYPE);
			bufferbuilder.addVertex(pose.last().pose(), xStart, bottom, zStart).setUv(1, 0).setColor(0xFFFFFF);
			bufferbuilder.addVertex(pose.last().pose(), xStart, top, zStart).setUv(1, 1).setColor(0xFFFFFF);
			bufferbuilder.addVertex(pose.last().pose(), xEnd, top, zEnd).setUv(0, 1).setColor(0xFFFFFF);
			bufferbuilder.addVertex(pose.last().pose(), xEnd, bottom, zEnd).setUv(0, 0).setColor(0xFFFFFF);
			shader.clear();

			if (buffer instanceof MultiBufferSource.BufferSource bufferSource)
				bufferSource.endBatch();

			if (lens.has(DataComponents.DYED_COLOR))
				renderOverlay(pose, buffer, lens.get(DataComponents.DYED_COLOR).rgb() + (cameraBlockEntity.getOpacity() << 24), xStart, xEnd, zStart, zEnd);
		}
	}

	private static void renderTexture(PoseStack pose, MultiBufferSource buffer, ResourceLocation texture, float xStart, float xEnd, float zStart, float zEnd, int packedLight, Vec3i normal) {
		VertexConsumer bufferBuilder = buffer.getBuffer(RenderType.entitySolid(texture));
		bufferBuilder.addVertex(pose.last().pose(), xStart, 0.125F, zStart).setUv(1, 1).setColor(0xFFFFFF).setLight(packedLight).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(pose.last(), normal.getX(), normal.getY(), normal.getZ());
		bufferBuilder.addVertex(pose.last().pose(), xStart, 0.875F, zStart).setUv(1, 0).setColor(0xFFFFFF).setLight(packedLight).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(pose.last(), normal.getX(), normal.getY(), normal.getZ());
		bufferBuilder.addVertex(pose.last().pose(), xEnd, 0.875F, zEnd).setUv(0, 0).setColor(0xFFFFFF).setLight(packedLight).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(pose.last(), normal.getX(), normal.getY(), normal.getZ());
		bufferBuilder.addVertex(pose.last().pose(), xEnd, 0.125F, zEnd).setUv(0, 1).setColor(0xFFFFFF).setLight(packedLight).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(pose.last(), normal.getX(), normal.getY(), normal.getZ());

		if (buffer instanceof MultiBufferSource.BufferSource bufferSource)
			bufferSource.endBatch();
	}

	private static void renderOverlay(PoseStack pose, MultiBufferSource buffer, int color, float xStart, float xEnd, float zStart, float zEnd) {
		VertexConsumer bufferBuilder = buffer.getBuffer(RenderType.gui());
		bufferBuilder.addVertex(pose.last().pose(), xStart, 0.125F, zStart).setColor(color);
		bufferBuilder.addVertex(pose.last().pose(), xStart, 0.875F, zStart).setColor(color);
		bufferBuilder.addVertex(pose.last().pose(), xEnd, 0.875F, zEnd).setColor(color);
		bufferBuilder.addVertex(pose.last().pose(), xEnd, 0.125F, zEnd).setColor(color);

		if (buffer instanceof MultiBufferSource.BufferSource bufferSource)
			bufferSource.endBatch();
	}
}

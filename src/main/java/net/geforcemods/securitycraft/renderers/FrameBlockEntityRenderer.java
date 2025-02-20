package net.geforcemods.securitycraft.renderers;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
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

			//TODO figure out how much of the below is actually needed (some things are already commented out without changing anything)
			GL20.glUseProgram(CameraController.shaderId); //From MyRenderHelper#drawFrameBufferUp -> ShaderManager#load

			int uniModelView = GL20.glGetUniformLocation(CameraController.shaderId, "modelView");
			int uniProjection = GL20.glGetUniformLocation(CameraController.shaderId, "projection");
			int uniSampler = GL20.glGetUniformLocation(CameraController.shaderId, "sampler");
			int uniWidth = GL20.glGetUniformLocation(CameraController.shaderId, "w");
			int uniHeight = GL20.glGetUniformLocation(CameraController.shaderId, "h");

			GL20.glUniformMatrix4fv(uniModelView, false, getMatrix(GL11.GL_MODELVIEW_MATRIX));
			GL20.glUniformMatrix4fv(uniProjection, false, getMatrix(GL11.GL_PROJECTION_MATRIX));
			GL20.glUniform1i(uniSampler, target.getColorTextureId());
			GL20.glUniform1f(uniWidth, Minecraft.getInstance().getWindow().getWidth());
			GL20.glUniform1f(uniHeight, Minecraft.getInstance().getWindow().getHeight());

			GlStateManager._enableTexture(); //MyRenderHelper#drawFrameBufferUp
			GlStateManager._activeTexture(GL13.GL_TEXTURE0);

			target.bindRead(); //ImmPtl MyRenderHelper drawFramebufferWithViewport
			GlStateManager._texParameter(3553, 10241, 9729); //TODO replace ImmPtl magic numbers with actual values, if findable
			GlStateManager._texParameter(3553, 10240, 9729);
			GlStateManager._texParameter(3553, 10242, 10496);
			GlStateManager._texParameter(3553, 10243, 10496);

			Tessellator tessellator = Tessellator.getInstance(); //ImmPtl ViewAreaRenderer#drawPortalViewTriangle (adapted for quads)
			BufferBuilder bufferBuilder = tessellator.getBuilder();
			Matrix4f lastPose = pose.last().pose();

			bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX);
			bufferBuilder.vertex(lastPose, xStart, margin, zStart).color(0xFF, 0xFF, 0xFF, 0xFF).uv(1, 0).endVertex(); //TODO These vertices are the most prone-to-break point
			bufferBuilder.vertex(lastPose, xStart, 1 - margin, zStart).color(0xFF, 0xFF, 0xFF, 0xFF).uv(1, 1).endVertex();
			bufferBuilder.vertex(lastPose, xEnd, 1 - margin, zEnd).color(0xFF, 0xFF, 0xFF, 0xFF).uv(0, 1).endVertex();
			bufferBuilder.vertex(lastPose, xEnd, margin, zEnd).color(0xFF, 0xFF, 0xFF, 0xFF).uv(0, 0).endVertex();
			//GL11.glEnable(GL32.GL_DEPTH_CLAMP);

			runWithTransformation(
					pose,
					tessellator::end
			);
			//GL11.glDisable(GL32.GL_DEPTH_CLAMP);  //ViewAreaRenderer#draw, still

			GlStateManager._glUseProgram(0);; //MyRenderHelper drawFramebufferWithViewport

			target.unbindRead();

			//GlStateManager._enableCull();

			if (lens.getItem() instanceof IDyeableArmorItem && ((IDyeableArmorItem) lens.getItem()).hasCustomColor(lens))
				renderOverlay(pose, buffer, xStart, xEnd, zStart, zEnd, ((IDyeableArmorItem) lens.getItem()).getColor(lens) + (cameraBlockEntity.getOpacity() << 24), packedLight, normal, margin);
		}
	}

	public static FloatBuffer getMatrix(int matrixId) {
		FloatBuffer temp = BufferUtils.createFloatBuffer(16);

		GL11.glGetFloatv(matrixId, temp);

		return temp;
	}

	public static void runWithTransformation(MatrixStack matrixStack, Runnable renderingFunc) {
		transformationPush(matrixStack);
		renderingFunc.run();
		transformationPop();
	}

	public static void transformationPop() {
		RenderSystem.matrixMode(GL11.GL_MODELVIEW);
		RenderSystem.popMatrix();
	}

	public static void transformationPush(MatrixStack matrixStack) {
		RenderSystem.matrixMode(GL11.GL_MODELVIEW);
		RenderSystem.pushMatrix();
		RenderSystem.loadIdentity();
		RenderSystem.multMatrix(matrixStack.last().pose());
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

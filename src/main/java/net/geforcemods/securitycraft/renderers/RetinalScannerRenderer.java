package net.geforcemods.securitycraft.renderers;

import javax.annotation.Nullable;

import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.blockentities.RetinalScannerBlockEntity;
import net.geforcemods.securitycraft.blocks.RetinalScannerBlock;
import net.geforcemods.securitycraft.items.ModuleItem;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.LightLayer;

public class RetinalScannerRenderer implements BlockEntityRenderer<RetinalScannerBlockEntity> {
	private static final float CORRECT_FACTOR = 1 / 550F;

	public RetinalScannerRenderer(BlockEntityRendererProvider.Context ctx) {}

	@Override
	public void render(RetinalScannerBlockEntity be, float partialTicks, PoseStack pose, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
		if (ClientHandler.DISGUISED_BLOCK_RENDER_DELEGATE.tryRenderDelegate(be, partialTicks, pose, buffer, combinedLight, combinedOverlay))
			return;

		Direction direction = be.getBlockState().getValue(RetinalScannerBlock.FACING);

		if (direction != null) {
			if (be.isModuleEnabled(ModuleType.DISGUISE) && ModuleItem.getBlockAddon(be.getModule(ModuleType.DISGUISE)) != null)
				return;

			pose.pushPose();

			switch (direction) {
				case NORTH:
					pose.translate(0.25F, 1.0F / 16.0F, 0.0F);
					break;
				case SOUTH:
					pose.translate(0.75F, 1.0F / 16.0F, 1.0F);
					pose.mulPose(Axis.YP.rotationDegrees(180.0F));
					break;
				case WEST:
					pose.translate(0.0F, 1.0F / 16.0F, 0.75F);
					pose.mulPose(Axis.YP.rotationDegrees(90.0F));
					break;
				case EAST:
					pose.translate(1.0F, 1.0F / 16.0F, 0.25F);
					pose.mulPose(Axis.YP.rotationDegrees(270.0F));
					break;
				default:
					break;
			}

			pose.scale(-1.0F, -1.0F, 1.0F);

			VertexConsumer vertexBuilder = buffer.getBuffer(RenderType.entityCutout(getSkinTexture(be.getPlayerProfile())));
			Pose last = pose.last();
			Matrix4f positionMatrix = last.pose();
			Vec3i normalVector = direction.getNormal();
			BlockPos offsetPos = be.getBlockPos().relative(direction);

			combinedLight = LightTexture.pack(be.getLevel().getBrightness(LightLayer.BLOCK, offsetPos), be.getLevel().getBrightness(LightLayer.SKY, offsetPos));

			// face
			vertexBuilder.addVertex(positionMatrix, CORRECT_FACTOR, CORRECT_FACTOR * 1.5F, 0F).setColor(255, 255, 255, 255).setUv(0.125F, 0.25F).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLight).setNormal(last, normalVector.getX(), normalVector.getY(), normalVector.getZ());
			vertexBuilder.addVertex(positionMatrix, CORRECT_FACTOR, -0.5F - CORRECT_FACTOR / 2F, 0F).setColor(255, 255, 255, 255).setUv(0.125F, 0.125F).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLight).setNormal(last, normalVector.getX(), normalVector.getY(), normalVector.getZ());
			vertexBuilder.addVertex(positionMatrix, -0.5F - CORRECT_FACTOR, -0.5F - CORRECT_FACTOR / 2, 0).setColor(255, 255, 255, 255).setUv(0.25F, 0.125F).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLight).setNormal(last, normalVector.getX(), normalVector.getY(), normalVector.getZ());
			vertexBuilder.addVertex(positionMatrix, -0.5F - CORRECT_FACTOR, CORRECT_FACTOR * 1.5F, 0F).setColor(255, 255, 255, 255).setUv(0.25F, 0.25F).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLight).setNormal(last, normalVector.getX(), normalVector.getY(), normalVector.getZ());

			// helmet
			vertexBuilder.addVertex(positionMatrix, CORRECT_FACTOR, CORRECT_FACTOR * 1.5F, 0F).setColor(255, 255, 255, 255).setUv(0.625F, 0.25F).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLight).setNormal(last, normalVector.getX(), normalVector.getY(), normalVector.getZ());
			vertexBuilder.addVertex(positionMatrix, CORRECT_FACTOR, -0.5F - CORRECT_FACTOR / 2F, 0F).setColor(255, 255, 255, 255).setUv(0.625F, 0.125F).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLight).setNormal(last, normalVector.getX(), normalVector.getY(), normalVector.getZ());
			vertexBuilder.addVertex(positionMatrix, -0.5F - CORRECT_FACTOR, -0.5F - CORRECT_FACTOR / 2, 0).setColor(255, 255, 255, 255).setUv(0.75F, 0.125F).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLight).setNormal(last, normalVector.getX(), normalVector.getY(), normalVector.getZ());
			vertexBuilder.addVertex(positionMatrix, -0.5F - CORRECT_FACTOR, CORRECT_FACTOR * 1.5F, 0F).setColor(255, 255, 255, 255).setUv(0.75F, 0.25F).setOverlay(OverlayTexture.NO_OVERLAY).setLight(combinedLight).setNormal(last, normalVector.getX(), normalVector.getY(), normalVector.getZ());

			pose.popPose();
		}
	}

	private static ResourceLocation getSkinTexture(@Nullable ResolvableProfile profile) {
		if (ConfigHandler.SERVER.retinalScannerFace.get() && profile != null)
			return Minecraft.getInstance().getSkinManager().getInsecureSkin(profile.gameProfile()).texture();
		else
			return DefaultPlayerSkin.getDefaultTexture();
	}
}
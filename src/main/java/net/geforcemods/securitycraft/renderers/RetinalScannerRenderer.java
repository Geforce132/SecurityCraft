package net.geforcemods.securitycraft.renderers;

import java.util.Map;

import javax.annotation.Nullable;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blockentities.RetinalScannerBlockEntity;
import net.geforcemods.securitycraft.blocks.RetinalScannerBlock;
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
import net.minecraft.world.entity.player.Player;
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
			if (be.isModuleEnabled(ModuleType.DISGUISE) && SCContent.DISGUISE_MODULE.get().getBlockAddon(be.getModule(ModuleType.DISGUISE).getTag()) != null)
				return;

			pose.pushPose();

			switch (direction) {
				case NORTH:
					pose.translate(0.25F, 1.0F / 16.0F, 0.0F);
					break;
				case SOUTH:
					pose.translate(0.75F, 1.0F / 16.0F, 1.0F);
					pose.mulPose(Vector3f.YP.rotationDegrees(180.0F));
					break;
				case WEST:
					pose.translate(0.0F, 1.0F / 16.0F, 0.75F);
					pose.mulPose(Vector3f.YP.rotationDegrees(90.0F));
					break;
				case EAST:
					pose.translate(1.0F, 1.0F / 16.0F, 0.25F);
					pose.mulPose(Vector3f.YP.rotationDegrees(270.0F));
					break;
				default:
					break;
			}

			pose.scale(-1.0F, -1.0F, 1.0F);

			VertexConsumer vertexBuilder = buffer.getBuffer(RenderType.entityCutout(getSkinTexture(be.getPlayerProfile())));
			Matrix4f positionMatrix = pose.last().pose();
			Matrix3f normalMatrix = pose.last().normal();
			Vec3i normalVector = direction.getNormal();
			BlockPos offsetPos = be.getBlockPos().relative(direction);

			combinedLight = LightTexture.pack(be.getLevel().getBrightness(LightLayer.BLOCK, offsetPos), be.getLevel().getBrightness(LightLayer.SKY, offsetPos));

			// face
			vertexBuilder.vertex(positionMatrix, CORRECT_FACTOR, CORRECT_FACTOR * 1.5F, 0F).color(255, 255, 255, 255).uv(0.125F, 0.25F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLight).normal(normalMatrix, normalVector.getX(), normalVector.getY(), normalVector.getZ()).endVertex();
			vertexBuilder.vertex(positionMatrix, CORRECT_FACTOR, -0.5F - CORRECT_FACTOR / 2F, 0F).color(255, 255, 255, 255).uv(0.125F, 0.125F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLight).normal(normalMatrix, normalVector.getX(), normalVector.getY(), normalVector.getZ()).endVertex();
			vertexBuilder.vertex(positionMatrix, -0.5F - CORRECT_FACTOR, -0.5F - CORRECT_FACTOR / 2, 0).color(255, 255, 255, 255).uv(0.25F, 0.125F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLight).normal(normalMatrix, normalVector.getX(), normalVector.getY(), normalVector.getZ()).endVertex();
			vertexBuilder.vertex(positionMatrix, -0.5F - CORRECT_FACTOR, CORRECT_FACTOR * 1.5F, 0F).color(255, 255, 255, 255).uv(0.25F, 0.25F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLight).normal(normalMatrix, normalVector.getX(), normalVector.getY(), normalVector.getZ()).endVertex();

			// helmet
			vertexBuilder.vertex(positionMatrix, CORRECT_FACTOR, CORRECT_FACTOR * 1.5F, 0F).color(255, 255, 255, 255).uv(0.625F, 0.25F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLight).normal(normalMatrix, normalVector.getX(), normalVector.getY(), normalVector.getZ()).endVertex();
			vertexBuilder.vertex(positionMatrix, CORRECT_FACTOR, -0.5F - CORRECT_FACTOR / 2F, 0F).color(255, 255, 255, 255).uv(0.625F, 0.125F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLight).normal(normalMatrix, normalVector.getX(), normalVector.getY(), normalVector.getZ()).endVertex();
			vertexBuilder.vertex(positionMatrix, -0.5F - CORRECT_FACTOR, -0.5F - CORRECT_FACTOR / 2, 0).color(255, 255, 255, 255).uv(0.75F, 0.125F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLight).normal(normalMatrix, normalVector.getX(), normalVector.getY(), normalVector.getZ()).endVertex();
			vertexBuilder.vertex(positionMatrix, -0.5F - CORRECT_FACTOR, CORRECT_FACTOR * 1.5F, 0F).color(255, 255, 255, 255).uv(0.75F, 0.25F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(combinedLight).normal(normalMatrix, normalVector.getX(), normalVector.getY(), normalVector.getZ()).endVertex();

			pose.popPose();
		}
	}

	private static ResourceLocation getSkinTexture(@Nullable GameProfile profile) {
		if (ConfigHandler.SERVER.retinalScannerFace.get() && profile != null) {
			Minecraft minecraft = Minecraft.getInstance();
			Map<Type, MinecraftProfileTexture> map = minecraft.getSkinManager().getInsecureSkinInformation(profile);
			return map.containsKey(Type.SKIN) ? minecraft.getSkinManager().registerTexture(map.get(Type.SKIN), Type.SKIN) : DefaultPlayerSkin.getDefaultSkin(Player.createPlayerUUID(profile));
		}
		else
			return DefaultPlayerSkin.getDefaultSkin();
	}
}
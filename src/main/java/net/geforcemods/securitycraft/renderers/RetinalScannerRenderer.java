package net.geforcemods.securitycraft.renderers;

import java.util.Map;

import javax.annotation.Nullable;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blockentities.RetinalScannerBlockEntity;
import net.geforcemods.securitycraft.blocks.RetinalScannerBlock;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.LightType;

public class RetinalScannerRenderer extends TileEntityRenderer<RetinalScannerBlockEntity> {
	private static final float CORRECT_FACTOR = 1 / 550F;

	public RetinalScannerRenderer(TileEntityRendererDispatcher terd) {
		super(terd);
	}

	@Override
	public void render(RetinalScannerBlockEntity be, float partialTicks, MatrixStack pose, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
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

			IVertexBuilder vertexBuilder = buffer.getBuffer(RenderType.entityCutout(getSkinTexture(be.getPlayerProfile())));
			Matrix4f positionMatrix = pose.last().pose();
			Matrix3f normalMatrix = pose.last().normal();
			Vector3i normalVector = direction.getNormal();
			BlockPos offsetPos = be.getBlockPos().relative(direction);

			combinedLight = LightTexture.pack(be.getLevel().getBrightness(LightType.BLOCK, offsetPos), be.getLevel().getBrightness(LightType.SKY, offsetPos));

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
			return map.containsKey(Type.SKIN) ? minecraft.getSkinManager().registerTexture(map.get(Type.SKIN), Type.SKIN) : DefaultPlayerSkin.getDefaultSkin(PlayerEntity.createPlayerUUID(profile));
		}
		else
			return DefaultPlayerSkin.getDefaultSkin();
	}
}
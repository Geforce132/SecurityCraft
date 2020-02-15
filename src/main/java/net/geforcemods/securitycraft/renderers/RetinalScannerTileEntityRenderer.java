package net.geforcemods.securitycraft.renderers;

import java.util.Map;

import javax.annotation.Nullable;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.geforcemods.securitycraft.blocks.RetinalScannerBlock;
import net.geforcemods.securitycraft.misc.CustomModules;
import net.geforcemods.securitycraft.tileentity.RetinalScannerTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.Matrix3f;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.LightType;

public class RetinalScannerTileEntityRenderer extends TileEntityRenderer<RetinalScannerTileEntity>
{
	private static final float CORRECT_FACTOR = 1 / 550F;

	public RetinalScannerTileEntityRenderer(TileEntityRendererDispatcher terd)
	{
		super(terd);
	}

	@Override
	public void render(RetinalScannerTileEntity te, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay)
	{
		Direction direction = te.getBlockState().get(RetinalScannerBlock.FACING);

		if(!te.hasModule(CustomModules.DISGUISE) && direction != null)
		{
			matrix.push();

			switch(direction)
			{
				case NORTH:
					matrix.translate(0.25F, 1.0F / 16.0F, 0.0F);
					break;
				case SOUTH:
					matrix.translate(0.75F, 1.0F / 16.0F, 1.0F);
					matrix.rotate(Vector3f.YP.rotationDegrees(180.0F));
					break;
				case WEST:
					matrix.translate(0.0F, 1.0F / 16.0F, 0.75F);
					matrix.rotate(Vector3f.YP.rotationDegrees(90.0F));
					break;
				case EAST:
					matrix.translate(1.0F, 1.0F / 16.0F, 0.25F);
					matrix.rotate(Vector3f.YP.rotationDegrees(270.0F));
					break;
				default:
					break;
			}

			matrix.scale(-1.0F, -1.0F, 1.0F);

			IVertexBuilder vertexBuilder = buffer.getBuffer(RenderType.entityCutout(getSkinTexture(te.getPlayerProfile())));
			Matrix4f positionMatrix = matrix.getLast().getPositionMatrix();
			Matrix3f normalMatrix = matrix.getLast().getNormalMatrix();
			Vec3i normalVector = direction.getDirectionVec();

			combinedLight = LightTexture.packLight(te.getWorld().getLightFor(LightType.BLOCK, te.getPos().offset(direction)), te.getWorld().getLightFor(LightType.SKY, te.getPos().offset(direction)));

			// face
			vertexBuilder.pos(positionMatrix, CORRECT_FACTOR, CORRECT_FACTOR * 1.5F, 0F).color(255, 255, 255, 255).tex(0.125F, 0.25F).overlay(OverlayTexture.DEFAULT_LIGHT).lightmap(combinedLight).normal(normalMatrix, normalVector.getX(), normalVector.getY(), normalVector.getZ()).endVertex();
			vertexBuilder.pos(positionMatrix, CORRECT_FACTOR, -0.5F - CORRECT_FACTOR / 2F, 0F).color(255, 255, 255, 255).tex(0.125F, 0.125F).overlay(OverlayTexture.DEFAULT_LIGHT).lightmap(combinedLight).normal(normalMatrix, normalVector.getX(), normalVector.getY(), normalVector.getZ()).endVertex();
			vertexBuilder.pos(positionMatrix, -0.5F - CORRECT_FACTOR, -0.5F - CORRECT_FACTOR / 2, 0).color(255, 255, 255, 255).tex(0.25F, 0.125F).overlay(OverlayTexture.DEFAULT_LIGHT).lightmap(combinedLight).normal(normalMatrix, normalVector.getX(), normalVector.getY(), normalVector.getZ()).endVertex();
			vertexBuilder.pos(positionMatrix, -0.5F - CORRECT_FACTOR, CORRECT_FACTOR * 1.5F, 0F).color(255, 255, 255, 255).tex(0.25F, 0.25F).overlay(OverlayTexture.DEFAULT_LIGHT).lightmap(combinedLight).normal(normalMatrix, normalVector.getX(), normalVector.getY(), normalVector.getZ()).endVertex();

			// helmet
			vertexBuilder.pos(positionMatrix, CORRECT_FACTOR, CORRECT_FACTOR * 1.5F, 0F).color(255, 255, 255, 255).tex(0.625F, 0.25F).overlay(OverlayTexture.DEFAULT_LIGHT).lightmap(combinedLight).normal(normalMatrix, normalVector.getX(), normalVector.getY(), normalVector.getZ()).endVertex();
			vertexBuilder.pos(positionMatrix, CORRECT_FACTOR, -0.5F - CORRECT_FACTOR / 2F, 0F).color(255, 255, 255, 255).tex(0.625F, 0.125F).overlay(OverlayTexture.DEFAULT_LIGHT).lightmap(combinedLight).normal(normalMatrix, normalVector.getX(), normalVector.getY(), normalVector.getZ()).endVertex();
			vertexBuilder.pos(positionMatrix, -0.5F - CORRECT_FACTOR, -0.5F - CORRECT_FACTOR / 2, 0).color(255, 255, 255, 255).tex(0.75F, 0.125F).overlay(OverlayTexture.DEFAULT_LIGHT).lightmap(combinedLight).normal(normalMatrix, normalVector.getX(), normalVector.getY(), normalVector.getZ()).endVertex();
			vertexBuilder.pos(positionMatrix, -0.5F - CORRECT_FACTOR, CORRECT_FACTOR * 1.5F, 0F).color(255, 255, 255, 255).tex(0.75F, 0.25F).overlay(OverlayTexture.DEFAULT_LIGHT).lightmap(combinedLight).normal(normalMatrix, normalVector.getX(), normalVector.getY(), normalVector.getZ()).endVertex();

			matrix.pop();
		}
	}

	private static ResourceLocation getSkinTexture(@Nullable GameProfile profile)
	{
		if(profile != null)
		{
			Minecraft minecraft = Minecraft.getInstance();
			Map<Type, MinecraftProfileTexture> map = minecraft.getSkinManager().loadSkinFromCache(profile);
			return map.containsKey(Type.SKIN) ? minecraft.getSkinManager().loadSkin(map.get(Type.SKIN), Type.SKIN) : DefaultPlayerSkin.getDefaultSkin(PlayerEntity.getUUID(profile));
		}
		else
			return DefaultPlayerSkin.getDefaultSkinLegacy();
	}
}
package net.geforcemods.securitycraft.renderers;

import java.util.Map;

import javax.annotation.Nullable;

import org.lwjgl.opengl.GL11;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.geforcemods.securitycraft.blocks.RetinalScannerBlock;
import net.geforcemods.securitycraft.misc.CustomModules;
import net.geforcemods.securitycraft.tileentity.RetinalScannerTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RetinalScannerTileEntityRenderer extends TileEntityRenderer<RetinalScannerTileEntity> {
	private static final float CORRECT_FACTOR = 1 / 550F;

	public RetinalScannerTileEntityRenderer(TileEntityRendererDispatcher terd)
	{
		super(terd);
	}

	@Override
	public void render(RetinalScannerTileEntity te, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
		Direction direction = te.getBlockState().get(RetinalScannerBlock.FACING);
		if (!te.hasModule(CustomModules.DISGUISE) && direction != null)
		{
			matrix.push();

			switch (direction) {
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
			RenderHelper.disableStandardItemLighting();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GL11.GL_ONE, GL11.GL_ZERO);

			IVertexBuilder vertexBuilder = buffer.getBuffer(RenderType.entityDecal(this.getSkinTexture(te.getPlayerProfile())));

			// face
			vertexBuilder.pos(0 + CORRECT_FACTOR, 0 + CORRECT_FACTOR * 1.5, 0).tex(0.125F, 0.25F).lightmap(combinedLight).overlay(combinedOverlay).endVertex();
			vertexBuilder.pos(0 + CORRECT_FACTOR, -0.5 - CORRECT_FACTOR / 2, 0).tex(0.125F, 0.125F).lightmap(combinedLight).overlay(combinedOverlay).endVertex();
			vertexBuilder.pos(-0.5 - CORRECT_FACTOR, -0.5 - CORRECT_FACTOR / 2, 0).tex(0.25F, 0.125F).lightmap(combinedLight).overlay(combinedOverlay).endVertex();
			vertexBuilder.pos(-0.5 - CORRECT_FACTOR, 0 + CORRECT_FACTOR * 1.5, 0).tex(0.25F, 0.25F).lightmap(combinedLight).overlay(combinedOverlay).endVertex();

			// helmet
			vertexBuilder.pos(0 + CORRECT_FACTOR, 0 + CORRECT_FACTOR * 1.5, 0).tex(0.625F, 0.25F).lightmap(combinedLight).overlay(combinedOverlay).endVertex();
			vertexBuilder.pos(0 + CORRECT_FACTOR, -0.5 - CORRECT_FACTOR / 2, 0).tex(0.625F, 0.125F).lightmap(combinedLight).overlay(combinedOverlay).endVertex();
			vertexBuilder.pos(-0.5 - CORRECT_FACTOR, -0.5 - CORRECT_FACTOR / 2, 0).tex(0.75F, 0.125F).lightmap(combinedLight).overlay(combinedOverlay).endVertex();
			vertexBuilder.pos(-0.5 - CORRECT_FACTOR, 0 + CORRECT_FACTOR * 1.5, 0).tex(0.75F, 0.25F).lightmap(combinedLight).overlay(combinedOverlay).endVertex();

			GlStateManager.disableBlend();
			matrix.pop();
		}
	}

	private ResourceLocation getSkinTexture(@Nullable GameProfile profile) {
		ResourceLocation resourcelocation = DefaultPlayerSkin.getDefaultSkinLegacy();
		if (profile != null) {
			Minecraft minecraft = Minecraft.getInstance();
			Map<Type, MinecraftProfileTexture> map = minecraft.getSkinManager().loadSkinFromCache(profile);
			if (map.containsKey(Type.SKIN)) {
				resourcelocation = minecraft.getSkinManager().loadSkin(map.get(Type.SKIN), Type.SKIN);
			} else {
				resourcelocation = DefaultPlayerSkin.getDefaultSkin(PlayerEntity.getUUID(profile));
			}
		}

		return resourcelocation;
	}
}
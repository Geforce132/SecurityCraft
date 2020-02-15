package net.geforcemods.securitycraft.renderers;

import java.util.Map;

import javax.annotation.Nullable;

import org.lwjgl.opengl.GL11;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.geforcemods.securitycraft.blocks.RetinalScannerBlock;
import net.geforcemods.securitycraft.misc.CustomModules;
import net.geforcemods.securitycraft.tileentity.RetinalScannerTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
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

			IVertexBuilder vertexBuilder = buffer.getBuffer(scFace(getSkinTexture(te.getPlayerProfile())));
			Matrix4f positionMatrix = matrix.getLast().getPositionMatrix();

			// face
			vertexBuilder.pos(positionMatrix, CORRECT_FACTOR, CORRECT_FACTOR * 1.5F, 0F).tex(0.125F, 0.25F).endVertex();
			vertexBuilder.pos(positionMatrix, CORRECT_FACTOR, -0.5F - CORRECT_FACTOR / 2F, 0F).tex(0.125F, 0.125F).endVertex();
			vertexBuilder.pos(positionMatrix, -0.5F - CORRECT_FACTOR, -0.5F - CORRECT_FACTOR / 2, 0).tex(0.25F, 0.125F).endVertex();
			vertexBuilder.pos(positionMatrix, -0.5F - CORRECT_FACTOR, CORRECT_FACTOR * 1.5F, 0F).tex(0.25F, 0.25F).endVertex();

			// helmet
			vertexBuilder.pos(positionMatrix, CORRECT_FACTOR, CORRECT_FACTOR * 1.5F, 0F).tex(0.625F, 0.25F).endVertex();
			vertexBuilder.pos(positionMatrix, CORRECT_FACTOR, -0.5F - CORRECT_FACTOR / 2F, 0F).tex(0.625F, 0.125F).endVertex();
			vertexBuilder.pos(positionMatrix, -0.5F - CORRECT_FACTOR, -0.5F - CORRECT_FACTOR / 2, 0).tex(0.75F, 0.125F).endVertex();
			vertexBuilder.pos(positionMatrix, -0.5F - CORRECT_FACTOR, CORRECT_FACTOR * 1.5F, 0F).tex(0.75F, 0.25F).endVertex();

			matrix.pop();
		}
	}

	public static RenderType scFace(ResourceLocation texture)
	{
		RenderType.State renderTypeState = RenderType.State.builder()
				.texture(new RenderState.TextureState(texture, false, false))
				.writeMask(RenderStateGetter.colorDepthWrite())
				.cull(RenderStateGetter.cullDisabled())
				.transparency(RenderStateGetter.sourceOnlyTransparency())
				.texturing(RenderStateGetter.defaultTexturing())
				.alpha(RenderStateGetter.almostFullAlpha())
				.build(false);
		return RenderType.get("sc_face", DefaultVertexFormats.POSITION_TEX, GL11.GL_QUADS, 256, renderTypeState);
	}

	private static ResourceLocation getSkinTexture(@Nullable GameProfile profile) {
		if(profile != null)
		{
			Minecraft minecraft = Minecraft.getInstance();
			Map<Type, MinecraftProfileTexture> map = minecraft.getSkinManager().loadSkinFromCache(profile);
			return map.containsKey(Type.SKIN) ? minecraft.getSkinManager().loadSkin(map.get(Type.SKIN), Type.SKIN) : DefaultPlayerSkin.getDefaultSkin(PlayerEntity.getUUID(profile));
		}
		else return DefaultPlayerSkin.getDefaultSkinLegacy();
	}

	//used to circumvent protected without access transformers
	static class RenderStateGetter extends RenderState
	{
		private static final TransparencyState SOURCE_ONLY_TRANSPARENCY = new TransparencyState("source_only_transparency", () -> {
			RenderSystem.enableBlend();
			RenderSystem.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		}, () -> {
			RenderSystem.disableBlend();
			RenderSystem.defaultBlendFunc();
		});
		private static final AlphaState ALMOST_FULL_ALPHA = new AlphaState(0.99999997F);

		/** Don't call */
		private RenderStateGetter(String name, Runnable setupTask, Runnable clearTask)
		{
			super(name, setupTask, clearTask);
		}

		static TransparencyState sourceOnlyTransparency()
		{
			return SOURCE_ONLY_TRANSPARENCY;
		}

		static WriteMaskState colorDepthWrite()
		{
			return COLOR_DEPTH_WRITE;
		}

		static CullState cullDisabled()
		{
			return CULL_DISABLED;
		}

		static TexturingState defaultTexturing()
		{
			return DEFAULT_TEXTURING;
		}

		static AlphaState almostFullAlpha()
		{
			return ALMOST_FULL_ALPHA;
		}
	}
}
package net.geforcemods.securitycraft.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.entity.BouncingBetty;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BouncingBettyRenderer extends EntityRenderer<BouncingBetty> {

	public BouncingBettyRenderer(EntityRendererProvider.Context ctx)
	{
		super(ctx);
		shadowRadius = 0.5F;
	}

	@Override
	public void render(BouncingBetty entity, float entityYaw, float partialTicks, PoseStack matrix, MultiBufferSource buffer, int packedLight)
	{
		matrix.pushPose();
		matrix.translate(0.0D, 0.5D, 0.0D);

		if(entity.fuse - partialTicks + 1.0F < 10.0F)
		{
			float alpha = 1.0F - (entity.fuse - partialTicks + 1.0F) / 10.0F;
			alpha = Mth.clamp(alpha, 0.0F, 1.0F);
			alpha *= alpha;
			alpha *= alpha;
			float scale = 1.0F + alpha * 0.3F;
			matrix.scale(scale, scale, scale);
		}

		matrix.mulPose(Vector3f.YP.rotationDegrees(-90.0F));
		matrix.translate(-0.5D, -0.5D, 0.5D);
		matrix.mulPose(Vector3f.YP.rotationDegrees(90.0F));
		Minecraft.getInstance().getBlockRenderer().renderSingleBlock(SCContent.BOUNCING_BETTY.get().defaultBlockState(), matrix, buffer, packedLight, OverlayTexture.NO_OVERLAY);
		matrix.popPose();
		super.render(entity, entityYaw, partialTicks, matrix, buffer, packedLight);
	}

	@Override
	public ResourceLocation getTextureLocation(BouncingBetty entity)
	{
		return TextureAtlas.LOCATION_BLOCKS;
	}
}
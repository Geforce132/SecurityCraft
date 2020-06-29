package net.geforcemods.securitycraft.renderers;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.entity.BouncingBettyEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BouncingBettyRenderer extends EntityRenderer<BouncingBettyEntity> {

	public BouncingBettyRenderer(EntityRendererManager renderManager)
	{
		super(renderManager);
		shadowSize = 0.5F;
	}

	@Override
	public void render(BouncingBettyEntity entity, float entityYaw, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int packedLight)
	{
		matrix.push();
		matrix.translate(0.0D, 0.5D, 0.0D);

		if(entity.fuse - partialTicks + 1.0F < 10.0F)
		{
			float alpha = 1.0F - (entity.fuse - partialTicks + 1.0F) / 10.0F;
			alpha = MathHelper.clamp(alpha, 0.0F, 1.0F);
			alpha *= alpha;
			alpha *= alpha;
			float scale = 1.0F + alpha * 0.3F;
			matrix.scale(scale, scale, scale);
		}

		matrix.rotate(Vector3f.YP.rotationDegrees(-90.0F));
		matrix.translate(-0.5D, -0.5D, 0.5D);
		matrix.rotate(Vector3f.YP.rotationDegrees(90.0F));
		Minecraft.getInstance().getBlockRendererDispatcher().renderBlock(SCContent.BOUNCING_BETTY.get().getDefaultState(), matrix, buffer, packedLight, OverlayTexture.NO_OVERLAY);
		matrix.pop();
		super.render(entity, entityYaw, partialTicks, matrix, buffer, packedLight);
	}

	@Override
	public ResourceLocation getEntityTexture(BouncingBettyEntity entity)
	{
		return AtlasTexture.LOCATION_BLOCKS_TEXTURE;
	}
}
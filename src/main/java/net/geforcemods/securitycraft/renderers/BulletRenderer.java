package net.geforcemods.securitycraft.renderers;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.entity.BulletEntity;
import net.geforcemods.securitycraft.models.BulletModel;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BulletRenderer extends EntityRenderer<BulletEntity>
{
	private static final ResourceLocation TEXTURE = new ResourceLocation(SecurityCraft.MODID + ":textures/entity/bullet.png");
	private static final BulletModel MODEL = new BulletModel();

	public BulletRenderer(EntityRendererManager renderManager)
	{
		super(renderManager);
	}

	@Override
	public void render(BulletEntity entity, float p_225623_2_, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int p_225623_6_)
	{
		matrix.rotate(new Quaternion(Vector3f.YP, entity.rotationYaw, true)); //YP
		MODEL.render(matrix, buffer.getBuffer(RenderType.entitySolid(getEntityTexture(entity))), p_225623_6_, OverlayTexture.DEFAULT_LIGHT, 1.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	public ResourceLocation getEntityTexture(BulletEntity entity)
	{
		return TEXTURE;
	}
}

package net.geforcemods.securitycraft.renderers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.entity.BulletEntity;
import net.geforcemods.securitycraft.models.BulletModel;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
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
	public void func_225623_a_(BulletEntity entity, float p_225623_2_, float partialTicks, MatrixStack stack, IRenderTypeBuffer buffer, int p_225623_6_)
	{
		float x = entity.getPosition().getX();
		float y = entity.getPosition().getY();
		float z = entity.getPosition().getZ();
		RenderSystem.pushMatrix();
		RenderSystem.translated(x, y, z);
		RenderSystem.rotatef(entity.rotationYaw, 0.0F, 1.0F, 0.0F);
		bindEntityTexture(entity);
		MODEL.render(entity, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
		RenderSystem.popMatrix();
	}

	@Override
	public ResourceLocation getEntityTexture(BulletEntity entity)
	{
		return TEXTURE;
	}
}

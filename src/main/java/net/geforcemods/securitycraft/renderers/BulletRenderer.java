package net.geforcemods.securitycraft.renderers;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.entity.Bullet;
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
public class BulletRenderer extends EntityRenderer<Bullet> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(SecurityCraft.MODID + ":textures/entity/bullet.png");
	private static final BulletModel MODEL = new BulletModel();

	public BulletRenderer(EntityRendererManager renderManager) {
		super(renderManager);
	}

	@Override
	public void render(Bullet entity, float p_225623_2_, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int p_225623_6_) {
		matrix.mulPose(new Quaternion(Vector3f.YP, entity.yRot, true)); //YP
		MODEL.renderToBuffer(matrix, buffer.getBuffer(RenderType.entitySolid(getTextureLocation(entity))), p_225623_6_, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	public ResourceLocation getTextureLocation(Bullet entity) {
		return TEXTURE;
	}
}

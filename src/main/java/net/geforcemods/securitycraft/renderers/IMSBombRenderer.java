package net.geforcemods.securitycraft.renderers;

import com.mojang.blaze3d.platform.GlStateManager;

import net.geforcemods.securitycraft.entity.IMSBombEntity;
import net.geforcemods.securitycraft.models.IMSBombModel;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class IMSBombRenderer extends EntityRenderer<IMSBombEntity> {

	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/entity/ims_bomb.png");

	/** instance of ModelIMSBomb for rendering */
	protected static final IMSBombModel modelBomb = new IMSBombModel();

	public IMSBombRenderer(EntityRendererManager renderManager){
		super(renderManager);
	}

	@Override
	public void doRender(IMSBombEntity imsBomb, double x, double y, double z, float entityYaw, float partialTicks) {
		GlStateManager.pushMatrix();

		GlStateManager.translatef((float)x - 0.1F, (float)y, (float)z - 0.1F);
		bindEntityTexture(imsBomb);
		GlStateManager.scalef(1.4F, 1.4F, 1.4F);
		modelBomb.render(imsBomb, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);

		GlStateManager.popMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(IMSBombEntity imsBomb) {
		return TEXTURE;
	}
}

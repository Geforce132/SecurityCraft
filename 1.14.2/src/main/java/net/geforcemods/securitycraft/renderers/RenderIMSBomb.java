package net.geforcemods.securitycraft.renderers;

import net.geforcemods.securitycraft.entity.EntityIMSBomb;
import net.geforcemods.securitycraft.models.ModelIMSBomb;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderIMSBomb extends Render<EntityIMSBomb> {

	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/entity/ims_bomb.png");

	/** instance of ModelIMSBomb for rendering */
	protected static final ModelIMSBomb modelBomb = new ModelIMSBomb();

	public RenderIMSBomb(RenderManager renderManager){
		super(renderManager);
	}

	@Override
	public void doRender(EntityIMSBomb imsBomb, double x, double y, double z, float entityYaw, float partialTicks) {
		GlStateManager.pushMatrix();

		GlStateManager.translatef((float)x - 0.1F, (float)y, (float)z - 0.1F);
		bindEntityTexture(imsBomb);
		GlStateManager.scalef(1.4F, 1.4F, 1.4F);
		modelBomb.render(imsBomb, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);

		GlStateManager.popMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityIMSBomb imsBomb) {
		return TEXTURE;
	}
}

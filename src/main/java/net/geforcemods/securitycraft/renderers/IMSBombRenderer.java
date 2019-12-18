package net.geforcemods.securitycraft.renderers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.geforcemods.securitycraft.entity.IMSBombEntity;
import net.geforcemods.securitycraft.models.IMSBombModel;
import net.minecraft.client.renderer.IRenderTypeBuffer;
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
	public void func_225623_a_(IMSBombEntity imsBomb, float p_225623_2_, float partialTicks, MatrixStack stack, IRenderTypeBuffer buffer, int p_225623_6_)
	{
		float x = imsBomb.getPosition().getX();
		float y = imsBomb.getPosition().getY();
		float z = imsBomb.getPosition().getZ();
		RenderSystem.pushMatrix();

		RenderSystem.translatef(x - 0.1F, y, z - 0.1F);
		bindEntityTexture(imsBomb);
		RenderSystem.scalef(1.4F, 1.4F, 1.4F);
		modelBomb.render(imsBomb, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);

		RenderSystem.popMatrix();
	}

	@Override
	public ResourceLocation getEntityTexture(IMSBombEntity imsBomb) {
		return TEXTURE;
	}
}

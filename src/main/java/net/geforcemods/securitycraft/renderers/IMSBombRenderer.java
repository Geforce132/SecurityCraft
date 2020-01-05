package net.geforcemods.securitycraft.renderers;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.geforcemods.securitycraft.entity.IMSBombEntity;
import net.geforcemods.securitycraft.models.IMSBombModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
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
	//TODO: does not work yet
	@Override
	public void func_225623_a_(IMSBombEntity imsBomb, float p_225623_2_, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int p_225623_6_)
	{
		matrix.func_227861_a_(-0.1D, 0, 0.1D); //translate
		matrix.func_227862_a_(1.4F, 1.4F, 1.4F); //scale
		Minecraft.getInstance().textureManager.bindTexture(getEntityTexture(imsBomb));
		modelBomb.func_225598_a_(matrix, buffer.getBuffer(RenderType.func_228634_a_(getEntityTexture(imsBomb))), p_225623_6_, OverlayTexture.field_229196_a_, 1.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	public ResourceLocation getEntityTexture(IMSBombEntity imsBomb) {
		return TEXTURE;
	}
}

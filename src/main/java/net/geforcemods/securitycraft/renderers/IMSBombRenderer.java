package net.geforcemods.securitycraft.renderers;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.geforcemods.securitycraft.entity.IMSBomb;
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
public class IMSBombRenderer extends EntityRenderer<IMSBomb> {
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/entity/ims_bomb.png");
	/** instance of ModelIMSBomb for rendering */
	protected static final IMSBombModel model = new IMSBombModel();

	public IMSBombRenderer(EntityRendererManager renderManager) {
		super(renderManager);
	}

	@Override
	public void render(IMSBomb imsBomb, float entityYaw, float partialTicks, MatrixStack pose, IRenderTypeBuffer buffer, int packedLight) {
		pose.translate(-0.1D, 0, 0.1D);
		pose.scale(1.4F, 1.4F, 1.4F);
		Minecraft.getInstance().textureManager.bind(getTextureLocation(imsBomb));
		model.renderToBuffer(pose, buffer.getBuffer(RenderType.entitySolid(getTextureLocation(imsBomb))), packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	public ResourceLocation getTextureLocation(IMSBomb imsBomb) {
		return TEXTURE;
	}
}

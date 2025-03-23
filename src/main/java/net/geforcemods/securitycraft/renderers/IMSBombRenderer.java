package net.geforcemods.securitycraft.renderers;

import com.mojang.blaze3d.vertex.PoseStack;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.entity.IMSBomb;
import net.geforcemods.securitycraft.models.IMSBombModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class IMSBombRenderer extends EntityRenderer<IMSBomb, EntityRenderState> {
	private static final ResourceLocation TEXTURE = SecurityCraft.resLoc("textures/entity/ims_bomb.png");
	private final IMSBombModel model;

	public IMSBombRenderer(EntityRendererProvider.Context ctx) {
		super(ctx);

		model = new IMSBombModel(ctx.bakeLayer(ClientHandler.IMS_BOMB_LOCATION));
	}

	@Override
	public void render(EntityRenderState state, PoseStack pose, MultiBufferSource buffer, int packedLight) {
		pose.translate(-0.1D, 0, 0.1D);
		pose.scale(1.4F, 1.4F, 1.4F);
		model.renderToBuffer(pose, buffer.getBuffer(RenderType.entitySolid(TEXTURE)), packedLight, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);
	}

	@Override
	public EntityRenderState createRenderState() {
		return new EntityRenderState();
	}
}

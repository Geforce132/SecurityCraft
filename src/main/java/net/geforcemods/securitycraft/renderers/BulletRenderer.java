package net.geforcemods.securitycraft.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.entity.sentry.Bullet;
import net.geforcemods.securitycraft.models.BulletModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.ArrowRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class BulletRenderer extends EntityRenderer<Bullet, ArrowRenderState> {
	private static final ResourceLocation TEXTURE = SecurityCraft.resLoc("textures/entity/bullet.png");
	private final BulletModel model;

	public BulletRenderer(EntityRendererProvider.Context ctx) {
		super(ctx);

		model = new BulletModel(ctx.bakeLayer(ClientHandler.BULLET_LOCATION));
	}

	@Override
	public void render(ArrowRenderState state, PoseStack pose, MultiBufferSource buffer, int packedLight) {
		pose.mulPose(Axis.YP.rotationDegrees(state.yRot));
		model.renderToBuffer(pose, buffer.getBuffer(RenderType.entitySolid(TEXTURE)), packedLight, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);
	}

	@Override
	public ArrowRenderState createRenderState() {
		return new ArrowRenderState();
	}
}

package net.geforcemods.securitycraft.renderers;

import com.mojang.blaze3d.vertex.PoseStack;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.blockentities.TrophySystemBlockEntity;
import net.geforcemods.securitycraft.renderers.state.TrophySystemRenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class TrophySystemRenderer implements BlockEntityRenderer<TrophySystemBlockEntity, TrophySystemRenderState> {
	/**
	 * The number of blocks away from the trophy system you can be for the laser beam between itself and the projectile to be
	 * rendered
	 */
	public static final int RENDER_DISTANCE = 50;

	public TrophySystemRenderer(BlockEntityRendererProvider.Context ctx) {}

	@Override
	public void submit(TrophySystemRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
		if (ClientHandler.DISGUISED_BLOCK_RENDER_DELEGATE.trySubmitDelegate(state.disguiseRenderState, poseStack, collector, camera))
			return;

		Vec3 target = state.target;

		if (target != null) {
			collector.submitCustomGeometry(poseStack, RenderType.lines(), (pose, builder) -> {
				BlockPos pos = state.blockPos;
				int r = state.r;
				int g = state.g;
				int b = state.b;

				//draws a line between the trophy system and the projectile that it's targeting
				builder.addVertex(pose, 0.5F, 0.75F, 0.5F).setColor(r, g, b, 255).setNormal(1.0F, 1.0F, 1.0F);
				builder.addVertex(pose, (float) (target.x() - pos.getX()), (float) (target.y() - pos.getY()), (float) (target.z() - pos.getZ())).setColor(r, g, b, 255).setNormal(1.0F, 1.0F, 1.0F);
			});
		}
	}

	@Override
	public TrophySystemRenderState createRenderState() {
		return new TrophySystemRenderState();
	}

	@Override
	public void extractRenderState(TrophySystemBlockEntity be, TrophySystemRenderState state, float partialTick, Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
		BlockEntityRenderer.super.extractRenderState(be, state, partialTick, cameraPos, crumblingOverlay);
		state.disguiseRenderState = ClientHandler.DISGUISED_BLOCK_RENDER_DELEGATE.tryExtractFromDelegate(be, partialTick, cameraPos, crumblingOverlay);

		Entity target = be.getTarget();

		if (target == null)
			state.target = null;
		else
			state.target = target.position();

		ItemStack lens = be.getLensContainer().getItem(0);
		int r = 255, g = 255, b = 255;

		if (lens.has(DataComponents.DYED_COLOR)) {
			int color = lens.get(DataComponents.DYED_COLOR).rgb();

			r = (color >> 0x10) & 0xFF;
			g = (color >> 0x8) & 0xFF;
			b = color & 0xFF;
		}

		state.r = r;
		state.g = g;
		state.b = b;
	}

	@Override
	public boolean shouldRenderOffScreen() {
		return true;
	}

	@Override
	public AABB getRenderBoundingBox(TrophySystemBlockEntity be) {
		return new AABB(be.getBlockPos()).inflate(RENDER_DISTANCE);
	}
}

package net.geforcemods.securitycraft.renderers;

import com.mojang.blaze3d.vertex.PoseStack;

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
		//TODO: render delegate
		//if (ClientHandler.DISGUISED_BLOCK_RENDER_DELEGATE.tryRenderDelegate(be, partialTicks, pose, buffer, combinedLight, combinedOverlay, cameraPos))
		//	return;
		if (!state.hasTarget)
			return;

		BlockPos bePos = state.blockPos;
		float r = state.r;
		float g = state.g;
		float b = state.b;

		collector.submitCustomGeometry(poseStack, RenderType.lines(), (pose, buffer) -> {
			buffer.addVertex(pose, 0.5F, 0.75F, 0.5F).setColor(r, g, b, 255).setNormal(1.0F, 1.0F, 1.0F);
			buffer.addVertex(pose, (float) (state.targetX - bePos.getX()), (float) (state.targetY - bePos.getY()), (float) (state.targetZ - bePos.getZ())).setColor(r, g, b, 255).setNormal(1.0F, 1.0F, 1.0F);

		});
	}

	@Override
	public TrophySystemRenderState createRenderState() {
		return new TrophySystemRenderState();
	}

	@Override
	public void extractRenderState(TrophySystemBlockEntity be, TrophySystemRenderState state, float partialTick, Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
		BlockEntityRenderer.super.extractRenderState(be, state, partialTick, cameraPos, crumblingOverlay);

		ItemStack lens = be.getLensContainer().getItem(0);
		Entity target = be.getTarget();

		if (target != null) {
			state.hasTarget = true;
			state.targetX = target.getX();
			state.targetY = target.getY();
			state.targetZ = target.getZ();
		}
		else
			state.hasTarget = false;

		if (lens.has(DataComponents.DYED_COLOR)) {
			int color = lens.get(DataComponents.DYED_COLOR).rgb();

			state.r = (color >> 0x10) & 0xFF;
			state.g = (color >> 0x8) & 0xFF;
			state.b = color & 0xFF;
		}
		else
			state.r = state.g = state.b = 255;
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

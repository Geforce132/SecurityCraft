package net.geforcemods.securitycraft.renderers;

import com.mojang.blaze3d.vertex.PoseStack;

import net.geforcemods.securitycraft.api.OwnableBlockEntity;
import net.geforcemods.securitycraft.blockentities.FrameBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.phys.Vec3;

// used by the reinforced observer and frame
public class OwnableBlockEntityRenderer extends DisguisableBlockEntityRenderer<OwnableBlockEntity> {
	public OwnableBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
		super(ctx);
	}

	@Override
	public boolean shouldRender(OwnableBlockEntity be, Vec3 pos) {
		return be.shouldRender();
	}

	@Override
	public void render(OwnableBlockEntity be, float partialTicks, PoseStack pose, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
		if (be instanceof FrameBlockEntity frame) {
			FrameBlockEntityRenderer.render(frame, partialTicks, pose, buffer, combinedLight, combinedOverlay);
			return;
		}

		super.render(be, partialTicks, pose, buffer, combinedLight, combinedOverlay);
	}
}

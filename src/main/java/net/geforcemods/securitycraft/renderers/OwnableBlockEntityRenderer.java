package net.geforcemods.securitycraft.renderers;

import com.mojang.blaze3d.vertex.PoseStack;

import net.geforcemods.securitycraft.api.OwnableBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.phys.Vec3;

// only used for the observer right now, as it doesn't have its own block entity type
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
		super.render(be, partialTicks, pose, buffer, combinedLight, combinedOverlay);
	}
}

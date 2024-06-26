package net.geforcemods.securitycraft.renderers;

import net.geforcemods.securitycraft.api.OwnableBlockEntity;
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
}

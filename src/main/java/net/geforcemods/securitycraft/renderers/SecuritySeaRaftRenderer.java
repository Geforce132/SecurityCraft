package net.geforcemods.securitycraft.renderers;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RaftRenderer;
import net.minecraft.client.renderer.entity.state.BoatRenderState;

public class SecuritySeaRaftRenderer extends RaftRenderer {
	private final EntityModel<BoatRenderState> model;

	public SecuritySeaRaftRenderer(EntityRendererProvider.Context ctx, ModelLayerLocation location) {
		super(ctx, location);
		model = new SecuritySeaRaftModel(ctx.bakeLayer(location));
	}

	@Override
	protected EntityModel<BoatRenderState> model() {
		return model;
	}
}

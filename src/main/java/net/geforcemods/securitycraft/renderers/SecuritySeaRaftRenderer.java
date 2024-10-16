package net.geforcemods.securitycraft.renderers;

import net.geforcemods.securitycraft.SecurityCraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RaftRenderer;
import net.minecraft.client.renderer.entity.state.BoatRenderState;
import net.minecraft.resources.ResourceLocation;

public class SecuritySeaRaftRenderer extends RaftRenderer {
	private final ResourceLocation texture;
	private final EntityModel<BoatRenderState> model;

	public SecuritySeaRaftRenderer(EntityRendererProvider.Context ctx, ModelLayerLocation location) {
		super(ctx, location);
		model = new SecuritySeaRaftModel(ctx.bakeLayer(location));
		texture = SecurityCraft.resLoc("textures/entity/" + location.model().getPath().replace("chest", "security_sea") + ".png");
	}

	@Override
	protected EntityModel<BoatRenderState> model() {
		return model;
	}

	@Override
	protected RenderType renderType() {
		return model.renderType(texture);
	}
}

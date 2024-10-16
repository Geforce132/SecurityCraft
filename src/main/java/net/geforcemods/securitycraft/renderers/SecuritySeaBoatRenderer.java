package net.geforcemods.securitycraft.renderers;

import net.geforcemods.securitycraft.SecurityCraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.BoatRenderState;
import net.minecraft.resources.ResourceLocation;

public class SecuritySeaBoatRenderer extends BoatRenderer {
	private final ResourceLocation texture;
	private final EntityModel<BoatRenderState> model;

	public SecuritySeaBoatRenderer(EntityRendererProvider.Context ctx, ModelLayerLocation location) {
		super(ctx, location);
		model = new SecuritySeaBoatModel(ctx.bakeLayer(location));
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

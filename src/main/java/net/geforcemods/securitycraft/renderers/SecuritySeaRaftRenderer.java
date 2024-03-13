package net.geforcemods.securitycraft.renderers;

import com.mojang.datafixers.util.Pair;

import net.geforcemods.securitycraft.SecurityCraft;
import net.minecraft.client.model.ChestRaftModel;
import net.minecraft.client.model.ListModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.vehicle.Boat;

public class SecuritySeaRaftRenderer extends BoatRenderer {
	private final Pair<ResourceLocation, ListModel<Boat>> textureAndModel;

	public SecuritySeaRaftRenderer(EntityRendererProvider.Context ctx) {
		super(ctx, true);
		textureAndModel = new Pair<>(new ResourceLocation(SecurityCraft.MODID, "textures/entity/security_sea_raft.png"), new ChestRaftModel(ctx.bakeLayer(ModelLayers.createChestBoatModelName(Boat.Type.BAMBOO))));
	}

	@Override
	public Pair<ResourceLocation, ListModel<Boat>> getModelWithLocation(Boat boat) {
		return textureAndModel;
	}
}

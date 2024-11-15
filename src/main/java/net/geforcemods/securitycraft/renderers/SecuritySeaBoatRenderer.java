package net.geforcemods.securitycraft.renderers;

import java.util.stream.Stream;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.models.SecuritySeaBoatModel;
import net.geforcemods.securitycraft.models.SecuritySeaRaftModel;
import net.minecraft.client.model.ListModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.Boat.Type;

public class SecuritySeaBoatRenderer extends BoatRenderer {
	public SecuritySeaBoatRenderer(EntityRendererProvider.Context ctx) {
		super(ctx, true);
		//@formatter:off
        boatResources = Stream.of(Boat.Type.values())
                .collect(ImmutableMap.toImmutableMap(
                        type -> type,
                        type -> Pair.of(new ResourceLocation(SecurityCraft.MODID, "textures/entity/security_sea_boat/" + type.getName() + ".png"), createBoatModel(ctx, type, true))));
	}

	@Override
	public ListModel<Boat> createBoatModel(Context ctx, Type type, boolean chestBoat) {
        ModelPart modelPart = ctx.bakeLayer(ModelLayers.createChestBoatModelName(type));

        if (type == Boat.Type.BAMBOO)
            return new SecuritySeaRaftModel(modelPart);
         else
            return new SecuritySeaBoatModel(modelPart);
	}
}

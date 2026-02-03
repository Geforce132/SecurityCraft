package net.geforcemods.securitycraft.models;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.geforcemods.securitycraft.ClientHandler;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.geom.ModelPart;

public class SecuritySeaBoatModel extends BoatModel {
	public SecuritySeaBoatModel(ModelPart modelPart) {
		super(modelPart);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int packedARGB) {
		int tinted = ClientHandler.mixWithReinforcedTintIfEnabled(packedARGB);
		List<ModelPart> parts = allParts();

		for (int i = 0; i < parts.size(); i++) {
			boolean isChestPart = i == 1 || i == 7 || i == 10;

			parts.get(i).render(poseStack, buffer, packedLight, packedOverlay, isChestPart ? packedARGB : tinted);
		}
	}
}

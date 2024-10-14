package net.geforcemods.securitycraft.renderers;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.geforcemods.securitycraft.ClientHandler;
import net.minecraft.client.model.RaftModel;
import net.minecraft.client.model.geom.ModelPart;

public class SecuritySeaRaftModel extends RaftModel {
	public SecuritySeaRaftModel(ModelPart modelPart) {
		super(modelPart);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int packedARGB) {
		int tinted = ClientHandler.mixWithReinforcedTintIfEnabled(packedARGB);
		List<ModelPart> parts = allParts();

		//The last three parts are the chest, and that one should not get tinted
		for (int i = 0; i < parts.size() - 2; i++) {
			parts.get(i).render(poseStack, buffer, packedLight, packedOverlay, tinted);
		}

		for (int i = parts.size() - 3; i < parts.size(); i++) {
			parts.get(i).render(poseStack, buffer, packedLight, packedOverlay, packedARGB);
		}
	}
}

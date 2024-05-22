package net.geforcemods.securitycraft.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.geforcemods.securitycraft.ClientHandler;
import net.minecraft.client.model.ChestRaftModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.FastColor;

public class SecuritySeaRaftModel extends ChestRaftModel {
	public SecuritySeaRaftModel(ModelPart modelPart) {
		super(modelPart);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		int packedColor = FastColor.ARGB32.color((int) (alpha * 255.0F), (int) (red * 255.0F), (int) (green * 255.0F), (int) (blue * 255.0F));
		int tinted = ClientHandler.mixWithReinforcedTintIfEnabled(packedColor);
		float tintedRed = FastColor.ARGB32.red(tinted) / 255.0F;
		float tintedGreen = FastColor.ARGB32.green(tinted) / 255.0F;
		float tintedBlue = FastColor.ARGB32.blue(tinted) / 255.0F;
		float tintedAlpha = FastColor.ARGB32.alpha(tinted) / 255.0F;
		var parts = parts();

		//The last three parts are the chest, and that one should not get tinted
		for (int i = 0; i < parts.size() - 2; i++) {
			parts.get(i).render(poseStack, buffer, packedLight, packedOverlay, tintedRed, tintedGreen, tintedBlue, tintedAlpha);
		}

		for (int i = parts.size() - 3; i < parts.size(); i++) {
			parts.get(i).render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		}
	}
}

package net.geforcemods.securitycraft.models;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.Entity;

public class SecureRedstoneInterfaceDishModel extends EntityModel<Entity> {
	private final ModelPart modelParts;

	public SecureRedstoneInterfaceDishModel(ModelPart root) {
		this.modelParts = root.getChild("cubes");
	}

	public static LayerDefinition createLayer() {
		MeshDefinition meshDefinition = new MeshDefinition();
		PartDefinition partDefinition = meshDefinition.getRoot();

		//@formatter:off
		partDefinition.addOrReplaceChild("cubes",
				CubeListBuilder.create()
				.texOffs(0, 4).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F)
				.texOffs(0, 2).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 1.0F, 1.0F)
				.texOffs(0, 0).addBox(-2.0F, 1.0F, -2.0F, 4.0F, 1.0F, 1.0F)
				.texOffs(7, 7).addBox(1.0F, -1.0F, -2.0F, 1.0F, 2.0F, 1.0F)
				.texOffs(0, 8).addBox(-2.0F, -1.0F, -2.0F, 1.0F, 2.0F, 1.0F),
				PartPose.offset(0.0F, 12.0F, 0.0F));
		//@formatter:on
		return LayerDefinition.create(meshDefinition, 16, 16);
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		modelParts.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	public void rotate(float rotation) {
		modelParts.yRot = rotation;
	}
}
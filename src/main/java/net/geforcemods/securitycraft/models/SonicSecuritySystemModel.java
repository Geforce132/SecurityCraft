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

public class SonicSecuritySystemModel extends EntityModel<Entity> {
	public final ModelPart dish;

	public SonicSecuritySystemModel(ModelPart modelPart) {
		dish = modelPart.getChild("dish");
	}

	public static LayerDefinition createLayer() {
		MeshDefinition meshDefinition = new MeshDefinition();
		PartDefinition partDefinition = meshDefinition.getRoot();

		//@formatter:off
		partDefinition.addOrReplaceChild("dish", CubeListBuilder.create()
				.texOffs(15, 0).addBox(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 2.0F)
				.texOffs(15, 3).addBox(-1.5F, -1.5F, 1.0F, 3.0F, 1.0F, 1.0F)
				.texOffs(15, 5).addBox(0.5F, -0.5F, 1.0F, 1.0F, 1.0F, 1.0F)
				.texOffs(15, 7).addBox(-1.5F, -0.5F, 1.0F, 1.0F, 1.0F, 1.0F)
				.texOffs(15, 9).addBox(-1.5F, 0.5F, 1.0F, 3.0F, 1.0F, 1.0F)
				.texOffs(0, 0).addBox(-2.5F, -2.5F, 1.5F, 5.0F, 1.0F, 1.0F)
				.texOffs(0, 2).addBox(1.5F, -1.5F, 1.5F, 1.0F, 3.0F, 1.0F)
				.texOffs(0, 6).addBox(-2.5F, -1.5F, 1.5F, 1.0F, 3.0F, 1.0F)
				.texOffs(0, 10).addBox(-2.5F, 1.5F, 1.5F, 5.0F, 1.0F, 1.0F),
				PartPose.offset(0.0F, 10.5F, 0.0F));
		//@formatter:on
		return LayerDefinition.create(meshDefinition, 32, 32);
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}

	@Override
	public void renderToBuffer(PoseStack pose, VertexConsumer buffer, int packedLight, int packedOverlay, int packedARGB) {
		dish.render(pose, buffer, packedLight, packedOverlay);
	}

	public void setRadarRotation(float rotation) {
		dish.yRot = rotation;
	}
}
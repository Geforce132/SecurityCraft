package net.geforcemods.securitycraft.models;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

public class SecureRedstoneInterfaceDishModel extends EntityModel<EntityRenderState> {
	private final ModelPart modelParts;

	public SecureRedstoneInterfaceDishModel(ModelPart root) {
		super(root);
		modelParts = root.getChild("cubes");
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

	public void rotate(float rotation) {
		modelParts.yRot = rotation;
	}
}
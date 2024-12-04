package net.geforcemods.securitycraft.models;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;

public class DisplayCaseModel extends Model {
	private final ModelPart door;

	public DisplayCaseModel(ModelPart root) {
		super(root, RenderType::entityCutout);
		door = root.getChild("door");
	}

	public static LayerDefinition createModelLayer() {
		MeshDefinition meshDefinition = new MeshDefinition();
		PartDefinition partDefinition = meshDefinition.getRoot();

		//@formatter:off
		partDefinition.addOrReplaceChild("main", CubeListBuilder.create()
				.texOffs(0, 23).addBox(-6.0F, -3.0F, -8.0F, 12.0F, 1.0F, 5.0F)
				.texOffs(36, 0).addBox(-6.0F, -13.0F, -8.0F, 1.0F, 10.0F, 5.0F)
				.texOffs(13, 4).addBox(-5.0F, -13.0F, -8.0F, 10.0F, 10.0F, 1.0F)
				.texOffs(0, 0).addBox(5.0F, -13.0F, -8.0F, 1.0F, 10.0F, 5.0F)
				.texOffs(0, 16).addBox(-6.0F, -14.0F, -8.0F, 12.0F, 1.0F, 5.0F), PartPose.offset(0.0F, 24.0F, 0.0F));
		partDefinition.addOrReplaceChild("door", CubeListBuilder.create()
				.texOffs(5, 31).addBox(1.0F, -5.0F, 0.0F, 10.0F, 10.0F, 1.0F)
				.texOffs(0, 31).addBox(11.0F, -5.0F, 0.0F, 1.0F, 10.0F, 1.0F)
				.texOffs(28, 31).addBox(0.0F, -5.0F, 0.0F, 1.0F, 10.0F, 1.0F)
				.texOffs(0, 43).addBox(0.0F, -6.0F, 0.0F, 12.0F, 1.0F, 1.0F)
				.texOffs(0, 46).addBox(0.0F, 5.0F, 0.0F, 12.0F, 1.0F, 1.0F)
				.texOffs(27, 43).addBox(11.0F, -1.5F, 1.0F, 1.0F, 3.0F, 1.0F), PartPose.offset(-6.0F, 16.0F, -3.0F));
		//@formatter:on

		return LayerDefinition.create(meshDefinition, 48, 48);
	}

	public void setUpAnim(float openness) {
		door.yRot = -(openness * (float) (Math.PI / 2.0F));
	}
}

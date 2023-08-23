package net.geforcemods.securitycraft.models;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.geforcemods.securitycraft.entity.sentry.Sentry;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class SentryModel extends EntityModel<Sentry> {
	public final ModelPart base;
	public final ModelPart body;
	public final ModelPart neck;
	public final ModelPart head;
	public final ModelPart hair;
	public final ModelPart rightEye;
	public final ModelPart leftEye;
	public final ModelPart nose;
	private final List<ModelPart> headPartList;

	public SentryModel(ModelPart modelPart) {
		base = modelPart.getChild("base");
		body = modelPart.getChild("body");
		neck = modelPart.getChild("neck");
		head = modelPart.getChild("head");
		hair = modelPart.getChild("hair");
		rightEye = modelPart.getChild("right_eye");
		leftEye = modelPart.getChild("left_eye");
		nose = modelPart.getChild("nose");
		headPartList = List.of(head, neck, rightEye, body, nose, leftEye, hair);
	}

	public static LayerDefinition createLayer() {
		MeshDefinition meshDefinition = new MeshDefinition();
		PartDefinition partDefinition = meshDefinition.getRoot();

		partDefinition.addOrReplaceChild("base", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 0.0F, 0.0F, 15.0F, 15.0F, 15.0F), PartPose.offset(-7.5F, 9.0F, -7.5F));
		partDefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 30).addBox(0.0F, 0.0F, 0.0F, 6.0F, 4.0F, 6.0F), PartPose.offset(-3.0F, 5.0F, -3.0F));
		partDefinition.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(45, 0).addBox(0.0F, 0.0F, 0.0F, 4.0F, 4.0F, 4.0F), PartPose.offset(-2.0F, 1.0F, -2.0F));
		partDefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(24, 30).addBox(0.0F, 0.0F, 0.0F, 8.0F, 5.0F, 6.0F), PartPose.offset(-4.0F, -4.0F, -3.0F));
		partDefinition.addOrReplaceChild("hair", CubeListBuilder.create().texOffs(0, 40).addBox(0.0F, 0.0F, 0.0F, 6.0F, 1.0F, 6.0F), PartPose.offset(-3.0F, -5.0F, -3.0F));
		partDefinition.addOrReplaceChild("right_eye", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 0.0F, 0.0F, 2.0F, 2.0F, 1.0F), PartPose.offset(-2.7F, -3.0F, -3.3F));
		partDefinition.addOrReplaceChild("left_eye", CubeListBuilder.create().texOffs(6, 0).addBox(0.0F, 0.0F, 0.0F, 2.0F, 2.0F, 1.0F), PartPose.offset(0.7F, -3.0F, -3.3F));
		partDefinition.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(0, 3).addBox(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 4.0F), PartPose.offset(-0.5F, -1.0F, -6.9F));
		return LayerDefinition.create(meshDefinition, 64, 64);
	}

	public void renderBase(PoseStack pose, VertexConsumer builder, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		base.render(pose, builder, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	public void renderToBuffer(PoseStack pose, VertexConsumer builder, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		headPartList.forEach(part -> part.render(pose, builder, packedLight, packedOverlay));
	}

	@Override
	public void setupAnim(Sentry entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}
}

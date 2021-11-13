package net.geforcemods.securitycraft.models;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.geforcemods.securitycraft.entity.SecurityCamera;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SecurityCameraModel extends EntityModel<SecurityCamera> {
	public ModelPart shape1;
	public ModelPart shape2;
	public ModelPart cameraRotationPoint;
	private final ImmutableList<ModelPart> partList;

	public SecurityCameraModel(ModelPart modelPart)
	{
		shape1 = modelPart.getChild("shape1");
		shape2 = modelPart.getChild("shape2");
		setRotateAngle(shape2, -0.5235987755982988F, 0.0F, 0.0F);
		cameraRotationPoint = modelPart.getChild("camera_rotation_point");
		setRotateAngle(cameraRotationPoint, 0.2617993877991494F, 0.0F, 0.0F);
		setRotateAngle(cameraRotationPoint.getChild("camera_body"), 0.2617993877991494F, 0.0F, 0.0F);
		partList = ImmutableList.of(cameraRotationPoint, shape1, shape2);
	}

	public static LayerDefinition createLayer()
	{
		MeshDefinition meshDefinition = new MeshDefinition();
		PartDefinition rootDefinition = meshDefinition.getRoot();
		PartDefinition shape2Definition = rootDefinition.addOrReplaceChild("shape2", CubeListBuilder.create().texOffs(2, 12).addBox(0.0F, 0.0F, 0.0F, 2.0F, 1.0F, 6.0F), PartPose.offset(-1.0F, 13.75F, 2.25F));
		PartDefinition cameraRotationPointDefinition = rootDefinition.addOrReplaceChild("camera_rotation_point", CubeListBuilder.create().texOffs(0, 25).addBox(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F), PartPose.offset(0.0F, 14.0F, 3.0F));
		PartDefinition cameraBodyDefinition = cameraRotationPointDefinition.addOrReplaceChild("camera_body", CubeListBuilder.create().texOffs(0, 25).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 3.0F, 8.0F), PartPose.offset(0.0F, 0.0F, -5.0F));

		rootDefinition.addOrReplaceChild("shape1", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 0.0F, 0.0F, 6.0F, 6.0F, 1.0F), PartPose.offset(-3.0F, 13.0F, 7.0F));
		shape2Definition.addOrReplaceChild("shape3", CubeListBuilder.create().texOffs(1, 12).addBox(0.0F, 0.0F, 0.0F, 2.0F, 1.0F, 7.0F), PartPose.offset(0.0F, 1.0F, 0.0F));
		cameraBodyDefinition.addOrReplaceChild("camera_lens_right", CubeListBuilder.create().texOffs(10, 40).addBox(-2.0F, 0.0F, 0.0F, 1.0F, 3.0F, 1.0F), PartPose.offset(3.0F, 0.0F, -3.0F));
		cameraBodyDefinition.addOrReplaceChild("camera_lens_left", CubeListBuilder.create().texOffs(0, 40).addBox(0.0F, 0.0F, 0.0F, 1.0F, 3.0F, 1.0F), PartPose.offset(-2.0F, 0.0F, -3.0F));
		cameraBodyDefinition.addOrReplaceChild("camera_lens_top", CubeListBuilder.create().texOffs(20, 40).addBox(0.0F, 0.0F, 0.0F, 2.0F, 1.0F, 1.0F), PartPose.offset(-1.0F, 0.0F, -3.0F));
		return LayerDefinition.create(meshDefinition, 64, 64);
	}

	@Override
	public void renderToBuffer(PoseStack matrix, VertexConsumer builder, int packedLight, int packedOverlay, float red, float green, float blue, float alpha)
	{
		partList.forEach(part -> part.render(matrix, builder, packedLight, packedOverlay));
	}

	public void setRotateAngle(ModelPart modelRenderer, float x, float y, float z) {
		modelRenderer.xRot = x;
		modelRenderer.yRot = y;
		modelRenderer.zRot = z;
	}

	@Override
	public void setupAnim(SecurityCamera entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}
}

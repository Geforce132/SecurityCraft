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
import net.minecraft.client.renderer.entity.state.EntityRenderState;

public class SecurityCameraModel extends EntityModel<EntityRenderState> {
	public static final float DEFAULT_X_ROT = 0.2617993877991494F;
	private ModelPart attachment;
	private ModelPart stickTop;
	private ModelPart cameraRotationPoint1;
	public final ModelPart cameraRotationPoint2;

	public SecurityCameraModel(ModelPart modelPart) {
		super(modelPart);
		attachment = modelPart.getChild("attachment");
		stickTop = modelPart.getChild("stick_top");
		stickTop.xRot = -(DEFAULT_X_ROT * 2);
		cameraRotationPoint1 = setUpRotationPoint(modelPart, "camera_rotation_point_1", "camera_body");
		cameraRotationPoint2 = setUpRotationPoint(modelPart, "camera_rotation_point_2", "camera_lens");
	}

	public static LayerDefinition createLayer() {
		MeshDefinition meshDefinition = new MeshDefinition();
		PartDefinition rootDefinition = meshDefinition.getRoot();
		PartDefinition stickTopDefinition = rootDefinition.addOrReplaceChild("stick_top", CubeListBuilder.create().texOffs(2, 12).addBox(0.0F, 0.0F, 0.0F, 2.0F, 1.0F, 6.0F), PartPose.offset(-1.0F, 13.75F, 2.25F));
		PartDefinition cameraRotationPoint1Definition = defineRotationPoint(rootDefinition, "camera_rotation_point_1");
		PartDefinition cameraRotationPoint2Definition = defineRotationPoint(rootDefinition, "camera_rotation_point_2");
		PartDefinition cameraBodyDefinition = cameraRotationPoint1Definition.addOrReplaceChild("camera_body", CubeListBuilder.create().texOffs(0, 25).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 3.0F, 8.0F), PartPose.offset(0.0F, 0.0F, -5.0F));

		rootDefinition.addOrReplaceChild("attachment", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 0.0F, 0.0F, 6.0F, 6.0F, 1.0F), PartPose.offset(-3.0F, 13.0F, 7.0F));
		stickTopDefinition.addOrReplaceChild("stick_bottom", CubeListBuilder.create().texOffs(1, 12).addBox(0.0F, 0.0F, 0.0F, 2.0F, 1.0F, 7.0F), PartPose.offset(0.0F, 1.0F, 0.0F));
		cameraBodyDefinition.addOrReplaceChild("camera_lens_right", CubeListBuilder.create().texOffs(10, 40).addBox(-2.0F, 0.0F, 0.0F, 1.0F, 3.0F, 1.0F), PartPose.offset(3.0F, 0.0F, -3.0F));
		cameraBodyDefinition.addOrReplaceChild("camera_lens_left", CubeListBuilder.create().texOffs(0, 40).addBox(0.0F, 0.0F, 0.0F, 1.0F, 3.0F, 1.0F), PartPose.offset(-2.0F, 0.0F, -3.0F));
		cameraBodyDefinition.addOrReplaceChild("camera_lens_top", CubeListBuilder.create().texOffs(20, 40).addBox(0.0F, 0.0F, 0.0F, 2.0F, 1.0F, 1.0F), PartPose.offset(-1.0F, 0.0F, -3.0F));
		cameraRotationPoint2Definition.addOrReplaceChild("camera_lens", CubeListBuilder.create().texOffs(0, 50).addBox(-1.99F, 0.01F, -2.01F, 3.98F, 2.98F, 0.01F), PartPose.offset(0.0F, 0.0F, -5.0F));
		return LayerDefinition.create(meshDefinition, 64, 64);
	}

	private static PartDefinition defineRotationPoint(PartDefinition parent, String name) {
		return parent.addOrReplaceChild(name, CubeListBuilder.create().texOffs(0, 25).addBox(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F), PartPose.offset(0.0F, 14.0F, 3.0F));
	}

	@Override
	public void renderToBuffer(PoseStack pose, VertexConsumer builder, int packedLight, int packedOverlay, int packedARGB) {
		attachment.render(pose, builder, packedLight, packedOverlay);
		stickTop.render(pose, builder, packedLight, packedOverlay);
		cameraRotationPoint1.render(pose, builder, packedLight, packedOverlay);
		cameraRotationPoint2.render(pose, builder, packedLight, packedOverlay, packedARGB);
	}

	public void rotateCameraX(float rot) {
		cameraRotationPoint1.xRot = rot;
		cameraRotationPoint2.xRot = rot;
	}

	public void rotateCameraY(float rot) {
		cameraRotationPoint1.yRot = rot;
		cameraRotationPoint2.yRot = rot;
	}

	private ModelPart setUpRotationPoint(ModelPart modelPart, String name, String child) {
		ModelPart rotationPoint = modelPart.getChild(name);

		rotationPoint.xRot = DEFAULT_X_ROT;
		rotationPoint.getChild(child).xRot = DEFAULT_X_ROT;
		return rotationPoint;
	}
}

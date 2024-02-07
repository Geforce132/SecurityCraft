package net.geforcemods.securitycraft.models;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.geforcemods.securitycraft.entity.camera.SecurityCamera;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * SecurityCamera - Geforce Created using Tabula 4.1.1
 */
@OnlyIn(Dist.CLIENT)
public class SecurityCameraModel extends EntityModel<SecurityCamera> {
	public static final float DEFAULT_X_ROT = 0.2617993877991494F;
	public final ModelRenderer attachment;
	public final ModelRenderer stickTop;
	public final ModelRenderer cameraRotationPoint1;
	public final ModelRenderer cameraRotationPoint2;
	public final ModelRenderer stickBottom;
	public final ModelRenderer cameraBody;
	public final ModelRenderer cameraLens;
	public final ModelRenderer cameraLensRight;
	public final ModelRenderer cameraLensLeft;
	public final ModelRenderer cameraLensTop;

	public SecurityCameraModel() {
		texWidth = 64;
		texHeight = 64;
		cameraRotationPoint1 = new ModelRenderer(this, 0, 25);
		cameraRotationPoint1.setPos(0.0F, 14.0F, 3.0F);
		cameraRotationPoint1.addBox(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
		cameraRotationPoint1.xRot = DEFAULT_X_ROT;
		cameraRotationPoint2 = new ModelRenderer(this, 0, 25);
		cameraRotationPoint2.setPos(0.0F, 14.0F, 3.0F);
		cameraRotationPoint2.addBox(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
		cameraRotationPoint2.xRot = DEFAULT_X_ROT;
		cameraLensRight = new ModelRenderer(this, 10, 40);
		cameraLensRight.setPos(3.0F, 0.0F, -3.0F);
		cameraLensRight.addBox(-2.0F, 0.0F, 0.0F, 1, 3, 1);
		stickBottom = new ModelRenderer(this, 1, 12);
		stickBottom.setPos(0.0F, 1.0F, 0.0F);
		stickBottom.addBox(0.0F, 0.0F, 0.0F, 2, 1, 7);
		cameraLensLeft = new ModelRenderer(this, 0, 40);
		cameraLensLeft.setPos(-2.0F, 0.0F, -3.0F);
		cameraLensLeft.addBox(0.0F, 0.0F, 0.0F, 1, 3, 1);
		cameraBody = new ModelRenderer(this, 0, 25);
		cameraBody.setPos(0.0F, 0.0F, -5.0F);
		cameraBody.addBox(-2.0F, 0.0F, -2.0F, 4, 3, 8);
		cameraBody.xRot = DEFAULT_X_ROT;
		cameraLens = new ModelRenderer(this, 0, 50);
		cameraLens.setPos(0.0F, 0.0F, -5.0F);
		cameraLens.addBox(-1.99F, 0.01F, -2.01F, 3.98F, 2.98F, 0.01F);
		cameraLens.xRot = DEFAULT_X_ROT;
		attachment = new ModelRenderer(this, 0, 0);
		attachment.setPos(-3.0F, 13.0F, 7.0F);
		attachment.addBox(0.0F, 0.0F, 0.0F, 6, 6, 1);
		cameraLensTop = new ModelRenderer(this, 20, 40);
		cameraLensTop.setPos(-1.0F, 0.0F, -3.0F);
		cameraLensTop.addBox(0.0F, 0.0F, 0.0F, 2, 1, 1);
		stickTop = new ModelRenderer(this, 2, 12);
		stickTop.setPos(-1.0F, 13.75F, 2.25F);
		stickTop.addBox(0.0F, 0.0F, 0.0F, 2, 1, 6);
		stickTop.xRot = -(DEFAULT_X_ROT * 2);
		cameraBody.addChild(cameraLensRight);
		stickTop.addChild(stickBottom);
		cameraBody.addChild(cameraLensLeft);
		cameraRotationPoint1.addChild(cameraBody);
		cameraRotationPoint2.addChild(cameraLens);
		cameraBody.addChild(cameraLensTop);
	}

	@Override
	public void renderToBuffer(MatrixStack pose, IVertexBuilder builder, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		cameraRotationPoint1.render(pose, builder, packedLight, packedOverlay);
		cameraRotationPoint2.render(pose, builder, packedLight, packedOverlay, red, green, blue, alpha);
		attachment.render(pose, builder, packedLight, packedOverlay);
		stickTop.render(pose, builder, packedLight, packedOverlay);
	}

	@Override
	public void setupAnim(SecurityCamera entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}

	public void rotateCameraX(float rot) {
		cameraRotationPoint1.xRot = rot;
		cameraRotationPoint2.xRot = rot;
	}

	public void rotateCameraY(float rot) {
		cameraRotationPoint1.yRot = rot;
		cameraRotationPoint2.yRot = rot;
	}
}

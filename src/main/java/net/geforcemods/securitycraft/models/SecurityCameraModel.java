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
	public final ModelRenderer shape1;
	public final ModelRenderer shape2;
	public final ModelRenderer cameraRotationPoint;
	public final ModelRenderer shape3;
	public final ModelRenderer cameraBody;
	public final ModelRenderer cameraLensRight;
	public final ModelRenderer cameraLensLeft;
	public final ModelRenderer cameraLensTop;

	public SecurityCameraModel() {
		texWidth = 64;
		texHeight = 64;
		cameraRotationPoint = new ModelRenderer(this, 0, 25);
		cameraRotationPoint.setPos(0.0F, 14.0F, 3.0F);
		cameraRotationPoint.addBox(0.0F, 0.0F, 0.0F, 1, 1, 1);
		setRotateAngle(cameraRotationPoint, 0.2617993877991494F, 0.0F, 0.0F);
		cameraLensRight = new ModelRenderer(this, 10, 40);
		cameraLensRight.setPos(3.0F, 0.0F, -3.0F);
		cameraLensRight.addBox(-2.0F, 0.0F, 0.0F, 1, 3, 1);
		shape3 = new ModelRenderer(this, 1, 12);
		shape3.setPos(0.0F, 1.0F, 0.0F);
		shape3.addBox(0.0F, 0.0F, 0.0F, 2, 1, 7);
		cameraLensLeft = new ModelRenderer(this, 0, 40);
		cameraLensLeft.setPos(-2.0F, 0.0F, -3.0F);
		cameraLensLeft.addBox(0.0F, 0.0F, 0.0F, 1, 3, 1);
		cameraBody = new ModelRenderer(this, 0, 25);
		cameraBody.setPos(0.0F, 0.0F, -5.0F);
		cameraBody.addBox(-2.0F, 0.0F, -2.0F, 4, 3, 8);
		setRotateAngle(cameraBody, 0.2617993877991494F, 0.0F, 0.0F);
		shape1 = new ModelRenderer(this, 0, 0);
		shape1.setPos(-3.0F, 13.0F, 7.0F);
		shape1.addBox(0.0F, 0.0F, 0.0F, 6, 6, 1);
		cameraLensTop = new ModelRenderer(this, 20, 40);
		cameraLensTop.setPos(-1.0F, 0.0F, -3.0F);
		cameraLensTop.addBox(0.0F, 0.0F, 0.0F, 2, 1, 1);
		shape2 = new ModelRenderer(this, 2, 12);
		shape2.setPos(-1.0F, 13.75F, 2.25F);
		shape2.addBox(0.0F, 0.0F, 0.0F, 2, 1, 6);
		setRotateAngle(shape2, -0.5235987755982988F, 0.0F, 0.0F);
		cameraBody.addChild(cameraLensRight);
		shape2.addChild(shape3);
		cameraBody.addChild(cameraLensLeft);
		cameraRotationPoint.addChild(cameraBody);
		cameraBody.addChild(cameraLensTop);
	}

	@Override
	public void renderToBuffer(MatrixStack pose, IVertexBuilder builder, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		cameraRotationPoint.render(pose, builder, packedLight, packedOverlay, red, green, blue, alpha);
		shape1.render(pose, builder, packedLight, packedOverlay, red, green, blue, alpha);
		shape2.render(pose, builder, packedLight, packedOverlay, red, green, blue, alpha);
	}

	public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.xRot = x;
		modelRenderer.yRot = y;
		modelRenderer.zRot = z;
	}

	@Override
	public void setupAnim(SecurityCamera entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}
}

package net.geforcemods.securitycraft.models;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.geforcemods.securitycraft.entity.SecurityCameraEntity;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * SecurityCamera - Geforce
 * Created using Tabula 4.1.1
 */
@OnlyIn(Dist.CLIENT)
public class SecurityCameraModel extends EntityModel<SecurityCameraEntity> {
	public ModelRenderer shape1;
	public ModelRenderer shape2;
	public ModelRenderer cameraRotationPoint;
	public ModelRenderer shape3;
	public ModelRenderer cameraBody;
	public ModelRenderer cameraLensRight;
	public ModelRenderer cameraLensLeft;
	public ModelRenderer cameraLensTop;

	public SecurityCameraModel() {
		textureWidth = 128;
		textureHeight = 64;
		cameraRotationPoint = new ModelRenderer(this, 0, 25);
		cameraRotationPoint.setRotationPoint(0.0F, 14.0F, 3.0F);
		cameraRotationPoint.addBox(0.0F, 0.0F, 0.0F, 1, 1, 1);
		setRotateAngle(cameraRotationPoint, 0.2617993877991494F, 0.0F, 0.0F);
		cameraLensRight = new ModelRenderer(this, 10, 40);
		cameraLensRight.setRotationPoint(3.0F, 0.0F, -3.0F);
		cameraLensRight.addBox(-2.0F, 0.0F, 0.0F, 1, 3, 1);
		shape3 = new ModelRenderer(this, 1, 12);
		shape3.setRotationPoint(0.0F, 1.0F, 0.0F);
		shape3.addBox(0.0F, 0.0F, 0.0F, 2, 1, 7);
		cameraLensLeft = new ModelRenderer(this, 0, 40);
		cameraLensLeft.setRotationPoint(-2.0F, 0.0F, -3.0F);
		cameraLensLeft.addBox(0.0F, 0.0F, 0.0F, 1, 3, 1);
		cameraBody = new ModelRenderer(this, 0, 25);
		cameraBody.setRotationPoint(0.0F, 0.0F, -5.0F);
		cameraBody.addBox(-2.0F, 0.0F, -2.0F, 4, 3, 8);
		setRotateAngle(cameraBody, 0.2617993877991494F, 0.0F, 0.0F);
		shape1 = new ModelRenderer(this, 0, 0);
		shape1.setRotationPoint(-3.0F, 13.0F, 7.0F);
		shape1.addBox(0.0F, 0.0F, 0.0F, 6, 6, 1);
		cameraLensTop = new ModelRenderer(this, 20, 40);
		cameraLensTop.setRotationPoint(-1.0F, 0.0F, -3.0F);
		cameraLensTop.addBox(0.0F, 0.0F, 0.0F, 2, 1, 1);
		shape2 = new ModelRenderer(this, 2, 12);
		shape2.setRotationPoint(-1.0F, 13.75F, 2.25F);
		shape2.addBox(0.0F, 0.0F, 0.0F, 2, 1, 6);
		setRotateAngle(shape2, -0.5235987755982988F, 0.0F, 0.0F);
		cameraBody.addChild(cameraLensRight);
		shape2.addChild(shape3);
		cameraBody.addChild(cameraLensLeft);
		cameraRotationPoint.addChild(cameraBody);
		cameraBody.addChild(cameraLensTop);
	}

	@Override
	public void render(MatrixStack matrix, IVertexBuilder builder, int packedLight, int packedOverlay, float red, float green, float blue, float alpha)
	{
		cameraRotationPoint.render(matrix, builder, packedLight, packedOverlay, red, green, blue, alpha);
		shape1.render(matrix, builder, packedLight, packedOverlay, red, green, blue, alpha);
		shape2.render(matrix, builder, packedLight, packedOverlay, red, green, blue, alpha);
	}

	/**
	 * This is a helper function from Tabula to set the rotation of model parts
	 */
	public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}

	@Override
	public void setRotationAngles(SecurityCameraEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}
}

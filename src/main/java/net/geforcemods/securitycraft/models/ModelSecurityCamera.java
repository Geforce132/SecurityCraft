package net.geforcemods.securitycraft.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * SecurityCamera - Geforce
 * Created using Tabula 4.1.1
 */
public class ModelSecurityCamera extends ModelBase {
	public ModelRenderer shape1;
	public ModelRenderer shape2;
	public ModelRenderer cameraRotationPoint;
	public ModelRenderer shape3;
	public ModelRenderer cameraBody;
	public ModelRenderer cameraLensRight;
	public ModelRenderer cameraLensLeft;
	public ModelRenderer cameraLensTop;

	public boolean reverseCameraRotation = false;

	public ModelSecurityCamera() {
		textureWidth = 128;
		textureHeight = 64;
		cameraRotationPoint = new ModelRenderer(this, 0, 25);
		cameraRotationPoint.setRotationPoint(0.0F, 14.0F, 3.0F);
		cameraRotationPoint.addBox(0.0F, 0.0F, 0.0F, 1, 1, 1, 0.0F);
		setRotateAngle(cameraRotationPoint, 0.2617993877991494F, 0.0F, 0.0F);
		cameraLensRight = new ModelRenderer(this, 10, 40);
		cameraLensRight.setRotationPoint(3.0F, 0.0F, -3.0F);
		cameraLensRight.addBox(-2.0F, 0.0F, 0.0F, 1, 3, 1, 0.0F);
		shape3 = new ModelRenderer(this, 1, 12);
		shape3.setRotationPoint(0.0F, 1.0F, 0.0F);
		shape3.addBox(0.0F, 0.0F, 0.0F, 2, 1, 7, 0.0F);
		cameraLensLeft = new ModelRenderer(this, 0, 40);
		cameraLensLeft.setRotationPoint(-2.0F, 0.0F, -3.0F);
		cameraLensLeft.addBox(0.0F, 0.0F, 0.0F, 1, 3, 1, 0.0F);
		cameraBody = new ModelRenderer(this, 0, 25);
		cameraBody.setRotationPoint(0.0F, 0.0F, -5.0F);
		cameraBody.addBox(-2.0F, 0.0F, -2.0F, 4, 3, 8, 0.0F);
		setRotateAngle(cameraBody, 0.2617993877991494F, 0.0F, 0.0F);
		shape1 = new ModelRenderer(this, 0, 0);
		shape1.setRotationPoint(-3.0F, 13.0F, 7.0F);
		shape1.addBox(0.0F, 0.0F, 0.0F, 6, 6, 1, 0.0F);
		cameraLensTop = new ModelRenderer(this, 20, 40);
		cameraLensTop.setRotationPoint(-1.0F, 0.0F, -3.0F);
		cameraLensTop.addBox(0.0F, 0.0F, 0.0F, 2, 1, 1, 0.0F);
		shape2 = new ModelRenderer(this, 2, 12);
		shape2.setRotationPoint(-1.0F, 13.75F, 2.25F);
		shape2.addBox(0.0F, 0.0F, 0.0F, 2, 1, 6, 0.0F);
		setRotateAngle(shape2, -0.5235987755982988F, 0.0F, 0.0F);
		cameraBody.addChild(cameraLensRight);
		shape2.addChild(shape3);
		cameraBody.addChild(cameraLensLeft);
		cameraRotationPoint.addChild(cameraBody);
		cameraBody.addChild(cameraLensTop);
	}

	@Override
	public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		cameraRotationPoint.render(scale);
		shape1.render(scale);
		shape2.render(scale);
	}

	/**
	 * This is a helper function from Tabula to set the rotation of model parts
	 */
	public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}

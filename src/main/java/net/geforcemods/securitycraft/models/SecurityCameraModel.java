package net.geforcemods.securitycraft.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;

/**
 * SecurityCamera - Geforce Created using Tabula 4.1.1
 */
public class SecurityCameraModel extends ModelBase {
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
	public float r, g, b;

	public SecurityCameraModel() {
		textureWidth = 64;
		textureHeight = 64;
		cameraRotationPoint1 = new ModelRenderer(this, 0, 25);
		cameraRotationPoint1.setRotationPoint(0.0F, 14.0F, 3.0F);
		cameraRotationPoint1.addBox(0.0F, 0.0F, 0.0F, 0, 0, 0, 0.0F);
		cameraRotationPoint1.rotateAngleX = DEFAULT_X_ROT;
		cameraRotationPoint2 = new ModelRenderer(this, 0, 25);
		cameraRotationPoint2.setRotationPoint(0.0F, 14.0F, 3.0F);
		cameraRotationPoint2.addBox(0.0F, 0.0F, 0.0F, 0, 0, 0, 0.0F);
		cameraRotationPoint2.rotateAngleX = DEFAULT_X_ROT;
		cameraLensRight = new ModelRenderer(this, 10, 40);
		cameraLensRight.setRotationPoint(3.0F, 0.0F, -3.0F);
		cameraLensRight.addBox(-2.0F, 0.0F, 0.0F, 1, 3, 1, 0.0F);
		stickBottom = new ModelRenderer(this, 1, 12);
		stickBottom.setRotationPoint(0.0F, 1.0F, 0.0F);
		stickBottom.addBox(0.0F, 0.0F, 0.0F, 2, 1, 7, 0.0F);
		cameraLensLeft = new ModelRenderer(this, 0, 40);
		cameraLensLeft.setRotationPoint(-2.0F, 0.0F, -3.0F);
		cameraLensLeft.addBox(0.0F, 0.0F, 0.0F, 1, 3, 1, 0.0F);
		cameraBody = new ModelRenderer(this, 0, 25);
		cameraBody.setRotationPoint(0.0F, 0.0F, -5.0F);
		cameraBody.addBox(-2.0F, 0.0F, -2.0F, 4, 3, 8, 0.0F);
		cameraBody.rotateAngleX = DEFAULT_X_ROT;
		cameraLens = new ModelRenderer(this, 0, 50);
		cameraLens.setRotationPoint(0.0F, 0.97F, -4.75F);
		cameraLens.addBox(-2.0F, 0.0F, -2.0F, 4, 2, 0, 0.0F);
		cameraLens.rotateAngleX = DEFAULT_X_ROT;
		attachment = new ModelRenderer(this, 0, 0);
		attachment.setRotationPoint(-3.0F, 13.0F, 7.0F);
		attachment.addBox(0.0F, 0.0F, 0.0F, 6, 6, 1, 0.0F);
		cameraLensTop = new ModelRenderer(this, 20, 40);
		cameraLensTop.setRotationPoint(-1.0F, 0.0F, -3.0F);
		cameraLensTop.addBox(0.0F, 0.0F, 0.0F, 2, 1, 1, 0.0F);
		stickTop = new ModelRenderer(this, 2, 12);
		stickTop.setRotationPoint(-1.0F, 13.75F, 2.25F);
		stickTop.addBox(0.0F, 0.0F, 0.0F, 2, 1, 6, 0.0F);
		stickTop.rotateAngleX = -(DEFAULT_X_ROT * 2);
		cameraBody.addChild(cameraLensRight);
		stickTop.addChild(stickBottom);
		cameraBody.addChild(cameraLensLeft);
		cameraRotationPoint1.addChild(cameraBody);
		cameraRotationPoint2.addChild(cameraLens);
		cameraBody.addChild(cameraLensTop);
	}

	@Override
	public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		cameraRotationPoint1.render(scale);
		GlStateManager.color(r, g, b);
		cameraRotationPoint2.render(scale);
		GlStateManager.color(1.0F, 1.0F, 1.0F);
		attachment.render(scale);
		stickTop.render(scale);
	}

	public void rotateCameraX(float rot) {
		cameraRotationPoint1.rotateAngleX = rot;
		cameraRotationPoint2.rotateAngleX = rot;
	}

	public void rotateCameraY(float rot) {
		cameraRotationPoint1.rotateAngleY = rot;
		cameraRotationPoint2.rotateAngleY = rot;
	}
}

package net.geforcemods.securitycraft.models;

import net.minecraft.client.renderer.entity.model.ModelBase;
import net.minecraft.client.renderer.entity.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * Taser - Geforce
 * Created using Tabula 4.1.1
 */
public class ModelTaser extends ModelBase {
	public ModelRenderer shape1;
	public ModelRenderer shape2;
	public ModelRenderer shape3;
	public ModelRenderer shape4;
	public ModelRenderer shape5;
	public ModelRenderer shape6;
	public ModelRenderer shape7;

	public ModelTaser() {
		textureWidth = 96;
		textureHeight = 64;
		shape6 = new ModelRenderer(this, 0, 38);
		shape6.setRotationPoint(0.0F, 0.0F, 0.0F);
		shape6.addBox(2.0F, 1.0F, 7.0F, 1, 2, 1, 0.0F);
		shape5 = new ModelRenderer(this, 0, 30);
		shape5.setRotationPoint(0.0F, 0.0F, 0.0F);
		shape5.addBox(0.0F, 1.0F, 7.0F, 1, 2, 1, 0.0F);
		shape2 = new ModelRenderer(this, 0, 10);
		shape2.setRotationPoint(0.55F, 2.5F, 1.0F);
		shape2.addBox(0.0F, 0.0F, 0.0F, 2, 5, 2, 0.0F);
		setRotateAngle(shape2, -0.3490658503988659F, 0.0F, 0.0F);
		shape7 = new ModelRenderer(this, 0, 45);
		shape7.setRotationPoint(0.0F, 0.0F, 0.0F);
		shape7.addBox(0.5F, 0.5F, -0.4F, 2, 3, 1, 0.0F);
		shape1 = new ModelRenderer(this, 0, 0);
		shape1.setRotationPoint(-1.5F, 15.0F, -4.0F);
		shape1.addBox(0.0F, 0.0F, 0.0F, 3, 4, 7, 0.0F);
		shape3 = new ModelRenderer(this, 0, 20);
		shape3.setRotationPoint(0.0F, 0.0F, 0.0F);
		shape3.addBox(0.0F, 0.0F, 7.0F, 3, 1, 1, 0.0F);
		shape4 = new ModelRenderer(this, 0, 25);
		shape4.setRotationPoint(0.0F, 0.0F, 0.0F);
		shape4.addBox(0.0F, 3.0F, 7.0F, 3, 1, 1, 0.0F);
		shape1.addChild(shape6);
		shape1.addChild(shape5);
		shape1.addChild(shape2);
		shape1.addChild(shape7);
		shape1.addChild(shape3);
		shape1.addChild(shape4);
	}

	@Override
	public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		shape1.render(scale);
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

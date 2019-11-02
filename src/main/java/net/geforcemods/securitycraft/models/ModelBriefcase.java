package net.geforcemods.securitycraft.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * Briefcase - Geforce
 * Created using Tabula 4.1.1
 */
public class ModelBriefcase extends ModelBase {
	public ModelRenderer shape1;
	public ModelRenderer shape2;
	public ModelRenderer shape3;
	public ModelRenderer shape3_1;

	public ModelBriefcase() {
		textureWidth = 48;
		textureHeight = 32;
		shape3_1 = new ModelRenderer(this, 0, 25);
		shape3_1.setRotationPoint(0.0F, 0.0F, 0.0F);
		shape3_1.addBox(2.0F, -3.0F, 1.0F, 8, 1, 2, 0.0F);
		shape3 = new ModelRenderer(this, 0, 15);
		shape3.setRotationPoint(0.0F, 0.0F, 0.0F);
		shape3.addBox(10.0F, -3.0F, 1.0F, 1, 3, 2, 0.0F);
		shape1 = new ModelRenderer(this, 0, 0);
		shape1.setRotationPoint(-6.0F, 14.0F, -2.0F);
		shape1.addBox(0.0F, 0.0F, 0.0F, 12, 10, 4, 0.0F);
		shape2 = new ModelRenderer(this, 10, 15);
		shape2.setRotationPoint(0.0F, 0.0F, 0.0F);
		shape2.addBox(1.0F, -3.0F, 1.0F, 1, 3, 2, 0.0F);
		shape1.addChild(shape3_1);
		shape1.addChild(shape3);
		shape1.addChild(shape2);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		shape1.render(f5);
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

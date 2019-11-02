package net.geforcemods.securitycraft.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * Key Panel - Geforce
 * Created using Tabula 4.1.1
 */
public class ModelFrame extends ModelBase {
	public ModelRenderer shape1;
	public ModelRenderer shape2;
	public ModelRenderer shape3;
	public ModelRenderer shape4;
	public ModelRenderer shape5;

	public ModelFrame() {
		textureWidth = 64;
		textureHeight = 64;
		shape1 = new ModelRenderer(this, 0, 0);
		shape1.setRotationPoint(-8.0F, 8.0F, -8.0F);
		shape1.addBox(0.0F, 0.0F, 1.0F, 16, 16, 15, 0.0F);
		shape4 = new ModelRenderer(this, 0, 40);
		shape4.setRotationPoint(-8.0F, 10.0F, -8.0F);
		shape4.addBox(0.0F, 0.0F, 0.0F, 2, 12, 1, 0.0F);
		shape3 = new ModelRenderer(this, 10, 40);
		shape3.setRotationPoint(0.0F, 14.0F, 0.0F);
		shape3.addBox(-8.0F, 8.0F, -8.0F, 16, 2, 1, 0.0F);
		shape5 = new ModelRenderer(this, 10, 45);
		shape5.setRotationPoint(6.0F, 10.0F, -8.0F);
		shape5.addBox(0.0F, 0.0F, 0.0F, 2, 12, 1, 0.0F);
		shape2 = new ModelRenderer(this, 0, 35);
		shape2.setRotationPoint(-8.0F, 8.0F, -8.0F);
		shape2.addBox(0.0F, 0.0F, 0.0F, 16, 2, 1, 0.0F);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		shape1.render(f5);
		shape4.render(f5);
		shape3.render(f5);
		shape5.render(f5);
		shape2.render(f5);
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

package net.geforcemods.securitycraft.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * Key Panel - Geforce
 * Created using Tabula 4.1.1
 */
public class ModelCompactCamera extends ModelBase {
	public ModelRenderer shape1;
	public ModelRenderer shape2;
	public ModelRenderer shape3;
	public ModelRenderer shape4;
	public ModelRenderer shape5;

	public ModelCompactCamera() {
		textureWidth = 64;
		textureHeight = 64;
		shape4 = new ModelRenderer(this, 0, 20);
		shape4.setRotationPoint(-8.0F, 10.0F, -8.0F);
		shape4.addBox(0.0F, 0.0F, 0.0F, 2, 12, 1, 0.0F);
		shape2 = new ModelRenderer(this, 25, 0);
		shape2.setRotationPoint(-8.0F, 8.0F, -8.0F);
		shape2.addBox(0.0F, 0.0F, 0.0F, 16, 2, 1, 0.0F);
		shape5 = new ModelRenderer(this, 10, 15);
		shape5.setRotationPoint(6.0F, 10.0F, -8.0F);
		shape5.addBox(0.0F, 0.0F, 0.0F, 2, 12, 1, 0.0F);
		shape3 = new ModelRenderer(this, 8, 5);
		shape3.setRotationPoint(0.0F, 14.0F, 0.0F);
		shape3.addBox(-8.0F, 8.0F, -8.0F, 16, 2, 1, 0.0F);
		shape1 = new ModelRenderer(this, 0, 33);
		shape1.setRotationPoint(-8.0F, 8.0F, -8.0F);
		shape1.addBox(0.0F, 0.0F, 1.0F, 16, 16, 15, 0.0F);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		shape4.render(f5);
		shape2.render(f5);
		shape5.render(f5);
		shape3.render(f5);
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

	@Override
	public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity entity){
		super.setRotationAngles(par1, par2, par3, par4, par5, par6, entity);
	}
}

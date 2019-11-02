package net.geforcemods.securitycraft.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * Claymore - Geforce
 * Created using Tabula 4.1.1
 */
public class ModelClaymore extends ModelBase {

	private boolean isActive = true;

	public ModelRenderer shape1;
	public ModelRenderer shape2;
	public ModelRenderer shape3;
	public ModelRenderer shape4;
	public ModelRenderer shape6;
	public ModelRenderer shape5;
	public ModelRenderer shape7;
	public ModelRenderer shape8;
	public ModelRenderer shape9;

	public ModelClaymore() {
		textureWidth = 16;
		textureHeight = 16;
		shape7 = new ModelRenderer(this, 5, 5);
		shape7.setRotationPoint(-4.0F, 19.0F, 4.0F);
		shape7.addBox(0.0F, 0.0F, 0.0F, 1, 1, 1, 0.0F);
		shape8 = new ModelRenderer(this, 0, 0);
		shape8.setRotationPoint(-5.0F, 19.0F, 5.0F);
		shape8.addBox(0.0F, 0.0F, 0.0F, 1, 1, 1, 0.0F);
		shape3 = new ModelRenderer(this, 5, 5);
		shape3.setRotationPoint(2.0F, 19.0F, 3.0F);
		shape3.addBox(0.0F, 0.0F, 0.0F, 1, 1, 1, 0.0F);
		shape4 = new ModelRenderer(this, 5, 5);
		shape4.setRotationPoint(3.0F, 19.0F, 4.0F);
		shape4.addBox(0.0F, 0.0F, 0.0F, 1, 1, 1, 0.0F);
		shape5 = new ModelRenderer(this, 0, 0);
		shape5.setRotationPoint(4.0F, 19.0F, 5.0F);
		shape5.addBox(0.0F, 0.0F, 0.0F, 1, 1, 1, 0.0F);
		shape6 = new ModelRenderer(this, 5, 5);
		shape6.setRotationPoint(-3.0F, 19.0F, 3.0F);
		shape6.addBox(0.0F, 0.0F, 0.0F, 1, 1, 1, 0.0F);
		shape9 = new ModelRenderer(this, 0, 11);
		shape9.setRotationPoint(3.0F, 20.0F, 0.0F);
		shape9.addBox(-7.0F, 0.0F, 1.0F, 8, 4, 1, 0.0F);
		shape2 = new ModelRenderer(this, 6, 10);
		shape2.setRotationPoint(-4.0F, 19.0F, 2.0F);
		shape2.addBox(0.0F, 0.0F, 0.0F, 8, 1, 1, 0.0F);
		shape1 = new ModelRenderer(this, 0, 11);
		shape1.setRotationPoint(3.0F, 20.0F, 1.0F);
		shape1.addBox(-7.0F, 0.0F, 1.0F, 8, 4, 1, 0.0F);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		shape7.render(f5);
		if(isActive)
			shape8.render(f5);
		shape3.render(f5);
		shape4.render(f5);
		if(isActive)
			shape5.render(f5);
		shape6.render(f5);
		shape9.render(f5);
		shape2.render(f5);
		shape1.render(f5);
	}

	/**
	 * This is a helper function from Tabula to set the rotation of model parts
	 */
	public void setRotation(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}

	@Override
	public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity entity){
		super.setRotationAngles(par1, par2, par3, par4, par5, par6, entity);
	}

	public void setActive(boolean par1){
		isActive = par1;
	}

}

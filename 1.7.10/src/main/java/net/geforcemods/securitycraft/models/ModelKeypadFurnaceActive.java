package net.geforcemods.securitycraft.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelKeypadFurnaceActive extends ModelBase {

	public ModelRenderer shape1;
	public ModelRenderer shape2;
	public ModelRenderer shape3;
	public ModelRenderer shape4;
	public ModelRenderer shape5;
	public ModelRenderer shape6;
	public ModelRenderer shape7;
	public ModelRenderer shape8;
	public ModelRenderer shape9;
	public ModelRenderer shape10;

	public ModelKeypadFurnaceActive() {
		this.textureWidth = 128;
		this.textureHeight = 128;
		this.shape1 = new ModelRenderer(this, 2, 2);
		this.shape1.setRotationPoint(-7.0F, 9.0F, -4.0F);
		this.shape1.addBox(0.0F, 0.0F, 0.0F, 14, 14, 11, 0.0F);
		this.shape2 = new ModelRenderer(this, 0, 40);
		this.shape2.setRotationPoint(-8.0F, 8.0F, -5.0F);
		this.shape2.addBox(0.0F, 0.0F, 0.0F, 16, 1, 13, 0.0F);
		this.shape7 = new ModelRenderer(this, 60, 120);
		this.shape7.setRotationPoint(-7.0F, 22.0F, -6.0F);
		this.shape7.addBox(0.0F, 0.0F, 0.0F, 14, 1, 1, 0.0F);
		this.shape8 = new ModelRenderer(this, 0, 0);
		this.shape8.setRotationPoint(-4.0F, 22.0F, -7.0F);
		this.shape8.addBox(0.0F, 0.0F, 0.0F, 1, 1, 1, 0.0F);
		this.shape6 = new ModelRenderer(this, 30, 100);
		this.shape6.setRotationPoint(7.0F, 9.0F, -5.0F);
		this.shape6.addBox(0.0F, 0.0F, 0.0F, 1, 14, 12, 0.0F);
		this.shape5 = new ModelRenderer(this, 0, 100);
		this.shape5.setRotationPoint(-8.0F, 9.0F, -5.0F);
		this.shape5.addBox(0.0F, 0.0F, 0.0F, 1, 14, 12, 0.0F);
		this.shape10 = new ModelRenderer(this, 50, 0);
		this.shape10.setRotationPoint(-4.0F, 22.0F, -8.0F);
		this.shape10.addBox(0.0F, 0.0F, 0.0F, 8, 1, 1, 0.0F);
		this.shape3 = new ModelRenderer(this, 0, 60);
		this.shape3.setRotationPoint(-8.0F, 23.0F, -5.0F);
		this.shape3.addBox(0.0F, 0.0F, 0.0F, 16, 1, 13, 0.0F);
		this.shape9 = new ModelRenderer(this, 0, 0);
		this.shape9.setRotationPoint(3.0F, 22.0F, -7.0F);
		this.shape9.addBox(0.0F, 0.0F, 0.0F, 1, 1, 1, 0.0F);
		this.shape4 = new ModelRenderer(this, 0, 80);
		this.shape4.setRotationPoint(-8.0F, 9.0F, 7.0F);
		this.shape4.addBox(0.0F, 0.0F, 0.0F, 16, 14, 1, 0.0F);
	}

	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
		this.shape1.render(f5);
		this.shape2.render(f5);
		this.shape7.render(f5);
		this.shape8.render(f5);
		this.shape6.render(f5);
		this.shape5.render(f5);
		this.shape10.render(f5);
		this.shape3.render(f5);
		this.shape9.render(f5);
		this.shape4.render(f5);
	}

	/**
	 * This is a helper function from Tabula to set the rotation of model parts
	 */
	 public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		 modelRenderer.rotateAngleX = x;
		 modelRenderer.rotateAngleY = y;
		 modelRenderer.rotateAngleZ = z;
	 }

	 public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity entity){
		 super.setRotationAngles(par1, par2, par3, par4, par5, par6, entity);
	 }
}

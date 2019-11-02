package net.geforcemods.securitycraft.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * KeypadFurnace - Geforce
 * Created using Tabula 4.1.1
 */
public class ModelKeypadFurnaceDeactivated extends ModelBase {
	public ModelRenderer shape7;
	public ModelRenderer shape8;
	public ModelRenderer shape9;
	public ModelRenderer shape10;
	public ModelRenderer shape20;

	public ModelKeypadFurnaceDeactivated() {
		textureWidth = 128;
		textureHeight = 128;
		shape20 = new ModelRenderer(this, 0, 0);
		shape20.setRotationPoint(-8.0F, 8.0F, -5.0F);
		shape20.addBox(0.0F, 0.0F, 0.0F, 16, 16, 13, 0.0F);
		shape9 = new ModelRenderer(this, 0, 0);
		shape9.setRotationPoint(3.0F, 9.0F, -7.0F);
		shape9.addBox(0.0F, 0.0F, 0.0F, 1, 1, 1, 0.0F);
		shape8 = new ModelRenderer(this, 0, 0);
		shape8.setRotationPoint(-4.0F, 9.0F, -7.0F);
		shape8.addBox(0.0F, 0.0F, 0.0F, 1, 1, 1, 0.0F);
		shape7 = new ModelRenderer(this, 90, 100);
		shape7.setRotationPoint(-7.0F, 9.0F, -6.0F);
		shape7.addBox(0.0F, 0.0F, 0.0F, 14, 14, 1, 0.0F);
		shape10 = new ModelRenderer(this, 50, 0);
		shape10.setRotationPoint(-4.0F, 9.0F, -8.0F);
		shape10.addBox(0.0F, 0.0F, 0.0F, 8, 1, 1, 0.0F);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		shape20.render(f5);
		shape9.render(f5);
		shape8.render(f5);
		shape7.render(f5);
		shape10.render(f5);
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
	public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity par7Entity) {
		super.setRotationAngles(par1, par2, par3, par4, par5, par6, par7Entity);
	}

}

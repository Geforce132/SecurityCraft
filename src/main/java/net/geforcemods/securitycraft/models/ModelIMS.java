package net.geforcemods.securitycraft.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * I.M.S. - Geforce
 * Created using Tabula 4.1.1
 */
public class ModelIMS extends ModelBase {
	public ModelRenderer bomb1;
	public ModelRenderer bomb2;
	public ModelRenderer bomb3;
	public ModelRenderer bomb4;
	public ModelRenderer center;

	public ModelIMS() {
		textureWidth = 32;
		textureHeight = 32;
		center = new ModelRenderer(this, 0, 15);
		center.setRotationPoint(-4.0F, 17.0F, -3.0F);
		center.addBox(0.0F, 0.0F, 0.0F, 8, 7, 6, 0.0F);
		bomb2 = new ModelRenderer(this, 0, 0);
		bomb2.setRotationPoint(-8.0F, 19.0F, -8.0F);
		bomb2.addBox(0.0F, 0.0F, 0.0F, 5, 5, 5, 0.0F);
		bomb3 = new ModelRenderer(this, 0, 0);
		bomb3.setRotationPoint(3.0F, 19.0F, 3.0F);
		bomb3.addBox(0.0F, 0.0F, 0.0F, 5, 5, 5, 0.0F);
		bomb4 = new ModelRenderer(this, 0, 0);
		bomb4.setRotationPoint(3.0F, 19.0F, -8.0F);
		bomb4.addBox(0.0F, 0.0F, 0.0F, 5, 5, 5, 0.0F);
		bomb1 = new ModelRenderer(this, 0, 0);
		bomb1.setRotationPoint(-8.0F, 19.0F, 3.0F);
		bomb1.addBox(0.0F, 0.0F, 0.0F, 5, 5, 5, 0.0F);
	}

	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5, int bombsRemaining) {
		if(bombsRemaining >= 4)
			bomb1.render(f5);

		if(bombsRemaining >= 3)
			bomb2.render(f5);

		if(bombsRemaining >= 2)
			bomb3.render(f5);

		if(bombsRemaining >= 1)
			bomb4.render(f5);

		if(bombsRemaining >= 0)
			center.render(f5);
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

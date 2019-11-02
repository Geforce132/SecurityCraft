package net.geforcemods.securitycraft.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * Alarm - Geforce
 * Created using Tabula 4.1.1
 */
public class ModelAlarm extends ModelBase {
	public ModelRenderer shape1;

	public ModelAlarm() {
		textureWidth = 64;
		textureHeight = 32;
		shape1 = new ModelRenderer(this, 0, 0);
		shape1.setRotationPoint(-4.0F, 16.0F, -4.0F);
		shape1.addBox(0.0F, 0.0F, 0.0F, 8, 8, 8, 0.0F);
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

	@Override
	public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity entity){
		super.setRotationAngles(par1, par2, par3, par4, par5, par6, entity);
	}

}

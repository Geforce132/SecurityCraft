package net.geforcemods.securitycraft.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * Protecto - Geforce
 * Created using Tabula 4.1.1
 */
public class ModelProtecto extends ModelBase {
	public ModelRenderer shape1;
	public ModelRenderer shape2;
	public ModelRenderer shape3;

	public ModelProtecto() {
		textureWidth = 64;
		textureHeight = 64;
		shape1 = new ModelRenderer(this, 0, 0);
		shape1.setRotationPoint(-8.0F, 8.0F, -3.0F);
		shape1.addBox(0.0F, 0.0F, 0.0F, 5, 16, 6, 0.0F);
		shape3 = new ModelRenderer(this, 0, 25);
		shape3.setRotationPoint(-3.0F, 8.0F, 8.0F);
		shape3.addBox(0.0F, 0.0F, 0.0F, 16, 16, 6, 0.0F);
		setRotateAngle(shape3, 0.0F, 1.5707963267948966F, 0.0F);
		shape2 = new ModelRenderer(this, 0, 0);
		shape2.setRotationPoint(8.0F, 8.0F, 3.0F);
		shape2.addBox(0.0F, 0.0F, 0.0F, 5, 16, 6, 0.0F);
		setRotateAngle(shape2, 0.0F, -3.141592653589793F, 0.0F);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		shape1.render(f5);
		shape3.render(f5);
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

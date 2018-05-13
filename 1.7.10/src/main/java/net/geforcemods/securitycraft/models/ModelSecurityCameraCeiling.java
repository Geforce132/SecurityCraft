package net.geforcemods.securitycraft.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * SecurityCameraCeiling - bl4ckscor3
 * Created using Tabula 4.1.1
 */
public class ModelSecurityCameraCeiling extends ModelBase
{
	public ModelRenderer cam;
	public ModelRenderer south_long;
	public ModelRenderer north_long;
	public ModelRenderer west;
	public ModelRenderer east;
	public ModelRenderer north_short;
	public ModelRenderer south_short;

	public ModelSecurityCameraCeiling()
	{
		textureWidth = 64;
		textureHeight = 32;
		north_long = new ModelRenderer(this, 8, 2);
		north_long.setRotationPoint(-2.0F, 8.0F, 1.0F);
		north_long.addBox(0.0F, 0.0F, 0.0F, 4, 1, 1, 0.0F);
		south_long = new ModelRenderer(this, 8, 0);
		south_long.setRotationPoint(-2.0F, 8.0F, -2.0F);
		south_long.addBox(0.0F, 0.0F, 0.0F, 4, 1, 1, 0.0F);
		south_short = new ModelRenderer(this, 18, 0);
		south_short.setRotationPoint(-1.0F, 8.0F, -3.0F);
		south_short.addBox(0.0F, 0.0F, 0.0F, 2, 1, 1, 0.0F);
		west = new ModelRenderer(this, 0, 3);
		west.setRotationPoint(-3.0F, 8.0F, -1.0F);
		west.addBox(0.0F, 0.0F, 0.0F, 2, 1, 2, 0.0F);
		east = new ModelRenderer(this, 0, 6);
		east.setRotationPoint(1.0F, 8.0F, -1.0F);
		east.addBox(0.0F, 0.0F, 0.0F, 2, 1, 2, 0.0F);
		north_short = new ModelRenderer(this, 18, 2);
		north_short.setRotationPoint(-1.0F, 8.0F, 2.0F);
		north_short.addBox(0.0F, 0.0F, 0.0F, 2, 1, 1, 0.0F);
		cam = new ModelRenderer(this, 0, 0);
		cam.setRotationPoint(-1.0F, 9.0F, -1.0F);
		cam.addBox(0.0F, 0.0F, 0.0F, 2, 1, 2, 0.0F);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
	{
		north_short.render(f5);
		cam.render(f5);
		south_short.render(f5);
		east.render(f5);
		west.render(f5);
		north_long.render(f5);
		south_long.render(f5);
	}

	/**
	 * This is a helper function from Tabula to set the rotation of model parts
	 */
	public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z)
	{
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}

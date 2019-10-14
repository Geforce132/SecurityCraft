package net.geforcemods.securitycraft.models;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * Trophy system - Geforce
 * Created using Tabula 7.1.0
 */
public class ModelTrophySystem extends ModelBase {
    public ModelRenderer center;
    public ModelRenderer neck;
    public ModelRenderer targetting_cube;
    public ModelRenderer top_cap;
    public ModelRenderer bottom_cap;
    public ModelRenderer base1;
    public ModelRenderer base2;
    public ModelRenderer base3;
    public ModelRenderer base4;
    public ModelRenderer leg1;
    public ModelRenderer leg2;
    public ModelRenderer leg3;
    public ModelRenderer leg4;

    public ModelTrophySystem() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.leg3 = new ModelRenderer(this, 5, 43);
        this.leg3.setRotationPoint(-2.5F, 15.0F, -0.5F);
        this.leg3.addBox(0.0F, 0.0F, 0.0F, 1, 9, 1, 0.0F);
        this.setRotateAngle(leg3, 0.0F, 0.0F, 0.39269908169872414F);
        this.bottom_cap = new ModelRenderer(this, 43, 0);
        this.bottom_cap.setRotationPoint(-1.0F, 11.1F, -1.0F);
        this.bottom_cap.addBox(0.0F, 0.0F, 0.0F, 2, 1, 2, 0.0F);
        this.leg2 = new ModelRenderer(this, 5, 43);
        this.leg2.setRotationPoint(-0.5F, 15.0F, -2.5F);
        this.leg2.addBox(0.0F, 0.0F, 0.0F, 1, 9, 1, 0.0F);
        this.setRotateAngle(leg2, -0.39269908169872414F, 0.0F, 0.0F);
        this.leg4 = new ModelRenderer(this, 5, 43);
        this.leg4.setRotationPoint(1.5F, 15.3F, -0.5F);
        this.leg4.addBox(0.0F, 0.0F, 0.0F, 1, 9, 1, 0.0F);
        this.setRotateAngle(leg4, 0.0F, 0.0F, -0.39269908169872414F);
        this.targetting_cube = new ModelRenderer(this, 20, 42);
        this.targetting_cube.setRotationPoint(-1.6F, 8.9F, -1.6F);
        this.targetting_cube.addBox(0.0F, 0.0F, 0.0F, 11, 11, 11, 0.0F);
        this.base4 = new ModelRenderer(this, 0, 32);
        this.base4.setRotationPoint(4.0F, 23.0F, -1.5F);
        this.base4.addBox(0.0F, 0.0F, 0.0F, 3, 1, 3, 0.0F);
        this.top_cap = new ModelRenderer(this, 43, 0);
        this.top_cap.setRotationPoint(-1.0F, 8.5F, -1.0F);
        this.top_cap.addBox(0.0F, 0.0F, 0.0F, 2, 1, 2, 0.0F);
        this.base3 = new ModelRenderer(this, 0, 32);
        this.base3.setRotationPoint(-7.0F, 23.0F, -1.5F);
        this.base3.addBox(0.0F, 0.0F, 0.0F, 3, 1, 3, 0.0F);
        this.base2 = new ModelRenderer(this, 0, 32);
        this.base2.setRotationPoint(-1.5F, 23.0F, -7.0F);
        this.base2.addBox(0.0F, 0.0F, 0.0F, 3, 1, 3, 0.0F);
        this.neck = new ModelRenderer(this, 20, -1);
        this.neck.setRotationPoint(-0.5F, 11.0F, -0.5F);
        this.neck.addBox(0.0F, 0.0F, 0.0F, 1, 3, 1, 0.0F);
        this.center = new ModelRenderer(this, 20, 20);
        this.center.setRotationPoint(-3.0F, 12.9F, -3.0F);
        this.center.addBox(0.0F, 0.0F, 0.0F, 11, 11, 11, 0.0F);
        this.base1 = new ModelRenderer(this, 0, 32);
        this.base1.setRotationPoint(-1.5F, 23.0F, 4.0F);
        this.base1.addBox(0.0F, 0.0F, 0.0F, 3, 1, 3, 0.0F);
        this.leg1 = new ModelRenderer(this, 5, 43);
        this.leg1.setRotationPoint(-0.5F, 15.3F, 1.5F);
        this.leg1.addBox(0.0F, 0.0F, 0.0F, 1, 9, 1, 0.0F);
        this.setRotateAngle(leg1, 0.39269908169872414F, 0.0F, 0.0F);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
        this.leg3.render(f5);
        this.bottom_cap.render(f5);
        this.leg2.render(f5);
        this.leg4.render(f5);
        GL11.glPushMatrix();
        GL11.glTranslatef(this.targetting_cube.offsetX, this.targetting_cube.offsetY, this.targetting_cube.offsetZ);
        GL11.glTranslatef(this.targetting_cube.rotationPointX * f5, this.targetting_cube.rotationPointY * f5, this.targetting_cube.rotationPointZ * f5);
        GL11.glScaled(0.29D, 0.25D, 0.29D);
        GL11.glTranslatef(-this.targetting_cube.offsetX, -this.targetting_cube.offsetY, -this.targetting_cube.offsetZ);
        GL11.glTranslatef(-this.targetting_cube.rotationPointX * f5, -this.targetting_cube.rotationPointY * f5, -this.targetting_cube.rotationPointZ * f5);
        this.targetting_cube.render(f5);
        GL11.glPopMatrix();
        this.base4.render(f5);
        this.top_cap.render(f5);
        this.base3.render(f5);
        this.base2.render(f5);
        this.neck.render(f5);
        GL11.glPushMatrix();
        GL11.glTranslatef(this.center.offsetX, this.center.offsetY, this.center.offsetZ);
        GL11.glTranslatef(this.center.rotationPointX * f5, this.center.rotationPointY * f5, this.center.rotationPointZ * f5);
        GL11.glScaled(0.55D, 0.45D, 0.55D);
        GL11.glTranslatef(-this.center.offsetX, -this.center.offsetY, -this.center.offsetZ);
        GL11.glTranslatef(-this.center.rotationPointX * f5, -this.center.rotationPointY * f5, -this.center.rotationPointZ * f5);
        this.center.render(f5);
        GL11.glPopMatrix();
        this.base1.render(f5);
        this.leg1.render(f5);
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

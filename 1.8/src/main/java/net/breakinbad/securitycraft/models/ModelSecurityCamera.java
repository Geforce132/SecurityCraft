package net.breakinbad.securitycraft.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * SecurityCamera - Geforce
 * Created using Tabula 4.1.1
 */
public class ModelSecurityCamera extends ModelBase {
    public ModelRenderer shape1;
    public ModelRenderer shape2;
    public ModelRenderer cameraRotationPoint;
    public ModelRenderer shape3;
    public ModelRenderer cameraBody;
    public ModelRenderer cameraLensRight;
    public ModelRenderer cameraLensLeft;
    public ModelRenderer cameraLensTop;
    
    public boolean reverseCameraRotation = false;

    public ModelSecurityCamera() {
        this.textureWidth = 128;
        this.textureHeight = 64;
        this.cameraRotationPoint = new ModelRenderer(this, 0, 25);
        this.cameraRotationPoint.setRotationPoint(0.0F, 14.0F, 3.0F);
        this.cameraRotationPoint.addBox(0.0F, 0.0F, 0.0F, 1, 1, 1, 0.0F);
        this.setRotateAngle(cameraRotationPoint, 0.2617993877991494F, 0.0F, 0.0F);
        this.cameraLensRight = new ModelRenderer(this, 10, 40);
        this.cameraLensRight.setRotationPoint(3.0F, 0.0F, -3.0F);
        this.cameraLensRight.addBox(-2.0F, 0.0F, 0.0F, 1, 3, 1, 0.0F);
        this.shape3 = new ModelRenderer(this, 1, 12);
        this.shape3.setRotationPoint(0.0F, 1.0F, 0.0F);
        this.shape3.addBox(0.0F, 0.0F, 0.0F, 2, 1, 7, 0.0F);
        this.cameraLensLeft = new ModelRenderer(this, 0, 40);
        this.cameraLensLeft.setRotationPoint(-2.0F, 0.0F, -3.0F);
        this.cameraLensLeft.addBox(0.0F, 0.0F, 0.0F, 1, 3, 1, 0.0F);
        this.cameraBody = new ModelRenderer(this, 0, 25);
        this.cameraBody.setRotationPoint(0.0F, 0.0F, -5.0F);
        this.cameraBody.addBox(-2.0F, 0.0F, -2.0F, 4, 3, 8, 0.0F);
        this.setRotateAngle(cameraBody, 0.2617993877991494F, 0.0F, 0.0F);
        this.shape1 = new ModelRenderer(this, 0, 0);
        this.shape1.setRotationPoint(-3.0F, 13.0F, 7.0F);
        this.shape1.addBox(0.0F, 0.0F, 0.0F, 6, 6, 1, 0.0F);
        this.cameraLensTop = new ModelRenderer(this, 20, 40);
        this.cameraLensTop.setRotationPoint(-1.0F, 0.0F, -3.0F);
        this.cameraLensTop.addBox(0.0F, 0.0F, 0.0F, 2, 1, 1, 0.0F);
        this.shape2 = new ModelRenderer(this, 2, 12);
        this.shape2.setRotationPoint(-1.0F, 13.75F, 2.25F);
        this.shape2.addBox(0.0F, 0.0F, 0.0F, 2, 1, 6, 0.0F);
        this.setRotateAngle(shape2, -0.5235987755982988F, 0.0F, 0.0F);
        this.cameraBody.addChild(this.cameraLensRight);
        this.shape2.addChild(this.shape3);
        this.cameraBody.addChild(this.cameraLensLeft);
        this.cameraRotationPoint.addChild(this.cameraBody);
        this.cameraBody.addChild(this.cameraLensTop);
    }

    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
        this.cameraRotationPoint.render(f5);
        this.shape1.render(f5);
        this.shape2.render(f5);
    }

    public void setCameraRotation(float rotation) {
        this.cameraRotationPoint.rotateAngleY = rotation;
    }
    
    /**
     * This is a helper function from Tabula to set the rotation of model parts
     */
    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
    
    public void setRotationAngles(float par1, float par2, float par3, float par4, float par5, float par6, Entity par7Entity) {
    	super.setRotationAngles(par1, par2, par3, par4, par5, par6, par7Entity);
    }
    
}

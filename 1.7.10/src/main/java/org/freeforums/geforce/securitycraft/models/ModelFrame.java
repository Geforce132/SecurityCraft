package org.freeforums.geforce.securitycraft.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * Key Panel - Geforce
 * Created using Tabula 4.1.1
 */
public class ModelFrame extends ModelBase {
    public ModelRenderer shape1;
    public ModelRenderer shape2;
    public ModelRenderer shape3;
    public ModelRenderer shape4;
    public ModelRenderer shape5;

    public ModelFrame() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.shape1 = new ModelRenderer(this, 0, 0);
        this.shape1.setRotationPoint(-8.0F, 8.0F, -8.0F);
        this.shape1.addBox(0.0F, 0.0F, 1.0F, 16, 16, 15, 0.0F);
        this.shape4 = new ModelRenderer(this, 0, 40);
        this.shape4.setRotationPoint(-8.0F, 10.0F, -8.0F);
        this.shape4.addBox(0.0F, 0.0F, 0.0F, 2, 12, 1, 0.0F);
        this.shape3 = new ModelRenderer(this, 10, 40);
        this.shape3.setRotationPoint(0.0F, 14.0F, 0.0F);
        this.shape3.addBox(-8.0F, 8.0F, -8.0F, 16, 2, 1, 0.0F);
        this.shape5 = new ModelRenderer(this, 10, 45);
        this.shape5.setRotationPoint(6.0F, 10.0F, -8.0F);
        this.shape5.addBox(0.0F, 0.0F, 0.0F, 2, 12, 1, 0.0F);
        this.shape2 = new ModelRenderer(this, 0, 35);
        this.shape2.setRotationPoint(-8.0F, 8.0F, -8.0F);
        this.shape2.addBox(0.0F, 0.0F, 0.0F, 16, 2, 1, 0.0F);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
        this.shape1.render(f5);
        this.shape4.render(f5);
        this.shape3.render(f5);
        this.shape5.render(f5);
        this.shape2.render(f5);
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

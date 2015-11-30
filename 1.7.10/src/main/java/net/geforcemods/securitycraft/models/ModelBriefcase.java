package net.geforcemods.securitycraft.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * Briefcase - Geforce
 * Created using Tabula 4.1.1
 */
public class ModelBriefcase extends ModelBase {
    public ModelRenderer shape1;
    public ModelRenderer shape2;
    public ModelRenderer shape3;
    public ModelRenderer shape3_1;

    public ModelBriefcase() {
        this.textureWidth = 48;
        this.textureHeight = 32;
        this.shape3_1 = new ModelRenderer(this, 0, 25);
        this.shape3_1.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.shape3_1.addBox(2.0F, -3.0F, 1.0F, 8, 1, 2, 0.0F);
        this.shape3 = new ModelRenderer(this, 0, 15);
        this.shape3.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.shape3.addBox(10.0F, -3.0F, 1.0F, 1, 3, 2, 0.0F);
        this.shape1 = new ModelRenderer(this, 0, 0);
        this.shape1.setRotationPoint(-6.0F, 14.0F, -2.0F);
        this.shape1.addBox(0.0F, 0.0F, 0.0F, 12, 10, 4, 0.0F);
        this.shape2 = new ModelRenderer(this, 10, 15);
        this.shape2.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.shape2.addBox(1.0F, -3.0F, 1.0F, 1, 3, 2, 0.0F);
        this.shape1.addChild(this.shape3_1);
        this.shape1.addChild(this.shape3);
        this.shape1.addChild(this.shape2);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
        this.shape1.render(f5);
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

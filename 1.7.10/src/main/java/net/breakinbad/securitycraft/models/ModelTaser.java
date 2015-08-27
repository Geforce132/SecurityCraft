package net.breakinbad.securitycraft.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * Taser - Geforce
 * Created using Tabula 4.1.1
 */
public class ModelTaser extends ModelBase {
    public ModelRenderer shape1;
    public ModelRenderer shape2;
    public ModelRenderer shape3;
    public ModelRenderer shape4;
    public ModelRenderer shape5;
    public ModelRenderer shape6;
    public ModelRenderer shape7;

    public ModelTaser() {
        this.textureWidth = 96;
        this.textureHeight = 64;
        this.shape2 = new ModelRenderer(this, 10, 14);
        this.shape2.setRotationPoint(0.55F, 2.5F, 1.0F);
        this.shape2.addBox(0.0F, -2.0F, 0.0F, 2, 7, 2, 0.0F);
        this.setRotateAngle(shape2, -0.3490658503988659F, 0.0F, 0.0F);
        this.shape4 = new ModelRenderer(this, 0, 25);
        this.shape4.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.shape4.addBox(0.0F, 3.0F, 7.0F, 3, 1, 1, 0.0F);
        this.shape1 = new ModelRenderer(this, 0, 0);
        this.shape1.setRotationPoint(-1.5F, 15.0F, -4.0F);
        this.shape1.addBox(0.0F, 0.0F, 0.0F, 3, 4, 7, 0.0F);
        this.shape6 = new ModelRenderer(this, 0, 38);
        this.shape6.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.shape6.addBox(2.0F, 1.0F, 7.0F, 1, 2, 1, 0.0F);
        this.shape7 = new ModelRenderer(this, 0, 45);
        this.shape7.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.shape7.addBox(0.5F, 0.5F, -0.4F, 2, 3, 1, 0.0F);
        this.shape3 = new ModelRenderer(this, 0, 20);
        this.shape3.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.shape3.addBox(0.0F, 0.0F, 7.0F, 3, 1, 1, 0.0F);
        this.shape5 = new ModelRenderer(this, 0, 30);
        this.shape5.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.shape5.addBox(0.0F, 1.0F, 7.0F, 1, 2, 1, 0.0F);
        this.shape1.addChild(this.shape2);
        this.shape1.addChild(this.shape4);
        this.shape1.addChild(this.shape6);
        this.shape1.addChild(this.shape7);
        this.shape1.addChild(this.shape3);
        this.shape1.addChild(this.shape5);
    }

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
    
    public void setRotationAngles(float p_78087_1_, float p_78087_2_, float p_78087_3_, float p_78087_4_, float p_78087_5_, float p_78087_6_, Entity p_78087_7_){
    	super.setRotationAngles(p_78087_1_, p_78087_2_, p_78087_3_, p_78087_4_, p_78087_5_, p_78087_6_, p_78087_7_);
    }

}

package net.geforcemods.securitycraft.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelMotionSensoredLight extends ModelBase {
    public ModelRenderer sensor;
    public ModelRenderer bottom;
    public ModelRenderer back;
    public ModelRenderer top;
    public ModelRenderer light;

    public ModelMotionSensoredLight() {
        this.textureWidth = 64;
        this.textureHeight = 32;
        this.bottom = new ModelRenderer(this, 10, 0);
        this.bottom.setRotationPoint(-1.0F, 20.0F, 6.0F);
        this.bottom.addBox(0.0F, 0.0F, 0.0F, 2, 1, 2, 0.0F);
        this.sensor = new ModelRenderer(this, 0, 0);
        this.sensor.setRotationPoint(-2.0F, 20.0F, 5.0F);
        this.sensor.addBox(0.0F, 0.0F, 0.0F, 4, 1, 1, 0.0F);
        this.back = new ModelRenderer(this, 0, 2);
        this.back.setRotationPoint(-1.0F, 17.0F, 7.0F);
        this.back.addBox(0.0F, 0.0F, 0.0F, 2, 3, 1, 0.0F);
        this.top = new ModelRenderer(this, 6, 3);
        this.top.setRotationPoint(-1.0F, 16.0F, 6.0F);
        this.top.addBox(0.0F, 0.0F, 0.0F, 2, 1, 2, 0.0F);
        this.light = new ModelRenderer(this, 18, 2);
        this.light.setRotationPoint(-2.0F, 15.0F, 5.0F);
        this.light.addBox(0.0F, 0.0F, 0.0F, 4, 3, 1, 0.0F);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
        this.bottom.render(f5);
        this.sensor.render(f5);
        this.back.render(f5);
        this.top.render(f5);
        this.light.render(f5);
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

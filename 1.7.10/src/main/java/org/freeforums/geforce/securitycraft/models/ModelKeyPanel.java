package org.freeforums.geforce.securitycraft.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * KeyPanel - bl4ckscor3
 * Created using Tabula 4.1.1
 */
public class ModelKeyPanel extends ModelBase {
    public ModelRenderer keyPanel;

    public ModelKeyPanel() {
        this.textureWidth = 64;
        this.textureHeight = 32;
        this.keyPanel = new ModelRenderer(this, 0, 0);
        this.keyPanel.setRotationPoint(-2.5F, 13.5F, 7.0F);
        this.keyPanel.addBox(0.0F, 0.0F, 0.0F, 5, 5, 1, 0.0F);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
        this.keyPanel.render(f5);
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

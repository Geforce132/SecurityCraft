package net.geforcemods.securitycraft.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * IMSBomb - Geforce
 * Created using Tabula 4.1.1
 */
public class ModelIMSBomb extends ModelBase {
	public ModelRenderer shape1;

	public ModelIMSBomb() {
		textureWidth = 24;
		textureHeight = 24;
		shape1 = new ModelRenderer(this, 0, 0);
		shape1.setRotationPoint(0.0F, 0.0F, 0.0F);
		shape1.addBox(0.0F, 0.0F, 0.0F, 3, 4, 3, 0.0F);
	}

	@Override
	public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		shape1.render(scale);
	}
}

package net.geforcemods.securitycraft.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class SecureRedstoneInterfaceDishModel extends ModelBase {
	private final ModelRenderer modelParts;

	public SecureRedstoneInterfaceDishModel() {
		textureWidth = 16;
		textureHeight = 16;
		modelParts = new ModelRenderer(this);
		//@formatter:off
		modelParts.setTextureOffset(0, 4).addBox(-1.0F, -1.0F, -1.0F, 2, 2, 2)
		.setTextureOffset(0, 2).addBox(-2.0F, -2.0F, -2.0F, 4, 1, 1)
		.setTextureOffset(0, 0).addBox(-2.0F, 1.0F, -2.0F, 4, 1, 1)
		.setTextureOffset(7, 7).addBox(1.0F, -1.0F, -2.0F, 1, 2, 1)
		.setTextureOffset(0, 8).addBox(-2.0F, -1.0F, -2.0F, 1, 2, 1);
		modelParts.setRotationPoint(0.0F, 12.0F, 0.0F);
		//@formatter:on
	}

	@Override
	public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		modelParts.render(scale);
	}

	public void rotate(float rotation) {
		modelParts.rotateAngleY = rotation;
	}
}
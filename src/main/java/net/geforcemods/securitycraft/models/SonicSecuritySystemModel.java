package net.geforcemods.securitycraft.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SonicSecuritySystemModel extends ModelBase {
	public final ModelRenderer radar;

	public SonicSecuritySystemModel() {
		textureWidth = 32;
		textureHeight = 32;

		radar = new ModelRenderer(this);
		radar.setRotationPoint(0.0F, 10.5F, 0.0F);
		radar.setTextureOffset(15, 0).addBox(-0.5F, -0.5F, -0.5F, 1, 1, 2, 0.0F);
		radar.setTextureOffset(15, 3).addBox(-1.5F, -1.5F, 1.0F, 3, 1, 1, 0.0F);
		radar.setTextureOffset(15, 5).addBox(0.5F, -0.5F, 1.0F, 1, 1, 1, 0.0F);
		radar.setTextureOffset(15, 7).addBox(-1.5F, -0.5F, 1.0F, 1, 1, 1, 0.0F);
		radar.setTextureOffset(15, 9).addBox(-1.5F, 0.5F, 1.0F, 3, 1, 1, 0.0F);
		radar.setTextureOffset(0, 0).addBox(-2.5F, -2.5F, 1.5F, 5, 1, 1, 0.0F);
		radar.setTextureOffset(0, 2).addBox(1.5F, -1.5F, 1.5F, 1, 3, 1, 0.0F);
		radar.setTextureOffset(0, 6).addBox(-2.5F, -1.5F, 1.5F, 1, 3, 1, 0.0F);
		radar.setTextureOffset(0, 10).addBox(-2.5F, 1.5F, 1.5F, 5, 1, 1, 0.0F);
	}

	@Override
	public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		radar.render(scale);
	}

	public void setRadarRotation(float rotation) {
		radar.rotateAngleY = rotation;
	}
}
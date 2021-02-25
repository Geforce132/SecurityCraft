package net.geforcemods.securitycraft.models;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SonicSecuritySystemModel extends EntityModel<Entity> {
	
	public ModelRenderer radar;
	public ModelRenderer bb_main;

	public SonicSecuritySystemModel() {
		textureWidth = 32;
		textureHeight = 32;

		radar = new ModelRenderer(this);
		radar.setRotationPoint(0.0F, 10.5F, 0.0F);
		radar.setTextureOffset(15, 0).addBox(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 2.0F, 0.0F, false);
		radar.setTextureOffset(15, 3).addBox(-1.5F, -1.5F, 1.0F, 3.0F, 1.0F, 1.0F, 0.0F, false);
		radar.setTextureOffset(15, 5).addBox(0.5F, -0.5F, 1.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);
		radar.setTextureOffset(15, 7).addBox(-1.5F, -0.5F, 1.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);
		radar.setTextureOffset(15, 9).addBox(-1.5F, 0.5F, 1.0F, 3.0F, 1.0F, 1.0F, 0.0F, false);
		radar.setTextureOffset(0, 0).addBox(-2.5F, -2.5F, 1.5F, 5.0F, 1.0F, 1.0F, 0.0F, false);
		radar.setTextureOffset(0, 2).addBox(1.5F, -1.5F, 1.5F, 1.0F, 3.0F, 1.0F, 0.0F, false);
		radar.setTextureOffset(0, 6).addBox(-2.5F, -1.5F, 1.5F, 1.0F, 3.0F, 1.0F, 0.0F, false);
		radar.setTextureOffset(0, 10).addBox(-2.5F, 1.5F, 1.5F, 5.0F, 1.0F, 1.0F, 0.0F, false);

		bb_main = new ModelRenderer(this);
		bb_main.setRotationPoint(0.0F, 24.0F, 0.0F);
		bb_main.setTextureOffset(0, 28).addBox(-1.5F, -1.0F, -1.5F, 3.0F, 1.0F, 3.0F, 0.0F, false);
		bb_main.setTextureOffset(0, 25).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 1.0F, 2.0F, 0.0F, false);
		bb_main.setTextureOffset(12, 20).addBox(-0.5F, -13.0F, -0.5F, 1.0F, 11.0F, 1.0F, 0.0F, false);
	}

	@Override
	public void setRotationAngles(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
		//previously the render function, render code was moved to a method below
	}

	@Override
	public void render(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
		radar.render(matrixStack, buffer, packedLight, packedOverlay);
		bb_main.render(matrixStack, buffer, packedLight, packedOverlay);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}
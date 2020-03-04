package net.geforcemods.securitycraft.models;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.geforcemods.securitycraft.entity.SentryEntity;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Sentry - bl4ckscor3
 * Created using Tabula 7.0.0
 */
@OnlyIn(Dist.CLIENT)
public class SentryModel extends EntityModel<SentryEntity>
{
	public ModelRenderer base;
	public ModelRenderer body;
	public ModelRenderer neck;
	public ModelRenderer head;
	public ModelRenderer hair;
	public ModelRenderer rightEye;
	public ModelRenderer leftEye;
	public ModelRenderer nose;

	public SentryModel()
	{
		textureWidth = 64;
		textureHeight = 64;
		base = new ModelRenderer(this, 0, 0);
		base.setRotationPoint(-7.5F, 9.0F, -7.5F);
		base.addBox(0.0F, 0.0F, 0.0F, 15, 15, 15);
		head = new ModelRenderer(this, 24, 30);
		head.setRotationPoint(-4.0F, -4.0F, -3.0F);
		head.addBox(0.0F, 0.0F, 0.0F, 8, 5, 6);
		neck = new ModelRenderer(this, 45, 0);
		neck.setRotationPoint(-2.0F, 1.0F, -2.0F);
		neck.addBox(0.0F, 0.0F, 0.0F, 4, 4, 4);
		rightEye = new ModelRenderer(this, 0, 0);
		rightEye.setRotationPoint(-2.7F, -3.0F, -3.3F);
		rightEye.addBox(0.0F, 0.0F, 0.0F, 2, 2, 1);
		body = new ModelRenderer(this, 0, 30);
		body.setRotationPoint(-3.0F, 5.0F, -3.0F);
		body.addBox(0.0F, 0.0F, 0.0F, 6, 4, 6);
		nose = new ModelRenderer(this, 0, 3);
		nose.setRotationPoint(-0.5F, -1.0F, -6.9F);
		nose.addBox(0.0F, 0.0F, 0.0F, 1, 1, 4);
		leftEye = new ModelRenderer(this, 6, 0);
		leftEye.setRotationPoint(0.7F, -3.0F, -3.3F);
		leftEye.addBox(0.0F, 0.0F, 0.0F, 2, 2, 1);
		hair = new ModelRenderer(this, 0, 40);
		hair.setRotationPoint(-3.0F, -5.0F, -3.0F);
		hair.addBox(0.0F, 0.0F, 0.0F, 6, 1, 6);
	}

	public void renderBase(MatrixStack matrix, IVertexBuilder builder, int packedLight, int packedOverlay, float red, float green, float blue, float alpha)
	{
		base.render(matrix, builder, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	public void render(MatrixStack matrix, IVertexBuilder builder, int packedLight, int packedOverlay, float red, float green, float blue, float alpha)
	{
		head.render(matrix, builder, packedLight, packedOverlay, red, green, blue, alpha);
		neck.render(matrix, builder, packedLight, packedOverlay, red, green, blue, alpha);
		rightEye.render(matrix, builder, packedLight, packedOverlay, red, green, blue, alpha);
		body.render(matrix, builder, packedLight, packedOverlay, red, green, blue, alpha);
		nose.render(matrix, builder, packedLight, packedOverlay, red, green, blue, alpha);
		leftEye.render(matrix, builder, packedLight, packedOverlay, red, green, blue, alpha);
		hair.render(matrix, builder, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	public void setRotationAngles(SentryEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}
}

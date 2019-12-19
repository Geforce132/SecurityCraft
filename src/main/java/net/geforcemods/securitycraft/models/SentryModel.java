package net.geforcemods.securitycraft.models;

import com.mojang.blaze3d.systems.RenderSystem;

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
		base.addBox(0.0F, 0.0F, 0.0F, 15, 15, 15, 0.0F);
		head = new ModelRenderer(this, 24, 30);
		head.setRotationPoint(-4.0F, -4.0F, -3.0F);
		head.addBox(0.0F, 0.0F, 0.0F, 8, 5, 6, 0.0F);
		neck = new ModelRenderer(this, 45, 0);
		neck.setRotationPoint(-2.0F, 1.0F, -2.0F);
		neck.addBox(0.0F, 0.0F, 0.0F, 4, 4, 4, 0.0F);
		rightEye = new ModelRenderer(this, 0, 0);
		rightEye.setRotationPoint(-2.7F, -3.0F, -3.3F);
		rightEye.addBox(0.0F, 0.0F, 0.0F, 2, 2, 1, 0.0F);
		body = new ModelRenderer(this, 0, 30);
		body.setRotationPoint(-3.0F, 5.0F, -3.0F);
		body.addBox(0.0F, 0.0F, 0.0F, 6, 4, 6, 0.0F);
		nose = new ModelRenderer(this, 0, 3);
		nose.setRotationPoint(-0.5F, -1.0F, -6.9F);
		nose.addBox(0.0F, 0.0F, 0.0F, 1, 1, 4, 0.0F);
		leftEye = new ModelRenderer(this, 6, 0);
		leftEye.setRotationPoint(0.7F, -3.0F, -3.3F);
		leftEye.addBox(0.0F, 0.0F, 0.0F, 2, 2, 1, 0.0F);
		hair = new ModelRenderer(this, 0, 40);
		hair.setRotationPoint(-3.0F, -5.0F, -3.0F);
		hair.addBox(0.0F, 0.0F, 0.0F, 6, 1, 6, 0.0F);
	}

	@Override
	public void render(SentryEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale)
	{
		base.render(scale);
		RenderSystem.pushMatrix();
		RenderSystem.rotatef(entity.getDataManager().get(SentryEntity.HEAD_ROTATION), 0.0F, 1.0F, 0.0F);
		RenderSystem.translatef(0.0F, entity.getHeadYTranslation(), 0.0F);
		head.render(scale);
		neck.render(scale);
		rightEye.render(scale);
		body.render(scale);
		nose.render(scale);
		leftEye.render(scale);
		hair.render(scale);
		RenderSystem.popMatrix();
	}
}

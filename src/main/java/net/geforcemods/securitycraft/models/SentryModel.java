package net.geforcemods.securitycraft.models;

import net.geforcemods.securitycraft.entity.sentry.Sentry;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;

/**
 * Sentry - bl4ckscor3 Created using Tabula 7.0.0
 */
public class SentryModel extends ModelBase {
	public final ModelRenderer base;
	public final ModelRenderer body;
	public final ModelRenderer neck;
	public final ModelRenderer head;
	public final ModelRenderer hair;
	public final ModelRenderer rightEye;
	public final ModelRenderer leftEye;
	public final ModelRenderer nose;

	public SentryModel() {
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
	public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		base.render(scale);
		GlStateManager.pushMatrix();

		if (entity instanceof Sentry) {
			float partialTicks = Minecraft.getMinecraft().getRenderPartialTicks();
			Sentry sentry = (Sentry) entity;

			GlStateManager.rotate(Utils.lerp(partialTicks, sentry.getOriginalHeadRotation(), sentry.getHeadRotation()), 0.0F, 1.0F, 0.0F);
			GlStateManager.translate(0.0F, sentry.getHeadYTranslation(partialTicks), 0.0F);
		}

		head.render(scale);
		neck.render(scale);
		rightEye.render(scale);
		body.render(scale);
		nose.render(scale);
		leftEye.render(scale);
		hair.render(scale);
		GlStateManager.popMatrix();
	}
}

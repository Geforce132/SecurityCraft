package net.geforcemods.securitycraft.mixin.taser;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.geforcemods.securitycraft.items.TaserItem;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

/**
 * If the player is holding a taser, makes it look like the player is actually holding it
 */
@Mixin(ModelBiped.class)
public class ModelBipedMixin {
	@Inject(method = "setRotationAngles", at = @At(value = "FIELD", target = "Lnet/minecraft/client/model/ModelBiped;swingProgress:F", ordinal = 0))
	private void securitycraft$positionArmsWhenHoldingTaser(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity e, CallbackInfo ci) {
		if (e instanceof EntityLivingBase) {
			EntityLivingBase entity = (EntityLivingBase) e;

			if (entity.getHeldItemMainhand().getItem() instanceof TaserItem || entity.getHeldItemOffhand().getItem() instanceof TaserItem) {
				ModelBiped model = (ModelBiped) (Object) this;
				ModelRenderer leftArm = model.bipedLeftArm;
				ModelRenderer rightArm = model.bipedRightArm;

				leftArm.rotateAngleY = 0.5F;
				rightArm.rotateAngleY = -0.5F;
				leftArm.rotateAngleX = rightArm.rotateAngleX = -1.5F;
			}
		}
	}
}

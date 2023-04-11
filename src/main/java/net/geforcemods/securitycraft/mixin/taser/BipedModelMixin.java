package net.geforcemods.securitycraft.mixin.taser;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.geforcemods.securitycraft.items.TaserItem;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;

/**
 * If the player is holding a taser, makes it look like the player is actually holding it
 */
@SuppressWarnings("rawtypes")
@Mixin(BipedModel.class)
public class BipedModelMixin {
	@Inject(method = "poseRightArm", at = @At("HEAD"), cancellable = true)
	private void securitycraft$positionRightArmWhenHoldingTaser(LivingEntity entity, CallbackInfo ci) {
		securitycraft$positionArms(entity, (BipedModel) (Object) this, ci);
	}

	@Inject(method = "poseLeftArm", at = @At("HEAD"), cancellable = true)
	private void securitycraft$positionLeftArmWhenHoldingTaser(LivingEntity entity, CallbackInfo ci) {
		securitycraft$positionArms(entity, (BipedModel) (Object) this, ci);
	}

	@Unique
	private void securitycraft$positionArms(LivingEntity entity, BipedModel model, CallbackInfo ci) {
		if (entity.getMainHandItem().getItem() instanceof TaserItem || entity.getOffhandItem().getItem() instanceof TaserItem) {
			ModelRenderer leftArm = model.leftArm;
			ModelRenderer rightArm = model.rightArm;

			leftArm.yRot = 0.5F;
			rightArm.yRot = -0.5F;
			leftArm.xRot = rightArm.xRot = -1.5F;
			ci.cancel();
		}
	}
}

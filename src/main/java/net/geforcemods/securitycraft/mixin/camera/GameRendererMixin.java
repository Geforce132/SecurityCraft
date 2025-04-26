package net.geforcemods.securitycraft.mixin.camera;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.platform.Window;

import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.entity.camera.FrameFeedHandler;
import net.geforcemods.securitycraft.entity.camera.SecurityCamera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

@Mixin(value = GameRenderer.class, priority = 1100)
public class GameRendererMixin {
	@Shadow
	@Final
	Minecraft minecraft;

	/**
	 * Makes sure camera zooming works, because the fov is only updated when the camera entity is the player itself
	 */
	@ModifyExpressionValue(method = "tickFov", at = @At(value = "CONSTANT", args = "floatValue=1.0F"))
	private float securitycraft$modifyInitialFValue(float original) {
		if (minecraft.cameraEntity instanceof SecurityCamera cam)
			return cam.getZoomAmount();
		else
			return original;
	}

	/**
	 * Renders the camera tint if a lens is installed. This cannot be done in a standard overlay, as the tint needs to exist even
	 * when the GUI is hidden with F1
	 */
	@Inject(method = "render", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Options;hideGui:Z", opcode = Opcodes.GETFIELD))
	private void securitycraft$renderCameraTint(float partialTicks, long nanoTime, boolean renderLevel, CallbackInfo ci, @Local GuiGraphics guiGraphics) {
		if (minecraft.cameraEntity instanceof SecurityCamera) {
			Level level = minecraft.level;
			BlockPos pos = minecraft.cameraEntity.blockPosition();
			Window window = minecraft.getWindow();

			if (!(level.getBlockEntity(pos) instanceof SecurityCameraBlockEntity be))
				return;

			ItemStack lens = be.getLensContainer().getItem(0);

			if (lens.getItem() instanceof DyeableLeatherItem item && item.hasCustomColor(lens))
				guiGraphics.fill(0, 0, window.getGuiScaledWidth(), window.getGuiScaledHeight(), item.getColor(lens) + (be.getOpacity() << 24));
		}
	}

	/**
	 * Makes sure distortion effects are not rendered in camera feeds
	 */
	@WrapOperation(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;lerp(FFF)F"))
	private float securitycraft$disableFeedDistortion(float delta, float start, float end, Operation<Float> original) {
		if (FrameFeedHandler.isCapturingCamera())
			return 0.0F;
		else
			return original.call(delta, start, end);
	}

	/**
	 * Prevents {@link Minecraft#hitResult} from being modified while the mod is capturing a Frame feed. This resolves issues
	 * like the wrong teleport position sometimes being suggested when using /tp
	 */
	@Inject(method = "pick", at = @At("HEAD"), cancellable = true)
	private void securitycraft$preventFramePick(float partialTicks, CallbackInfo ci) {
		if (FrameFeedHandler.isCapturingCamera())
			ci.cancel();
	}
}

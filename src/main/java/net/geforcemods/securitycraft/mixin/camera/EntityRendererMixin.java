package net.geforcemods.securitycraft.mixin.camera;

import javax.vecmath.Vector3f;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.entity.camera.CameraController;
import net.geforcemods.securitycraft.entity.camera.SecurityCamera;
import net.geforcemods.securitycraft.items.LensItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.culling.ClippingHelperImpl;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(value = EntityRenderer.class, priority = 1100)
public abstract class EntityRendererMixin {
	@Shadow
	@Final
	private Minecraft mc;
	@Shadow
	private float fogColorRed;
	@Shadow
	private float fogColorGreen;
	@Shadow
	private float fogColorBlue;

	@Shadow
	protected abstract void orientCamera(float partialTicks);

	/**
	 * Makes sure camera zooming works, because the fov is only updated when the camera entity is the player itself
	 */
	@ModifyConstant(method = "updateFovModifierHand", constant = @Constant(floatValue = 1.0F))
	private float securitycraft$modifyInitialFValue(float f) {
		if (mc.getRenderViewEntity() instanceof SecurityCamera)
			return ((SecurityCamera) mc.getRenderViewEntity()).getZoomAmount();
		else
			return f;
	}

	/**
	 * Captures, copies and stores the last used clipping helper used by rendering any level. This happens regardless of if any
	 * frame is active, though the memory implications from this should be minimal.
	 */
	@Inject(method = "renderWorldPass", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/culling/ICamera;setPosition(DDD)V"))
	private void securitycraft$captureMainLevelClippingHelper(int pass, float partialTicks, long finishTimeNano, CallbackInfo ci) {
		ClippingHelper cameraClippingHelper = new ClippingHelper();
		ClippingHelper currentClippingHelper = ClippingHelperImpl.getInstance();

		cameraClippingHelper.frustum = currentClippingHelper.frustum.clone();
		cameraClippingHelper.projectionMatrix = currentClippingHelper.projectionMatrix.clone();
		cameraClippingHelper.modelviewMatrix = currentClippingHelper.modelviewMatrix.clone();
		cameraClippingHelper.clippingMatrix = currentClippingHelper.clippingMatrix.clone();

		CameraController.lastUsedClippingHelper = cameraClippingHelper;
	}

	/**
	 * Sets the FOV value for frame feed capture to 90. Using debugView instead of this mixin is not a feasible solution, as
	 * debugView also prevents e.g. entities from rendering at all.
	 */
	@Inject(method = "getFOVModifier", at = @At("HEAD"), cancellable = true)
	private void securitycraft$modifyFOVForCameraRendering(float partialTicks, boolean useFOVSetting, CallbackInfoReturnable<Float> cir) {
		if (CameraController.currentlyCapturedCamera != null)
			cir.setReturnValue(90.0F);
	}

	/**
	 * Renders the camera tint if a lens is installed. This cannot be done in a standard overlay, as the tint needs to exist even
	 * when the GUI is hidden with F1
	 */
	@Inject(method = "updateCameraAndRender", at = @At(value = "FIELD", target = "Lnet/minecraft/client/settings/GameSettings;hideGUI:Z", opcode = Opcodes.GETFIELD), locals = LocalCapture.CAPTURE_FAILSOFT)
	private void securitycraft$renderCameraTint(float partialTicks, long nanoTime, CallbackInfo ci, boolean flag, ScaledResolution scaledresolution, int i1, int j1, final int k1, final int l1, int i2, int j, long k, long l) {
		if (mc.getRenderViewEntity() instanceof SecurityCamera) {
			World level = mc.world;
			BlockPos pos = mc.getRenderViewEntity().getPosition().down();
			TileEntity te = level.getTileEntity(pos);

			if (!(te instanceof SecurityCameraBlockEntity))
				return;

			SecurityCameraBlockEntity be = (SecurityCameraBlockEntity) te;
			ItemStack lens = be.getLensContainer().getStackInSlot(0);
			Item item = lens.getItem();

			if (item instanceof LensItem && ((LensItem) item).hasColor(lens)) {
				GlStateManager.enableBlend();
				GlStateManager.alphaFunc(516, 0.1F);
				mc.entityRenderer.setupOverlayRendering();
				Gui.drawRect(0, 0, i1, j1, ((LensItem) item).getColor(lens) + (be.getOpacity() << 24));
				GlStateManager.disableBlend();
			}
		}
	}

	/**
	 * Makes sure distortion effects are not rendered in camera feeds
	 */
	@Inject(method = "setupCameraTransform", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;rotate(FFFF)V", ordinal = 0), cancellable = true)
	private void securitycraft$disableFeedDistortion(float partialTicks, int pass, CallbackInfo ci) {
		if (CameraController.currentlyCapturedCamera != null) {
			orientCamera(partialTicks);
			ci.cancel();
		}
	}

	/**
	 * Minecraft does not specifically render a fog on the entire sky, but only renders it where e.g. a color gradient is
	 * required and uses the GL background color instead. Since this background (which is controlled by the fog color
	 * calculations) cannot be captured by the frame feed itself, the necessary background color is stored here to make it
	 * accessible to the frame feed renderer, which manually renders the background behind the frame feed.
	 */
	@Inject(method = "updateFogColor", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;clearColor(FFFF)V"))
	private void setFrameFeedBackgroundColor(float partialTicks, CallbackInfo ci) {
		if (CameraController.currentlyCapturedCamera != null) {
			CameraController.currentlyCapturedCamera.getRight().setBackgroundColor(new Vector3f(fogColorRed, fogColorGreen, fogColorBlue));
		}
	}
}

package net.geforcemods.securitycraft.mixin.camera;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(value = EntityRenderer.class, priority = 1100)
public class EntityRendererMixin {
	@Shadow
	@Final
	Minecraft mc;

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
	@ModifyVariable(method = "setupCameraTransform", at = @At(value = "JUMP", opcode = Opcodes.IFGT, shift = At.Shift.BEFORE))
	private float securitycraft$disableFeedDistortion(float original) {
		return CameraController.currentlyCapturedCamera != null ? 0.0F : original;
	}
}

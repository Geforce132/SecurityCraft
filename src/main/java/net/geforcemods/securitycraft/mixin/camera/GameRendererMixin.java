package net.geforcemods.securitycraft.mixin.camera;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.entity.camera.CameraController;
import net.geforcemods.securitycraft.entity.camera.SecurityCamera;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.item.IDyeableArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

@Mixin(value = GameRenderer.class, priority = 1100)
public class GameRendererMixin {
	@Shadow
	@Final
	Minecraft minecraft;

	/**
	 * Makes sure camera zooming works, because the fov is only updated when the camera entity is the player itself
	 */
	@ModifyConstant(method = "tickFov", constant = @Constant(floatValue = 1.0F))
	private float securitycraft$modifyInitialFValue(float f) {
		if (minecraft.cameraEntity instanceof SecurityCamera)
			return ((SecurityCamera) minecraft.cameraEntity).getZoomAmount();
		else
			return f;
	}

	/**
	 * Renders the camera tint if a lens is installed. This cannot be done in a standard overlay, as the tint needs to exist even
	 * when the GUI is hidden with F1
	 */
	@Inject(method = "render", at = @At(value = "FIELD", target = "Lnet/minecraft/client/GameSettings;hideGui:Z", opcode = Opcodes.GETFIELD), locals = LocalCapture.CAPTURE_FAILSOFT)
	private void securitycraft$renderCameraTint(float partialTicks, long nanoTime, boolean renderLevel, CallbackInfo ci, int i, int j, MainWindow mainwindow, MatrixStack matrixstack) {
		if (minecraft.cameraEntity instanceof SecurityCamera) {
			World level = minecraft.level;
			BlockPos pos = minecraft.cameraEntity.blockPosition();
			TileEntity te = level.getBlockEntity(pos);

			if (!(te instanceof SecurityCameraBlockEntity))
				return;

			SecurityCameraBlockEntity be = (SecurityCameraBlockEntity) te;
			ItemStack lens = be.getLensContainer().getItem(0);
			Item item = lens.getItem();

			if (item instanceof IDyeableArmorItem && ((IDyeableArmorItem) item).hasCustomColor(lens))
				AbstractGui.fill(matrixstack, 0, 0, mainwindow.getGuiScaledWidth(), mainwindow.getGuiScaledHeight(), ((IDyeableArmorItem) item).getColor(lens) + (be.getOpacity() << 24));
		}
	}

	/**
	 * Makes sure distortion effects are not rendered in camera feeds
	 */
	@Redirect(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;lerp(FFF)F"))
	private float securitycraft$disableFeedDistortion(float delta, float start, float end) {
		return CameraController.isCapturingCamera() ? 0.0F : MathHelper.lerp(delta, start, end);
	}
}

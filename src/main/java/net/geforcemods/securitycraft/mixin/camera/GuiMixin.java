package net.geforcemods.securitycraft.mixin.camera;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.platform.Window;

import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.entity.camera.SecurityCamera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

@Mixin(value = Gui.class, priority = 1100)
public class GuiMixin {
	@Shadow
	@Final
	private Minecraft minecraft;

	/**
	 * Renders the camera tint if a lens is installed. This cannot be done in a standard overlay, as the tint needs to exist even
	 * when the GUI is hidden with F1
	 */
	@Inject(method = "extractRenderState", at = @At("TAIL"))
	private void securitycraft$renderCameraTint(DeltaTracker deltaTracker, boolean shouldRenderLevel, boolean resourcesLoaded, CallbackInfo ci, @Local GuiGraphicsExtractor graphics) {
		if (minecraft.getCameraEntity() instanceof SecurityCamera) {
			Level level = minecraft.level;
			BlockPos pos = minecraft.getCameraEntity().blockPosition();
			Window window = minecraft.getWindow();

			if (!(level.getBlockEntity(pos) instanceof SecurityCameraBlockEntity be))
				return;

			ItemStack lens = be.getLensContainer().getItem(0);

			if (lens.has(DataComponents.DYED_COLOR))
				graphics.fill(0, 0, window.getGuiScaledWidth(), window.getGuiScaledHeight(), lens.get(DataComponents.DYED_COLOR).rgb() + (be.getOpacity() << 24));
		}
	}
}

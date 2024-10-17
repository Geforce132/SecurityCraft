package net.geforcemods.securitycraft.mixin.camera;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;

import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.ViewArea;
import net.minecraft.core.SectionPos;

/**
 * Fixes camera chunks disappearing when the player entity moves while viewing a camera (e.g. while being in a minecart or
 * falling)
 */
@Mixin(value = LevelRenderer.class, priority = 1100)
public class LevelRendererMixin {
	@Shadow
	@Final
	private Minecraft minecraft;

	@WrapWithCondition(method = "setupRender", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ViewArea;repositionCamera(Lnet/minecraft/core/SectionPos;)V"))
	public boolean securitycraft$shouldRepositionCamera(ViewArea viewArea, SectionPos sectionPos) {
		return !PlayerUtils.isPlayerMountedOnCamera(minecraft.player);
	}
}

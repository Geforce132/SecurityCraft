package net.geforcemods.securitycraft.mixin.camera;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;

import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.ViewArea;
import net.minecraft.world.entity.Entity;

@Mixin(value = LevelRenderer.class, priority = 1100)
public class LevelRendererMixin {
	@Shadow
	@Final
	private Minecraft minecraft;

	/**
	 * Fixes camera chunks disappearing when the player entity moves while viewing a camera (e.g. while being in a minecart or
	 * falling)
	 */
	@WrapWithCondition(method = "setupRender", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ViewArea;repositionCamera(DD)V"))
	public boolean securitycraft$shouldRepositionCamera(ViewArea viewArea, double x, double z) {
		return !PlayerUtils.isPlayerMountedOnCamera(minecraft.player);
	}

	/**
	 * Fixes the player not rendering for itself when their camera is not themselves
	 */
	@ModifyExpressionValue(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;getEntity()Lnet/minecraft/world/entity/Entity;", ordinal = 3))
	public Entity securitycraft$makePlayerRenderForItself(Entity cameraEntity, @Local Entity playerEntity) {
		if (playerEntity instanceof LocalPlayer player && PlayerUtils.isPlayerMountedOnCamera(player))
			return playerEntity;

		return cameraEntity;
	}
}

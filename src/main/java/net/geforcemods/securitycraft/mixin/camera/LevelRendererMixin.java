package net.geforcemods.securitycraft.mixin.camera;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.ViewArea;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

@Mixin(value = LevelRenderer.class, priority = 1100)
public class LevelRendererMixin {
	@Shadow
	@Final
	private Minecraft minecraft;
	@Unique
	private Player securitycraft$tempEntity;

	/**
	 * Fixes camera chunks disappearing when the player entity moves while viewing a camera (e.g. while being in a minecart or
	 * falling)
	 */
	@Redirect(method = "setupRender", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ViewArea;repositionCamera(DD)V"))
	public void securitycraft$onRepositionCamera(ViewArea viewArea, double x, double z) {
		if (!PlayerUtils.isPlayerMountedOnCamera(minecraft.player))
			viewArea.repositionCamera(x, z);
	}

	/**
	 * Stores the player viewing a camera that is about to be rendered in a temporary field and sets the actual entity field in
	 * LevelRenderer#renderLevel to null to pass the rendering checks in order to actually make the player render (usually
	 * players wouldn't render if their camera is not themselves)
	 */
	@ModifyVariable(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;isOutsideBuildHeight(I)Z"))
	public Entity securitycraft$storeTempCameraPlayer(Entity original) {
		if (original instanceof LocalPlayer player && PlayerUtils.isPlayerMountedOnCamera(player)) {
			securitycraft$tempEntity = player;
			original = null;
		}

		return original;
	}

	/**
	 * Re-assigns the entity field in LevelRenderer#renderLevel that was reset in the upper mixin to make the rest of the code
	 * run as usual.
	 */
	@ModifyVariable(method = "renderLevel", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/LevelRenderer;renderedEntities:I", ordinal = 2))
	public Entity securitycraft$reassignCameraPlayer(Entity original) {
		if (securitycraft$tempEntity instanceof LocalPlayer && PlayerUtils.isPlayerMountedOnCamera(securitycraft$tempEntity)) {
			original = securitycraft$tempEntity;
			securitycraft$tempEntity = null;
		}

		return original;
	}
}

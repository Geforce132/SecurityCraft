package net.geforcemods.securitycraft.mixin.camera;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.geforcemods.securitycraft.entity.camera.SecurityCamera;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

/**
 * Lets entities get sent to the client even though they're not in range of the player
 */
@Mixin(value = ChunkMap.TrackedEntity.class, priority = 1100)
public abstract class TrackedEntityMixin {
	@Shadow
	@Final
	ServerEntity serverEntity;
	@Shadow
	@Final
	Entity entity;
	@Unique
	private boolean shouldBeSent = false;

	/**
	 * Checks if this entity is in range of a camera that is currently being viewed, and stores the result in the field
	 * shouldBeSent
	 */
	@Inject(method = "updatePlayer", at = @At(value = "FIELD", target = "Lnet/minecraft/world/phys/Vec3;x:D", ordinal = 0), locals = LocalCapture.CAPTURE_FAILSOFT)
	private void securitycraft$onUpdatePlayer(ServerPlayer player, CallbackInfo callback, Vec3 unused, double viewDistance) {
		if (PlayerUtils.isPlayerMountedOnCamera(player)) {
			Vec3 relativePosToCamera = player.getCamera().position().subtract(entity.position());

			if (relativePosToCamera.x >= -viewDistance && relativePosToCamera.x <= viewDistance && relativePosToCamera.z >= -viewDistance && relativePosToCamera.z <= viewDistance)
				shouldBeSent = true;
		}
	}

	/**
	 * Enables entities that should be sent as well as security camera entities to be sent to the client
	 */
	@ModifyVariable(method = "updatePlayer", name = "flag", at = @At(value = "JUMP", opcode = Opcodes.IFEQ, shift = At.Shift.BEFORE, ordinal = 1))
	public boolean securitycraft$modifyFlag(boolean original) {
		boolean originalShouldBeSent = this.shouldBeSent;

		this.shouldBeSent = false;
		return entity instanceof SecurityCamera || original || originalShouldBeSent;
	}
}

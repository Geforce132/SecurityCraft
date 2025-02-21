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

import net.geforcemods.securitycraft.misc.BlockEntityTracker;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

/**
 * Enables entities that are in range of a player-viewed camera, as well as the mounted security camera entity, to be sent to
 * the client.
 */
@Mixin(value = ChunkMap.TrackedEntity.class, priority = 1100)
public abstract class TrackedEntityMixin {
	@Shadow
	@Final
	Entity entity;
	@Unique
	private boolean securitycraft$shouldBeSent = false;

	/**
	 * Checks if this entity is in range of a camera that is currently being viewed, and stores the result in the field
	 * shouldBeSent
	 */
	@Inject(method = "updatePlayer", at = @At(value = "FIELD", target = "Lnet/minecraft/world/phys/Vec3;x:D", ordinal = 0), locals = LocalCapture.CAPTURE_FAILSOFT)
	private void securitycraft$onUpdatePlayer(ServerPlayer player, CallbackInfo ci, Vec3 unused, double viewDistance) {
		if (!BlockEntityTracker.FRAME_VIEWED_SECURITY_CAMERAS.getBlockEntitiesAround(player.level, entity.blockPosition(), (int) viewDistance).isEmpty())
			securitycraft$shouldBeSent = true;
		else if (PlayerUtils.isPlayerMountedOnCamera(player)) {
			if (entity == player.camera) //If the player is mounted to a camera entity, that entity always needs to be sent to the client regardless of distance
				securitycraft$shouldBeSent = true;

			Vec3 relativePosToCamera = player.getCamera().position().subtract(entity.position());

			if (relativePosToCamera.x >= -viewDistance && relativePosToCamera.x <= viewDistance && relativePosToCamera.z >= -viewDistance && relativePosToCamera.z <= viewDistance)
				securitycraft$shouldBeSent = true;
		}
	}

	/**
	 * Enables entities that should be sent as well as security camera entities to be sent to the client
	 */
	@ModifyVariable(method = "updatePlayer", name = "flag", at = @At(value = "JUMP", opcode = Opcodes.IFEQ, shift = At.Shift.BEFORE, ordinal = 1))
	private boolean securitycraft$modifyFlag(boolean original) {
		if (securitycraft$shouldBeSent) {
			this.securitycraft$shouldBeSent = false;
			return true;
		}

		return original;
	}
}

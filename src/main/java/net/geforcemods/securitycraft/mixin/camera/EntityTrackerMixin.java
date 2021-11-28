package net.geforcemods.securitycraft.mixin.camera;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.geforcemods.securitycraft.entity.camera.SecurityCameraEntity;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TrackedEntity;
import net.minecraft.world.server.ChunkManager;

/**
 * Lets entities get sent to the client even though they're not in range of the player
 */
@Mixin(ChunkManager.EntityTracker.class)
public abstract class EntityTrackerMixin {
	@Shadow
	@Final
	private TrackedEntity entry;

	@Shadow
	@Final
	private Entity entity;

	private boolean shouldBeSent = false;

	/**
	 * Checks if this entity is in range of a camera that is currently being viewed, and stores the result in the field shouldBeSent
	 */
	@Inject(method = "updateTrackingState(Lnet/minecraft/entity/player/ServerPlayerEntity;)V", at = @At(value = "INVOKE_ASSIGN", target = "Ljava/lang/Math;min(II)I"), locals = LocalCapture.CAPTURE_FAILSOFT)
	private void onUpdateTrackingState(ServerPlayerEntity player, CallbackInfo callback, Vec3d unused, int viewDistance) {
		if (PlayerUtils.isPlayerMountedOnCamera(player)) {
			Vec3d relativePosToCamera = player.getSpectatingEntity().getPositionVec().subtract(entry.func_219456_b());

			if(relativePosToCamera.x >= -viewDistance && relativePosToCamera.x <= viewDistance && relativePosToCamera.z >= -viewDistance && relativePosToCamera.z <= viewDistance) {
				shouldBeSent = true;
			}
		}
	}

	/**
	 * Enables entities that should be sent as well as security camera entities to be sent to the client
	 */
	@ModifyVariable(method = "updateTrackingState(Lnet/minecraft/entity/player/ServerPlayerEntity;)V", name = "flag", at = @At(value = "JUMP", opcode = Opcodes.IFEQ, shift = At.Shift.BEFORE))
	public boolean modifyFlag(boolean original) {
		boolean shouldBeSent = this.shouldBeSent;

		this.shouldBeSent = false;
		return entity instanceof SecurityCameraEntity || original || shouldBeSent;
	}
}

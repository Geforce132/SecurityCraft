package net.geforcemods.securitycraft.mixin.camera;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.llamalad7.mixinextras.sugar.Local;

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

	@ModifyVariable(method = "updatePlayer", name = "flag", at = @At(value = "JUMP", opcode = Opcodes.IFEQ, shift = At.Shift.BEFORE, ordinal = 2))
	private boolean securitycraft$modifyFlag(boolean original, ServerPlayer player, @Local(ordinal = 0) double viewDistance) {
		if (original)
			return true;

		Entity camera = player.getCamera();

		if (!BlockEntityTracker.FRAME_VIEWED_SECURITY_CAMERAS.getBlockEntitiesAround(player.level(), entity.blockPosition(), (int) viewDistance).isEmpty())
			return true;
		else if (PlayerUtils.isPlayerMountedOnCamera(player)) {
			if (entity == player.camera) //If the player is mounted to a camera entity, that entity always needs to be sent to the client regardless of distance
				return true;

			Vec3 relativePosToCamera = camera.position().subtract(entity.position());

			return relativePosToCamera.x >= -viewDistance && relativePosToCamera.x <= viewDistance && relativePosToCamera.z >= -viewDistance && relativePosToCamera.z <= viewDistance;
		}

		return false;
	}
}

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

import net.geforcemods.securitycraft.entity.camera.SecurityCamera;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

/**
 * Lets entities get sent to the client even though they're not in range of a player
 */
@Mixin(ChunkMap.TrackedEntity.class)
public abstract class TrackedEntityMixin {
	@Shadow
	@Final
	Entity entity;

	@Shadow
	@Final
	ServerEntity serverEntity;

	private boolean shouldBeSent = false;

	/**
	 * Checks if this entity is in range of a camera that is currently being viewed, and stores the result in the field shouldBeSent
	 */
	@Inject(method = "updatePlayer", at = @At(value = "INVOKE_ASSIGN", target = "Ljava/lang/Math;min(II)I"), locals = LocalCapture.CAPTURE_FAILSOFT)
	private void onUpdatePlayer(ServerPlayer player, CallbackInfo callback, Vec3 unused, int viewDistance) {
		if (PlayerUtils.isPlayerMountedOnCamera(player)) {
			Vec3 relativePosToCamera = player.getCamera().position().subtract(serverEntity.sentPos());

			if(relativePosToCamera.x >= -viewDistance && relativePosToCamera.x <= viewDistance && relativePosToCamera.z >= -viewDistance && relativePosToCamera.z <= viewDistance) {
				shouldBeSent = true;
			}
		}
	}

	/**
	 * Modifies a variable in updatePlayer to enable entities that should be sent and SecurityCameras to be sent to the client
	 */
	@ModifyVariable(method = "updatePlayer", name = "flag", at = @At(value = "JUMP", opcode = Opcodes.IFEQ, shift = At.Shift.BEFORE))
	public boolean modifyFlag(boolean original) {
		boolean shouldBeSent = this.shouldBeSent;

		this.shouldBeSent = false;
		return entity instanceof SecurityCamera || original || shouldBeSent;
	}
}

package net.geforcemods.securitycraft.mixin.camera;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.geforcemods.securitycraft.entity.camera.SecurityCamera;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.world.entity.Entity;

/**
 * Allows camera entities to be placed in the world regardless of the distance to a player
 */
@Mixin(ChunkMap.TrackedEntity.class)
public class TrackedEntityMixin {
	@Shadow
	@Final
	Entity entity;

	@ModifyVariable(method = "updatePlayer", name = "flag", at = @At(value = "JUMP", opcode = Opcodes.IFEQ, shift = At.Shift.BEFORE))
	public boolean modifyFlag(boolean original) {
		return entity instanceof SecurityCamera || original;
	}
}

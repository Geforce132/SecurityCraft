package net.geforcemods.securitycraft.mixin.passcode;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.geforcemods.securitycraft.api.IPasscodeProtected;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

/**
 * Marks any passcode-protected entity or block entity that is saved to a structure file to save its salt into its tag
 * when it is written to NBT. This allows these passcode-protected objects to still work with their original passcode, even
 * if their salt has been removed from the level data in the meantime.
 */
@Mixin(StructureTemplate.class)
public class StructureTemplateMixin {
	@ModifyVariable(method = "fillFromWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/BlockEntity;saveWithId()Lnet/minecraft/nbt/CompoundTag;"))
	private BlockEntity securitycraft$markBlockEntityForSaltSaving(BlockEntity original) {
		if (original instanceof IPasscodeProtected passcodeProtected)
			passcodeProtected.setSaveSalt(true);

		return original;
	}

	@ModifyVariable(method = "fillEntityList", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;<init>(DDD)V"))
	private Entity securitycraft$markEntityForSaltSaving(Entity original) {
		if (original instanceof IPasscodeProtected passcodeProtected)
			passcodeProtected.setSaveSalt(true);

		return original;
	}
}

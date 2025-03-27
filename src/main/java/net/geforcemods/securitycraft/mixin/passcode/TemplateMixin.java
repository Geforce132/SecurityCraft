package net.geforcemods.securitycraft.mixin.passcode;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.geforcemods.securitycraft.api.IPasscodeProtected;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.gen.structure.template.Template;

/**
 * Marks any passcode-protected entity or block entity that is saved to a structure file to save its salt into its tag
 * when it is written to NBT. This allows these passcode-protected objects to still work with their original passcode, even
 * if their salt has been removed from the level data in the meantime.
 */
@Mixin(Template.class)
public class TemplateMixin {
	@ModifyVariable(method = "takeBlocksFromWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/tileentity/TileEntity;writeToNBT(Lnet/minecraft/nbt/NBTTagCompound;)Lnet/minecraft/nbt/NBTTagCompound;"))
	private TileEntity securitycraft$markBlockEntityForSaltSaving(TileEntity original) {
		if (original instanceof IPasscodeProtected)
			((IPasscodeProtected) original).setSaveSalt(true);

		return original;
	}

	@ModifyVariable(method = "takeEntitiesFromWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Vec3d;<init>(DDD)V"))
	private Entity securitycraft$markEntityForSaltSaving(Entity original) {
		if (original instanceof IPasscodeProtected)
			((IPasscodeProtected) original).setSaveSalt(true);

		return original;
	}
}

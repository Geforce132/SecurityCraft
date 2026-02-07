package net.geforcemods.securitycraft.mixin.datafix;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.datafixers.schemas.Schema;

import net.minecraft.util.filefix.FileFix;
import net.minecraft.util.filefix.fixes.DimensionStorageFileFix;
import net.minecraft.util.filefix.operations.FileFixOperations;

/**
 * Moves SecurityCraft's salt data from data/securitycraft-salts.dat to data/securitycraft/salts.dat
 */
@Mixin(DimensionStorageFileFix.class)
public abstract class DimensionStorageFileFixMixin extends FileFix {
	private DimensionStorageFileFixMixin(Schema schema) {
		super(schema);
	}

	@Inject(method = "makeFixer", at = @At("TAIL"))
	private void securitycraft$moveSalts(CallbackInfo ci) {
		addFileFixOperation(FileFixOperations.move("data/securitycraft-salts.dat", "data/securitycraft/salts.dat"));
	}
}

package net.geforcemods.securitycraft.mixin.passcode;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.geforcemods.securitycraft.api.IPasscodeProtected;
import net.minecraft.network.play.ServerPlayNetHandler;
import net.minecraft.tileentity.TileEntity;

/**
 * Marks any passcode-protected block entity that is saved to an item using CTRL-pick-block to save its salt into its tag
 * when it is written to NBT. This allows these passcode-protected objects to still work with their original passcode, even
 * if their salt has been removed from the level data in the meantime.
 */
@Mixin(ServerPlayNetHandler.class)
public class ServerPlayNetHandlerMixin {
	@ModifyVariable(method = "handleSetCreativeModeSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/tileentity/TileEntity;save(Lnet/minecraft/nbt/CompoundNBT;)Lnet/minecraft/nbt/CompoundNBT;"))
	private TileEntity securitycraft$markBlockEntityForSaltSaving(TileEntity original) {
		if (original instanceof IPasscodeProtected)
			((IPasscodeProtected) original).setSaveSalt(true);

		return original;
	}
}

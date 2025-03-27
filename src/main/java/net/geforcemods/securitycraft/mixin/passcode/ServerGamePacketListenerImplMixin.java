package net.geforcemods.securitycraft.mixin.passcode;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.geforcemods.securitycraft.api.IPasscodeProtected;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Marks any passcode-protected block entity that is saved to an item using CTRL-pick-block to save its salt into its tag
 * when it is written to NBT. This allows these passcode-protected objects to still work with their original passcode, even
 * if their salt has been removed from the level data in the meantime.
 */
@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerImplMixin {
	@ModifyVariable(method = "handleSetCreativeModeSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;registryAccess()Lnet/minecraft/core/RegistryAccess;"))
	private BlockEntity securitycraft$markBlockEntityForSaltSaving(BlockEntity original) {
		if (original instanceof IPasscodeProtected passcodeProtected)
			passcodeProtected.setSaveSalt(true);

		return original;
	}
}

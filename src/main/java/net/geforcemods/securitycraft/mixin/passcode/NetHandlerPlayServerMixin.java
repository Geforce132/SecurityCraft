package net.geforcemods.securitycraft.mixin.passcode;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.geforcemods.securitycraft.api.IPasscodeProtected;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.tileentity.TileEntity;

/**
 * Marks any passcode-protected block entity that is saved to an item using CTRL-pick-block to save its salt into its tag
 * when it is written to NBT. This allows these passcode-protected objects to still work with their original passcode, even
 * if their salt has been removed from the level data in the meantime.
 */
@Mixin(NetHandlerPlayServer.class)
public class NetHandlerPlayServerMixin {
	@ModifyVariable(method = "processCreativeInventoryAction", at = @At(value = "INVOKE", target = "Lnet/minecraft/tileentity/TileEntity;writeToNBT(Lnet/minecraft/nbt/NBTTagCompound;)Lnet/minecraft/nbt/NBTTagCompound;"))
	private TileEntity securitycraft$markBlockEntityForSaltSaving(TileEntity original) {
		if (original instanceof IPasscodeProtected)
			((IPasscodeProtected) original).setSaveSalt(true);

		return original;
	}
}

package net.geforcemods.securitycraft.mixin.f3;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

import me.cominixo.betterf3.modules.TargetModule;
import net.geforcemods.securitycraft.misc.F3Spoofer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

/**
 * Changes the targeted block/fluid text on the right side of the screen to the disguised block/fluid, so that the player cannot see the actual block (sand mine, fake water, inventory scanner (if disguised))
 */
@Mixin(TargetModule.class)
public class BetterF3TargetModuleMixin
{
	@Redirect(method="update", at=@At(value="INVOKE", target="Lnet/minecraft/client/multiplayer/ClientLevel;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"))
	public BlockState spoofBlockState(ClientLevel level, BlockPos pos)
	{
		return F3Spoofer.spoofBlockState(level.getBlockState(pos), pos);
	}

	@ModifyVariable(method="update", name="fluidState", at=@At(value="INVOKE_ASSIGN", target="Lnet/minecraft/client/multiplayer/ClientLevel;getFluidState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/material/FluidState;"))
	public FluidState spoofFluidState(FluidState originalState)
	{
		return F3Spoofer.spoofFluidState(originalState);
	}
}

package net.geforcemods.securitycraft.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import me.cominixo.betterf3.modules.TargetModule;
import net.geforcemods.securitycraft.misc.F3Spoofer;
import net.minecraft.fluid.FluidState;

@Mixin(TargetModule.class)
public class BetterF3TargetModuleMixin
{
	//TODO: get the position somehow, without recomputing the ray trace
	//	@ModifyVariable(method="update", at=@At(value="INVOKE_ASSIGN", target="Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"))
	//	public BlockState spoofBlockState(BlockState originalState)
	//	{
	//		return F3Spoofer.spoofBlockState(originalState, pos);
	//	}

	@ModifyVariable(method="update", at=@At(value="INVOKE_ASSIGN", target="Lnet/minecraft/world/World;getFluidState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/fluid/FluidState;"))
	public FluidState spoofFluidState(FluidState originalState)
	{
		return F3Spoofer.spoofFluidState(originalState);
	}
}

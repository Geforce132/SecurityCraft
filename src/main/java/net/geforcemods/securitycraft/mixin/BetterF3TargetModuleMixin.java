package net.geforcemods.securitycraft.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

import me.cominixo.betterf3.modules.TargetModule;
import net.geforcemods.securitycraft.misc.F3Spoofer;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;

@Mixin(TargetModule.class)
public class BetterF3TargetModuleMixin
{
	@Redirect(method="update", at=@At(value="INVOKE", target="Lnet/minecraft/client/world/ClientWorld;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"))
	public BlockState spoofBlockState(ClientWorld world, BlockPos pos)
	{
		return F3Spoofer.spoofBlockState(world.getBlockState(pos), pos);
	}

	@ModifyVariable(method="update", name="fluidState", at=@At(value="INVOKE_ASSIGN", target="Lnet/minecraft/client/world/ClientWorld;getFluidState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/fluid/FluidState;"))
	public FluidState spoofFluidState(FluidState originalState)
	{
		return F3Spoofer.spoofFluidState(originalState);
	}
}

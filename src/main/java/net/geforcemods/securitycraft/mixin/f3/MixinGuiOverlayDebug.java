package net.geforcemods.securitycraft.mixin.f3;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.geforcemods.securitycraft.blocks.BlockDisguisable;
import net.geforcemods.securitycraft.blocks.mines.BlockFullMineBase;
import net.geforcemods.securitycraft.blocks.mines.BlockFurnaceMine;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFurnace;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiOverlayDebug;
import net.minecraft.init.Blocks;

@Mixin(GuiOverlayDebug.class)
public class MixinGuiOverlayDebug
{
	@ModifyVariable(method="getDebugInfoRight", at=@At(value="INVOKE_ASSIGN", target="Lnet/minecraft/client/multiplayer/WorldClient;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/state/IBlockState;"))
	public IBlockState spoofBlockState(IBlockState originalState)
	{
		Block originalBlock = originalState.getBlock();

		if(originalBlock instanceof BlockDisguisable)
		{
			IBlockState disguisedState = ((BlockDisguisable)originalBlock).getDisguisedBlockState(Minecraft.getMinecraft().world, Minecraft.getMinecraft().objectMouseOver.getBlockPos());

			return disguisedState != null ? disguisedState : originalState;
		}
		else if(originalBlock instanceof BlockFullMineBase)
			return ((BlockFullMineBase)originalBlock).getBlockDisguisedAs().getDefaultState();
		else if(originalBlock instanceof BlockFurnaceMine)
			return Blocks.FURNACE.getDefaultState().withProperty(BlockFurnace.FACING, originalState.getValue(BlockFurnace.FACING));

		return originalState;
	}
}

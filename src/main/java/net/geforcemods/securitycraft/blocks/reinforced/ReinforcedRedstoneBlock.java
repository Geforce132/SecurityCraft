package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Arrays;
import java.util.List;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IDoorActivator;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class ReinforcedRedstoneBlock extends BaseReinforcedBlock
{
	public ReinforcedRedstoneBlock(Block.Properties properties, Block vB)
	{
		super(properties, vB);
	}

	@Override
	public boolean canProvidePower(BlockState state)
	{
		return true;
	}

	@Override
	public int getWeakPower(BlockState state, IBlockReader world, BlockPos pos, Direction side)
	{
		return 15;
	}

	public static class DoorActivator implements IDoorActivator {
		private final List<Block> blocks = Arrays.asList(SCContent.REINFORCED_REDSTONE_BLOCK.get());

		@Override
		public boolean isPowering(World world, BlockPos pos, BlockState state, TileEntity te) {
			return true;
		}

		@Override
		public List<Block> getBlocks() {
			return blocks;
		}
	}
}

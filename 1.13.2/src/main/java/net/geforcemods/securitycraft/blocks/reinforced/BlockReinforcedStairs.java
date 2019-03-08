package net.geforcemods.securitycraft.blocks.reinforced;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

//TODO: keep, but make ready for different blocks (similar to BlockReinforcedRotatedPillar)
public class BlockReinforcedStairs extends BlockStairs {

	public BlockReinforcedStairs(Block baseBlock, int meta) {
		super(meta != 0 ? baseBlock.getStateFromMeta(meta) : baseBlock.getDefaultState());
		useNeighborBrightness = true;

		if(baseBlock == SCContent.reinforcedWoodPlanks)
			setSoundType(SoundType.WOOD);
		else
			setSoundType(SoundType.STONE);
	}

	@Override
	public void onReplaced(IBlockState state, World world, BlockPos pos, IBlockState newState, boolean isMoving)
	{
		super.onReplaced(state, world, pos, newState, isMoving);
		world.removeTileEntity(pos);
	}

	@Override
	public TileEntity createTileEntity(IBlockState state, IBlockReader reader) {
		return new TileEntityOwnable();
	}

}

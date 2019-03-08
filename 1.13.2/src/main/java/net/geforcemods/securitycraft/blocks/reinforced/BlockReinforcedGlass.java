package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Arrays;
import java.util.Random;

import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockGlass;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

//TODO: keep, but make ready for 16 different blocks (getVanillaBlock needs to be modular)
public class BlockReinforcedGlass extends BlockGlass implements IReinforcedBlock {

	public BlockReinforcedGlass(Material material) {
		super(material, false);
		setSoundType(SoundType.GLASS);
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

	@Override
	public int quantityDropped(Random random)
	{
		return 1;
	}

	@Override
	public Block getVanillaBlock()
	{
		return Arrays.asList(new Block[] {
				Blocks.GLASS
		});
	}
}

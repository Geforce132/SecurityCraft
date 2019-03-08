package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStainedGlassPane;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

//TODO: keep, but make ready for 16 different blocks (getVanillaBlock needs to be modular, extend BlockReinforcedPane)
public class BlockReinforcedStainedGlassPanes extends BlockStainedGlassPane implements IReinforcedBlock
{
	public BlockReinforcedStainedGlassPanes()
	{
		setSoundType(SoundType.GLASS);
	}

	@Override
	public TileEntity createTileEntity(IBlockState state, IBlockReader reader)
	{
		return new TileEntityOwnable();
	}

	@Override
	public float[] getBeaconColorMultiplier(IBlockState state, World world, BlockPos pos, BlockPos beaconPos)
	{
		//sponge fix
		if(world.isRemote)
			return state.getValue(COLOR).getColorComponentValues();
		else
			return new float[] {0.0F, 0.0F, 0.0F};
	}

	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
	{
		return Arrays.asList(new ItemStack[] {new ItemStack(SCContent.reinforcedStainedGlassPanes, 1, state.getValue(COLOR).getMetadata())});
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
				Blocks.STAINED_GLASS_PANE
		});
	}
}

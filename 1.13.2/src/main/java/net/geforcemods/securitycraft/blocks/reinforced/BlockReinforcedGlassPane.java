package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPane;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.IBlockReader;

//TODO: rename to BlockReinforcedPane, and make ready for different blocks (similar to BlockReinforcedRotatedPillar)
public class BlockReinforcedGlassPane extends BlockPane implements IReinforcedBlock
{
	public BlockReinforcedGlassPane()
	{
		super(Material.GLASS, false);
		setSoundType(SoundType.GLASS);
	}

	@Override
	public TileEntity createTileEntity(IBlockState state, IBlockReader reader)
	{
		return new TileEntityOwnable();
	}

	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
	{
		return Arrays.asList(new ItemStack[] {new ItemStack(SCContent.reinforcedGlassPane)});
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
				Blocks.GLASS_PANE
		});
	}
}

package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.List;

import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPane;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import scala.actors.threadpool.Arrays;

public class BlockReinforcedGlassPane extends BlockPane implements ITileEntityProvider, IReinforcedBlock
{
	public BlockReinforcedGlassPane()
	{
		super(Material.GLASS, false);
		setSoundType(SoundType.GLASS);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta)
	{
		return new TileEntityOwnable();
	}

	@Override
	public List<Block> getVanillaBlocks()
	{
		return Arrays.asList(new Block[] {
				Blocks.GLASS_PANE
		});
	}

	@Override
	public int getAmount()
	{
		return 1;
	}
}

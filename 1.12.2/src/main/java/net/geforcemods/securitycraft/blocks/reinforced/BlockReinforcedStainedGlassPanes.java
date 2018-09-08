package net.geforcemods.securitycraft.blocks.reinforced;

import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.minecraft.block.BlockStainedGlassPane;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockReinforcedStainedGlassPanes extends BlockStainedGlassPane implements ITileEntityProvider
{
	public BlockReinforcedStainedGlassPanes()
	{
		setSoundType(SoundType.GLASS);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta)
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
}

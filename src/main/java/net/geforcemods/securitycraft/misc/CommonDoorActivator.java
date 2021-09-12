package net.geforcemods.securitycraft.misc;

import java.util.Arrays;
import java.util.List;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IDoorActivator;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CommonDoorActivator implements IDoorActivator
{
	private List<Block> blocks = Arrays.asList(new Block[] {
			SCContent.LASER_BLOCK.get(),
			SCContent.RETINAL_SCANNER.get(),
			SCContent.KEYPAD.get(),
			SCContent.KEYCARD_READER.get(),
			SCContent.REINFORCED_STONE_PRESSURE_PLATE.get(),
			SCContent.REINFORCED_OAK_PRESSURE_PLATE.get(),
			SCContent.REINFORCED_SPRUCE_PRESSURE_PLATE.get(),
			SCContent.REINFORCED_BIRCH_PRESSURE_PLATE.get(),
			SCContent.REINFORCED_JUNGLE_PRESSURE_PLATE.get(),
			SCContent.REINFORCED_ACACIA_PRESSURE_PLATE.get(),
			SCContent.REINFORCED_DARK_OAK_PRESSURE_PLATE.get(),
			SCContent.REINFORCED_STONE_BUTTON.get(),
			SCContent.REINFORCED_OAK_BUTTON.get(),
			SCContent.REINFORCED_SPRUCE_BUTTON.get(),
			SCContent.REINFORCED_BIRCH_BUTTON.get(),
			SCContent.REINFORCED_JUNGLE_BUTTON.get(),
			SCContent.REINFORCED_ACACIA_BUTTON.get(),
			SCContent.REINFORCED_DARK_OAK_BUTTON.get(),
			SCContent.REINFORCED_LEVER.get(),
			SCContent.REINFORCED_OBSERVER.get()
	});

	@Override
	public boolean isPowering(World world, BlockPos pos, BlockState state, TileEntity te)
	{
		return state.get(BlockStateProperties.POWERED);
	}

	@Override
	public List<Block> getBlocks()
	{
		return blocks;
	}
}

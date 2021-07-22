package net.geforcemods.securitycraft.misc;

import java.util.Arrays;
import java.util.List;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IDoorActivator;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

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
			SCContent.REINFORCED_CRIMSON_PRESSURE_PLATE.get(),
			SCContent.REINFORCED_WARPED_PRESSURE_PLATE.get(),
			SCContent.REINFORCED_POLISHED_BLACKSTONE_PRESSURE_PLATE.get(),
			SCContent.REINFORCED_STONE_BUTTON.get(),
			SCContent.REINFORCED_OAK_BUTTON.get(),
			SCContent.REINFORCED_SPRUCE_BUTTON.get(),
			SCContent.REINFORCED_BIRCH_BUTTON.get(),
			SCContent.REINFORCED_JUNGLE_BUTTON.get(),
			SCContent.REINFORCED_ACACIA_BUTTON.get(),
			SCContent.REINFORCED_DARK_OAK_BUTTON.get(),
			SCContent.REINFORCED_CRIMSON_BUTTON.get(),
			SCContent.REINFORCED_WARPED_BUTTON.get(),
			SCContent.REINFORCED_POLISHED_BLACKSTONE_BUTTON.get(),
			SCContent.REINFORCED_LEVER.get()
	});

	@Override
	public boolean isPowering(Level world, BlockPos pos, BlockState state, BlockEntity te)
	{
		return state.getValue(BlockStateProperties.POWERED);
	}

	@Override
	public List<Block> getBlocks()
	{
		return blocks;
	}
}

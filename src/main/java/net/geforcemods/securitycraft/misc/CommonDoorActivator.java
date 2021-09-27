package net.geforcemods.securitycraft.misc;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IDoorActivator;
import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CommonDoorActivator implements Function<Object,IDoorActivator>, IDoorActivator
{
	private final PropertyBool poweredProperty = PropertyBool.create("powered");
	private List<Block> blocks = Arrays.asList(new Block[] {
			SCContent.laserBlock,
			SCContent.retinalScanner,
			SCContent.keypad,
			SCContent.keycardReader,
			SCContent.reinforcedStonePressurePlate,
			SCContent.reinforcedWoodenPressurePlate,
			SCContent.reinforcedStoneButton,
			SCContent.reinforcedWoodenButton,
			SCContent.reinforcedLever,
			SCContent.reinforcedObserver,
			SCContent.keyPanelFloorCeilingBlock,
			SCContent.keyPanelWallBlock
	});

	@Override
	public IDoorActivator apply(Object o)
	{
		return this;
	}

	@Override
	public boolean isPowering(World world, BlockPos pos, IBlockState state, TileEntity te)
	{
		return state.getValue(poweredProperty);
	}

	@Override
	public List<Block> getBlocks()
	{
		return blocks;
	}
}

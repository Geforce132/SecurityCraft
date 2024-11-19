package net.geforcemods.securitycraft.misc;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IDoorActivator;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockLever;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CommonDoorActivator implements Function<Object, IDoorActivator>, IDoorActivator {
	private final PropertyBool poweredProperty = PropertyBool.create("powered");
	//@formatter:off
	private List<Block> blocks = Arrays.asList(
			SCContent.keyPanelFloorCeilingBlock,
			SCContent.keyPanelWallBlock,
			SCContent.keycardLockFloorCeilingBlock,
			SCContent.keycardLockWallBlock,
			SCContent.keycardReader,
			SCContent.keypad,
			SCContent.laserBlock,
			SCContent.panicButton,
			SCContent.portableRadar,
			SCContent.reinforcedLever,
			SCContent.reinforcedObserver,
			SCContent.reinforcedStoneButton,
			SCContent.reinforcedWoodenButton,
			SCContent.retinalScanner,
			SCContent.riftStabilizer,
			SCContent.securityCamera,
			SCContent.sonicSecuritySystem);
	//@formatter:on

	@Override
	public IDoorActivator apply(Object o) {
		return this;
	}

	@Override
	public boolean isPowering(World world, BlockPos pos, IBlockState state, TileEntity te, EnumFacing direction, int distance) {
		if (state.getValue(poweredProperty)) {
			if (distance == 2) {
				if (state.getPropertyKeys().contains(BlockLever.FACING))
					return direction == state.getValue(BlockLever.FACING).getFacing();
				else if (state.getPropertyKeys().contains(BlockDirectional.FACING))
					return direction == state.getValue(BlockDirectional.FACING);
			}

			return true;
		}

		return false;
	}

	@Override
	public List<Block> getBlocks() {
		return blocks;
	}
}

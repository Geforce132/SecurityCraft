package net.geforcemods.securitycraft.misc;

import java.util.ArrayList;
import java.util.List;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IDoorActivator;
import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class CommonDoorActivator implements IDoorActivator {
	//@formatter:off
	private static final List<Block> BLOCKS = new ArrayList<>(List.of(
			SCContent.KEYCARD_LOCK.get(),
			SCContent.KEYCARD_READER.get(),
			SCContent.KEYPAD.get(),
			SCContent.KEY_PANEL_BLOCK.get(),
			SCContent.LASER_BLOCK.get(),
			SCContent.PORTABLE_RADAR.get(),
			SCContent.REINFORCED_LEVER.get(),
			SCContent.REINFORCED_OBSERVER.get(),
			SCContent.RETINAL_SCANNER.get(),
			SCContent.SECURITY_CAMERA.get(),
			SCContent.SONIC_SECURITY_SYSTEM.get()));
	//@formatter:on

	public static boolean addActivator(Block block) {
		if (BLOCKS.contains(block))
			return false;
		else
			return BLOCKS.add(block);
	}

	@Override
	public boolean isPowering(Level level, BlockPos pos, BlockState state, BlockEntity be, Direction direction, int distance) {
		if (state.getValue(BlockStateProperties.POWERED)) {
			if (distance == 2) {
				//checking that e.g. a lever/button is correctly attached to the block
				if (state.hasProperty(BlockStateProperties.ATTACH_FACE) && state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
					Direction.Axis offsetAxis = direction.getAxis();
					Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
					AttachFace attachFace = state.getValue(BlockStateProperties.ATTACH_FACE);

					switch (offsetAxis) {
						case X, Z:
							if (attachFace != AttachFace.WALL || direction != facing)
								return false;

							break;
						case Y:
							if ((direction == Direction.UP && attachFace != AttachFace.FLOOR) || (direction == Direction.DOWN && attachFace != AttachFace.CEILING))
								return false;

							break;
					}
				}
				else if (state.hasProperty(BlockStateProperties.FACING))
					return state.getValue(BlockStateProperties.FACING) == direction;
				else if (state.hasProperty(SecurityCameraBlock.FACING))
					return state.getValue(SecurityCameraBlock.FACING) == direction;
			}

			return true;
		}

		return false;
	}

	@Override
	public List<Block> getBlocks() {
		return BLOCKS;
	}
}

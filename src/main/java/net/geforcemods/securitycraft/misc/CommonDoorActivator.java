package net.geforcemods.securitycraft.misc;

import java.util.Arrays;
import java.util.List;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IDoorActivator;
import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.properties.AttachFace;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CommonDoorActivator implements IDoorActivator {
	//@formatter:off
	private List<Block> blocks = Arrays.asList(
			SCContent.KEYCARD_LOCK.get(),
			SCContent.KEYCARD_READER.get(),
			SCContent.KEYPAD.get(),
			SCContent.KEY_PANEL_BLOCK.get(),
			SCContent.LASER_BLOCK.get(),
			SCContent.PANIC_BUTTON.get(),
			SCContent.PORTABLE_RADAR.get(),
			SCContent.REINFORCED_ACACIA_BUTTON.get(),
			SCContent.REINFORCED_BIRCH_BUTTON.get(),
			SCContent.REINFORCED_CRIMSON_BUTTON.get(),
			SCContent.REINFORCED_DARK_OAK_BUTTON.get(),
			SCContent.REINFORCED_JUNGLE_BUTTON.get(),
			SCContent.REINFORCED_LEVER.get(),
			SCContent.REINFORCED_OAK_BUTTON.get(),
			SCContent.REINFORCED_OBSERVER.get(),
			SCContent.REINFORCED_POLISHED_BLACKSTONE_BUTTON.get(),
			SCContent.REINFORCED_SPRUCE_BUTTON.get(),
			SCContent.REINFORCED_STONE_BUTTON.get(),
			SCContent.REINFORCED_WARPED_BUTTON.get(),
			SCContent.RETINAL_SCANNER.get(),
			SCContent.RIFT_STABILIZER.get(),
			SCContent.SECURITY_CAMERA.get(),
			SCContent.SONIC_SECURITY_SYSTEM.get());
	//@formatter:on

	@Override
	public boolean isPowering(World level, BlockPos pos, BlockState state, TileEntity be, Direction direction, int distance) {
		if (state.getValue(BlockStateProperties.POWERED)) {
			if (distance == 2) {
				//checking that e.g. a lever/button is correctly attached to the block
				if (state.hasProperty(BlockStateProperties.ATTACH_FACE) && state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
					Direction.Axis offsetAxis = direction.getAxis();
					Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
					AttachFace attachFace = state.getValue(BlockStateProperties.ATTACH_FACE);

					switch (offsetAxis) {
						case X:
						case Z:
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
		return blocks;
	}
}

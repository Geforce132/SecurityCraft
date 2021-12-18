package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.tileentity.ScannerDoorTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.world.IBlockReader;

public class ScannerDoorBlock extends SpecialDoorBlock {
	public ScannerDoorBlock(Properties properties) {
		super(properties);
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new ScannerDoorTileEntity();
	}

	@Override
	public Item getDoorItem() {
		return SCContent.SCANNER_DOOR_ITEM.get();
	}

	public static Direction.Axis getFacingAxis(BlockState state) {
		Direction facing = state.get(DoorBlock.FACING);

		return state.get(DoorBlock.OPEN) ? facing.rotateY().getAxis() : facing.getAxis();
	}
}

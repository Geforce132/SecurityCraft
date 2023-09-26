package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blockentities.ScannerDoorBlockEntity;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.world.IBlockReader;

public class ScannerDoorBlock extends SpecialDoorBlock {
	public ScannerDoorBlock(AbstractBlock.Properties properties) {
		super(properties);
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader level) {
		return new ScannerDoorBlockEntity();
	}

	@Override
	public Item getDoorItem() {
		return SCContent.SCANNER_DOOR_ITEM.get();
	}

	public static Direction.Axis getFacingAxis(BlockState state) {
		Direction facing = state.getValue(DoorBlock.FACING);

		return state.getValue(DoorBlock.OPEN) ? facing.getClockWise().getAxis() : facing.getAxis();
	}
}

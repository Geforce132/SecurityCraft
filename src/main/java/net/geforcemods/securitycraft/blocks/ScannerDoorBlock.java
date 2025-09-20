package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blockentities.ScannerDoorBlockEntity;
import net.geforcemods.securitycraft.util.LevelUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;

public class ScannerDoorBlock extends SpecialDoorBlock {
	public ScannerDoorBlock(BlockBehaviour.Properties properties, BlockSetType blockSetType) {
		super(properties, blockSetType);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new ScannerDoorBlockEntity(pos, state);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return level.isClientSide() ? null : BaseEntityBlock.createTickerHelper(type, SCContent.SCANNER_DOOR_BLOCK_ENTITY.get(), LevelUtils::blockEntityTicker);
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

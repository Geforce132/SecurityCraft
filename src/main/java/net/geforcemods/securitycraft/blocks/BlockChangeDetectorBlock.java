package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blockentities.BlockChangeDetectorBlockEntity;
import net.geforcemods.securitycraft.util.LevelUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class BlockChangeDetectorBlock extends DisguisableBlock {
	public BlockChangeDetectorBlock(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (!level.isClientSide && level.getBlockEntity(pos) instanceof BlockChangeDetectorBlockEntity be) {
			if (player.isShiftKeyDown()) {
				be.changeMode();
				System.out.println(be.getMode());
			}
			else {
				be.changeRange();
				System.out.println(be.getRange());
			}
			//			NetworkHooks.openGui((ServerPlayer) player, be, pos);
		}

		return InteractionResult.SUCCESS;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new BlockChangeDetectorBlockEntity(pos, state);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return createTickerHelper(type, SCContent.beTypeBlockChangeDetector, LevelUtils::blockEntityTicker);
	}
}

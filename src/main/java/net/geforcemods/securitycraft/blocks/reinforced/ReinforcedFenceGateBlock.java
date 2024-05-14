package net.geforcemods.securitycraft.blocks.reinforced;

import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.geforcemods.securitycraft.blockentities.AllowlistOnlyBlockEntity;
import net.geforcemods.securitycraft.blockentities.ReinforcedFenceGateBlockEntity;
import net.geforcemods.securitycraft.blocks.OwnableFenceGateBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;

public class ReinforcedFenceGateBlock extends OwnableFenceGateBlock implements IReinforcedBlock {
	private final Block vanillaBlock;

	public ReinforcedFenceGateBlock(BlockBehaviour.Properties properties, WoodType woodType, Block vanillaBlock) {
		super(properties, woodType);
		this.vanillaBlock = vanillaBlock;
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		//only allow the owner or players on the allowlist to access a reinforced fence gate
		if (level.getBlockEntity(pos) instanceof AllowlistOnlyBlockEntity be && (be.isOwnedBy(player) || be.isAllowed(player))) {
			if (state.getValue(OPEN)) {
				state = state.setValue(OPEN, false);
				level.setBlock(pos, state, 10);
			}
			else {
				Direction direction = player.getDirection();

				if (state.getValue(FACING) == direction.getOpposite())
					state = state.setValue(FACING, direction);

				state = state.setValue(OPEN, true);
				level.setBlock(pos, state, 10);
			}

			boolean isOpen = state.getValue(OPEN);

			level.playSound(null, pos, isOpen ? openSound : closeSound, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.1F + 0.9F);
			level.gameEvent(player, isOpen ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
		}

		return InteractionResult.sidedSuccess(level.isClientSide);
	}

	@Override
	public boolean triggerEvent(BlockState state, Level level, BlockPos pos, int id, int param) {
		BlockEntity be = level.getBlockEntity(pos);

		return be != null && be.triggerEvent(id, param);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new ReinforcedFenceGateBlockEntity(pos, state);
	}

	@Override
	public Block getVanillaBlock() {
		return vanillaBlock;
	}
}

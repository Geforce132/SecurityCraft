package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blockentities.UsernameLoggerBlockEntity;
import net.geforcemods.securitycraft.network.client.OpenScreen;
import net.geforcemods.securitycraft.network.client.OpenScreen.DataType;
import net.geforcemods.securitycraft.util.LevelUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.network.PacketDistributor;

public class UsernameLoggerBlock extends DisguisableBlock {
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

	public UsernameLoggerBlock(BlockBehaviour.Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false));
	}

	@Override
	public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
		if (!level.isClientSide && level.getBlockEntity(pos) instanceof UsernameLoggerBlockEntity be && (be.isOwnedBy(player) || be.isAllowed(player))) {
			if (be.isDisabled())
				player.displayClientMessage(Utils.localize("gui.securitycraft:scManual.disabled"), true);
			else
				PacketDistributor.sendToPlayer((ServerPlayer) player, new OpenScreen(DataType.USERNAME_LOGGER, pos));
		}

		return InteractionResult.SUCCESS;
	}

	@Override
	public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
		return direction != null && direction.getAxis() != Axis.Y;
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		return super.getStateForPlacement(ctx).setValue(FACING, ctx.getHorizontalDirection().getOpposite());
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(FACING, WATERLOGGED);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new UsernameLoggerBlockEntity(pos, state);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return !level.isClientSide ? BaseEntityBlock.createTickerHelper(type, SCContent.USERNAME_LOGGER_BLOCK_ENTITY.get(), LevelUtils::blockEntityTicker) : null;
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}
}

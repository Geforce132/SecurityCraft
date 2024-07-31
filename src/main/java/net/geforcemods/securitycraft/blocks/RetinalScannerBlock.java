package net.geforcemods.securitycraft.blocks;

import java.util.Random;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blockentities.RetinalScannerBlockEntity;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;

public class RetinalScannerBlock extends DisguisableBlock {
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

	public RetinalScannerBlock(AbstractBlock.Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(POWERED, false).setValue(WATERLOGGED, false));
	}

	@Override
	public void setPlacedBy(World level, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack) {
		if (entity instanceof PlayerEntity) {
			TileEntity te = level.getBlockEntity(pos);

			if (!level.isClientSide && te instanceof RetinalScannerBlockEntity)
				((RetinalScannerBlockEntity) te).setPlayerProfile(((PlayerEntity) entity).getGameProfile());

			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(level, pos, (PlayerEntity) entity));
		}
	}

	@Override
	public void tick(BlockState state, ServerWorld level, BlockPos pos, Random random) {
		if (!level.isClientSide && state.getValue(POWERED)) {
			level.setBlockAndUpdate(pos, state.setValue(POWERED, false));
			BlockUtils.updateIndirectNeighbors(level, pos, SCContent.RETINAL_SCANNER.get());
		}
	}

	@Override
	public void onRemove(BlockState state, World level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock()) && state.getValue(POWERED)) {
			level.updateNeighborsAt(pos, this);
			BlockUtils.updateIndirectNeighbors(level, pos, this);
		}

		super.onRemove(state, level, pos, newState, isMoving);
	}

	@Override
	public boolean isSignalSource(BlockState state) {
		return true;
	}

	@Override
	public boolean shouldCheckWeakPower(BlockState state, IWorldReader level, BlockPos pos, Direction side) {
		return false;
	}

	@Override
	public int getSignal(BlockState state, IBlockReader level, BlockPos pos, Direction side) {
		if (state.getValue(POWERED))
			return 15;
		else
			return 0;
	}

	@Override
	public int getDirectSignal(BlockState state, IBlockReader level, BlockPos pos, Direction side) {
		return state.getValue(POWERED) ? 15 : 0;
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext ctx) {
		return super.getStateForPlacement(ctx).setValue(FACING, ctx.getHorizontalDirection().getOpposite()).setValue(POWERED, false);
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(FACING, POWERED, WATERLOGGED);
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader level) {
		return new RetinalScannerBlockEntity();
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
